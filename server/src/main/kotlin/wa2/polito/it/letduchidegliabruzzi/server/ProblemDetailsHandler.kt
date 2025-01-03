package wa2.polito.it.letduchidegliabruzzi.server

import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import wa2.polito.it.letduchidegliabruzzi.server.controller.httpexception.*


@RestControllerAdvice
class ProblemDetailsHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(CustomerNotFoundException::class)
    fun handleCustomerNotFound(e: CustomerNotFoundException): ResponseEntity<ProblemDetail> {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail)
    }
    @ExceptionHandler(EmployeeNotFoundException::class)
    fun handleEmployeeNotFound(e: EmployeeNotFoundException): ResponseEntity<ProblemDetail> {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail)
    }

    @ExceptionHandler(EmployeeRoleException::class)
    fun handleWrongEmployeeRole(e: EmployeeRoleException): ResponseEntity<ProblemDetail> {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message!!)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail)
    }

    @ExceptionHandler(DuplicateEmployeeException::class)
    fun handleDuplicateEmployee(e: DuplicateEmployeeException): ResponseEntity<ProblemDetail> {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.message!!)
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail)
    }

    @ExceptionHandler(ProductNotFoundException::class)
    fun handleProductNotFound(e: ProductNotFoundException): ResponseEntity<ProblemDetail> {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail)
    }

    @ExceptionHandler(DuplicateProductException::class)
    fun handleProductNotFound(e: DuplicateProductException): ResponseEntity<ProblemDetail> {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message!!)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail)
    }

    @ExceptionHandler(DuplicateCustomerException::class)
    fun handleDuplicateCustomer(e: DuplicateCustomerException): ResponseEntity<ProblemDetail> {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.message!!)
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleBodyValidation(e: ConstraintViolationException): ResponseEntity<ProblemDetail>{
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message!!)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail)
    }
    @ExceptionHandler(wa2.polito.it.letduchidegliabruzzi.server.controller.httpexception.ConstraintViolationException::class)
    fun handleBodyValidation(e: wa2.polito.it.letduchidegliabruzzi.server.controller.httpexception.ConstraintViolationException): ResponseEntity<ProblemDetail>{
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message!!)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail)
    }
    @ExceptionHandler(TicketNotFoundException::class)
    fun handleTicketNotFound(e: TicketNotFoundException): ResponseEntity<ProblemDetail> {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail)
    }

    @ExceptionHandler(TicketDuplicatedException::class)
    fun handleDuplicateTicket(e: TicketDuplicatedException): ResponseEntity<ProblemDetail> {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.message!!)
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail)
    }
}
