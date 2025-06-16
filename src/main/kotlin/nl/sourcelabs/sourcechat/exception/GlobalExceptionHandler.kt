package nl.sourcelabs.sourcechat.exception

import jakarta.validation.ConstraintViolationException
import org.apache.logging.log4j.LogManager
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import java.time.Instant

@ControllerAdvice
class GlobalExceptionHandler {
    
    companion object {
        private val logger = LogManager.getLogger(GlobalExceptionHandler::class.java)
    }
    
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        ex: MethodArgumentNotValidException,
        request: WebRequest
    ): ResponseEntity<ProblemDetail> {
        logger.warn("Validation failed: {}", ex.bindingResult.allErrors)
        
        val errors = ex.bindingResult.fieldErrors.associate { 
            it.field to (it.defaultMessage ?: "Invalid value")
        }
        
        return createProblemDetail(
            HttpStatus.BAD_REQUEST,
            "Validation failed",
            "Please check the provided values",
            request,
            mapOf("validation_errors" to errors)
        )
    }
    
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(
        ex: ConstraintViolationException,
        request: WebRequest
    ): ResponseEntity<ProblemDetail> {
        logger.warn("Constraint violation: {}", ex.constraintViolations)
        
        val errors = ex.constraintViolations.associate {
            it.propertyPath.toString() to it.message
        }
        
        return createProblemDetail(
            HttpStatus.BAD_REQUEST,
            "Validation failed",
            "Please check the provided values",
            request,
            mapOf("validation_errors" to errors)
        )
    }
    
    @ExceptionHandler(ChatServiceException::class)
    fun handleChatServiceException(
        ex: ChatServiceException,
        request: WebRequest
    ): ResponseEntity<ProblemDetail> {
        logger.error("Chat service error: {}", ex.message, ex)
        
        return createProblemDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Chat Service Error",
            "Unable to process chat request. Please try again.",
            request
        )
    }
    
    @ExceptionHandler(HourRegistrationException::class)
    fun handleHourRegistrationException(
        ex: HourRegistrationException,
        request: WebRequest
    ): ResponseEntity<ProblemDetail> {
        logger.error("Hour registration error: {}", ex.message, ex)
        
        return createProblemDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Hour Registration Error",
            "Unable to process hour registration. Please try again.",
            request
        )
    }
    
    @ExceptionHandler(BusinessValidationException::class)
    fun handleBusinessValidationException(
        ex: BusinessValidationException,
        request: WebRequest
    ): ResponseEntity<ProblemDetail> {
        logger.warn("Business validation failed: {}", ex.message)
        
        return createProblemDetail(
            HttpStatus.BAD_REQUEST,
            "Business Validation Error",
            ex.message ?: "Business validation failed",
            request
        )
    }
    
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(
        ex: IllegalArgumentException,
        request: WebRequest
    ): ResponseEntity<ProblemDetail> {
        logger.warn("Bad request - illegal argument: {}", ex.message)
        
        return createProblemDetail(
            HttpStatus.BAD_REQUEST,
            "Bad Request",
            ex.message ?: "Invalid request parameters",
            request
        )
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
        logger.error("Unexpected exception occurred: {}", ex.message, ex)
        
        return createProblemDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Internal Server Error",
            "An unexpected error occurred. Please contact support if this persists.",
            request
        )
    }
    
    private fun createProblemDetail(
        status: HttpStatus,
        title: String,
        detail: String,
        request: WebRequest,
        properties: Map<String, Any> = emptyMap()
    ): ResponseEntity<ProblemDetail> {
        val problemDetail = ProblemDetail.forStatusAndDetail(status, detail).apply {
            this.title = title
            setProperty("timestamp", Instant.now())
            setProperty("path", request.getDescription(false).removePrefix("uri="))
            properties.forEach { (key, value) -> setProperty(key, value) }
        }
        
        return ResponseEntity.status(status).body(problemDetail)
    }
}