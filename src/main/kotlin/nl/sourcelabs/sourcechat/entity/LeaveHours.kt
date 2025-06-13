package nl.sourcelabs.sourcechat.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.time.LocalDateTime

@Table("leave_hours")
data class LeaveHours(
    @Id
    val id: Long? = null,
    val employeeId: String,
    val leaveType: LeaveType,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val totalHours: Double,
    val description: String?,
    val status: LeaveStatus = LeaveStatus.PENDING,
    val requestedAt: LocalDateTime = LocalDateTime.now(),
    val approvedAt: LocalDateTime? = null,
    val approvedBy: String? = null
)

enum class LeaveType {
    ANNUAL_LEAVE,
    SICK_LEAVE,
    PERSONAL_LEAVE,
    MATERNITY_LEAVE,
    PATERNITY_LEAVE,
    BEREAVEMENT_LEAVE,
    OTHER
}

enum class LeaveStatus {
    PENDING,
    APPROVED,
    REJECTED,
    CANCELLED
}