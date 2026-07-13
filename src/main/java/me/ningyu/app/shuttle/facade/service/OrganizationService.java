package me.ningyu.app.shuttle.facade.service;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.shuttle.domain.entity.Organization;
import me.ningyu.app.shuttle.domain.repo.OrganizationRepository;
import me.ningyu.app.shuttle.model.organization.CreateOrganizationRequest;
import me.ningyu.app.shuttle.model.organization.UpdateOrganizationRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OrganizationService
{
    private final OrganizationRepository organizationRepository;

    
    @Nonnull
    public Organization createOrganization(CreateOrganizationRequest request)
    {
        if (organizationRepository.existsByCode(request.getCode()))
        {
            throw new IllegalArgumentException("组织编码已存在");
        }

        Organization organization = Organization.builder()
                .name(request.getName())
                .code(request.getCode())
                .build();

        return organizationRepository.save(organization);
    }

    @Nonnull
    public Organization updateOrganization(Long id, UpdateOrganizationRequest request)
    {
        Organization organization = organizationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("组织不存在"));

        organization.setName(request.getName());

        return organizationRepository.save(organization);
    }

    public void deleteOrganization(Long id)
    {
        Organization organization = organizationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("组织不存在"));

        organizationRepository.delete(organization);
    }

    @Nonnull
    public Organization getOrganizationById(Long id)
    {
        return organizationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("组织不存在"));
    }

    @Nonnull
    public Organization getOrganizationByCode(String code)
    {
        return organizationRepository.findByCode(code).orElseThrow(() -> new IllegalArgumentException("组织不存在"));
    }

    @Nonnull
    public List<Organization> getOrganizationsByName(String name)
    {
        return organizationRepository.findByNameContaining(name);
    }

    @Nonnull
    public List<Organization> getAllOrganizations()
    {
        return organizationRepository.findAll();
    }
}
