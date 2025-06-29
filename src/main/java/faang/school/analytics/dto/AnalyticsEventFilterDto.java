package faang.school.analytics.dto;

import faang.school.analytics.model.Interval;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AnalyticsEventFilterDto(
        Interval interval,
        LocalDateTime from,
        LocalDateTime to
) {
}