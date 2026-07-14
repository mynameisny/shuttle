package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.FareRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface FareRuleRepository extends JpaRepository<FareRule, Long>, PagingAndSortingRepository<FareRule, Long>, JpaSpecificationExecutor<FareRule>
{
    List<FareRule> findByFareAttributeId(Long fareAttributeId);

    List<FareRule> findByLineId(Long lineId);

    List<FareRule> findByOriginIdAndDestinationId(String originId, String destinationId);
}
