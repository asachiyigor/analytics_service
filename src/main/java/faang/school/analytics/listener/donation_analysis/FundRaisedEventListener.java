package faang.school.analytics.listener.donation_analysis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.analytics.dto.FundRaisedEvent;
import faang.school.analytics.mapper.AnalyticsEventMapper;
import faang.school.analytics.model.AnalyticsEvent;
import faang.school.analytics.service.AnalyticsEventService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class FundRaisedEventListener implements MessageListener {

    private final ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(FundRaisedEventListener.class);
    private final AnalyticsEventMapper analyticsEventMapper;
    private final AnalyticsEventService analyticsEventService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            FundRaisedEvent event = objectMapper.readValue(message.getBody(), FundRaisedEvent.class);
            log.info("Received event: {}", event);

            AnalyticsEvent analyticsEvent = analyticsEventMapper.toEntity(event);
            analyticsEventService.saveEvent(analyticsEvent);
            log.info("Saved event to database: {}", analyticsEvent);
            log.info("Message handling completed.");

        } catch (IOException e) {
            String messageBody = new String(message.getBody());
            log.error("Failed to parse event: {}", messageBody, e);
            throw new RuntimeException("Error parsing FundRaisedEvent", e);
        }
    }
}