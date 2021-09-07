package me.ningyu.app.shuttleserver.repository;

import me.ningyu.app.shuttleserver.entity.CitizenIDCard;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CitizenIDCardRepository extends JpaRepository<CitizenIDCard, Long>
{
}
