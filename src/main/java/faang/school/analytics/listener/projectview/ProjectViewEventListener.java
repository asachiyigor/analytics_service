package faang.school.analytics.listener.projectview;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.analytics.client.UserServiceClient;
import faang.school.analytics.listener.AbstractEventListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class ProjectViewEventListener extends AbstractEventListener<ProjectViewEvent> implements MessageListener {

    public ProjectViewEventListener(
            @Value("${spring.data.redis.channel.projects_view_channel.name}") String channelName,
            ObjectMapper objectMapper,
            UserServiceClient userServiceClient) {
        super(channelName, objectMapper, userServiceClient);
    }


    @Override
    public void onMessage(Message message, byte[] pattern) {
        handleEvent(message, ProjectViewEvent.class, event -> {
            System.out.println(event);
        });
    }
}
