package wa2.polito.it.letduchidegliabruzzi.server.controller

import io.micrometer.observation.annotation.Observed
import jakarta.validation.Valid
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import wa2.polito.it.letduchidegliabruzzi.server.controller.body.ChatBodyRequest
import wa2.polito.it.letduchidegliabruzzi.server.controller.body.ChatBodyResponse
import wa2.polito.it.letduchidegliabruzzi.server.controller.body.MessageBodyRequest
import wa2.polito.it.letduchidegliabruzzi.server.controller.body.MessageBodyResponse
import wa2.polito.it.letduchidegliabruzzi.server.controller.httpexception.*
import wa2.polito.it.letduchidegliabruzzi.server.dal.authDao.UserDTO
import wa2.polito.it.letduchidegliabruzzi.server.dal.authDao.UserService
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.attachment.Attachment
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.attachment.AttachmentDTO
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.attachment.AttachmentService
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.attachment.toDTO
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.chat.ChatService
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.message.MessageService
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.ticket.TicketService
import java.security.Principal
import java.util.Base64

@Validated
@RestController
@Observed
@Slf4j
class MessageController(
    private val messageService: MessageService,
    private val chatService: ChatService,
    private val userService: UserService,
    private val attachmentService: AttachmentService,
    private val ticketService: TicketService
) {
    private val log: Logger = LoggerFactory.getLogger(MessageController::class.java)

    @GetMapping("/API/message/{id}")
    fun getMessage(@PathVariable id: Int): MessageBodyResponse? {
        val message = messageService.getMessage(id)
        if (message == null) {
            log.error("Error getting a message: Message not found with Id $id")
            throw MessageNotFoundException("Message not found with Id: $id")
        }
        val userInfo = userService.getUserByUsername(message.senderUsername)
        if (userInfo == null) {
            log.error("Error getting a message: User not found with username ${message.senderUsername}")
            throw UsernameNotFoundException("User not found with username ${message.senderUsername}")
        }
        return MessageBodyResponse(
            message.messageID,
            message.chat.chatID,
            message.text,
            message.createdAt,
            message.senderUsername,
            userInfo.name,
            userInfo.surname,
            message.attachments
        )
    }

    @GetMapping("/API/message/chat/{id}")
    fun getChatByTicketID(@PathVariable id: Int): List<MessageBodyResponse> {
        val chat = chatService.getChatInfo(id)
        if (chat == null) {
            log.error("Error getting a chat: Chat not found with Id $id")
            throw ChatNotFoundException("Chat not found with Id: $id")
        }
        val messages = messageService.getMessagesByChatID(id)

        val users = mutableMapOf<String, UserDTO>()

        val listMessages: List<MessageBodyResponse> = messages.map {
            if (users[it.senderUsername] == null) {
                val user = userService.getUserByUsername(it.senderUsername)
                if (user != null) {
                    users[it.senderUsername] = user
                }
            }
            MessageBodyResponse(
                it.messageID,
                it.chat.chatID,
                it.text,
                it.createdAt,
                it.senderUsername,
                users[it.senderUsername]!!.name,
                users[it.senderUsername]!!.surname,
                it.attachments
            )
        }
        return listMessages
    }

    @PostMapping("/API/message")
    @ResponseStatus(HttpStatus.CREATED)
    fun addMessage(
        @Valid @RequestBody body: MessageBodyRequest,
        br: BindingResult,
        principal: Principal
    ): MessageBodyResponse? {
        // Check if the body is valid
        if (br.hasErrors()) {
            log.error("Error adding a Message: Body validation failed with errors ${br.allErrors}")
            throw ConstraintViolationException("Body validation failed")
        }

        val username = principal.name

        val chat = chatService.getChatInfo(body.chatID)
        if (chat == null) {
            log.error("Error adding a Message: Chat not found with Id ${body.chatID}")
            throw ChatNotFoundException("Chat not found with Id ${body.chatID}")
        }

        val ticket = ticketService.getTicket(chat.ticketID!!)
        if (ticket == null) {
            log.error("Error adding a Message: Ticket not found with Id ${chat.ticketID}")
            throw TicketNotFoundException("Chat not found with Id ${chat.ticketID}")
        }

        // Check if the user is authorized to send message
        if (ticket.customer.username != username && ticket.employee?.username != username) {
            log.error("Error adding a Message: User is not authorized with username $username")
            throw MessageUserNotAuthorizedException("User is not authorized")
        }

        val user = userService.getUserByUsername(username)
        if (user == null) {
            log.error("Error adding a message: User not found with username $username")
            throw UsernameNotFoundException("User not found with username $username")
        }

        val message = messageService.addMessage(body.chatID, username, body.text)
        val messageAttachments :MutableList<AttachmentDTO> = mutableListOf()
        if(body.attachments != null){
            body.attachments.forEach{
                val fileBase64 = Base64.getEncoder().encodeToString(it.bytes)
                val newAttachment = message.messageID?.let { it1 -> attachmentService.addAttachment(it1,fileBase64) }
                if(newAttachment != null){
                    messageAttachments.add(newAttachment.toDTO())
                }
            }
        }
        log.info("Correctly added a new message with id ${message.messageID}")
        return MessageBodyResponse(
            message.messageID,
            message.chat.chatID,
            message.text,
            message.createdAt,
            message.senderUsername,
            user.name,
            user.surname,
            messageAttachments
        )
    }
}