package nl.sourcelabs.sourcechat.repository

import nl.sourcelabs.sourcechat.entity.LeaveHours
import nl.sourcelabs.sourcechat.entity.LeaveStatus
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface LeaveHoursRepository : CrudRepository<LeaveHours, Long> {
    
    @Query("SELECT * FROM leave_hours WHERE employee_id = :employeeId ORDER BY start_date DESC")
    fun findByEmployeeIdOrderByStartDateDesc(employeeId: String): List<LeaveHours>
    
    @Query("SELECT * FROM leave_hours WHERE employee_id = :employeeId AND status = :status ORDER BY start_date DESC")
    fun findByEmployeeIdAndStatus(employeeId: String, status: LeaveStatus): List<LeaveHours>
    
    @Query("SELECT * FROM leave_hours WHERE start_date >= :fromDate AND end_date <= :toDate ORDER BY start_date")
    fun findByDateRange(fromDate: LocalDate, toDate: LocalDate): List<LeaveHours>
    
    @Query("SELECT SUM(total_hours) FROM leave_hours WHERE employee_id = :employeeId AND status = 'APPROVED' AND EXTRACT(YEAR FROM start_date) = :year")
    fun getTotalApprovedLeaveHoursForYear(employeeId: String, year: Int): Double?
}