package faang.school.analytics.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.analytics.dto.recommendation.RecommendationEventDto;
import faang.school.analytics.mapper.AnalyticsEventMapper;
import faang.school.analytics.model.AnalyticsEvent;
import faang.school.analytics.model.EventType;
import faang.school.analytics.service.AnalyticsEventService;
import java.io.IOException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.DefaultMessage;
import org.springframework.data.redis.connection.Message;

@ExtendWith(MockitoExtension.class)
class RecommendationEventListenerTest {

  @InjectMocks
  private RecommendationEventListener recommendationEventListener;
  @Mock
  private AnalyticsEventService analyticsEventService;
  @Mock
  private ObjectMapper objectMapper;
  @Spy
  private AnalyticsEventMapper analyticsEventMapper = Mappers.getMapper(AnalyticsEventMapper.class);

  @Test
  @DisplayName("Should throw exception when no message received")
  void testInvalidMessageReceived() throws IOException {
    byte[] messageBody = "{}".getBytes();
    byte[] channelTopic = "recommendation_channel".getBytes();
    Message message = new DefaultMessage(channelTopic, messageBody);

    when(objectMapper.readValue(messageBody, RecommendationEventDto.class))
        .thenThrow(new RuntimeException("Error deserializing JSON to object"));

    var exception = assertThrows(RuntimeException.class,
        () -> recommendationEventListener.onMessage(message, null));

    assertEquals("Error deserializing JSON to object", exception.getMessage());
  }

  @Test
  @DisplayName("Should save analytics data into DB")
  void testValidMessageReceived() throws IOException {
    String jsonMessageBody = "";
    byte[] messageBody = jsonMessageBody.getBytes();
    byte[] channelTopic = "recommendation_channel".getBytes();
    Message message = new DefaultMessage(channelTopic, messageBody);
    LocalDateTime eventTime = LocalDateTime.now();

    RecommendationEventDto eventDto = RecommendationEventDto.builder()
        .receiverId(1L)
        .authorId(2L)
        .receivedAt(eventTime.toString())
        .build();

    AnalyticsEvent analyticsEvent = AnalyticsEvent.builder()
        .receiverId(1L)
        .actorId(2L)
        .eventType(EventType.RECOMMENDATION_RECEIVED)
        .receivedAt(eventTime)
        .build();

    when(objectMapper.readValue(messageBody, RecommendationEventDto.class))
        .thenReturn(eventDto);
    recommendationEventListener.onMessage(message, null);

    verify(objectMapper, times(1)).readValue(messageBody, RecommendationEventDto.class);
    verify(analyticsEventMapper, times(1)).toEntity(eventDto);
    verify(analyticsEventService, times(1)).saveEvent(analyticsEvent);

  }
}