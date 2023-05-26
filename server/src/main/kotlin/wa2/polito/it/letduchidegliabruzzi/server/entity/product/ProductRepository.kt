package wa2.polito.it.letduchidegliabruzzi.server.entity.product

import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository:JpaRepository<Product, String> {
}