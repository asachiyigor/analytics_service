package faang.school.analytics.service;

import faang.school.analytics.dto.AnalyticsEventDto;
import faang.school.analytics.dto.AnalyticsEventFilterDto;
import faang.school.analytics.filter.AnalyticsEventFilter;
import faang.school.analytics.mapper.AnalyticsEventMapper;
import faang.school.analytics.model.AnalyticsEvent;
import faang.school.analytics.model.EventType;
import faang.school.analytics.model.Interval;
import faang.school.analytics.repository.AnalyticsEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsEventServiceImpl implements AnalyticsEventService {

  private final List<AnalyticsEventFilter> analyticsEventFilters;
  private final AnalyticsEventMapper analyticEventMapper;
  private final AnalyticsEventRepository analyticsEventRepository;

  @Override
  public void saveEvent(AnalyticsEvent event) {
    analyticsEventRepository.save(event);
    log.info("Saved analytics data into DB: {}", event);
  }

  @Override
  public List<AnalyticsEventDto> getAnalytics(long receiverId, EventType eventType,
      Interval interval, LocalDateTime from, LocalDateTime to) {
    AnalyticsEventFilterDto filters = AnalyticsEventFilterDto.builder()
        .interval(interval)
        .from(from)
        .to(to)
        .build();
    Stream<AnalyticsEvent> analyticsEvents = analyticsEventRepository.findByReceiverIdAndEventType(
        receiverId, eventType);

    return analyticsEventFilters.stream()
        .filter(filter -> filter.isApplicable(filters))
        .reduce(analyticsEvents, (stream, filter) -> filter.apply(stream, filters),
            (newStream, oldStream) -> newStream)
        .sorted(Comparator.comparing(AnalyticsEvent::getReceivedAt))
        .map(analyticEventMapper::toDto)
        .toList();
  }
}