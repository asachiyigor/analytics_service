package faang.school.analytics.service;

import faang.school.analytics.dto.AnalyticsEventDto;
import faang.school.analytics.model.AnalyticsEvent;
import faang.school.analytics.model.EventType;
import faang.school.analytics.model.Interval;

import java.time.LocalDateTime;

import java.util.List;

public interface AnalyticsEventService {

    AnalyticsEvent saveEvent(AnalyticsEvent event);

    List<AnalyticsEventDto> getAnalytics(long receiverId, EventType eventType, Interval interval, LocalDateTime from,
                                         LocalDateTime to);
}