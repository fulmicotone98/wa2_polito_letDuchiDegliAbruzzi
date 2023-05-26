package wa2.polito.it.letduchidegliabruzzi.server.controller.httpexception

/* --- CUSTOMER EXCEPTIONS --- */
class CustomerNotFoundException(message: String) : RuntimeException(message)
class DuplicateCustomerException(message: String) : RuntimeException(message)

/* --- EMPLOYEE EXCEPTIONS --- */
class EmployeeNotFoundException(message: String): RuntimeException(message)
class EmployeeRoleException(message: String): RuntimeException(message)

/* --- PRODUCT EXCEPTIONS --- */
class ProductNotFoundException(message: String) : RuntimeException(message)

/* --- TICKET EXCEPTIONS ---*/
class TicketNotFoundException(message: String) : RuntimeException(message)
class TicketDuplicatedException(message: String) : RuntimeException(message)

/* --- GENERAL EXCEPTIONS ---*/
class ConstraintViolationException(message: String): RuntimeException(message)



