package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.gtfs.Shape;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShapeRepository extends JpaRepository<Shape, Long>, PagingAndSortingRepository<Shape, Long>, JpaSpecificationExecutor<Shape>
{
    List<Shape> findByShapeIdOrderByShapePtSequenceAsc(String shapeId);

    /**
     * 删除指定形状的全部点（全量替换场景）。
     * flushAutomatically 保证删除先于后续插入执行；clearAutomatically 清理一级缓存避免读到已删除的旧点。
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM shape s WHERE s.shapeId = :shapeId")
    void deleteByShapeId(@Param("shapeId") String shapeId);
}
