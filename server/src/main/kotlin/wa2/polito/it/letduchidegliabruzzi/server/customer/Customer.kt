package wa2.polito.it.letduchidegliabruzzi.server.customer

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name="customers")
class Customer {
    @Id
    var email = ""
    var name = ""
    var surname = ""
    var phoneNumber = ""
    var address = ""
}