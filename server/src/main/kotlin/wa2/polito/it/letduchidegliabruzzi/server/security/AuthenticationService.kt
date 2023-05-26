package wa2.polito.it.letduchidegliabruzzi.server.security

interface AuthenticationService {
    fun authenticate(credentials: Credentials) : String?
}