package faang.school.analytics.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.analytics.dto.AdBoughtEventDto;
import faang.school.analytics.dto.FundRaisedEvent;
import faang.school.analytics.dto.recommendation.RecommendationEventDto;
import faang.school.analytics.mapper.AnalyticsEventMapper;
import faang.school.analytics.model.AnalyticsEvent;
import faang.school.analytics.model.EventType;
import faang.school.analytics.service.AnalyticsEventService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.DefaultMessage;
import org.springframework.data.redis.connection.Message;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdBoughtEventListenerTest {

    @Mock
    private AnalyticsEventService analyticsEventService;
    @Mock
    private AnalyticsEventMapper analyticsEventMapper;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private AdBoughtEventListener listener;
    @Mock
    private Message message;

    private static final String CHANNEL_NAME = "ad-bought-channel";

    @BeforeEach
    void setUp() {
        listener = new AdBoughtEventListener(
                CHANNEL_NAME,
                objectMapper,
                analyticsEventService,
                analyticsEventMapper
        );
    }

    @Test
    @DisplayName("Should successfully process and save valid ad bought event")
    void shouldProcessAndSaveValidAdBoughtEvent() throws Exception {
        AdBoughtEventDto eventDto = AdBoughtEventDto.builder()
                .postId(1L)
                .build();
        byte[] messageBody = "{}".getBytes();
        when(message.getBody()).thenReturn(messageBody);
        when(objectMapper.readValue(messageBody, AdBoughtEventDto.class)).thenReturn(eventDto);
        AnalyticsEvent analyticsEvent = new AnalyticsEvent();
        when(analyticsEventMapper.toEntity(eventDto)).thenReturn(analyticsEvent);
        listener.onMessage(message, null);
        verify(analyticsEventService).saveEvent(any(AnalyticsEvent.class));
        verify(analyticsEventMapper).toEntity(eventDto);
        assertEquals(EventType.POST_AD_BOUGHT, analyticsEvent.getEventType());
    }

    @Test
    @DisplayName("Should throw exception when receiving invalid JSON message")
    void shouldThrowExceptionWhenReceivingInvalidJson() throws IOException {
        byte[] body = "invalid json".getBytes();
        byte[] channel = "ad-bought-channel".getBytes();
        Message message = new DefaultMessage(channel, body);
        when(objectMapper.readValue(body, AdBoughtEventDto.class))
                .thenThrow(new RuntimeException("Error deserializing"));
        var exception = assertThrows(RuntimeException.class,
                () -> listener.onMessage(message, null));
        Assertions.assertEquals("Error deserializing", exception.getMessage());
        verify(objectMapper, times(1)).readValue(body, AdBoughtEventDto.class);
        verify(analyticsEventMapper, never()).toEntity(any(RecommendationEventDto.class));
        verify(analyticsEventService, never()).saveEvent(any());
    }

    @Test
    @DisplayName("Should handle IOException and prevent event processing")
    void shouldHandleIOExceptionAndPreventEventProcessing() throws Exception {
        Message message = mock(Message.class);
        byte[] messageBody = "invalid json".getBytes();
        when(message.getBody()).thenReturn(messageBody);
        when(objectMapper.readValue(messageBody, FundRaisedEvent.class))
                .thenThrow(new IOException("Invalid JSON"));
        assertThrows(RuntimeException.class, () -> listener.onMessage(message, null));
        verify(objectMapper, times(1)).readValue(messageBody, AdBoughtEventDto.class);
        verifyNoInteractions(analyticsEventMapper);
        verifyNoInteractions(analyticsEventService);
    }
}