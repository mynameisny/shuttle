package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.OperationLogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * 运营记录 Repository
 */
@Repository
public interface OperationLogEntryRepository extends JpaRepository<OperationLogEntry, Long>, PagingAndSortingRepository<OperationLogEntry, Long>, JpaSpecificationExecutor<OperationLogEntry>
{
    /**
     * 查询某个班次的所有运营记录
     */
    List<OperationLogEntry> findByShiftIdOrderByActualArrivalTimeAsc(Long shiftId);

    /**
     * 查询某个班次在某个站点的运营记录
     */
    @Query("SELECT ole FROM operation_log_entry ole WHERE ole.shift.id = :shiftId AND ole.stop.id = :stopId")
    List<OperationLogEntry> findByShiftIdAndStopId(@Param("shiftId") Long shiftId, @Param("stopId") Long stopId);

    /**
     * 查询某一天的所有运营记录
     */
    @Query("SELECT ole FROM operation_log_entry ole WHERE DATE(ole.actualArrivalTime) = :date ORDER BY ole.actualArrivalTime ASC")
    List<OperationLogEntry> findByOperatingDate(@Param("date") LocalDate date);
}
