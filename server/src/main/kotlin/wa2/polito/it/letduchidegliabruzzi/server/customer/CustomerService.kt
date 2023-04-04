package wa2.polito.it.letduchidegliabruzzi.server.customer

interface CustomerService {
    fun getProfile(email :String) : CustomerDTO?

    fun addProfile() :CustomerDTO

    fun updateProfile(email :String) :CustomerDTO

}