package wa2.polito.it.letduchidegliabruzzi.server.entity.product

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import wa2.polito.it.letduchidegliabruzzi.server.entity.customer.Customer

@Entity
@Table(name="product")
class Product(
    @Id
    var ean: String,
    var name:String,
    var brand: String,
    @ManyToOne
    @JoinColumn(name = "customer_email")
    var customer: Customer
)