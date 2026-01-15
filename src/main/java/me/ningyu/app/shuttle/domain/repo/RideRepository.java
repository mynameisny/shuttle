package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.RideRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RideRepository extends JpaRepository<RideRecord, Long>, PagingAndSortingRepository<RideRecord, Long>, JpaSpecificationExecutor<RideRecord>
{
    boolean existsByScheduleId(Long scheduleId);
}
