package me.ningyu.app.shuttleserver.repository;

import me.ningyu.app.shuttleserver.entity.pathway.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long>
{}
