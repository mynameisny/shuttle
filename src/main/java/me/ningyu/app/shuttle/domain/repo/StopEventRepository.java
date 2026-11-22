package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.extension.StopEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StopEventRepository extends JpaRepository<StopEvent, Long>
{
    /**
     * 全部到发记录（按日期倒序、站序正序）。
     */
    List<StopEvent> findAllByOrderByEventDateDescStopSequenceAsc();

    /**
     * 某班次的全部到发记录（按日期倒序、站序正序）。
     */
    List<StopEvent> findByTripTripIdOrderByEventDateDescStopSequenceAsc(String tripId);

    /**
     * 某运营日的全部到发记录（按站序正序）。
     */
    List<StopEvent> findByEventDateOrderByStopSequenceAsc(LocalDate eventDate);

    /**
     * 某班次在某运营日的到发记录（按站序正序）。
     */
    List<StopEvent> findByTripTripIdAndEventDateOrderByStopSequenceAsc(String tripId, LocalDate eventDate);

    /**
     * 某班次在某日期区间内的到发记录（按日期正序、站序正序）。
     */
    List<StopEvent> findByTripTripIdAndEventDateBetweenOrderByEventDateAscStopSequenceAsc(
            String tripId, LocalDate startDate, LocalDate endDate);

    /**
     * 定位唯一记录：班次 + 日期 + 站序（用于上报 upsert）。
     */
    Optional<StopEvent> findByTripTripIdAndEventDateAndStopSequence(String tripId, LocalDate eventDate, Integer stopSequence);
}
