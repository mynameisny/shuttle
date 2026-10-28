package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.gtfs.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface TransferRepository extends JpaRepository<Transfer, Long>, PagingAndSortingRepository<Transfer, Long>, JpaSpecificationExecutor<Transfer>
{
    List<Transfer> findByFromStopId(Long fromStopId);

    List<Transfer> findByToStopId(Long toStopId);

    List<Transfer> findByFromStopIdAndToStopId(Long fromStopId, Long toStopId);

    List<Transfer> findByFromRouteId(Long fromRouteId);

    List<Transfer> findByToRouteId(Long toRouteId);
}
