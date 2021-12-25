package me.ningyu.app.locator.domain.user.repository;

import me.ningyu.app.locator.domain.map.entity.Point;
import me.ningyu.app.locator.domain.user.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverRepository extends JpaRepository<Driver, String>, QuerydslPredicateExecutor<Driver>
{}
