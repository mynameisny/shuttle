package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.gtfs.Pathway;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface PathwayRepository extends JpaRepository<Pathway, Long>, PagingAndSortingRepository<Pathway, Long>, JpaSpecificationExecutor<Pathway>
{
    Optional<Pathway> findByPathwayId(String pathwayId);

    List<Pathway> findByFromStopId(Long fromStopId);

    List<Pathway> findByToStopId(Long toStopId);

    List<Pathway> findByFromStopIdAndToStopId(Long fromStopId, Long toStopId);
}
