package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.gtfs.FareLegRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface FareLegRuleRepository extends JpaRepository<FareLegRule, Long>, PagingAndSortingRepository<FareLegRule, Long>, JpaSpecificationExecutor<FareLegRule>
{
}
