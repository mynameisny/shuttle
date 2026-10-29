package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.gtfs.FareProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface FareProductRepository extends JpaRepository<FareProduct, Long>, PagingAndSortingRepository<FareProduct, Long>, JpaSpecificationExecutor<FareProduct>
{
}
