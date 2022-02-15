package me.ningyu.app.locator.service;

import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.locator.common.vo.BrandDto;
import me.ningyu.app.locator.domain.vehicle.entity.Brand;
import me.ningyu.app.locator.domain.vehicle.repository.BrandRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BrandService
{
    private BrandRepository brandRepository;

    public Brand add(BrandDto dto)
    {
        Brand entity = new Brand();
        BeanUtils.copyProperties(dto, entity);
        return brandRepository.save(entity);
    }
}
