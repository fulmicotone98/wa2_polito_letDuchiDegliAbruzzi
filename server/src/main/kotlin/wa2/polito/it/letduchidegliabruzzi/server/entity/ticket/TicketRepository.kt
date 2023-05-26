package wa2.polito.it.letduchidegliabruzzi.server.entity.ticket

import org.springframework.data.jpa.repository.JpaRepository

interface TicketRepository:JpaRepository<Ticket, Int> {
}