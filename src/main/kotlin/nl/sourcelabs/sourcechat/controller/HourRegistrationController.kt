package nl.sourcelabs.sourcechat.controller

import nl.sourcelabs.sourcechat.dto.HourRegistrationResponse
import nl.sourcelabs.sourcechat.dto.RegisterBillableHoursRequest
import nl.sourcelabs.sourcechat.dto.RegisterLeaveHoursRequest
import nl.sourcelabs.sourcechat.entity.BillableClientHours
import nl.sourcelabs.sourcechat.entity.LeaveHours
import nl.sourcelabs.sourcechat.service.HourRegistrationService
import org.apache.logging.log4j.LogManager
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
@RestController
@RequestMapping("/api/hours")
@CrossOrigin(origins = ["*"])
class HourRegistrationController(
    private val hourRegistrationService: HourRegistrationService
) {
    
    private val logger = LogManager.getLogger()
    
    @PostMapping("/leave")
    fun registerLeaveHours(@RequestBody request: RegisterLeaveHoursRequest): ResponseEntity<HourRegistrationResponse> {
        logger.info("REST API: Register leave hours request - employeeId: {}, leaveType: {}, hours: {}", 
            request.employeeId, request.leaveType, request.totalHours)
        
        val response = hourRegistrationService.registerLeaveHours(request)
        logger.info("REST API: Leave hours registered successfully - employeeId: {}, id: {}", 
            request.employeeId, response.id)
        return ResponseEntity.ok(response)
    }
    
    @PostMapping("/billable")
    fun registerBillableHours(@RequestBody request: RegisterBillableHoursRequest): ResponseEntity<HourRegistrationResponse> {
        logger.info("REST API: Register billable hours request - employeeId: {}, client: {}, hours: {}", 
            request.employeeId, request.clientName, request.hoursWorked)
        
        val response = hourRegistrationService.registerBillableHours(request)
        logger.info("REST API: Billable hours registered successfully - employeeId: {}, client: {}, id: {}", 
            request.employeeId, request.clientName, response.id)
        return ResponseEntity.ok(response)
    }
    
    @GetMapping("/leave/{employeeId}")
    fun getLeaveHours(@PathVariable employeeId: String): ResponseEntity<List<LeaveHours>> {
        logger.info("REST API: Get leave hours request - employeeId: {}", employeeId)
        
        val leaveHours = hourRegistrationService.getLeaveHoursByEmployee(employeeId)
        logger.info("REST API: Retrieved leave hours - employeeId: {}, count: {}", employeeId, leaveHours.size)
        return ResponseEntity.ok(leaveHours)
    }
    
    @GetMapping("/billable/{employeeId}")
    fun getBillableHours(@PathVariable employeeId: String): ResponseEntity<List<BillableClientHours>> {
        logger.info("REST API: Get billable hours request - employeeId: {}", employeeId)
        
        val billableHours = hourRegistrationService.getBillableHoursByEmployee(employeeId)
        logger.info("REST API: Retrieved billable hours - employeeId: {}, count: {}", employeeId, billableHours.size)
        return ResponseEntity.ok(billableHours)
    }
    
    @GetMapping("/leave/{employeeId}/total/{year}")
    fun getTotalLeaveHours(
        @PathVariable employeeId: String,
        @PathVariable year: Int
    ): ResponseEntity<Map<String, Any>> {
        logger.info("REST API: Get total leave hours request - employeeId: {}, year: {}", employeeId, year)
        
        val totalHours = hourRegistrationService.getTotalLeaveHoursForYear(employeeId, year)
        logger.info("REST API: Total leave hours calculated - employeeId: {}, year: {}, total: {}", 
            employeeId, year, totalHours)
        return ResponseEntity.ok(mapOf(
            "employeeId" to employeeId,
            "year" to year,
            "totalLeaveHours" to totalHours
        ))
    }
    
    @GetMapping("/billable/{employeeId}/total/{year}")
    fun getTotalBillableHours(
        @PathVariable employeeId: String,
        @PathVariable year: Int
    ): ResponseEntity<Map<String, Any>> {
        logger.info("REST API: Get total billable hours request - employeeId: {}, year: {}", employeeId, year)
        
        val totalHours = hourRegistrationService.getTotalBillableHoursForYear(employeeId, year)
        logger.info("REST API: Total billable hours calculated - employeeId: {}, year: {}, total: {}", 
            employeeId, year, totalHours)
        return ResponseEntity.ok(mapOf(
            "employeeId" to employeeId,
            "year" to year,
            "totalBillableHours" to totalHours
        ))
    }
    
    @GetMapping("/health")
    fun health(): ResponseEntity<Map<String, String>> {
        logger.info("REST API: Hour registration health check requested")
        return ResponseEntity.ok(mapOf("status" to "OK", "service" to "HourRegistrationController"))
    }
}