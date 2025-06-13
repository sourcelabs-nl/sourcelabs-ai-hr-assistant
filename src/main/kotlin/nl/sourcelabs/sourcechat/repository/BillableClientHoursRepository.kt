package nl.sourcelabs.sourcechat.repository

import nl.sourcelabs.sourcechat.entity.BillableClientHours
import nl.sourcelabs.sourcechat.entity.BillableStatus
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface BillableClientHoursRepository : CrudRepository<BillableClientHours, Long> {
    
    @Query("SELECT * FROM billable_client_hours WHERE employee_id = :employeeId ORDER BY work_date DESC")
    fun findByEmployeeIdOrderByWorkDateDesc(employeeId: String): List<BillableClientHours>
    
    @Query("SELECT * FROM billable_client_hours WHERE employee_id = :employeeId AND status = :status ORDER BY work_date DESC")
    fun findByEmployeeIdAndStatus(employeeId: String, status: BillableStatus): List<BillableClientHours>
    
    @Query("SELECT * FROM billable_client_hours WHERE client_name = :clientName ORDER BY work_date DESC")
    fun findByClientNameOrderByWorkDateDesc(clientName: String): List<BillableClientHours>
    
    @Query("SELECT * FROM billable_client_hours WHERE work_date >= :fromDate AND work_date <= :toDate ORDER BY work_date")
    fun findByWorkDateBetween(fromDate: LocalDate, toDate: LocalDate): List<BillableClientHours>
    
    @Query("SELECT SUM(hours_worked) FROM billable_client_hours WHERE employee_id = :employeeId AND status IN ('APPROVED', 'INVOICED') AND EXTRACT(YEAR FROM work_date) = :year")
    fun getTotalBillableHoursForYear(employeeId: String, year: Int): Double?
    
    @Query("SELECT SUM(hours_worked) FROM billable_client_hours WHERE employee_id = :employeeId AND client_name = :clientName AND status IN ('APPROVED', 'INVOICED') AND work_date >= :fromDate AND work_date <= :toDate")
    fun getTotalBillableHoursForClientAndPeriod(employeeId: String, clientName: String, fromDate: LocalDate, toDate: LocalDate): Double?
}