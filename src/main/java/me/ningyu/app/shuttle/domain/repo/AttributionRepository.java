package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.gtfs.Attribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AttributionRepository extends JpaRepository<Attribution, Long>, PagingAndSortingRepository<Attribution, Long>, JpaSpecificationExecutor<Attribution>
{
}
