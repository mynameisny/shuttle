package me.ningyu.app.easymonger.domain.coupon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, String>, QuerydslPredicateExecutor<Account>
{
}
