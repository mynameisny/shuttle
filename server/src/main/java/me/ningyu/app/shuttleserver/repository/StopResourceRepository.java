package me.ningyu.app.shuttleserver.repository;


import me.ningyu.app.shuttleserver.entity.pathway.StopResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StopResourceRepository extends JpaRepository<StopResource, Long>
{}
