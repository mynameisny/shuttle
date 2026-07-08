package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.Line;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface LineRepository extends JpaRepository<Line, Long>, PagingAndSortingRepository<Line, Long>, JpaSpecificationExecutor<Line>
{
    List<Line> findByOrganizationId(Long organizationId);
}
