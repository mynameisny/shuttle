package me.ningyu.app.locator.service;

import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.locator.common.vo.StationDto;
import me.ningyu.app.locator.domain.map.entity.Station;
import me.ningyu.app.locator.domain.map.repository.StationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StationService
{
    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository)
    {
        this.stationRepository = stationRepository;
    }


    public Station add(StationDto dto)
    {
        return null;
    }

    public void remove(String id)
    {
    }

    public Station update(String id, StationDto dto)
    {
        return null;
    }

    public StationDto get(String id)
    {
        return null;
    }

    public Page<Station> list(Predicate predicate, Pageable pageable)
    {
        return stationRepository.findAll(predicate, pageable);
    }
}
