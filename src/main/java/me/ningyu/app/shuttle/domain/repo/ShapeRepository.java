package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.gtfs.Shape;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ShapeRepository extends JpaRepository<Shape, Long>, PagingAndSortingRepository<Shape, Long>, JpaSpecificationExecutor<Shape>
{
    List<Shape> findByShapeIdOrderByShapePtSequenceAsc(String shapeId);
}
