package me.ningyu.app.locator.service;

import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.locator.common.vo.StationDto;
import me.ningyu.app.locator.domain.map.entity.Station;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class StationService
{

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

    public List<StationDto> list(Predicate predicate, Pageable pageable, Sort sort)
    {
        return null;
    }
}
