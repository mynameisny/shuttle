package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface OrganizationRepository extends JpaRepository<Organization, Long>, PagingAndSortingRepository<Organization, Long>, JpaSpecificationExecutor<Organization>
{
}
