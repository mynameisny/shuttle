package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.gtfs.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface LevelRepository extends JpaRepository<Level, Long>, PagingAndSortingRepository<Level, Long>, JpaSpecificationExecutor<Level>
{
    Optional<Level> findByLevelId(String levelId);
}
