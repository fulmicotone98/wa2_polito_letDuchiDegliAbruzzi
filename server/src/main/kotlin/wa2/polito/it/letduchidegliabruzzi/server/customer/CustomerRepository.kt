package wa2.polito.it.letduchidegliabruzzi.server.customer

import org.springframework.data.jpa.repository.JpaRepository

interface CustomerRepository: JpaRepository<Customer,Int> {
    fun findByEmail(email :String): Customer?
}