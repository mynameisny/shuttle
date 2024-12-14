package me.ningyu.app.easymonger.domain.coupon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, String>, QuerydslPredicateExecutor<Coupon>
{
    Optional<Coupon> findByCode(String code);
}
