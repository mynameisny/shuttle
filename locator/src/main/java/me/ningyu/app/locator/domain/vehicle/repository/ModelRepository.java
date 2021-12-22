package me.ningyu.app.locator.domain.vehicle.repository;

import me.ningyu.app.locator.domain.vehicle.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelRepository extends JpaRepository<Repository, String>, QuerydslPredicateExecutor<Repository>
{}
