package me.ningyu.app.shuttle.iam.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IamUserRepository extends JpaRepository<IamUser, Long>
{
    Optional<IamUser> findByUsername(String username);

    boolean existsByUsername(String username);
}
