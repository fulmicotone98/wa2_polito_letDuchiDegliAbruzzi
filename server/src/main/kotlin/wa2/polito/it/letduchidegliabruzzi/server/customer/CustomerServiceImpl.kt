package wa2.polito.it.letduchidegliabruzzi.server.customer

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Service
class CustomerServiceImpl(private val customerRepository: CustomerRepository) : CustomerService {

    @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
    override fun getProfile(email: String): CustomerDTO? {
        return customerRepository.findByEmail(email)?.toDTO()
    }

    @Transactional (isolation = Isolation.SERIALIZABLE)
    override fun addProfile(customerDTO: CustomerDTO): Customer {
        return customerRepository.save(customerDTO.toCustomer())
    }

    @Transactional (isolation = Isolation.SERIALIZABLE)
    override fun updateProfile(oldCustomerDTO: CustomerDTO, newCustomerDTO: CustomerDTO): Customer {
        val oldCustomer: Customer = oldCustomerDTO.toCustomer()
        // Update customer properties only if the corresponding newCustomerDTO property is not empty
        newCustomerDTO.address.takeIf { it.isNotBlank() }?.let { oldCustomer.address = it }
        newCustomerDTO.email.takeIf { it.isNotBlank() }?.let { oldCustomer.email = it }
        newCustomerDTO.name.takeIf { it.isNotBlank() }?.let { oldCustomer.name = it }
        newCustomerDTO.surname.takeIf { it.isNotBlank() }?.let { oldCustomer.surname = it }
        newCustomerDTO.phonenumber.takeIf { it.isNotBlank() }?.let { oldCustomer.phonenumber = it }
        return customerRepository.save(oldCustomer)
    }
}