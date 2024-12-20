package faang.school.analytics.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.analytics.dto.AnalyticsEventDto;
import faang.school.analytics.mapper.AnalyticsEventMapper;
import faang.school.analytics.model.AnalyticsEvent;
import faang.school.analytics.model.EventType;
import faang.school.analytics.service.AnalyticsEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LikeEventListener extends AbstractEventListener<AnalyticsEventDto> implements MessageListener {
    public LikeEventListener(
            AnalyticsEventService analyticsEventService,
            AnalyticsEventMapper analyticsEventMapper,
            ObjectMapper objectMapper) {
        super(analyticsEventService, analyticsEventMapper, objectMapper);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        AnalyticsEventDto analyticsEventDto = handleEvent(message, AnalyticsEventDto.class);
        AnalyticsEvent analyticsEvent = analyticsEventMapper.toEntity(analyticsEventDto);
        analyticsEvent.setEventType(EventType.POST_LIKE);
        analyticsEventService.saveEvent(analyticsEvent);
        log.info("Processed event: {}", analyticsEvent);
    }
}

