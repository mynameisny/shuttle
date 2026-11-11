package me.ningyu.app.shuttle.facade.service;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.shuttle.domain.entity.gtfs.Calendar;
import me.ningyu.app.shuttle.domain.repo.CalendarRepository;
import me.ningyu.app.shuttle.model.calendar.CreateCalendarRequest;
import me.ningyu.app.shuttle.model.calendar.UpdateCalendarRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CalendarService
{
    private final CalendarRepository calendarRepository;

    @Nonnull
    public Calendar createCalendar(CreateCalendarRequest request)
    {
        if (calendarRepository.findByServiceId(request.getServiceId()).isPresent())
        {
            throw new IllegalArgumentException("服务日历已存在: " + request.getServiceId());
        }

        Calendar c = Calendar.builder()
                .serviceId(request.getServiceId())
                .monday(Boolean.TRUE.equals(request.getMonday()))
                .tuesday(Boolean.TRUE.equals(request.getTuesday()))
                .wednesday(Boolean.TRUE.equals(request.getWednesday()))
                .thursday(Boolean.TRUE.equals(request.getThursday()))
                .friday(Boolean.TRUE.equals(request.getFriday()))
                .saturday(Boolean.TRUE.equals(request.getSaturday()))
                .sunday(Boolean.TRUE.equals(request.getSunday()))
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        return calendarRepository.save(c);
    }

    @Nonnull
    public Calendar updateCalendar(Long id, UpdateCalendarRequest request)
    {
        Calendar c = calendarRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("服务日历不存在: id=" + id));

        if (request.getMonday() != null) c.setMonday(request.getMonday());
        if (request.getTuesday() != null) c.setTuesday(request.getTuesday());
        if (request.getWednesday() != null) c.setWednesday(request.getWednesday());
        if (request.getThursday() != null) c.setThursday(request.getThursday());
        if (request.getFriday() != null) c.setFriday(request.getFriday());
        if (request.getSaturday() != null) c.setSaturday(request.getSaturday());
        if (request.getSunday() != null) c.setSunday(request.getSunday());
        if (request.getStartDate() != null) c.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) c.setEndDate(request.getEndDate());

        return calendarRepository.save(c);
    }

    public void deleteCalendar(Long id)
    {
        Calendar c = calendarRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("服务日历不存在: id=" + id));
        calendarRepository.delete(c);
    }

    @Nonnull
    public Calendar getCalendarById(Long id)
    {
        return calendarRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("服务日历不存在: id=" + id));
    }

    @Nonnull
    public Calendar getCalendarByServiceId(String serviceId)
    {
        return calendarRepository.findByServiceId(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("服务日历不存在: serviceId=" + serviceId));
    }

    @Nonnull
    public List<Calendar> getAllCalendars()
    {
        return calendarRepository.findAll();
    }

    /**
     * 查询某日期生效的日历（startDate <= date <= endDate）。
     */
    @Nonnull
    public List<Calendar> getCalendarsActiveOn(LocalDate date)
    {
        return calendarRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqual(date, date);
    }
}
