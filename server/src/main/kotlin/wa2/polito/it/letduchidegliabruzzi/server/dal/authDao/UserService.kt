package wa2.polito.it.letduchidegliabruzzi.server.dal.authDao

import wa2.polito.it.letduchidegliabruzzi.server.controller.body.UserBody

interface UserService {
    fun getUserByUsername(username: String): UserDTO?
    fun getUserByEmail(emailID: String): UserDTO?
    fun addUser(userBody: UserBody, groups: List<String>): Int
    fun updateUserByUsername(username: String, user: UserDTO)

    fun getUserInfo(): UserDTO

    fun getAllExperts(): List<UserDTO?>
}