package faang.school.analytics.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.analytics.dto.premium.BoughtPremiumEventDto;
import faang.school.analytics.mapper.AnalyticsEventMapper;
import faang.school.analytics.model.AnalyticsEvent;
import faang.school.analytics.model.EventType;
import faang.school.analytics.service.AnalyticsEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BoughtPremiumEventListener extends
        AbstractEventListener<BoughtPremiumEventDto> implements
        MessageListener {

    public BoughtPremiumEventListener(
            @Value("${spring.data.redis.channel.bought-premium}") String channelName,
            AnalyticsEventService analyticsEventService,
            AnalyticsEventMapper analyticsEventMapper,
            ObjectMapper objectMapper) {
        super(channelName, objectMapper, analyticsEventService, analyticsEventMapper);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        handleEvent(message, BoughtPremiumEventDto.class, event -> {
            AnalyticsEvent analyticsEvent = analyticsEventMapper.toEntity(event);
            analyticsEvent.setEventType(EventType.BOUGHT_PREMIUM);
            analyticsEventService.saveEvent(analyticsEvent);
            log.info("Event data received: {}", event);
        });
    }
}
