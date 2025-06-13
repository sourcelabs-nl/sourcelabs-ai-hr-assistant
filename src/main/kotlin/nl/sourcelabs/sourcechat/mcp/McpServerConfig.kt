package nl.sourcelabs.sourcechat.mcp

import nl.sourcelabs.sourcechat.dto.RegisterBillableHoursRequest
import nl.sourcelabs.sourcechat.dto.RegisterLeaveHoursRequest
import nl.sourcelabs.sourcechat.entity.LeaveType
import nl.sourcelabs.sourcechat.entity.TravelType
import nl.sourcelabs.sourcechat.service.HourRegistrationService
import org.springframework.ai.tool.ToolCallbackProvider
import org.springframework.ai.tool.method.MethodToolCallbackProvider
import org.springframework.ai.tool.annotation.Tool
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class HourRegistrationMcpService(
    private val hourRegistrationService: HourRegistrationService
) {

    @Tool(description = "Register leave hours for an employee")
    fun registerLeaveHours(
        employeeId: String,
        leaveType: String,
        startDate: String,
        endDate: String,
        totalHours: Double,
        description: String? = null
    ): String {
        return try {
            val request = RegisterLeaveHoursRequest(
                employeeId = employeeId,
                leaveType = LeaveType.valueOf(leaveType.uppercase()),
                startDate = LocalDate.parse(startDate),
                endDate = LocalDate.parse(endDate),
                totalHours = totalHours,
                description = description
            )
            
            val response = hourRegistrationService.registerLeaveHours(request)
            "✅ ${response.message}"
        } catch (e: Exception) {
            "❌ Error registering leave hours: ${e.message}"
        }
    }

    @Tool(description = "Register billable client hours for an employee")
    fun registerBillableHours(
        employeeId: String,
        clientName: String,
        location: String,
        workDate: String,
        hoursWorked: Double,
        description: String,
        projectName: String? = null,
        travelType: String? = null,
        travelKilometers: Double? = null,
        travelFromLocation: String? = null,
        travelToLocation: String? = null,
        hourlyRate: Double? = null
    ): String {
        return try {
            val request = RegisterBillableHoursRequest(
                employeeId = employeeId,
                clientName = clientName,
                projectName = projectName,
                location = location,
                workDate = LocalDate.parse(workDate),
                hoursWorked = hoursWorked,
                description = description,
                travelType = travelType?.let { TravelType.valueOf(it.uppercase()) },
                travelKilometers = travelKilometers,
                travelFromLocation = travelFromLocation,
                travelToLocation = travelToLocation,
                hourlyRate = hourlyRate
            )
            
            val response = hourRegistrationService.registerBillableHours(request)
            "✅ ${response.message}"
        } catch (e: Exception) {
            "❌ Error registering billable hours: ${e.message}"
        }
    }

    @Tool(description = "Get total leave hours for an employee in a specific year")
    fun getLeaveHoursSummary(employeeId: String, year: Int): String {
        return try {
            val totalHours = hourRegistrationService.getTotalLeaveHoursForYear(employeeId, year)
            "Employee $employeeId has used $totalHours leave hours in $year"
        } catch (e: Exception) {
            "❌ Error retrieving leave hours: ${e.message}"
        }
    }

    @Tool(description = "Get total billable hours for an employee in a specific year")
    fun getBillableHoursSummary(employeeId: String, year: Int): String {
        return try {
            val totalHours = hourRegistrationService.getTotalBillableHoursForYear(employeeId, year)
            "Employee $employeeId has logged $totalHours billable hours in $year"
        } catch (e: Exception) {
            "❌ Error retrieving billable hours: ${e.message}"
        }
    }

    @Tool(description = "Get recent leave history for an employee")
    fun getLeaveHistory(employeeId: String): String {
        return try {
            val leaveHours = hourRegistrationService.getLeaveHoursByEmployee(employeeId)
            if (leaveHours.isEmpty()) {
                "No leave hours found for employee $employeeId"
            } else {
                val summary = leaveHours.take(5).joinToString("\n") { leave ->
                    "• ${leave.leaveType} from ${leave.startDate} to ${leave.endDate} (${leave.totalHours}h) - ${leave.status}"
                }
                "Recent leave history for employee $employeeId:\n$summary"
            }
        } catch (e: Exception) {
            "❌ Error retrieving leave history: ${e.message}"
        }
    }

    @Tool(description = "Get recent billable hours history for an employee")
    fun getBillableHistory(employeeId: String): String {
        return try {
            val billableHours = hourRegistrationService.getBillableHoursByEmployee(employeeId)
            if (billableHours.isEmpty()) {
                "No billable hours found for employee $employeeId"
            } else {
                val summary = billableHours.take(5).joinToString("\n") { hours ->
                    "• ${hours.workDate}: ${hours.hoursWorked}h for ${hours.clientName} at ${hours.location} - ${hours.status}"
                }
                "Recent billable hours for employee $employeeId:\n$summary"
            }
        } catch (e: Exception) {
            "❌ Error retrieving billable history: ${e.message}"
        }
    }
}

@Configuration
class McpServerConfig {

    @Bean
    fun hourRegistrationToolCallbackProvider(hourRegistrationMcpService: HourRegistrationMcpService): ToolCallbackProvider {
        return MethodToolCallbackProvider.builder()
            .toolObjects(hourRegistrationMcpService)
            .build()
    }
}