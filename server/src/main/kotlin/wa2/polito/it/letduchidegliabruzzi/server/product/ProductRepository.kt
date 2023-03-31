package wa2.polito.it.letduchidegliabruzzi.server.product

import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository:JpaRepository<Product, String> {
}