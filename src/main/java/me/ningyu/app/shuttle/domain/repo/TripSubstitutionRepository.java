package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.extension.TripSubstitution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TripSubstitutionRepository extends JpaRepository<TripSubstitution, Long>
{
    /**
     * 按班次查代班（某班次去/返的全部代班记录）。
     */
    List<TripSubstitution> findByTripTripId(String tripId);

    /**
     * 按班次 + 日期查代班（某天该班次是否有人代班）。
     */
    List<TripSubstitution> findByTripTripIdAndSubstituteDate(String tripId, LocalDate substituteDate);

    /**
     * 按代班司机查其代班记录（一名司机可代多个班次）。
     */
    List<TripSubstitution> findBySubstituteDriverId(Long substituteDriverId);
}
