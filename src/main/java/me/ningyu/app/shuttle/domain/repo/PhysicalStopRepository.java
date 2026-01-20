package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.PhysicalStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 物理站点 Repository
 * 按照 GUIDES.md 规范，原 Stop 改为 PhysicalStop
 */
public interface PhysicalStopRepository extends JpaRepository<PhysicalStop, Long>, PagingAndSortingRepository<PhysicalStop, Long>, JpaSpecificationExecutor<PhysicalStop>
{
}
