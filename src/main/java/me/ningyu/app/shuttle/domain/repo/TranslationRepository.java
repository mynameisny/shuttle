package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.gtfs.Translation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TranslationRepository extends JpaRepository<Translation, Long>, PagingAndSortingRepository<Translation, Long>, JpaSpecificationExecutor<Translation>
{
}
