package me.ningyu.app.locator.domain.vehicle.repository;

import me.ningyu.app.locator.domain.vehicle.entity.Brand;
import me.ningyu.app.locator.domain.vehicle.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand, String>, QuerydslPredicateExecutor<Brand>
{}
