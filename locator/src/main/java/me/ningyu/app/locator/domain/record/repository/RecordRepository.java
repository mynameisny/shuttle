package me.ningyu.app.locator.domain.record.repository;

import me.ningyu.app.locator.domain.map.entity.Point;
import me.ningyu.app.locator.domain.record.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordRepository extends JpaRepository<Record, String>, QuerydslPredicateExecutor<Record>
{}
