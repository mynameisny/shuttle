package me.ningyu.app.easymonger.domain.coupon;

import me.ningyu.app.easymonger.domain.auth.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String>, QuerydslPredicateExecutor<Role>
{
    Optional<Role> findByCode(String code);
}
