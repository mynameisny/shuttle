package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.gtfs.Network;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface NetworkRepository extends JpaRepository<Network, Long>, PagingAndSortingRepository<Network, Long>, JpaSpecificationExecutor<Network>
{
}
