package me.ningyu.app.shuttleserver.repository;

import me.ningyu.app.shuttleserver.entity.human.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<Driver, Long>
{
}
