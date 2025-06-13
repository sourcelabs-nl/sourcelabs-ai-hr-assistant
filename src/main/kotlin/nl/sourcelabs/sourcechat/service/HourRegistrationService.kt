package nl.sourcelabs.sourcechat.service

import nl.sourcelabs.sourcechat.dto.HourRegistrationResponse
import nl.sourcelabs.sourcechat.dto.RegisterBillableHoursRequest
import nl.sourcelabs.sourcechat.dto.RegisterLeaveHoursRequest
import nl.sourcelabs.sourcechat.entity.BillableClientHours
import nl.sourcelabs.sourcechat.entity.BillableStatus
import nl.sourcelabs.sourcechat.entity.LeaveHours
import nl.sourcelabs.sourcechat.entity.LeaveStatus
import nl.sourcelabs.sourcechat.repository.BillableClientHoursRepository
import nl.sourcelabs.sourcechat.repository.LeaveHoursRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class HourRegistrationService(
    private val leaveHoursRepository: LeaveHoursRepository,
    private val billableClientHoursRepository: BillableClientHoursRepository
) {
    
    fun registerLeaveHours(request: RegisterLeaveHoursRequest): HourRegistrationResponse {
        val leaveHours = LeaveHours(
            employeeId = request.employeeId,
            leaveType = request.leaveType,
            startDate = request.startDate,
            endDate = request.endDate,
            totalHours = request.totalHours,
            description = request.description,
            status = LeaveStatus.PENDING,
            requestedAt = LocalDateTime.now()
        )
        
        val saved = leaveHoursRepository.save(leaveHours)
        
        return HourRegistrationResponse(
            id = saved.id!!,
            message = "Leave hours registered successfully. Request ID: ${saved.id}",
            status = "PENDING"
        )
    }
    
    fun registerBillableHours(request: RegisterBillableHoursRequest): HourRegistrationResponse {
        val billableHours = BillableClientHours(
            employeeId = request.employeeId,
            clientName = request.clientName,
            projectName = request.projectName,
            location = request.location,
            workDate = request.workDate,
            hoursWorked = request.hoursWorked,
            description = request.description,
            travelType = request.travelType,
            travelKilometers = request.travelKilometers,
            travelFromLocation = request.travelFromLocation,
            travelToLocation = request.travelToLocation,
            hourlyRate = request.hourlyRate,
            status = BillableStatus.PENDING,
            createdAt = LocalDateTime.now()
        )
        
        val saved = billableClientHoursRepository.save(billableHours)
        
        return HourRegistrationResponse(
            id = saved.id!!,
            message = "Billable hours registered successfully for client ${request.clientName}. Entry ID: ${saved.id}",
            status = "PENDING"
        )
    }
    
    fun getLeaveHoursByEmployee(employeeId: String): List<LeaveHours> {
        return leaveHoursRepository.findByEmployeeIdOrderByStartDateDesc(employeeId)
    }
    
    fun getBillableHoursByEmployee(employeeId: String): List<BillableClientHours> {
        return billableClientHoursRepository.findByEmployeeIdOrderByWorkDateDesc(employeeId)
    }
    
    fun getTotalLeaveHoursForYear(employeeId: String, year: Int): Double {
        return leaveHoursRepository.getTotalApprovedLeaveHoursForYear(employeeId, year) ?: 0.0
    }
    
    fun getTotalBillableHoursForYear(employeeId: String, year: Int): Double {
        return billableClientHoursRepository.getTotalBillableHoursForYear(employeeId, year) ?: 0.0
    }
}