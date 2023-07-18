package wa2.polito.it.letduchidegliabruzzi.server.dal.dao.message

import org.springframework.data.jpa.repository.JpaRepository

interface MessageRepository:JpaRepository<Message, Int> {
}