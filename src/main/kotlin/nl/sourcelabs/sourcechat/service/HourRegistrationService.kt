package nl.sourcelabs.sourcechat.service

import nl.sourcelabs.sourcechat.dto.HourRegistrationResponse
import nl.sourcelabs.sourcechat.dto.RegisterBillableHoursRequest
import nl.sourcelabs.sourcechat.dto.RegisterLeaveHoursRequest
import nl.sourcelabs.sourcechat.entity.BillableClientHours
import nl.sourcelabs.sourcechat.entity.BillableStatus
import nl.sourcelabs.sourcechat.entity.LeaveHours
import nl.sourcelabs.sourcechat.entity.LeaveStatus
import nl.sourcelabs.sourcechat.exception.HourRegistrationException
import nl.sourcelabs.sourcechat.repository.BillableClientHoursRepository
import nl.sourcelabs.sourcechat.repository.LeaveHoursRepository
import org.apache.logging.log4j.LogManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
@Transactional
class HourRegistrationService(
    private val leaveHoursRepository: LeaveHoursRepository,
    private val billableClientHoursRepository: BillableClientHoursRepository
) {
    
    companion object {
        private val logger = LogManager.getLogger(HourRegistrationService::class.java)
    }
    
    fun registerLeaveHours(request: RegisterLeaveHoursRequest): HourRegistrationResponse {
        logger.info("Registering leave hours - employeeId: {}, leaveType: {}, hours: {}", 
            request.employeeId, request.leaveType, request.totalHours)
        
        validateLeaveHoursRequest(request)
        
        return try {
            val leaveHours = request.toEntity()
            val saved = leaveHoursRepository.save(leaveHours)
            
            saved.id?.let { id ->
                HourRegistrationResponse(
                    id = id,
                    message = "Leave hours registered successfully. Request ID: $id",
                    status = LeaveStatus.PENDING.name
                ).also {
                    logger.info("Leave hours registered successfully - id: {}, employeeId: {}", id, request.employeeId)
                }
            } ?: throw IllegalStateException("Failed to save leave hours - no ID generated")
        } catch (e: Exception) {
            logger.error("Failed to register leave hours - employeeId: {}", request.employeeId, e)
            throw HourRegistrationException("Failed to register leave hours", e)
        }
    }
    
    private fun validateLeaveHoursRequest(request: RegisterLeaveHoursRequest) {
        require(request.endDate >= request.startDate) { 
            "End date must be after or equal to start date" 
        }
        require(request.totalHours > 0) { 
            "Total hours must be positive" 
        }
        
        val daysBetween = ChronoUnit.DAYS.between(request.startDate, request.endDate) + 1
        val maxHours = daysBetween * 8.0
        require(request.totalHours <= maxHours) {
            "Total hours (${request.totalHours}) exceed maximum for the date range ($maxHours hours for $daysBetween days)"
        }
    }
    
    // Extension function for cleaner conversion
    private fun RegisterLeaveHoursRequest.toEntity(): LeaveHours = LeaveHours(
        employeeId = employeeId,
        leaveType = leaveType,
        startDate = startDate,
        endDate = endDate,
        totalHours = totalHours,
        description = description,
        status = LeaveStatus.PENDING,
        requestedAt = LocalDateTime.now()
    )
    
    fun registerBillableHours(request: RegisterBillableHoursRequest): HourRegistrationResponse {
        logger.info("Registering billable hours - employeeId: {}, client: {}, hours: {}", 
            request.employeeId, request.clientName, request.hoursWorked)
        
        validateBillableHoursRequest(request)
        
        return try {
            val billableHours = request.toEntity()
            val saved = billableClientHoursRepository.save(billableHours)
            
            saved.id?.let { id ->
                HourRegistrationResponse(
                    id = id,
                    message = "Billable hours registered successfully for client ${request.clientName}. Entry ID: $id",
                    status = BillableStatus.PENDING.name
                ).also {
                    logger.info("Billable hours registered successfully - id: {}, employeeId: {}, client: {}", 
                        id, request.employeeId, request.clientName)
                }
            } ?: throw IllegalStateException("Failed to save billable hours - no ID generated")
        } catch (e: Exception) {
            logger.error("Failed to register billable hours - employeeId: {}, client: {}", 
                request.employeeId, request.clientName, e)
            throw HourRegistrationException("Failed to register billable hours", e)
        }
    }
    
    private fun validateBillableHoursRequest(request: RegisterBillableHoursRequest) {
        require(request.hoursWorked > 0) { 
            "Hours worked must be positive" 
        }
        require(request.hoursWorked <= 24.0) {
            "Hours worked cannot exceed 24 hours per day"
        }
        
        // Validate travel information consistency
        if (request.travelKilometers != null && request.travelKilometers > 0) {
            require(request.travelType != null) {
                "Travel type is required when travel kilometers are specified"
            }
        }
    }
    
    // Extension function for cleaner conversion
    private fun RegisterBillableHoursRequest.toEntity(): BillableClientHours = BillableClientHours(
        employeeId = employeeId,
        clientName = clientName,
        projectName = projectName,
        location = location,
        workDate = workDate,
        hoursWorked = hoursWorked,
        description = description,
        travelType = travelType,
        travelKilometers = travelKilometers,
        travelFromLocation = travelFromLocation,
        travelToLocation = travelToLocation,
        hourlyRate = hourlyRate,
        status = BillableStatus.PENDING,
        createdAt = LocalDateTime.now()
    )
    
    @Transactional(readOnly = true)
    fun getLeaveHoursByEmployee(employeeId: String): List<LeaveHours> {
        logger.debug("Retrieving leave hours for employee: {}", employeeId)
        return try {
            leaveHoursRepository.findByEmployeeIdOrderByStartDateDesc(employeeId)
        } catch (e: Exception) {
            logger.error("Failed to retrieve leave hours for employee: {}", employeeId, e)
            throw HourRegistrationException("Failed to retrieve leave hours", e)
        }
    }
    
    @Transactional(readOnly = true)
    fun getBillableHoursByEmployee(employeeId: String): List<BillableClientHours> {
        logger.debug("Retrieving billable hours for employee: {}", employeeId)
        return try {
            billableClientHoursRepository.findByEmployeeIdOrderByWorkDateDesc(employeeId)
        } catch (e: Exception) {
            logger.error("Failed to retrieve billable hours for employee: {}", employeeId, e)
            throw HourRegistrationException("Failed to retrieve billable hours", e)
        }
    }
    
    @Transactional(readOnly = true)
    fun getTotalLeaveHoursForYear(employeeId: String, year: Int): Double {
        logger.debug("Calculating total leave hours for employee: {}, year: {}", employeeId, year)
        return try {
            leaveHoursRepository.getTotalApprovedLeaveHoursForYear(employeeId, year) ?: 0.0
        } catch (e: Exception) {
            logger.error("Failed to calculate total leave hours for employee: {}, year: {}", employeeId, year, e)
            throw HourRegistrationException("Failed to calculate total leave hours", e)
        }
    }
    
    @Transactional(readOnly = true)
    fun getTotalBillableHoursForYear(employeeId: String, year: Int): Double {
        logger.debug("Calculating total billable hours for employee: {}, year: {}", employeeId, year)
        return try {
            billableClientHoursRepository.getTotalBillableHoursForYear(employeeId, year) ?: 0.0
        } catch (e: Exception) {
            logger.error("Failed to calculate total billable hours for employee: {}, year: {}", employeeId, year, e)
            throw HourRegistrationException("Failed to calculate total billable hours", e)
        }
    }
}