package faang.school.analytics.filter;

import faang.school.analytics.dto.AnalyticsEventFilterDto;
import faang.school.analytics.model.AnalyticsEvent;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class AnalyticsPeriodFilter implements AnalyticsEventFilter {

    @Override
    public Stream<AnalyticsEvent> apply(Stream<AnalyticsEvent> eventStream, AnalyticsEventFilterDto filters) {
        return eventStream.filter(event ->
                event.getReceivedAt().isAfter(filters.from()) && event.getReceivedAt().isBefore(filters.to()));
    }

    @Override
    public boolean isApplicable(AnalyticsEventFilterDto filters) {
        return filters.interval() == null && filters.from() != null && filters.to() != null;
    }
}