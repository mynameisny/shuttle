package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.gtfs.FareTransferRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface FareTransferRuleRepository extends JpaRepository<FareTransferRule, Long>, PagingAndSortingRepository<FareTransferRule, Long>, JpaSpecificationExecutor<FareTransferRule>
{
}
