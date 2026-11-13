package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.extension.Captain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaptainRepository extends JpaRepository<Captain, Long>
{
}
