package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.gtfs.Stop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StopRepository extends JpaRepository<Stop, Long>, PagingAndSortingRepository<Stop, Long>, JpaSpecificationExecutor<Stop>
{
    Optional<Stop> findByStopId(String stopId);

    boolean existsByStopId(String stopId);

    List<Stop> findByParentStationId(Long parentStationId);

    List<Stop> findByLocationType(String locationType);

    List<Stop> findByStopNameContaining(String stopName);

    /**
     * 全量站点按台账显示顺序查询。
     * display_order 空值（未排序/新建站点）排最后；MySQL ASC 时 NULL 在最前，须 CASE 兜底反转。
     * 同序号时按主键保证稳定序。
     * 注意实体名是 @Entity(name = "stop")（小写），HQL 须用实体名而非类名。
     */
    @Query("SELECT s FROM stop s ORDER BY CASE WHEN s.displayOrder IS NULL THEN 1 ELSE 0 END, s.displayOrder ASC, s.id ASC")
    List<Stop> findAllOrdered();

    /**
     * 按名称模糊搜索，结果保持与 findAllOrdered 一致的台账顺序。
     */
    @Query("SELECT s FROM stop s WHERE s.stopName LIKE CONCAT('%', :keyword, '%') ORDER BY CASE WHEN s.displayOrder IS NULL THEN 1 ELSE 0 END, s.displayOrder ASC, s.id ASC")
    List<Stop> findByStopNameContainingOrdered(@Param("keyword") String keyword);
}