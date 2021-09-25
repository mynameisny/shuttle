package me.ningyu.app.shuttleserver.repository;

import me.ningyu.app.shuttleserver.entity.track.Stop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StopRepository extends JpaRepository<Stop, Long>
{}
