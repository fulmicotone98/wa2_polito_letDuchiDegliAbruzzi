package wa2.polito.it.letduchidegliabruzzi.server.dal.dao.product

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name="product")
class Product(
    @Id
    val ean: String,
    val name:String,
    val brand: String,
    val customerUsername: String
)