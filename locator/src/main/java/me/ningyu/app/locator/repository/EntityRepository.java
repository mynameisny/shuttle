package me.ningyu.app.locator.repository;

import me.ningyu.app.locator.entity.Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntityRepository extends JpaRepository<Entity, Long>
{

}
