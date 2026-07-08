package me.ningyu.app.shuttle.facade.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.shuttle.domain.entity.Line;
import me.ningyu.app.shuttle.domain.entity.Organization;
import me.ningyu.app.shuttle.domain.repo.LineRepository;
import me.ningyu.app.shuttle.domain.repo.OrganizationRepository;
import me.ningyu.app.shuttle.domain.repo.RouteRepository;
import me.ningyu.app.shuttle.model.route.CreateLineRequest;
import me.ningyu.app.shuttle.model.route.UpdateLineRequest;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class LineService
{
    private final LineRepository lineRepository;
    private final OrganizationRepository organizationRepository;
    private final RouteRepository routeRepository;

    @NonNull
    public Line createLine(CreateLineRequest request)
    {
        Organization organization = organizationRepository.findById(request.getOrganizationId())
                .orElseThrow(() -> new IllegalArgumentException("组织不存在"));

        Line line = Line.builder()
                .organization(organization)
                .code(request.getCode())
                .name(request.getName())
                .color(request.getColor())
                .description(request.getDescription())
                .build();

        return lineRepository.save(line);
    }

    @NonNull
    public Line updateLine(Long id, UpdateLineRequest request)
    {
        Line line = lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("线路不存在"));

        line.setName(request.getName());
        line.setColor(request.getColor());
        line.setDescription(request.getDescription());

        return lineRepository.save(line);
    }

    public void deleteLine(Long id)
    {
        Line line = lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("线路不存在"));

        if (routeRepository.existsByLineId(id))
        {
            throw new IllegalStateException("该线路下存在线路方向，不能删除");
        }

        lineRepository.delete(line);
    }

    @NonNull
    public Line getLineById(Long id)
    {
        return lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("线路不存在"));
    }

    @NonNull
    public List<Line> getAllLines()
    {
        return lineRepository.findAll();
    }

    @NonNull
    public List<Line> getLinesByOrganizationId(Long organizationId)
    {
        return lineRepository.findByOrganizationId(organizationId);
    }
}
