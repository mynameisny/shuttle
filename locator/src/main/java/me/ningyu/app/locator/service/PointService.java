package me.ningyu.app.locator.service;

import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.locator.entity.Point;
import me.ningyu.app.locator.repository.PointRepository;
import me.ningyu.app.locator.vo.PointDto;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PointService
{
    private final PointRepository pointRepository;

    public PointService(PointRepository pointRepository)
    {
        this.pointRepository = pointRepository;
    }


    public Point save(PointDto pointDto)
    {
        Point entity = new Point();
        BeanUtils.copyProperties(pointDto, entity);
        return pointRepository.save(entity);
    }
}
