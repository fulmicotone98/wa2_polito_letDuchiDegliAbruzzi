package wa2.polito.it.letduchidegliabruzzi.server.customer

interface CustomerService {
    fun getProfile(email :String) : CustomerDTO?

    fun addProfile(customerDTO: CustomerDTO) :Customer

    fun updateProfile(oldCustomerDTO: CustomerDTO ,newCustomerDTO: CustomerDTO) :Customer

}