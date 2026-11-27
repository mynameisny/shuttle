package me.ningyu.app.shuttle.iam.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IamRoleRepository extends JpaRepository<IamRole, Long>
{
    Optional<IamRole> findByCode(String code);

    boolean existsByCode(String code);
}
