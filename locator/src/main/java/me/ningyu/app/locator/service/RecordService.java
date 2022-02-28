package me.ningyu.app.locator.service;

import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.locator.common.vo.RecordDto;
import me.ningyu.app.locator.domain.map.entity.Station;
import me.ningyu.app.locator.domain.record.entity.Record;
import me.ningyu.app.locator.domain.record.repository.RecordRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

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
}
