package faang.school.analytics.mapper;

import faang.school.analytics.dto.AnalyticsEventDto;
import faang.school.analytics.dto.FundRaisedEvent;
import faang.school.analytics.dto.premium.BoughtPremiumEventDto;
import faang.school.analytics.dto.recommendation.RecommendationEventDto;
import faang.school.analytics.model.AnalyticsEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AnalyticsEventMapper {

  AnalyticsEventDto toDto(AnalyticsEvent analyticsEvent);

  AnalyticsEvent toEntity(AnalyticsEventDto analyticsEventDto);

  @Mapping(target = "receiverId", source = "userId")
  @Mapping(target = "actorId", source = "userId")
  @Mapping(target = "receivedAt", source = "receivedAt")
  AnalyticsEvent toEntity(BoughtPremiumEventDto eventDto);

  @Mapping(target = "receiverId", source = "receiverId")
  @Mapping(target = "actorId", source = "authorId")
  AnalyticsEvent toEntity(RecommendationEventDto eventDto);

    @Mapping(source = "userId", target = "actorId")
    @Mapping(source = "amount", target = "receiverId")
    @Mapping(source = "donationTime", target = "receivedAt")
    AnalyticsEvent toEntity(FundRaisedEvent analyticsEventDto);
}