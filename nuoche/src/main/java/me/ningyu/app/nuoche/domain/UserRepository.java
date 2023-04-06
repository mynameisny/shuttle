package me.ningyu.app.nuoche.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String>, QuerydslPredicateExecutor<User>
{
    Optional<User> findByCode(String code);

    Optional<User> findByUserKey(String userKey);

    void deleteByCode(String code);
}
