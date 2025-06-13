package nl.sourcelabs.sourcechat.dto

import nl.sourcelabs.sourcechat.entity.LeaveType
import nl.sourcelabs.sourcechat.entity.TravelType
import java.time.LocalDate

data class RegisterLeaveHoursRequest(
    val employeeId: String,
    val leaveType: LeaveType,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val totalHours: Double,
    val description: String?
)

data class RegisterBillableHoursRequest(
    val employeeId: String,
    val clientName: String,
    val projectName: String?,
    val location: String,
    val workDate: LocalDate,
    val hoursWorked: Double,
    val description: String,
    val travelType: TravelType? = null,
    val travelKilometers: Double? = null,
    val travelFromLocation: String? = null,
    val travelToLocation: String? = null,
    val hourlyRate: Double? = null
)

data class HourRegistrationResponse(
    val id: Long,
    val message: String,
    val status: String
)