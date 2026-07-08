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

@Repository
public interface OperationLogEntryRepository extends JpaRepository<OperationLogEntry, Long>, PagingAndSortingRepository<OperationLogEntry, Long>, JpaSpecificationExecutor<OperationLogEntry>
{
    List<OperationLogEntry> findByTripIdOrderByActualArrivalTimeAsc(Long tripId);

    @Query("SELECT ole FROM operation_log_entry ole WHERE ole.trip.id = :tripId AND ole.stop.id = :stopId")
    List<OperationLogEntry> findByTripIdAndStopId(@Param("tripId") Long tripId, @Param("stopId") Long stopId);

    @Query("SELECT ole FROM operation_log_entry ole WHERE DATE(ole.actualArrivalTime) = :date ORDER BY ole.actualArrivalTime ASC")
    List<OperationLogEntry> findByOperatingDate(@Param("date") LocalDate date);
}
