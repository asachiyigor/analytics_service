package faang.school.analytics.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.analytics.dto.AnalyticsEventDto;
import faang.school.analytics.dto.recommendation.RecommendationEventDto;
import faang.school.analytics.mapper.AnalyticsEventMapper;
import faang.school.analytics.model.AnalyticsEvent;
import faang.school.analytics.model.EventType;
import faang.school.analytics.service.AnalyticsEventService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.redis.connection.DefaultMessage;
import org.springframework.data.redis.connection.Message;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class LikeEventListenerTest {
    @Mock
    private AnalyticsEventService analyticsEventService;
    @Mock
    private AnalyticsEventMapper analyticsEventMapper;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private LikeEventListener likeEventListener;

    @Test
    @DisplayName("Test onMessage - Successful Event Processing")
    public void testOnMessageSuccess() throws IOException {
        String json = "{\"id\":1,\"receiverId\":2,\"actorId\":3,\"eventType\":\"POST_LIKE\",\"receivedAt\":\"2024-12-07 23:03:55\"}";
        byte[] body = json.getBytes();
        byte[] channel = "like-analytics-topic".getBytes();
        Message message = new DefaultMessage(channel, body);
        LocalDateTime now = LocalDateTime.now();
        AnalyticsEventDto analyticsEventDto = AnalyticsEventDto.builder()
                .id(1L)
                .receiverId(2L)
                .actorId(3L)
                .receivedAt(now)
                .build();

        AnalyticsEvent analyticsEvent = AnalyticsEvent
                .builder()
                .id(1L)
                .receiverId(2L)
                .receivedAt(now)
                .build();

        when(objectMapper.readValue(body, AnalyticsEventDto.class)).thenReturn(analyticsEventDto);
        when(analyticsEventMapper.toEntity(analyticsEventDto)).thenReturn(analyticsEvent);

        likeEventListener.onMessage(message, null);

        verify(objectMapper, times(1)).readValue(body, AnalyticsEventDto.class);
        verify(analyticsEventMapper, times(1)).toEntity(analyticsEventDto);
        verify(analyticsEventService, times(1)).saveEvent(analyticsEvent);
        assertThat(analyticsEvent.getEventType()).isEqualTo(EventType.POST_LIKE);
    }

    @Test
    @DisplayName("Test onMessage - Deserialization Error")
    public void testOnMessageError() throws IOException {
        byte[] body = "invalid json".getBytes();
        byte[] channel = "like-analytics-topic".getBytes();
        Message message = new DefaultMessage(channel, body);

        when(objectMapper.readValue(body, AnalyticsEventDto.class))
                .thenThrow(new IOException("Error deserializing"));

        RuntimeException runtimeException = assertThrows(RuntimeException.class,
                () -> likeEventListener.onMessage(message, null));
        Assertions.assertEquals("Error deserializing JSON to object", runtimeException.getMessage());

        verify(objectMapper, times(1)).readValue(body, AnalyticsEventDto.class);
        verify(analyticsEventMapper, never()).toEntity(any(RecommendationEventDto.class));
        verify(analyticsEventService, never()).saveEvent(any());
    }
}