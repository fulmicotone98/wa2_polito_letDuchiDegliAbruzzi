package wa2.polito.it.letduchidegliabruzzi.server.dal.dao.ticket

import org.springframework.data.jpa.repository.JpaRepository

interface TicketRepository:JpaRepository<Ticket, Int> {
}