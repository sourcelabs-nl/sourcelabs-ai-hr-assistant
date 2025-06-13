package nl.sourcelabs.sourcechat.controller

import nl.sourcelabs.sourcechat.dto.HourRegistrationResponse
import nl.sourcelabs.sourcechat.dto.RegisterBillableHoursRequest
import nl.sourcelabs.sourcechat.dto.RegisterLeaveHoursRequest
import nl.sourcelabs.sourcechat.entity.BillableClientHours
import nl.sourcelabs.sourcechat.entity.LeaveHours
import nl.sourcelabs.sourcechat.service.HourRegistrationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/hours")
@CrossOrigin(origins = ["*"])
class HourRegistrationController(
    private val hourRegistrationService: HourRegistrationService
) {
    
    @PostMapping("/leave")
    fun registerLeaveHours(@RequestBody request: RegisterLeaveHoursRequest): ResponseEntity<HourRegistrationResponse> {
        return try {
            val response = hourRegistrationService.registerLeaveHours(request)
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                HourRegistrationResponse(
                    id = -1,
                    message = "Error registering leave hours: ${e.message}",
                    status = "ERROR"
                )
            )
        }
    }
    
    @PostMapping("/billable")
    fun registerBillableHours(@RequestBody request: RegisterBillableHoursRequest): ResponseEntity<HourRegistrationResponse> {
        return try {
            val response = hourRegistrationService.registerBillableHours(request)
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                HourRegistrationResponse(
                    id = -1,
                    message = "Error registering billable hours: ${e.message}",
                    status = "ERROR"
                )
            )
        }
    }
    
    @GetMapping("/leave/{employeeId}")
    fun getLeaveHours(@PathVariable employeeId: String): ResponseEntity<List<LeaveHours>> {
        return try {
            val leaveHours = hourRegistrationService.getLeaveHoursByEmployee(employeeId)
            ResponseEntity.ok(leaveHours)
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(emptyList())
        }
    }
    
    @GetMapping("/billable/{employeeId}")
    fun getBillableHours(@PathVariable employeeId: String): ResponseEntity<List<BillableClientHours>> {
        return try {
            val billableHours = hourRegistrationService.getBillableHoursByEmployee(employeeId)
            ResponseEntity.ok(billableHours)
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(emptyList())
        }
    }
    
    @GetMapping("/leave/{employeeId}/total/{year}")
    fun getTotalLeaveHours(
        @PathVariable employeeId: String,
        @PathVariable year: Int
    ): ResponseEntity<Map<String, Any>> {
        return try {
            val totalHours = hourRegistrationService.getTotalLeaveHoursForYear(employeeId, year)
            ResponseEntity.ok(mapOf(
                "employeeId" to employeeId,
                "year" to year,
                "totalLeaveHours" to totalHours
            ))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to (e.message ?: "Unknown error")))
        }
    }
    
    @GetMapping("/billable/{employeeId}/total/{year}")
    fun getTotalBillableHours(
        @PathVariable employeeId: String,
        @PathVariable year: Int
    ): ResponseEntity<Map<String, Any>> {
        return try {
            val totalHours = hourRegistrationService.getTotalBillableHoursForYear(employeeId, year)
            ResponseEntity.ok(mapOf(
                "employeeId" to employeeId,
                "year" to year,
                "totalBillableHours" to totalHours
            ))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to (e.message ?: "Unknown error")))
        }
    }
    
    @GetMapping("/health")
    fun health(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(mapOf("status" to "OK", "service" to "HourRegistrationController"))
    }
}