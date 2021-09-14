package me.ningyu.app.shuttleserver.repository;

import me.ningyu.app.shuttleserver.entity.Line;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LineRepository extends JpaRepository<Line, Long>
{}
