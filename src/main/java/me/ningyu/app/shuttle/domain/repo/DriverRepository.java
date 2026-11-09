package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.extension.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long>
{
}
