package me.ningyu.app.locator.domain.vehicle.repository;

import me.ningyu.app.locator.domain.vehicle.entity.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelRepository extends JpaRepository<Model, String>, QuerydslPredicateExecutor<Model>
{}
