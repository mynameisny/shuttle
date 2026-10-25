package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.FeedInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface FeedInfoRepository extends JpaRepository<FeedInfo, Long>, PagingAndSortingRepository<FeedInfo, Long>, JpaSpecificationExecutor<FeedInfo>
{
    Optional<FeedInfo> findByAgencyId(Long agencyId);
}
