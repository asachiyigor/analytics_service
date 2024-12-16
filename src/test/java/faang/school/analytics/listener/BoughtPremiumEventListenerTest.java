package faang.school.analytics.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.analytics.dto.premium.BoughtPremiumEventDto;
import faang.school.analytics.mapper.AnalyticsEventMapper;
import faang.school.analytics.model.AnalyticsEvent;
import faang.school.analytics.model.EventType;
import faang.school.analytics.service.AnalyticsEventService;
import java.io.IOException;
import java.math.BigDecimal;
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
class BoughtPremiumEventListenerTest {

  @InjectMocks
  private BoughtPremiumEventListener boughtPremiumEventListener;
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
    byte[] channelTopic = "bought_premium_channel".getBytes();
    Message message = new DefaultMessage(channelTopic, messageBody);

    when(objectMapper.readValue(messageBody, BoughtPremiumEventDto.class))
        .thenThrow(new RuntimeException("Error deserializing JSON to object"));

    var exception = assertThrows(RuntimeException.class,
        () -> boughtPremiumEventListener.onMessage(message, null));

    verify(objectMapper, times(1)).readValue(messageBody, BoughtPremiumEventDto.class);

    assertEquals("Error deserializing JSON to object", exception.getMessage());
  }

  @Test
  @DisplayName("Should save analytics data into DB")
  void testValidMessageReceived() throws IOException {
    String jsonMessageBody = "";
    byte[] messageBody = jsonMessageBody.getBytes();
    byte[] channelTopic = "bought_premium_channel".getBytes();
    Message message = new DefaultMessage(channelTopic, messageBody);
    LocalDateTime eventTime = LocalDateTime.now();

    BoughtPremiumEventDto eventDto = BoughtPremiumEventDto.builder()
        .userId(1L)
        .sum(BigDecimal.valueOf(10))
        .days(30)
        .receivedAt(eventTime.toString())
        .build();

    AnalyticsEvent analyticsEvent = AnalyticsEvent.builder()
        .actorId(1L)
        .receiverId(1L)
        .eventType(EventType.BOUGHT_PREMIUM)
        .receivedAt(eventTime)
        .build();

    when(objectMapper.readValue(messageBody, BoughtPremiumEventDto.class))
        .thenReturn(eventDto);
    boughtPremiumEventListener.onMessage(message, null);

    verify(objectMapper, times(1)).readValue(messageBody, BoughtPremiumEventDto.class);
    verify(analyticsEventMapper, times(1)).toEntity(eventDto);
    verify(analyticsEventService, times(1)).saveEvent(analyticsEvent);

  }
}