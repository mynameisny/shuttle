package me.ningyu.app.locator.domain.route.repository;

import me.ningyu.app.locator.domain.route.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteRepository extends JpaRepository<Route, String>, QuerydslPredicateExecutor<Route>
{}
