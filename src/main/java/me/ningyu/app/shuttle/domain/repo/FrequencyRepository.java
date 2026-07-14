package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.Frequency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface FrequencyRepository extends JpaRepository<Frequency, Long>, PagingAndSortingRepository<Frequency, Long>, JpaSpecificationExecutor<Frequency>
{
    List<Frequency> findByTripId(Long tripId);
}
