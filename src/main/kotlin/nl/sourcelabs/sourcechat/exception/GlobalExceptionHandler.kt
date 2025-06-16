package nl.sourcelabs.sourcechat.exception

import org.apache.logging.log4j.LogManager
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import java.time.Instant

@ControllerAdvice
class GlobalExceptionHandler {
    
    private val logger = LogManager.getLogger()
    
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(
        ex: IllegalArgumentException,
        request: WebRequest
    ): ResponseEntity<ProblemDetail> {
        logger.warn("Bad request - illegal argument: {}, path: {}", ex.message, request.getDescription(false))
        
        val problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            ex.message ?: "Invalid request parameters"
        )
        problemDetail.title = "Bad Request"
        problemDetail.setProperty("timestamp", Instant.now())
        problemDetail.setProperty("path", request.getDescription(false).removePrefix("uri="))
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail)
    }
    
    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElementException(
        ex: NoSuchElementException,
        request: WebRequest
    ): ResponseEntity<ProblemDetail> {
        logger.warn("Resource not found: {}, path: {}", ex.message, request.getDescription(false))
        
        val problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            ex.message ?: "Resource not found"
        )
        problemDetail.title = "Not Found"
        problemDetail.setProperty("timestamp", Instant.now())
        problemDetail.setProperty("path", request.getDescription(false).removePrefix("uri="))
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail)
    }
    
    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(
        ex: RuntimeException,
        request: WebRequest
    ): ResponseEntity<ProblemDetail> {
        logger.error("Runtime exception occurred: {}, path: {}", ex.message, request.getDescription(false), ex)
        
        val problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred. Please try again later."
        )
        problemDetail.title = "Internal Server Error"
        problemDetail.setProperty("timestamp", Instant.now())
        problemDetail.setProperty("path", request.getDescription(false).removePrefix("uri="))
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail)
    }
    
    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        ex: Exception,
        request: WebRequest
    ): ResponseEntity<ProblemDetail> {
        logger.error("Unexpected exception occurred: {}, path: {}", ex.message, request.getDescription(false), ex)
        
        val problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred. Please contact support if this persists."
        )
        problemDetail.title = "Internal Server Error"
        problemDetail.setProperty("timestamp", Instant.now())
        problemDetail.setProperty("path", request.getDescription(false).removePrefix("uri="))
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail)
    }
}