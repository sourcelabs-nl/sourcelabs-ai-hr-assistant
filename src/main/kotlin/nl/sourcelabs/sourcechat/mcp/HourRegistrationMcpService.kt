package nl.sourcelabs.sourcechat.mcp

import nl.sourcelabs.sourcechat.dto.RegisterBillableHoursRequest
import nl.sourcelabs.sourcechat.dto.RegisterLeaveHoursRequest
import nl.sourcelabs.sourcechat.entity.LeaveType
import nl.sourcelabs.sourcechat.entity.TravelType
import nl.sourcelabs.sourcechat.service.HourRegistrationService
import org.apache.logging.log4j.LogManager
import org.springframework.ai.tool.annotation.Tool
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class HourRegistrationMcpService(
    private val hourRegistrationService: HourRegistrationService
) {

    companion object {
        private val logger = LogManager.getLogger(HourRegistrationMcpService::class.java)
    }
    
    @Tool(description = "Register leave hours for an employee")
    fun registerLeaveHours(
        employeeId: String,
        leaveType: String,
        startDate: String,
        endDate: String,
        totalHours: Double,
        description: String? = null
    ): String {
        logger.info("MCP Tool: registerLeaveHours called - employeeId: {}, leaveType: {}, startDate: {}, totalHours: {}", 
                   employeeId, leaveType, startDate, totalHours)
        return try {
            val request = RegisterLeaveHoursRequest(
                employeeId = employeeId,
                leaveType = LeaveType.valueOf(leaveType.uppercase()),
                startDate = LocalDate.parse(startDate),
                endDate = LocalDate.parse(endDate),
                totalHours = totalHours,
                description = description
            )
            
            val result = hourRegistrationService.registerLeaveHours(request).message
            logger.info("MCP Tool: registerLeaveHours completed successfully - employeeId: {}", employeeId)
            "✅ $result"
        } catch (e: Exception) {
            val errorMessage = "Error in registerLeaveHours: ${e.message}"
            logger.error("MCP Tool: registerLeaveHours failed - employeeId: {}, error: {}", employeeId, e.message, e)
            "❌ $errorMessage"
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
        logger.info("MCP Tool: registerBillableHours called - employeeId: {}, clientName: {}, workDate: {}, hoursWorked: {}", 
                   employeeId, clientName, workDate, hoursWorked)
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
            
            val result = hourRegistrationService.registerBillableHours(request).message
            logger.info("MCP Tool: registerBillableHours completed successfully - employeeId: {}", employeeId)
            "✅ $result"
        } catch (e: Exception) {
            val errorMessage = "Error in registerBillableHours: ${e.message}"
            logger.error("MCP Tool: registerBillableHours failed - employeeId: {}, error: {}", employeeId, e.message, e)
            "❌ $errorMessage"
        }
    }

    @Tool(description = "Get total leave hours for an employee in a specific year")
    fun getLeaveHoursSummary(employeeId: String, year: Int): String {
        logger.info("MCP Tool: getLeaveHoursSummary called - employeeId: {}, year: {}", employeeId, year)
        return try {
            val totalHours = hourRegistrationService.getTotalLeaveHoursForYear(employeeId, year)
            val result = "Employee $employeeId has used $totalHours leave hours in $year"
            logger.info("MCP Tool: getLeaveHoursSummary completed - employeeId: {}, year: {}, totalHours: {}", employeeId, year, totalHours)
            result
        } catch (e: Exception) {
            val result = "❌ Error retrieving leave hours: ${e.message}"
            logger.error("MCP Tool: getLeaveHoursSummary failed - employeeId: {}, year: {}, error: {}", employeeId, year, e.message, e)
            result
        }
    }

    @Tool(description = "Get total billable hours for an employee in a specific year")
    fun getBillableHoursSummary(employeeId: String, year: Int): String {
        logger.info("MCP Tool: getBillableHoursSummary called - employeeId: {}, year: {}", employeeId, year)
        return try {
            val totalHours = hourRegistrationService.getTotalBillableHoursForYear(employeeId, year)
            val result = "Employee $employeeId has logged $totalHours billable hours in $year"
            logger.info("MCP Tool: getBillableHoursSummary completed - employeeId: {}, year: {}, totalHours: {}", employeeId, year, totalHours)
            result
        } catch (e: Exception) {
            val result = "❌ Error retrieving billable hours: ${e.message}"
            logger.error("MCP Tool: getBillableHoursSummary failed - employeeId: {}, year: {}, error: {}", employeeId, year, e.message, e)
            result
        }
    }

    @Tool(description = "Get recent leave history for an employee")
    fun getLeaveHistory(employeeId: String): String {
        logger.info("MCP Tool: getLeaveHistory called - employeeId: {}", employeeId)
        return try {
            val leaveHours = hourRegistrationService.getLeaveHoursByEmployee(employeeId)
            val result = if (leaveHours.isEmpty()) {
                "No leave hours found for employee $employeeId"
            } else {
                val summary = leaveHours.take(5).joinToString("\n") { leave ->
                    "• ${leave.leaveType} from ${leave.startDate} to ${leave.endDate} (${leave.totalHours}h) - ${leave.status}"
                }
                "Recent leave history for employee $employeeId:\n$summary"
            }
            logger.info("MCP Tool: getLeaveHistory completed - employeeId: {}, recordCount: {}", employeeId, leaveHours.size)
            result
        } catch (e: Exception) {
            val result = "❌ Error retrieving leave history: ${e.message}"
            logger.error("MCP Tool: getLeaveHistory failed - employeeId: {}, error: {}", employeeId, e.message, e)
            result
        }
    }

    @Tool(description = "Get recent billable hours history for an employee")
    fun getBillableHistory(employeeId: String): String {
        logger.info("MCP Tool: getBillableHistory called - employeeId: {}", employeeId)
        return try {
            val billableHours = hourRegistrationService.getBillableHoursByEmployee(employeeId)
            val result = if (billableHours.isEmpty()) {
                "No billable hours found for employee $employeeId"
            } else {
                val summary = billableHours.take(5).joinToString("\n") { hours ->
                    "• ${hours.workDate}: ${hours.hoursWorked}h for ${hours.clientName} at ${hours.location} - ${hours.status}"
                }
                "Recent billable hours for employee $employeeId:\n$summary"
            }
            logger.info("MCP Tool: getBillableHistory completed - employeeId: {}, recordCount: {}", employeeId, billableHours.size)
            result
        } catch (e: Exception) {
            val result = "❌ Error retrieving billable history: ${e.message}"
            logger.error("MCP Tool: getBillableHistory failed - employeeId: {}, error: {}", employeeId, e.message, e)
            result
        }
    }
}