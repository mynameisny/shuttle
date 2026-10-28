package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.gtfs.FareAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface FareAttributeRepository extends JpaRepository<FareAttribute, Long>, PagingAndSortingRepository<FareAttribute, Long>, JpaSpecificationExecutor<FareAttribute>
{
    Optional<FareAttribute> findByCode(String code);

    List<FareAttribute> findByAgencyId(Long agencyId);
}
