package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long>, PagingAndSortingRepository<Organization, Long>, JpaSpecificationExecutor<Organization>
{
    Optional<Organization> findByCode(String code);

    boolean existsByCode(String code);

    List<Organization> findByNameContaining(String name);
}
