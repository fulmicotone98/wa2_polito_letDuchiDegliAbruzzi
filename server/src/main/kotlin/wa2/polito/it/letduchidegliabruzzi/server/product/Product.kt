package wa2.polito.it.letduchidegliabruzzi.server.product

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name="products")
class Product {
    @Id
    var ean = ""
    var name = ""
    var brand = ""
}