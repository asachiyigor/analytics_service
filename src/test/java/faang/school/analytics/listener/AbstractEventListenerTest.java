package faang.school.analytics.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.analytics.mapper.AnalyticsEventMapper;
import faang.school.analytics.service.AnalyticsEventService;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import java.io.IOException;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbstractEventListenerTest {
    private AbstractEventListener<TestEvent> abstractEventListener;

    private String channelName;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private AnalyticsEventService analyticsEventService;

    @Mock
    private AnalyticsEventMapper analyticsEventMapper;

    @Mock
    private Message message;

    @Mock
    private Consumer<TestEvent> consumer;

    private TestEvent event;
    private byte[] messageBytes;


    @BeforeEach
    void setUp() {
        channelName = "test-channel";
        messageBytes = new byte[100];
        event = new TestEvent("test");
        abstractEventListener =
                new AbstractEventListener<>(channelName, objectMapper, analyticsEventService, analyticsEventMapper) {
        };
    }

    @Test
    void testHandleEvent_Positive() throws IOException {
        when(message.getBody()).thenReturn(messageBytes);
        when(objectMapper.readValue(messageBytes, TestEvent.class)).thenReturn(event);
        abstractEventListener.handleEvent(message, TestEvent.class, consumer);
        verify(objectMapper).readValue(messageBytes, TestEvent.class);
    }

    @Test
    void testHandleEvent_Negative() throws IOException {
        when(message.getBody()).thenReturn(messageBytes);
        when(objectMapper.readValue(messageBytes, TestEvent.class)).thenThrow(IOException.class);
        abstractEventListener.handleEvent(message, TestEvent.class, consumer);
        verify(objectMapper, times(1)).readValue(messageBytes, TestEvent.class);
        verify(consumer, never()).accept(event);
    }

    @Test
    void getListenerAdapter() {
        MessageListenerAdapter adapter = abstractEventListener.getListenerAdapter();
        assertNotNull(adapter);
        assertEquals(abstractEventListener, adapter.getDelegate());
    }

    @Test
    void getChannelTopic() {
        ChannelTopic topic = abstractEventListener.getChannelTopic();
        assertNotNull(topic);
        assertEquals(channelName, topic.getTopic());
    }


    @Data
    private static class TestEvent {
        private String name;

        TestEvent(String name) {
            this.name = name;
        }
    }
}