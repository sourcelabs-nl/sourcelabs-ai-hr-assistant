package nl.sourcelabs.sourcechat.tools

import com.fasterxml.jackson.annotation.JsonProperty
import nl.sourcelabs.sourcechat.dto.RegisterBillableHoursRequest
import nl.sourcelabs.sourcechat.dto.RegisterLeaveHoursRequest
import nl.sourcelabs.sourcechat.entity.LeaveType
import nl.sourcelabs.sourcechat.entity.TravelType
import nl.sourcelabs.sourcechat.service.HourRegistrationService
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
class HourRegistrationTools(
    private val hourRegistrationService: HourRegistrationService
) {

    @Bean
    fun registerLeaveHours(): java.util.function.Function<LeaveRegistrationRequest, String> {
        return java.util.function.Function { request -> registerLeaveHoursInternal(request) }
    }

    @Bean  
    fun registerBillableHours(): java.util.function.Function<BillableHoursRegistrationRequest, String> {
        return java.util.function.Function { request -> registerBillableHoursInternal(request) }
    }

    @Bean
    fun getTotalLeaveHours(): java.util.function.Function<EmployeeYearRequest, String> {
        return java.util.function.Function { request -> getTotalLeaveHoursInternal(request) }
    }

    @Bean
    fun getTotalBillableHours(): java.util.function.Function<EmployeeYearRequest, String> {
        return java.util.function.Function { request -> getTotalBillableHoursInternal(request) }
    }

    @Bean
    fun getLeaveHistory(): java.util.function.Function<EmployeeRequest, String> {
        return java.util.function.Function { request -> getLeaveHistoryInternal(request) }
    }

    @Bean
    fun getBillableHistory(): java.util.function.Function<EmployeeRequest, String> {
        return java.util.function.Function { request -> getBillableHistoryInternal(request) }
    }

    private fun registerLeaveHoursInternal(request: LeaveRegistrationRequest): String {
        return try {
            val leaveRequest = RegisterLeaveHoursRequest(
                employeeId = request.employeeId,
                leaveType = LeaveType.valueOf(request.leaveType.uppercase()),
                startDate = LocalDate.parse(request.startDate),
                endDate = LocalDate.parse(request.endDate),
                totalHours = request.totalHours,
                description = request.description
            )
            
            val response = hourRegistrationService.registerLeaveHours(leaveRequest)
            "✅ ${response.message}"
        } catch (e: Exception) {
            "❌ Error registering leave hours: ${e.message}"
        }
    }

    private fun registerBillableHoursInternal(request: BillableHoursRegistrationRequest): String {
        return try {
            val billableRequest = RegisterBillableHoursRequest(
                employeeId = request.employeeId,
                clientName = request.clientName,
                projectName = request.projectName,
                location = request.location,
                workDate = LocalDate.parse(request.workDate),
                hoursWorked = request.hoursWorked,
                description = request.description,
                travelType = request.travelType?.let { TravelType.valueOf(it.uppercase()) },
                travelKilometers = request.travelKilometers,
                travelFromLocation = request.travelFromLocation,
                travelToLocation = request.travelToLocation,
                hourlyRate = request.hourlyRate
            )
            
            val response = hourRegistrationService.registerBillableHours(billableRequest)
            "✅ ${response.message}"
        } catch (e: Exception) {
            "❌ Error registering billable hours: ${e.message}"
        }
    }

    private fun getTotalLeaveHoursInternal(request: EmployeeYearRequest): String {
        return try {
            val totalHours = hourRegistrationService.getTotalLeaveHoursForYear(request.employeeId, request.year)
            "Employee ${request.employeeId} has used $totalHours leave hours in ${request.year}"
        } catch (e: Exception) {
            "❌ Error retrieving leave hours: ${e.message}"
        }
    }

    private fun getTotalBillableHoursInternal(request: EmployeeYearRequest): String {
        return try {
            val totalHours = hourRegistrationService.getTotalBillableHoursForYear(request.employeeId, request.year)
            "Employee ${request.employeeId} has logged $totalHours billable hours in ${request.year}"
        } catch (e: Exception) {
            "❌ Error retrieving billable hours: ${e.message}"
        }
    }

    private fun getLeaveHistoryInternal(request: EmployeeRequest): String {
        return try {
            val leaveHours = hourRegistrationService.getLeaveHoursByEmployee(request.employeeId)
            if (leaveHours.isEmpty()) {
                "No leave hours found for employee ${request.employeeId}"
            } else {
                val summary = leaveHours.take(5).joinToString("\n") { leave ->
                    "• ${leave.leaveType} from ${leave.startDate} to ${leave.endDate} (${leave.totalHours}h) - ${leave.status}"
                }
                "Recent leave history for employee ${request.employeeId}:\n$summary"
            }
        } catch (e: Exception) {
            "❌ Error retrieving leave history: ${e.message}"
        }
    }

    private fun getBillableHistoryInternal(request: EmployeeRequest): String {
        return try {
            val billableHours = hourRegistrationService.getBillableHoursByEmployee(request.employeeId)
            if (billableHours.isEmpty()) {
                "No billable hours found for employee ${request.employeeId}"
            } else {
                val summary = billableHours.take(5).joinToString("\n") { hours ->
                    "• ${hours.workDate}: ${hours.hoursWorked}h for ${hours.clientName} at ${hours.location} - ${hours.status}"
                }
                "Recent billable hours for employee ${request.employeeId}:\n$summary"
            }
        } catch (e: Exception) {
            "❌ Error retrieving billable history: ${e.message}"
        }
    }
}

// Data classes for tool parameters
data class LeaveRegistrationRequest(
    @JsonProperty("employeeId") val employeeId: String,
    @JsonProperty("leaveType") val leaveType: String, // ANNUAL_LEAVE, SICK_LEAVE, etc.
    @JsonProperty("startDate") val startDate: String, // YYYY-MM-DD format
    @JsonProperty("endDate") val endDate: String, // YYYY-MM-DD format
    @JsonProperty("totalHours") val totalHours: Double,
    @JsonProperty("description") val description: String?
)

data class BillableHoursRegistrationRequest(
    @JsonProperty("employeeId") val employeeId: String,
    @JsonProperty("clientName") val clientName: String,
    @JsonProperty("projectName") val projectName: String?,
    @JsonProperty("location") val location: String,
    @JsonProperty("workDate") val workDate: String, // YYYY-MM-DD format
    @JsonProperty("hoursWorked") val hoursWorked: Double,
    @JsonProperty("description") val description: String,
    @JsonProperty("travelType") val travelType: String?, // CAR, BIKE, PUBLIC_TRANSPORT, etc.
    @JsonProperty("travelKilometers") val travelKilometers: Double?,
    @JsonProperty("travelFromLocation") val travelFromLocation: String?,
    @JsonProperty("travelToLocation") val travelToLocation: String?,
    @JsonProperty("hourlyRate") val hourlyRate: Double?
)

data class EmployeeRequest(
    @JsonProperty("employeeId") val employeeId: String
)

data class EmployeeYearRequest(
    @JsonProperty("employeeId") val employeeId: String,
    @JsonProperty("year") val year: Int
)