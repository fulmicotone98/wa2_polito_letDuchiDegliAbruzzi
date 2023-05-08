package wa2.polito.it.letduchidegliabruzzi.server.chat

import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
class ChatController(private val chatService: ChatService) {

    @GetMapping("/API/chat/{id}")
    fun getChatInfo(@PathVariable id: Int): ChatDTO? {
        return chatService.getChatInfo(id)
    }

}