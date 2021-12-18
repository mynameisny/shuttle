package me.ningyu.app.locator.domain.device.repository;

import me.ningyu.app.locator.domain.device.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends JpaRepository<Device, String>, QuerydslPredicateExecutor<Device>
{
}
