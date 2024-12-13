package faang.school.analytics.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.analytics.dto.SearchAppearanceEventDto;
import faang.school.analytics.exception.SearchAppearanceDeserializationException;
import faang.school.analytics.mapper.AnalyticsEventMapper;
import faang.school.analytics.model.AnalyticsEvent;
import faang.school.analytics.service.AnalyticsEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchAppearanceEventListener implements MessageListener {
    private final AnalyticsEventMapper analyticsEventMapper;
    private final AnalyticsEventService analyticsEventService;

    public void onMessage(Message message, byte[] pattern) {
        SearchAppearanceEventDto inputEvent = null;
        try {
            inputEvent = new ObjectMapper().readValue(message.getBody(), SearchAppearanceEventDto.class);
        } catch (IOException e) {
            log.error("Error deserializing JSON to SearchAppearanceEventDto", e);
            throw new SearchAppearanceDeserializationException("Failed to deserialize search appearance event", e);
        }
        SearchAppearanceEventDto event = SearchAppearanceEventDto.builder()
                .foundUserId(inputEvent.getFoundUserId())
                .requesterId(inputEvent.getRequesterId())
                .build();
        AnalyticsEvent analyticsEvent = analyticsEventMapper.toEntity(event);
        analyticsEvent.setReceivedAt(LocalDateTime.now());
        analyticsEventService.saveEvent(analyticsEvent);
    }
}