package me.ningyu.app.locator.service;

import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.locator.common.enums.StationStatus;
import me.ningyu.app.locator.common.exception.NotfoundException;
import me.ningyu.app.locator.common.vo.AddressDto;
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
    public StationDto add(StationDto dto)
    {
        Station entity = new Station();
        BeanUtils.copyProperties(dto, entity);
        entity.setStatus(StationStatus.NORMAL);

        Station saved = stationRepository.save(entity);
        return StationDto.buildFromEntity(saved);
    }

    @Transactional
    public void remove(String code)
    {
        Station station = stationRepository.findByCode(code).orElseThrow(() -> new NotfoundException(String.format("站点(%s)不存在", code)));

        stationRepository.deleteByCode(code);

        log.info("站点（{}）已被删除", station);
    }

    @Transactional
    public StationDto update(String code, StationDto dto)
    {
        Station station = stationRepository.findByCode(code).orElseThrow(() -> new NotfoundException(String.format("站点(%s)不存在", code)));
        BeanUtils.copyProperties(station, dto);

        Station saved = stationRepository.save(station);
        return StationDto.buildFromEntity(saved);
    }

    public StationDto get(String code)
    {
        Station station = stationRepository.findByCode(code).orElseThrow(() -> new NotfoundException(String.format("站点(%s)不存在", code)));
        return StationDto.buildFromEntity(station);
    }

    public Page<StationDto> list(Predicate predicate, Pageable pageable)
    {
        Page<Station> list = stationRepository.findAll(predicate, pageable);
        return list.map(StationDto::buildFromEntity);
    }
}
