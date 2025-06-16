package nl.sourcelabs.sourcechat.dto

import jakarta.validation.constraints.*
import nl.sourcelabs.sourcechat.entity.LeaveType
import nl.sourcelabs.sourcechat.entity.TravelType
import java.time.LocalDate

data class RegisterLeaveHoursRequest(
    @field:NotBlank(message = "Employee ID is required")
    val employeeId: String,
    
    @field:NotNull(message = "Leave type is required")
    val leaveType: LeaveType,
    
    @field:NotNull(message = "Start date is required")
    val startDate: LocalDate,
    
    @field:NotNull(message = "End date is required")
    val endDate: LocalDate,
    
    @field:Positive(message = "Total hours must be positive")
    @field:DecimalMax(value = "24.0", message = "Cannot exceed 24 hours per day")
    val totalHours: Double,
    
    @field:Size(max = 500, message = "Description too long")
    val description: String?
) {
    @AssertTrue(message = "End date must be after or equal to start date")
    fun isValidDateRange(): Boolean = !endDate.isBefore(startDate)
}

data class RegisterBillableHoursRequest(
    @field:NotBlank(message = "Employee ID is required")
    val employeeId: String,
    
    @field:NotBlank(message = "Client name is required")
    @field:Size(max = 100, message = "Client name too long")
    val clientName: String,
    
    @field:Size(max = 100, message = "Project name too long")
    val projectName: String?,
    
    @field:NotBlank(message = "Location is required")
    @field:Size(max = 100, message = "Location too long")
    val location: String,
    
    @field:NotNull(message = "Work date is required")
    val workDate: LocalDate,
    
    @field:Positive(message = "Hours worked must be positive")
    @field:DecimalMax(value = "24.0", message = "Cannot exceed 24 hours per day")
    val hoursWorked: Double,
    
    @field:NotBlank(message = "Description is required")
    @field:Size(max = 500, message = "Description too long")
    val description: String,
    
    val travelType: TravelType? = null,
    
    @field:PositiveOrZero(message = "Travel kilometers must be zero or positive")
    @field:DecimalMax(value = "10000.0", message = "Travel kilometers seems unrealistic")
    val travelKilometers: Double? = null,
    
    @field:Size(max = 100, message = "Travel from location too long")
    val travelFromLocation: String? = null,
    
    @field:Size(max = 100, message = "Travel to location too long")
    val travelToLocation: String? = null,
    
    @field:PositiveOrZero(message = "Hourly rate must be zero or positive")
    @field:DecimalMax(value = "1000.0", message = "Hourly rate seems unrealistic")
    val hourlyRate: Double? = null
)

data class HourRegistrationResponse(
    val id: Long,
    val message: String,
    val status: String
)