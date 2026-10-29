package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.gtfs.FareMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface FareMediaRepository extends JpaRepository<FareMedia, Long>, PagingAndSortingRepository<FareMedia, Long>, JpaSpecificationExecutor<FareMedia>
{
}
