package faang.school.analytics.filter;

import faang.school.analytics.dto.AnalyticsEventFilterDto;
import faang.school.analytics.model.AnalyticsEvent;

import java.util.stream.Stream;

public interface AnalyticsEventFilter {

    Stream<AnalyticsEvent> apply(Stream<AnalyticsEvent> eventStream, AnalyticsEventFilterDto filters);

    boolean isApplicable(AnalyticsEventFilterDto filters);
}