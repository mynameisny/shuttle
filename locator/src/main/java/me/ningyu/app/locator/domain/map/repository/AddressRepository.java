package me.ningyu.app.locator.domain.map.repository;

import me.ningyu.app.locator.domain.map.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, String>, QuerydslPredicateExecutor<Address>
{
    Optional<Address> findByCode(String code);

    void deleteByCode(String code);
}
