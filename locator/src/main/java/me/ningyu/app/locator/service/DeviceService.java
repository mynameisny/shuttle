package me.ningyu.app.locator.service;

import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.locator.common.vo.DeviceDto;
import me.ningyu.app.locator.domain.device.entity.Device;
import me.ningyu.app.locator.domain.device.repository.DeviceRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DeviceService
{
    private DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository)
    {
        this.deviceRepository = deviceRepository;
    }

    public Device add(DeviceDto dto)
    {
        Device entity = new Device();
        BeanUtils.copyProperties(dto, entity);
        return deviceRepository.save(entity);
    }
}
