package me.ningyu.app.locator.service;

import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.locator.common.exception.NotfoundException;
import me.ningyu.app.locator.common.vo.AddressDto;
import me.ningyu.app.locator.domain.map.entity.Address;
import me.ningyu.app.locator.domain.map.repository.AddressRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Slf4j
public class AddressService
{
    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository)
    {
        this.addressRepository = addressRepository;
    }

    @Transactional
    public AddressDto add(AddressDto dto)
    {
        Address entity = new Address();
        BeanUtils.copyProperties(dto, entity);

        Address saved = addressRepository.save(entity);
        return AddressDto.buildFromEntity(saved);
    }

    @Transactional
    public void remove(String code)
    {
        Address station = addressRepository.findByCode(code).orElseThrow(() -> new NotfoundException(String.format("地址(%s)不存在", code)));

        addressRepository.deleteByCode(code);

        log.info("地址（{}）已被删除", station);
    }

    @Transactional
    public AddressDto update(String code, AddressDto dto)
    {
        Address address = addressRepository.findByCode(code).orElseThrow(() -> new NotfoundException(String.format("地址(%s)不存在", code)));
        BeanUtils.copyProperties(address, dto);

        Address saved = addressRepository.save(address);
        return AddressDto.buildFromEntity(saved);
    }

    public AddressDto get(String code)
    {
        Address address = addressRepository.findByCode(code).orElseThrow(() -> new NotfoundException(String.format("地址(%s)不存在", code)));
        return AddressDto.buildFromEntity(address);
    }

    public Page<Address> list(Predicate predicate, Pageable pageable)
    {
        return addressRepository.findAll(predicate, pageable);
    }
}
