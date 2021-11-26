package me.ningyu.app.locator.service;

import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.locator.entity.Point;
import me.ningyu.app.locator.repository.PointRepository;
import me.ningyu.app.locator.vo.PointDto;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Slf4j
public class PointService
{
    private final PointRepository pointRepository;

    public PointService(PointRepository pointRepository)
    {
        this.pointRepository = pointRepository;
    }


    @Transactional
    public Point save(PointDto pointDto)
    {
        Point entity = new Point();
        BeanUtils.copyProperties(pointDto, entity);
        return pointRepository.save(entity);
    }

    @Transactional
    public void remove(Long id)
    {
        pointRepository.deleteById(id);
    }

    public Point findById(Long id)
    {
        return pointRepository.findById(id).get();
    }

    public Page<Point> list(Predicate predicate, Pageable pageable)
    {
        return pointRepository.findAll(predicate, pageable);
    }
}
