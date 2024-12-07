package faang.school.analytics.mapper;

import faang.school.analytics.dto.AnalyticsEventDto;
import faang.school.analytics.dto.SearchAppearanceEventDto;
import faang.school.analytics.model.AnalyticsEvent;
import faang.school.analytics.model.EventType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = EventType.class)
public interface AnalyticsEventMapper {

    AnalyticsEventDto toDto(AnalyticsEvent analyticsEvent);

    @Mapping(target = "receiverId", source = "foundUserId")
    @Mapping(target = "actorId", source = "requesterId")
    @Mapping(target = "eventType", expression = "java(EventType.PROFILE_APPEARED_IN_SEARCH)")
    AnalyticsEvent toEntity(SearchAppearanceEventDto searchAppearanceEventDto);
}