package nl.sourcelabs.sourcechat.exception

/**
 * Base exception for service layer errors
 */
abstract class ServiceException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

/**
 * Exception thrown when chat service operations fail
 */
class ChatServiceException(message: String, cause: Throwable? = null) : ServiceException(message, cause)

/**
 * Exception thrown when hour registration service operations fail
 */
class HourRegistrationException(message: String, cause: Throwable? = null) : ServiceException(message, cause)

/**
 * Exception thrown when document service operations fail
 */
class DocumentServiceException(message: String, cause: Throwable? = null) : ServiceException(message, cause)

/**
 * Exception thrown when MCP tool operations fail
 */
class McpToolException(message: String, cause: Throwable? = null) : ServiceException(message, cause)

/**
 * Exception thrown when business validation fails
 */
class BusinessValidationException(message: String, cause: Throwable? = null) : ServiceException(message, cause)

/**
 * Exception thrown when external service calls fail
 */
class ExternalServiceException(message: String, cause: Throwable? = null) : ServiceException(message, cause)