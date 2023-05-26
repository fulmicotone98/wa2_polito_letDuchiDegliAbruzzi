package wa2.polito.it.letduchidegliabruzzi.server.controller

import jakarta.validation.Valid
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
class MessageController {

//    @PostMapping("API/chat/{id}/send")
//    fun pushMessage(@PathVariable id: Int,
//                    @Valid @RequestParam senderID: Int,
//                    @Valid @RequestParam text: String) {
//        TODO()
//    }
}