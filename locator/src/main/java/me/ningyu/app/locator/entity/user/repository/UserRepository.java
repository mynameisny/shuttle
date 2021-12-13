package me.ningyu.app.locator.entity.user.repository;

import me.ningyu.app.locator.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository  extends JpaRepository<Point, String>, QuerydslPredicateExecutor<Point>
{}
