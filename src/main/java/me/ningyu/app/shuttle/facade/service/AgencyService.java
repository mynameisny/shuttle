package me.ningyu.app.shuttle.facade.service;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.shuttle.domain.entity.gtfs.Agency;
import me.ningyu.app.shuttle.domain.repo.AgencyRepository;
import me.ningyu.app.shuttle.model.agency.CreateAgencyRequest;
import me.ningyu.app.shuttle.model.agency.UpdateAgencyRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AgencyService
{
    private final AgencyRepository agencyRepository;

    @Nonnull
    public Agency createAgency(CreateAgencyRequest request)
    {
        if (agencyRepository.existsByAgencyId(request.getAgencyId()))
        {
            throw new IllegalArgumentException("机构ID已存在");
        }

        Agency agency = Agency.builder()
                .agencyId(request.getAgencyId())
                .agencyName(request.getAgencyName())
                .agencyUrl(request.getAgencyUrl())
                .agencyTimezone(request.getAgencyTimezone())
                .agencyLang(request.getAgencyLang())
                .agencyPhone(request.getAgencyPhone())
                .agencyEmail(request.getAgencyEmail())
                .agencyFareUrl(request.getAgencyFareUrl())
                .build();

        return agencyRepository.save(agency);
    }

    @Nonnull
    public Agency updateAgency(Long id, UpdateAgencyRequest request)
    {
        Agency agency = agencyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("机构不存在"));

        if (request.getAgencyName() != null)
        {
            agency.setAgencyName(request.getAgencyName());
        }
        if (request.getAgencyUrl() != null)
        {
            agency.setAgencyUrl(request.getAgencyUrl());
        }
        if (request.getAgencyTimezone() != null)
        {
            agency.setAgencyTimezone(request.getAgencyTimezone());
        }
        if (request.getAgencyLang() != null)
        {
            agency.setAgencyLang(request.getAgencyLang());
        }
        if (request.getAgencyPhone() != null)
        {
            agency.setAgencyPhone(request.getAgencyPhone());
        }
        if (request.getAgencyEmail() != null)
        {
            agency.setAgencyEmail(request.getAgencyEmail());
        }
        if (request.getAgencyFareUrl() != null)
        {
            agency.setAgencyFareUrl(request.getAgencyFareUrl());
        }

        return agencyRepository.save(agency);
    }

    public void deleteAgency(Long id)
    {
        Agency agency = agencyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("机构不存在"));
        agencyRepository.delete(agency);
    }

    @Nonnull
    public Agency getAgencyById(Long id)
    {
        return agencyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("机构不存在"));
    }

    @Nonnull
    public Agency getAgencyByAgencyId(String agencyId)
    {
        return agencyRepository.findByAgencyId(agencyId)
                .orElseThrow(() -> new IllegalArgumentException("机构不存在"));
    }

    @Nonnull
    public List<Agency> getAgenciesByAgencyName(String agencyName)
    {
        return agencyRepository.findByAgencyNameContaining(agencyName);
    }

    @Nonnull
    public List<Agency> getAllAgencies()
    {
        return agencyRepository.findAll();
    }
}