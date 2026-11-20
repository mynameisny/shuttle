package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.extension.StopPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StopPhotoRepository extends JpaRepository<StopPhoto, Long>
{
    List<StopPhoto> findByStopIdOrderBySortOrderAscIdAsc(String stopId);

    void deleteByStopId(String stopId);
}
