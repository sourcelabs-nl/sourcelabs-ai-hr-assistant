package nl.sourcelabs.sourcechat.controller

import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Pattern
import nl.sourcelabs.sourcechat.dto.HourRegistrationResponse
import nl.sourcelabs.sourcechat.dto.RegisterBillableHoursRequest
import nl.sourcelabs.sourcechat.dto.RegisterLeaveHoursRequest
import nl.sourcelabs.sourcechat.entity.BillableClientHours
import nl.sourcelabs.sourcechat.entity.LeaveHours
import nl.sourcelabs.sourcechat.service.HourRegistrationService
import org.apache.logging.log4j.LogManager
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
@RestController
@RequestMapping("/api/hours")
@CrossOrigin(origins = ["\${app.cors.allowed-origins:http://localhost:3000}"])
@Validated
class HourRegistrationController(
    private val hourRegistrationService: HourRegistrationService
) {
    
    companion object {
        private val logger = LogManager.getLogger(HourRegistrationController::class.java)
    }
    
    @PostMapping("/leave")
    fun registerLeaveHours(@Valid @RequestBody request: RegisterLeaveHoursRequest): ResponseEntity<HourRegistrationResponse> {
        logger.info("Register leave hours request - employeeId: {}, leaveType: {}, hours: {}", 
            request.employeeId, request.leaveType, request.totalHours)
        
        val response = hourRegistrationService.registerLeaveHours(request)
        logger.info("Leave hours registered successfully - employeeId: {}, id: {}", 
            request.employeeId, response.id)
        return ResponseEntity.ok(response)
    }
    
    @PostMapping("/billable")
    fun registerBillableHours(@Valid @RequestBody request: RegisterBillableHoursRequest): ResponseEntity<HourRegistrationResponse> {
        logger.info("Register billable hours request - employeeId: {}, client: {}, hours: {}", 
            request.employeeId, request.clientName, request.hoursWorked)
        
        val response = hourRegistrationService.registerBillableHours(request)
        logger.info("Billable hours registered successfully - employeeId: {}, client: {}, id: {}", 
            request.employeeId, request.clientName, response.id)
        return ResponseEntity.ok(response)
    }
    
    @GetMapping("/leave/{employeeId}")
    fun getLeaveHours(
        @PathVariable @Pattern(regexp = "^[a-zA-Z0-9-_]+$", message = "Invalid employee ID format") 
        employeeId: String
    ): ResponseEntity<List<LeaveHours>> {
        logger.info("Get leave hours request - employeeId: {}", employeeId)
        
        val leaveHours = hourRegistrationService.getLeaveHoursByEmployee(employeeId)
        logger.info("Retrieved leave hours - employeeId: {}, count: {}", employeeId, leaveHours.size)
        return ResponseEntity.ok(leaveHours)
    }
    
    @GetMapping("/billable/{employeeId}")
    fun getBillableHours(
        @PathVariable @Pattern(regexp = "^[a-zA-Z0-9-_]+$", message = "Invalid employee ID format") 
        employeeId: String
    ): ResponseEntity<List<BillableClientHours>> {
        logger.info("Get billable hours request - employeeId: {}", employeeId)
        
        val billableHours = hourRegistrationService.getBillableHoursByEmployee(employeeId)
        logger.info("Retrieved billable hours - employeeId: {}, count: {}", employeeId, billableHours.size)
        return ResponseEntity.ok(billableHours)
    }
    
    @GetMapping("/leave/{employeeId}/total/{year}")
    fun getTotalLeaveHours(
        @PathVariable @Pattern(regexp = "^[a-zA-Z0-9-_]+$", message = "Invalid employee ID format") 
        employeeId: String,
        @PathVariable @Min(2020, message = "Year must be 2020 or later") 
        @Max(2030, message = "Year must be 2030 or earlier") 
        year: Int
    ): ResponseEntity<Map<String, Any>> {
        logger.info("Get total leave hours request - employeeId: {}, year: {}", employeeId, year)
        
        val totalHours = hourRegistrationService.getTotalLeaveHoursForYear(employeeId, year)
        logger.info("Total leave hours calculated - employeeId: {}, year: {}, total: {}", 
            employeeId, year, totalHours)
        return ResponseEntity.ok(mapOf(
            "employeeId" to employeeId,
            "year" to year,
            "totalLeaveHours" to totalHours
        ))
    }
    
    @GetMapping("/billable/{employeeId}/total/{year}")
    fun getTotalBillableHours(
        @PathVariable @Pattern(regexp = "^[a-zA-Z0-9-_]+$", message = "Invalid employee ID format") 
        employeeId: String,
        @PathVariable @Min(2020, message = "Year must be 2020 or later") 
        @Max(2030, message = "Year must be 2030 or earlier") 
        year: Int
    ): ResponseEntity<Map<String, Any>> {
        logger.info("Get total billable hours request - employeeId: {}, year: {}", employeeId, year)
        
        val totalHours = hourRegistrationService.getTotalBillableHoursForYear(employeeId, year)
        logger.info("Total billable hours calculated - employeeId: {}, year: {}, total: {}", 
            employeeId, year, totalHours)
        return ResponseEntity.ok(mapOf(
            "employeeId" to employeeId,
            "year" to year,
            "totalBillableHours" to totalHours
        ))
    }
    
    @GetMapping("/health")
    fun health(): ResponseEntity<Map<String, Any>> {
        logger.debug("Hour registration health check requested")
        return ResponseEntity.ok(mapOf(
            "status" to "OK", 
            "service" to "HourRegistrationController",
            "timestamp" to System.currentTimeMillis()
        ))
    }
}