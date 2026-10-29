package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.gtfs.BookingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BookingRuleRepository extends JpaRepository<BookingRule, Long>, PagingAndSortingRepository<BookingRule, Long>, JpaSpecificationExecutor<BookingRule>
{
}
