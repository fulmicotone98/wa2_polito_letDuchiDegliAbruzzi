package wa2.polito.it.letduchidegliabruzzi.server.dal.dao.product

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import wa2.polito.it.letduchidegliabruzzi.server.controller.httpexception.ProductNotFoundException
import wa2.polito.it.letduchidegliabruzzi.server.dal.authDao.UserServiceImpl

@Service
class ProductServiceImpl(
    private val productRepository: ProductRepository,
    private val userService: UserServiceImpl
) : ProductService {
    @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
    override fun getAll(): List<ProductDTO> {
        return productRepository.findAll().map {
            val customer = userService.getUserByUsername(it.customerUsername)
            it.toDTO(customer!!)
        }
    }

    @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
    override fun getAllByUser(customerUsername: String): List<ProductDTO> {
        return productRepository.findAll().filter { it.customerUsername == customerUsername }.map {
            val customer = userService.getUserByUsername(it.customerUsername)
            it.toDTO(customer!!)
        }
    }

    @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
    override fun getProduct(ean: String): ProductDTO? {
        val product = productRepository.findByIdOrNull(ean) ?: return null
        val customer = userService.getUserByUsername(product.customerUsername)
        return product.toDTO(customer!!)
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    override fun addProduct(ean: String, brand: String, name: String, customerUsername: String): Product {
        val customer = userService.getUserByUsername(customerUsername)
        val newProductDTO = ProductDTO(ean, name, brand, customer!!)
        return productRepository.save(newProductDTO.toProduct())
    }
}