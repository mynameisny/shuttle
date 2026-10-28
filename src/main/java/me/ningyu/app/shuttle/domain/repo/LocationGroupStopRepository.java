package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.gtfs.LocationGroup;
import me.ningyu.app.shuttle.domain.entity.gtfs.LocationGroupStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface LocationGroupStopRepository extends JpaRepository<LocationGroupStop, Long>, PagingAndSortingRepository<LocationGroupStop, Long>, JpaSpecificationExecutor<LocationGroupStop>
{
    List<LocationGroupStop> findByLocationGroup(LocationGroup locationGroup);
}
