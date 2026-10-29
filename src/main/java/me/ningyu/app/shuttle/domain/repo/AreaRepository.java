package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.gtfs.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AreaRepository extends JpaRepository<Area, Long>, PagingAndSortingRepository<Area, Long>, JpaSpecificationExecutor<Area>
{
}
