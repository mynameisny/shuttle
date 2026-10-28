package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.gtfs.ServiceCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ServiceCalendarRepository extends JpaRepository<ServiceCalendar, Long>, PagingAndSortingRepository<ServiceCalendar, Long>, JpaSpecificationExecutor<ServiceCalendar>
{
    Optional<ServiceCalendar> findByServiceId(String serviceId);

    Optional<ServiceCalendar> findByCode(String code);

    List<ServiceCalendar> findByAgencyId(Long agencyId);

    List<ServiceCalendar> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate date1, LocalDate date2);
}
