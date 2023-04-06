package wa2.polito.it.letduchidegliabruzzi.server

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import wa2.polito.it.letduchidegliabruzzi.server.customer.CustomerNotFoundException
import wa2.polito.it.letduchidegliabruzzi.server.customer.DuplicateCustomerException
import wa2.polito.it.letduchidegliabruzzi.server.product.ProductNotFoundException


@RestControllerAdvice
class ProblemDetailsHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(CustomerNotFoundException::class)
    fun handleProductNotFound(e: CustomerNotFoundException): ResponseEntity<ProblemDetail> {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail)
    }

    @ExceptionHandler(ProductNotFoundException::class)
    fun handleCustomerNotFound(e: ProductNotFoundException): ResponseEntity<ProblemDetail> {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail)
    }

    @ExceptionHandler(DuplicateCustomerException::class)
    fun handleDuplicateCustomer(e: DuplicateCustomerException): ResponseEntity<ProblemDetail> {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.message!!)
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail)
    }
}
