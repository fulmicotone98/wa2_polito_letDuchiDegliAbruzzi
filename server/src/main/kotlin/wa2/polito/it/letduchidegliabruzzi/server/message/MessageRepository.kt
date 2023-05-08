package wa2.polito.it.letduchidegliabruzzi.server.message

import org.springframework.data.jpa.repository.JpaRepository

interface MessageRepository:JpaRepository<Message, Int> {
}