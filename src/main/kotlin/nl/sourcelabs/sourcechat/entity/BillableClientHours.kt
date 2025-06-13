package nl.sourcelabs.sourcechat.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.time.LocalDateTime

@Table("billable_client_hours")
data class BillableClientHours(
    @Id
    val id: Long? = null,
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
    val hourlyRate: Double? = null,
    val status: BillableStatus = BillableStatus.PENDING,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val submittedAt: LocalDateTime? = null,
    val approvedAt: LocalDateTime? = null,
    val invoicedAt: LocalDateTime? = null
)

enum class TravelType {
    CAR,
    BIKE,
    PUBLIC_TRANSPORT,
    FLIGHT,
    TRAIN,
    OTHER,
    NO_TRAVEL
}

enum class BillableStatus {
    PENDING,
    SUBMITTED,
    APPROVED,
    INVOICED,
    REJECTED
}