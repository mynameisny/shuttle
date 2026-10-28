package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.gtfs.LocationGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface LocationGroupRepository extends JpaRepository<LocationGroup, Long>, PagingAndSortingRepository<LocationGroup, Long>, JpaSpecificationExecutor<LocationGroup>
{
    Optional<LocationGroup> findByLocationGroupId(String locationGroupId);
}
