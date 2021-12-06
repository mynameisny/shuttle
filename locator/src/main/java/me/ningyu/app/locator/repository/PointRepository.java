package me.ningyu.app.locator.repository;

import me.ningyu.app.locator.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PointRepository extends JpaRepository<Point, String>, QuerydslPredicateExecutor<Point>
{}
