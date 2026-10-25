package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.Agency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface AgencyRepository extends JpaRepository<Agency, Long>, PagingAndSortingRepository<Agency, Long>, JpaSpecificationExecutor<Agency>
{
    Optional<Agency> findByAgencyId(String agencyId);

    boolean existsByAgencyId(String agencyId);

    List<Agency> findByAgencyNameContaining(String agencyName);
}