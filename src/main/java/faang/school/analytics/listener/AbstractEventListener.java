package faang.school.analytics.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.analytics.dto.AdBoughtEventDto;
import faang.school.analytics.mapper.AnalyticsEventMapper;
import faang.school.analytics.service.AnalyticsEventService;
import org.springframework.data.redis.connection.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import java.io.IOException;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventListener<T> {
    private final String channelName;
    private final ObjectMapper objectMapper;
    protected final AnalyticsEventService analyticsEventService;
    protected final AnalyticsEventMapper analyticsEventMapper;

    protected void handleEvent(Message message, Class<T> eventClass, Consumer<T> consumer) {
        try {
            T event = objectMapper.readValue(message.getBody(), eventClass);
            log.info("Received event: {}", event);
            consumer.accept(event);
        } catch (IOException e) {
            log.error("Failed to handle event", e);
        }
    }

    public MessageListenerAdapter getListenerAdapter() {
        return new MessageListenerAdapter(this);
    }

    public ChannelTopic getChannelTopic() {
        return new ChannelTopic(this.channelName);
    }
}