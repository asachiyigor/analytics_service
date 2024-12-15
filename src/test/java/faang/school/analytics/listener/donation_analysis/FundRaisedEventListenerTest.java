package faang.school.analytics.listener.donation_analysis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.analytics.dto.FundRaisedEvent;
import faang.school.analytics.mapper.AnalyticsEventMapper;
import faang.school.analytics.model.AnalyticsEvent;
import faang.school.analytics.model.EventType;
import faang.school.analytics.service.AnalyticsEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.connection.Message;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class FundRaisedEventListenerTest {
    @InjectMocks
    private FundRaisedEventListener eventListener;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private AnalyticsEventMapper analyticsEventMapper;

    @Mock
    private AnalyticsEventService analyticsEventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testOnMessage_SuccessfulProcessing() throws Exception {
        Message message = mock(Message.class);
        byte[] messageBody = "{\"userId\":1,\"amount\":100,\"timestamp\":\"2024-12-11T10:00:00\"}".getBytes();
        when(message.getBody()).thenReturn(messageBody);

        FundRaisedEvent event = new FundRaisedEvent(1L, BigDecimal.valueOf(1), EventType.POST_VIEW, LocalDateTime.now());
        when(objectMapper.readValue(messageBody, FundRaisedEvent.class)).thenReturn(event);

        AnalyticsEvent analyticsEvent = new AnalyticsEvent();
        when(analyticsEventMapper.toEntity(event)).thenReturn(analyticsEvent);

        eventListener.onMessage(message, null);

        verify(objectMapper, times(1)).readValue(messageBody, FundRaisedEvent.class);
        verify(analyticsEventMapper, times(1)).toEntity(event);
        verify(analyticsEventService, times(1)).saveEvent(analyticsEvent);
    }

    @Test
    public void testOnMessage_ExceptionThrown() throws Exception {

        Message message = mock(Message.class);
        byte[] messageBody = "invalid json".getBytes();
        when(message.getBody()).thenReturn(messageBody);

        when(objectMapper.readValue(messageBody, FundRaisedEvent.class))
                .thenThrow(new IOException("Invalid JSON"));

        assertThrows(RuntimeException.class, () -> eventListener.onMessage(message, null));

        verify(objectMapper, times(1)).readValue(messageBody, FundRaisedEvent.class);
        verifyNoInteractions(analyticsEventMapper);
        verifyNoInteractions(analyticsEventService);
    }
}
