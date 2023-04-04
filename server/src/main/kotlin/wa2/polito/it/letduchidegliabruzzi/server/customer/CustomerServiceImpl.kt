package wa2.polito.it.letduchidegliabruzzi.server.customer

import org.springframework.stereotype.Service

@Service
class CustomerServiceImpl(private val customerRepository: CustomerRepository): CustomerService {
    override fun getProfile(email: String): CustomerDTO? {
        return customerRepository.findByEmail(email)?.toDTO()
    }

    override fun addProfile(): CustomerDTO {
        TODO("Not yet implemented")
    }

    override fun updateProfile(email: String): CustomerDTO {
        TODO("Not yet implemented")
    }

}