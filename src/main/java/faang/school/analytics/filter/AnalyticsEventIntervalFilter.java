package faang.school.analytics.filter;

import faang.school.analytics.dto.AnalyticsEventFilterDto;
import faang.school.analytics.model.AnalyticsEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import java.util.stream.Stream;

@Component
public class AnalyticsEventIntervalFilter implements AnalyticsEventFilter {

    @Override
    public Stream<AnalyticsEvent> apply(Stream<AnalyticsEvent> eventStream, AnalyticsEventFilterDto filters) {
        return eventStream.filter(event ->
                event.getReceivedAt().plus(filters.interval().getTimeInterval())
                        .isAfter(LocalDateTime.now()));
    }

    @Override
    public boolean isApplicable(AnalyticsEventFilterDto filters) {
        return filters.interval() != null;
    }
}