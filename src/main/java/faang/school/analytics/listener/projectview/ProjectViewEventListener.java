package faang.school.analytics.listener.projectview;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.analytics.listener.AbstractEventListener;
import faang.school.analytics.mapper.AnalyticsEventMapper;
import faang.school.analytics.model.AnalyticsEvent;
import faang.school.analytics.model.EventType;
import faang.school.analytics.service.AnalyticsEventServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProjectViewEventListener extends AbstractEventListener<ProjectViewEvent> implements MessageListener {


    public ProjectViewEventListener(
            @Value("${spring.data.redis.channel.projects_view_channel.name}") String channelName,
            ObjectMapper objectMapper,
            AnalyticsEventMapper analyticsEventMapper,
            AnalyticsEventServiceImpl analyticsEventServiceImpl) {
        super(channelName, objectMapper, analyticsEventServiceImpl, analyticsEventMapper);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        handleEvent(message, ProjectViewEvent.class, event -> {
            AnalyticsEvent analEvent = analyticsEventMapper.fromProjectViewToAnalyticsEvent(event);
            analEvent.setEventType(EventType.PROJECT_VIEW);
            AnalyticsEvent savedEvent = analyticsEventService.saveEvent(analEvent);
            log.info("Saved project view event: {}", savedEvent);
        });
    }
}
