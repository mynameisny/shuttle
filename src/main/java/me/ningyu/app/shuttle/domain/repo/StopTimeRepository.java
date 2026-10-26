package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.StopTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface StopTimeRepository extends JpaRepository<StopTime, Long>, PagingAndSortingRepository<StopTime, Long>, JpaSpecificationExecutor<StopTime>
{
    List<StopTime> findByTripIdOrderByStopSequenceAsc(Long tripId);

    Optional<StopTime> findFirstByTripIdOrderByStopSequenceAsc(Long tripId);
}