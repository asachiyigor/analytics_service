package faang.school.analytics.service;

import faang.school.analytics.model.AnalyticsEvent;
import faang.school.analytics.repository.AnalyticsEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsEventServiceImpl implements AnalyticsEventService {

    private final AnalyticsEventRepository analyticsEventRepository;

    @Override
    public void saveEvent(AnalyticsEvent event) {
        analyticsEventRepository.save(event);
        log.info("Saved follower event: {}", event);

    }
}
