package me.ningyu.app.locator.service;

import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.locator.common.vo.StationDto;
import me.ningyu.app.locator.domain.map.entity.Station;
import me.ningyu.app.locator.domain.map.repository.StationRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Slf4j
public class StationService
{
    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository)
    {
        this.stationRepository = stationRepository;
    }

    @Transactional
    public Station add(StationDto dto)
    {
        Station entity = new Station();
        BeanUtils.copyProperties(dto, entity);
        return stationRepository.save(entity);
    }

    @Transactional
    public void remove(String id)
    {
        Station station = Optional.of(stationRepository.findById(id)).get().orElseThrow(() -> new RuntimeException(String.format("站点%s不存在", id)));
        stationRepository.deleteById(id);
    }

    @Transactional
    public Station update(String id, StationDto dto)
    {
        Station station = Optional.of(stationRepository.findById(id)).get().orElseThrow(() -> new RuntimeException(String.format("站点%s不存在", id)));
        BeanUtils.copyProperties(station, dto);
        return stationRepository.save(station);
    }

    public StationDto get(String id)
    {
        Station station = Optional.of(stationRepository.findById(id)).get().orElseThrow(() -> new RuntimeException(String.format("站点%s不存在", id)));
        StationDto dto = new StationDto();
        BeanUtils.copyProperties(station, dto);
        return dto;
    }

    public Page<Station> list(Predicate predicate, Pageable pageable)
    {
        return stationRepository.findAll(predicate, pageable);
    }
}
