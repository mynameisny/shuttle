package me.ningyu.app.locator.service;

import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.locator.common.exception.NotfoundException;
import me.ningyu.app.locator.common.vo.BrandDto;
import me.ningyu.app.locator.common.vo.RecordDto;
import me.ningyu.app.locator.common.vo.StationDto;
import me.ningyu.app.locator.domain.map.entity.Station;
import me.ningyu.app.locator.domain.record.entity.Record;
import me.ningyu.app.locator.domain.record.repository.RecordRepository;
import me.ningyu.app.locator.domain.vehicle.entity.Brand;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Slf4j
public class RecordService
{
    private final RecordRepository recordRepository;

    public RecordService(RecordRepository recordRepository)
    {
        this.recordRepository = recordRepository;
    }


    public Record add(RecordDto dto)
    {
        Record entity = new Record();
        BeanUtils.copyProperties(dto, entity);
        return recordRepository.save(entity);
    }

    @Transactional
    public void remove(String id)
    {
        Optional.of(recordRepository.findById(id)).get().orElseThrow(() -> new NotfoundException(String.format("记录%s不存在", id)));
        recordRepository.deleteById(id);
    }

    @Transactional
    public Record update(String id, BrandDto dto)
    {
        Record brand = Optional.of(recordRepository.findById(id)).get().orElseThrow(() -> new RuntimeException(String.format("记录%s不存在", id)));
        BeanUtils.copyProperties(brand, dto);
        return recordRepository.save(brand);
    }

    public RecordDto get(String id)
    {
        Record record = Optional.of(recordRepository.findById(id)).get().orElseThrow(() -> new RuntimeException(String.format("记录%s不存在", id)));
        RecordDto dto = new RecordDto();
        BeanUtils.copyProperties(record, dto);
        return dto;
    }

    public Page<Record> list(Predicate predicate, Pageable pageable)
    {
        return recordRepository.findAll(predicate, pageable);
    }
}
