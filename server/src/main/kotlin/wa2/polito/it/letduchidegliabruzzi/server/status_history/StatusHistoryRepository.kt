package wa2.polito.it.letduchidegliabruzzi.server.status_history

import org.springframework.data.jpa.repository.JpaRepository

interface StatusHistoryRepository: JpaRepository<StatusHistory, Int> {
}