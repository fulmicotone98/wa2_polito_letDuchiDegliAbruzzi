package wa2.polito.it.letduchidegliabruzzi.server.chat

import org.springframework.data.jpa.repository.JpaRepository

interface ChatRepository: JpaRepository<Chat, Int> {
}