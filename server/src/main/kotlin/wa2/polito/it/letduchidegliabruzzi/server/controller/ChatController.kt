package wa2.polito.it.letduchidegliabruzzi.server.controller

import io.micrometer.observation.annotation.Observed
import jakarta.validation.Valid
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import wa2.polito.it.letduchidegliabruzzi.server.controller.body.ChatBodyRequest
import wa2.polito.it.letduchidegliabruzzi.server.controller.body.ChatBodyResponse
import wa2.polito.it.letduchidegliabruzzi.server.controller.httpexception.*
import wa2.polito.it.letduchidegliabruzzi.server.controller.httpexception.ChatNotFoundException
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.attachment.AttachmentService
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.chat.ChatDTO
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.chat.ChatService
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.chat.toDTO
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.message.MessageService
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.ticket.TicketService
import java.security.Principal
import java.util.*

@Validated
@RestController
@Observed
@Slf4j
@RequestMapping("/API")
class ChatController(
    private val chatService: ChatService,
    private val tickerService: TicketService,
    private val messageService: MessageService,
    private val attachmentService: AttachmentService
) {

    private val log: Logger = LoggerFactory.getLogger(ChatController::class.java)

    @GetMapping("/chat/{id}")
    fun getChatInfo(@PathVariable id: Int): ChatDTO? {
        val chat = chatService.getChatInfo(id)
        if (chat == null) {
            log.error("Error getting a chat: Chat not found with Id $id")
            throw ChatNotFoundException("Chat not found with Id: $id")
        }
        return chat
    }

    @GetMapping("/chat/ticket/{id}")
    fun getChatByTicketID(@PathVariable id: Int): ChatDTO? {
        val ticket = tickerService.getTicket(id)
        if (ticket == null) {
            log.error("Error getting a Chat: Ticket not found with Id $id")
            throw TicketNotFoundException("Ticket not found with Id $id")
        }
        val chat = chatService.getChatByTicketID(id)
        if (chat == null) {
            log.error("Error getting a chat: Chat not found with Id $id")
            throw ChatNotFoundException("Chat not found with Id: $id")
        }
        return chat
    }

    @PostMapping("/chat")
    @ResponseStatus(HttpStatus.CREATED)
    fun addChat(
        @Valid @RequestBody body: ChatBodyRequest,
        br: BindingResult,
        principal: Principal
    ): ChatBodyResponse? {
        // Check if the body is valid
        if (br.hasErrors()) {
            log.error("Error adding a Chat: Body validation failed with errors ${br.allErrors}")
            throw ConstraintViolationException("Body validation failed")
        }

        val customerUsername = principal.name

        // Check if the chat exists
        val oldChat = chatService.getChatInfo(body.ticketID)
        if (oldChat != null) {
            log.error("Error adding a Chat: Chat has been created before")
            throw DuplicateChatException("Chat has been created before")
        }

        val ticket = tickerService.getTicket(body.ticketID)
        if (ticket == null) {
            log.error("Error adding a Chat: Ticket not found with Id ${body.ticketID}")
            throw TicketNotFoundException("Ticket not found with Id ${body.ticketID}")
        }

        // Check if the customer owns that ticket
        if (ticket.customer.username != customerUsername) {
            log.error("Error adding a Chat: No ticket for the customer $customerUsername")
            throw TicketNotFoundException("Ticket not found")
        }

        val chat = chatService.addChat(body.ticketID)
        ticket.chat = chat.toDTO()
        tickerService.editTicket(ticket,false);
        val message = messageService.addMessage(chat.chatID!!, customerUsername, body.message)
        body.files?.forEach {
            attachmentService.addAttachment(message.messageID!!, it)
        }
        log.info("Correctly added a new chat with id ${chat.chatID}")
        return ChatBodyResponse(chat.chatID)
    }

}