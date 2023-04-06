package wa2.polito.it.letduchidegliabruzzi.server.customer

import org.springframework.stereotype.Service

@Service
class CustomerServiceImpl(private val customerRepository: CustomerRepository): CustomerService {
    override fun getProfile(email: String): CustomerDTO? {
        return customerRepository.findByEmail(email)?.toDTO()
    }

    override fun addProfile(customerDTO: CustomerDTO): Customer {
        return customerRepository.save(customerDTO.toCustomer())
    }

    override fun updateProfile(oldCustomerDTO: CustomerDTO ,newCustomerDTO: CustomerDTO): Customer{
        val oldCustomer :Customer = oldCustomerDTO.toCustomer()
        oldCustomer.address = newCustomerDTO.address
        oldCustomer.email = newCustomerDTO.email
        oldCustomer.name = newCustomerDTO.name
        oldCustomer.surname = newCustomerDTO.surname
        oldCustomer.phonenumber = newCustomerDTO.phonenumber
        return customerRepository.save(oldCustomer)
    }

}