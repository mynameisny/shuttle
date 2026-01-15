package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.Stop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface StopRepository extends JpaRepository<Stop, Long>, PagingAndSortingRepository<Stop, Long>, JpaSpecificationExecutor<Stop>
{
}
