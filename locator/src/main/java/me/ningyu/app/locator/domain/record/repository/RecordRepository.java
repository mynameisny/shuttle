package me.ningyu.app.locator.domain.record.repository;

import me.ningyu.app.locator.domain.record.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordRepository extends JpaRepository<Record, String>
{}
