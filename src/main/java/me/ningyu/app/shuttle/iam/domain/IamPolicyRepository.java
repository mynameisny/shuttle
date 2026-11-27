package me.ningyu.app.shuttle.iam.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IamPolicyRepository extends JpaRepository<IamPolicy, Long>
{
    List<IamPolicy> findAllByOrderByIdAsc();
}
