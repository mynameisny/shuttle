package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.Stop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface StopRepository extends JpaRepository<Stop, Long>, PagingAndSortingRepository<Stop, Long>, JpaSpecificationExecutor<Stop>
{
    Optional<Stop> findByStopId(String stopId);

    boolean existsByStopId(String stopId);

    List<Stop> findByParentStationId(Long parentStationId);

    List<Stop> findByLocationType(String locationType);
}