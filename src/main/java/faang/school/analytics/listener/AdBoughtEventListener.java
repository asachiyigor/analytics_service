package faang.school.analytics.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.analytics.dto.AdBoughtEventDto;
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
public class AdBoughtEventListener extends AbstractEventListener<AdBoughtEventDto> implements MessageListener {

    public AdBoughtEventListener(
            @Value("${spring.data.redis.channel.ad-bought-channel.name}")
            String channelName,
            ObjectMapper objectMapper,
            AnalyticsEventService analyticsEventService,
            AnalyticsEventMapper analyticsEventMapper) {
        super(channelName, objectMapper, analyticsEventService, analyticsEventMapper);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        handleEvent(message, AdBoughtEventDto.class, event -> {
            AnalyticsEvent analyticsEvent = analyticsEventMapper.toEntity(event);
            analyticsEvent.setEventType(EventType.POST_AD_BOUGHT);
            analyticsEventService.saveEvent(analyticsEvent);
            log.info("Processed event: {}", analyticsEvent);
        });
    }
}