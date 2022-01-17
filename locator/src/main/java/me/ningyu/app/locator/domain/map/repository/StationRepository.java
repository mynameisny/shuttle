package me.ningyu.app.locator.domain.map.repository;

import me.ningyu.app.locator.domain.map.entity.Point;
import me.ningyu.app.locator.domain.map.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StationRepository extends JpaRepository<Station, String>, QuerydslPredicateExecutor<Station>
{}
