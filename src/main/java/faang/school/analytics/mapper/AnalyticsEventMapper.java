package faang.school.analytics.mapper;

import faang.school.analytics.dto.AnalyticsEventDto;
import faang.school.analytics.listener.projectview.ProjectViewEvent;
import faang.school.analytics.model.AnalyticsEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AnalyticsEventMapper {

    AnalyticsEventDto toDto(AnalyticsEvent analyticsEvent);

    AnalyticsEvent toEntity(AnalyticsEventDto analyticsEventDto);

    @Mapping(source = "createdAt", target = "receivedAt")
    @Mapping(source = "projectId", target = "receiverId")
    @Mapping(source = "userId", target = "actorId")
    AnalyticsEvent fromProjectViewToAnalyticsEvent(ProjectViewEvent event);
}