package me.ningyu.app.shuttle.facade.service;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.shuttle.domain.entity.gtfs.Calendar;
import me.ningyu.app.shuttle.domain.entity.gtfs.CalendarDate;
import me.ningyu.app.shuttle.domain.repo.CalendarDateRepository;
import me.ningyu.app.shuttle.domain.repo.CalendarRepository;
import me.ningyu.app.shuttle.enums.ExceptionType;
import me.ningyu.app.shuttle.model.calendar.AddCalendarDateRequest;
import me.ningyu.app.shuttle.model.calendar.CreateCalendarRequest;
import me.ningyu.app.shuttle.model.calendar.UpdateCalendarRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CalendarService
{
    private final CalendarRepository calendarRepository;
    private final CalendarDateRepository calendarDateRepository;

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

    // ---------- 例外日期（calendar_dates：法定假日 / 调休 / 临时停运） ----------

    /**
     * 某服务日历的全部例外日期。
     */
    @Nonnull
    public List<CalendarDate> listCalendarDates(Long calendarId)
    {
        return calendarDateRepository.findByServiceCalendarId(calendarId);
    }

    /**
     * 给某服务日历添加一条例外日期（同日期已存在则拒绝，避免覆盖歧义）。
     */
    @Nonnull
    public CalendarDate addCalendarDate(Long calendarId, AddCalendarDateRequest request)
    {
        Calendar c = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "服务日历不存在: id=" + calendarId));
        calendarDateRepository.findByServiceCalendarIdAndDate(calendarId, request.getDate())
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "该日历已存在 " + request.getDate() + " 的例外，请先删除再改");
                });
        CalendarDate cd = CalendarDate.builder()
                .serviceCalendar(c)
                .date(request.getDate())
                .exceptionType(request.getExceptionType())
                .build();
        return calendarDateRepository.save(cd);
    }

    /**
     * 删除一条例外日期。
     */
    public void deleteCalendarDate(Long calendarDateId)
    {
        calendarDateRepository.deleteById(calendarDateId);
    }

    /**
     * 全量替换某服务日历的例外日期（日历勾选 / JSON 批量导入的统一入口）。
     *
     * <p>语义：以传入列表为目标态，diff 现有例外后应用——
     * 目标里没有的现有日期删除、目标里有的日期新增或更新类型。
     * 前端「日历勾选 + JSON 导入」合并出完整清单后一次提交，避免逐条调接口。
     *
     * @return 替换后的完整例外日期列表（按日期升序）
     */
    @Nonnull
    public List<CalendarDate> replaceCalendarDates(Long calendarId, List<AddCalendarDateRequest> requests)
    {
        Calendar c = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "服务日历不存在: id=" + calendarId));
        if (requests == null)
        {
            requests = List.of();
        }

        // 校验目标清单：日期/类型非空、无重复日期（避免同日期两种类型歧义）
        Set<LocalDate> seen = new HashSet<>();
        for (AddCalendarDateRequest r : requests)
        {
            if (r.getDate() == null)
            {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "例外日期不能为空");
            }
            if (r.getExceptionType() == null)
            {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "例外类型不能为空（REMOVED/ADDED）");
            }
            if (!seen.add(r.getDate()))
            {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "例外日期重复: " + r.getDate());
            }
        }

        Map<LocalDate, ExceptionType> target = requests.stream()
                .collect(Collectors.toMap(AddCalendarDateRequest::getDate, AddCalendarDateRequest::getExceptionType));

        List<CalendarDate> existing = calendarDateRepository.findByServiceCalendarId(calendarId);
        Map<LocalDate, CalendarDate> existingByDate = existing.stream()
                .collect(Collectors.toMap(CalendarDate::getDate, Function.identity()));

        // 1) 删除目标里没有的现有日期
        for (CalendarDate cd : existing)
        {
            if (!target.containsKey(cd.getDate()))
            {
                calendarDateRepository.delete(cd);
            }
        }

        // 2) 新增或更新目标里的日期
        for (Map.Entry<LocalDate, ExceptionType> e : target.entrySet())
        {
            CalendarDate cd = existingByDate.get(e.getKey());
            if (cd == null)
            {
                cd = CalendarDate.builder()
                        .serviceCalendar(c)
                        .date(e.getKey())
                        .exceptionType(e.getValue())
                        .build();
            }
            else
            {
                cd.setExceptionType(e.getValue());
            }
            calendarDateRepository.save(cd);
        }

        return calendarDateRepository.findByServiceCalendarId(calendarId).stream()
                .sorted(java.util.Comparator.comparing(CalendarDate::getDate))
                .collect(Collectors.toList());
    }

    // ---------- 运营日判定（calendar + calendar_dates 的消费点） ----------

    /**
     * 判定某服务日历在指定日期是否运营。
     *
     * <p>三段判定（GTFS 语义）：
     * <ol>
     *   <li>calendar_dates 例外优先：当日 REMOVED → 停运；ADDED → 运营；</li>
     *   <li>否则看起止日期：date 不在 [startDate, endDate] 内 → 停运；</li>
     *   <li>否则看星期规则：date 是周几、该日历当天是否为运营日。</li>
     * </ol>
     */
    public boolean isServiceActive(String serviceId, LocalDate date)
    {
        Calendar c = calendarRepository.findByServiceId(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("服务日历不存在: " + serviceId));
        return isServiceActive(c, date);
    }

    /**
     * 判定某日历（已加载实体）在指定日期是否运营。
     */
    public boolean isServiceActive(Calendar c, LocalDate date)
    {
        // 1. 例外日期优先
        return calendarDateRepository.findByServiceCalendarIdAndDate(c.getId(), date)
                .map(cd -> cd.getExceptionType() == ExceptionType.ADDED)
                // 2 + 3. 起止日期 + 星期规则
                .orElseGet(() -> !date.isBefore(c.getStartDate()) && !date.isAfter(c.getEndDate())
                        && weekdayActive(c, date.getDayOfWeek()));
    }

    private boolean weekdayActive(Calendar c, DayOfWeek dow)
    {
        return switch (dow)
        {
            case MONDAY -> Boolean.TRUE.equals(c.getMonday());
            case TUESDAY -> Boolean.TRUE.equals(c.getTuesday());
            case WEDNESDAY -> Boolean.TRUE.equals(c.getWednesday());
            case THURSDAY -> Boolean.TRUE.equals(c.getThursday());
            case FRIDAY -> Boolean.TRUE.equals(c.getFriday());
            case SATURDAY -> Boolean.TRUE.equals(c.getSaturday());
            case SUNDAY -> Boolean.TRUE.equals(c.getSunday());
        };
    }
}
