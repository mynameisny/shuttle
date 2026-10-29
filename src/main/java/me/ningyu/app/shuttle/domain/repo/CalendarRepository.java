package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.gtfs.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CalendarRepository extends JpaRepository<Calendar, Long>, PagingAndSortingRepository<Calendar, Long>, JpaSpecificationExecutor<Calendar>
{
    Optional<Calendar> findByServiceId(String serviceId);

    Optional<Calendar> findByCode(String code);

    List<Calendar> findByAgencyId(Long agencyId);

    List<Calendar> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate date1, LocalDate date2);
}
