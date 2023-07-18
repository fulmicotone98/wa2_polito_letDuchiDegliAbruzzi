package wa2.polito.it.letduchidegliabruzzi.server.dal.dao.product

import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository:JpaRepository<Product, String> {
}