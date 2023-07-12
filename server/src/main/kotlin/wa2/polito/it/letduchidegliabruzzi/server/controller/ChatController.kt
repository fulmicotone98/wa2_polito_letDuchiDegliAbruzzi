package wa2.polito.it.letduchidegliabruzzi.server.controller

import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RestController
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.chat.ChatService

@Validated
@RestController
class ChatController(private val chatService: ChatService) {

//    @GetMapping("/API/chat/{id}")
//    fun getChatInfo(@PathVariable id: Int): ChatDTO? {
//        return chatService.getChatInfo(id)
//    }

}