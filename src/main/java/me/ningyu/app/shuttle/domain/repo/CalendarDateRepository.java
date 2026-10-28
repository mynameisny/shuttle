package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.gtfs.CalendarDate;
import me.ningyu.app.shuttle.enums.ExceptionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDate;
import java.util.List;

public interface CalendarDateRepository extends JpaRepository<CalendarDate, Long>, PagingAndSortingRepository<CalendarDate, Long>, JpaSpecificationExecutor<CalendarDate>
{
    List<CalendarDate> findByServiceCalendarId(Long serviceCalendarId);

    List<CalendarDate> findByDate(LocalDate date);

    List<CalendarDate> findByServiceCalendarIdAndExceptionType(Long serviceCalendarId, ExceptionType exceptionType);
}
