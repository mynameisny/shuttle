package me.ningyu.app.locator.service;

import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.locator.common.exception.NotfoundException;
import me.ningyu.app.locator.common.vo.BrandDto;
import me.ningyu.app.locator.domain.map.entity.Station;
import me.ningyu.app.locator.domain.vehicle.entity.Brand;
import me.ningyu.app.locator.domain.vehicle.repository.BrandRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

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

    @Transactional
    public void remove(String id)
    {
        Optional.of(brandRepository.findById(id)).get().orElseThrow(() -> new NotfoundException(String.format("品牌%s不存在", id)));
        brandRepository.deleteById(id);
    }

    public Brand update(String id, BrandDto dto)
    {
        Brand brand = Optional.of(brandRepository.findById(id)).get().orElseThrow(() -> new RuntimeException(String.format("品牌%s不存在", id)));
        BeanUtils.copyProperties(brand, dto);
        return brandRepository.save(brand);
    }
}
