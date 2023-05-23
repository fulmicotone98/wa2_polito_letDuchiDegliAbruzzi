package wa2.polito.it.letduchidegliabruzzi.server.entity.chat

import org.springframework.data.jpa.repository.JpaRepository

interface ChatRepository: JpaRepository<Chat, Int> {
}