package faang.school.analytics.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.analytics.dto.SearchAppearanceEventDto;
import faang.school.analytics.mapper.AnalyticsEventMapper;
import faang.school.analytics.model.AnalyticsEvent;
import faang.school.analytics.model.EventType;
import faang.school.analytics.service.AnalyticsEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.Message;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchAppearanceEventListenerTest {

    @Mock
    private AnalyticsEventMapper analyticsEventMapper;

    @Mock
    private AnalyticsEventService analyticsEventService;

    @Mock
    private Message message;

    private SearchAppearanceEventListener listener;
    private ObjectMapper objectMapper;
    private SearchAppearanceEventDto inputEvent;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        listener = new SearchAppearanceEventListener(analyticsEventMapper, analyticsEventService);
        inputEvent = SearchAppearanceEventDto.builder()
                .requesterId(123L)
                .foundUserId(456L)
                .build();
    }

    @Test
    @DisplayName("When valid search appearance event is received, should process and save event successfully")
    void onMessage_ValidEvent_ShouldProcessAndSaveEvent() throws IOException {
        String jsonPayload = objectMapper.writeValueAsString(inputEvent);
        when(message.getBody()).thenReturn(jsonPayload.getBytes());
        AnalyticsEvent analyticsEvent = new AnalyticsEvent();
        analyticsEvent.setActorId(inputEvent.getRequesterId());
        analyticsEvent.setReceiverId(inputEvent.getFoundUserId());
        analyticsEvent.setEventType(EventType.PROFILE_APPEARED_IN_SEARCH);
        when(analyticsEventMapper.toEntity(any(SearchAppearanceEventDto.class))).thenReturn(analyticsEvent);
        listener.onMessage(message, null);
        ArgumentCaptor<AnalyticsEvent> eventCaptor = ArgumentCaptor.forClass(AnalyticsEvent.class);
        verify(analyticsEventService).saveEvent(eventCaptor.capture());
        AnalyticsEvent savedEvent = eventCaptor.getValue();
        assertEquals(inputEvent.getRequesterId(), savedEvent.getActorId());
        assertEquals(inputEvent.getFoundUserId(), savedEvent.getReceiverId());
        assertNotNull(savedEvent.getReceivedAt());
        verify(analyticsEventMapper).toEntity(any(SearchAppearanceEventDto.class));
    }

    @Test
    @DisplayName("When invalid JSON payload is received, should throw RuntimeException")
    void onMessage_InvalidJsonPayload_ShouldThrowRuntimeException() {
        when(message.getBody()).thenReturn(new byte[0]);
        assertThrows(RuntimeException.class, () -> {
            listener.onMessage(message, null);
        });
        verify(analyticsEventService, never()).saveEvent(any());
    }
}