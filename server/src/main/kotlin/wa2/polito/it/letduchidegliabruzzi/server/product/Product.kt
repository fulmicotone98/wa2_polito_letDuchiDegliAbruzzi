package wa2.polito.it.letduchidegliabruzzi.server.product

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import wa2.polito.it.letduchidegliabruzzi.server.customer.Customer

@Entity
@Table(name="product")
class Product {
    @Id
    var ean = ""
    var name = ""
    var brand = ""
    @ManyToOne
    @JoinColumn(name = "customerEmail")
    var customer: Customer? = null
}