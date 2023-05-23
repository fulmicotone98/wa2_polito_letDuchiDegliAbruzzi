package wa2.polito.it.letduchidegliabruzzi.server.entity.customer

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name="customer")
class Customer(
    @Id
    var email: String,
    var name: String,
    var surname: String,
    var phonenumber: String,
    var address: String
)