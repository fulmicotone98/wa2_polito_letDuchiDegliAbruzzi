package wa2.polito.it.letduchidegliabruzzi.server.customer

import org.springframework.stereotype.Service

@Service
class CustomerServiceImpl(private val customerRepository: CustomerRepository) : CustomerService {
    override fun getProfile(email: String): CustomerDTO? {
        return customerRepository.findByEmail(email)?.toDTO()
    }

    override fun addProfile(customerDTO: CustomerDTO): Customer {
        return customerRepository.save(customerDTO.toCustomer())
    }

//    override fun updateProfile(oldCustomerDTO: CustomerDTO, newCustomerDTO: CustomerDTO): Customer {
//        val oldCustomer: Customer = oldCustomerDTO.toCustomer()
//        oldCustomer.address = if (newCustomerDTO.address != "") newCustomerDTO.address else oldCustomer.address
//        oldCustomer.email = if (newCustomerDTO.email != "") newCustomerDTO.email else oldCustomer.email
//        oldCustomer.name = if (newCustomerDTO.name != "") newCustomerDTO.name else oldCustomer.name
//        oldCustomer.surname = if (newCustomerDTO.surname != "") newCustomerDTO.surname else oldCustomer.surname
//        oldCustomer.phonenumber =
//            if (newCustomerDTO.phonenumber != "") newCustomerDTO.phonenumber else oldCustomer.phonenumber
//        return customerRepository.save(oldCustomer)
//    }

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