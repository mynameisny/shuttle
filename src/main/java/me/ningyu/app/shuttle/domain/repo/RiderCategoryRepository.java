package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.gtfs.RiderCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface RiderCategoryRepository extends JpaRepository<RiderCategory, Long>, PagingAndSortingRepository<RiderCategory, Long>, JpaSpecificationExecutor<RiderCategory>
{
}
