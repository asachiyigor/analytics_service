package faang.school.analytics.config.redis;

import faang.school.analytics.listener.AbstractEventListener;
import lombok.RequiredArgsConstructor;
import faang.school.analytics.listener.donation_analysis.FundRaisedEventListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    private final RedisProperties redisProperties;
    private final List<AbstractEventListener<?>> eventListeners;

    @Value("${spring.data.redis.channels.name}")
    private String fundRaisedTopic;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration configuration =
                new RedisStandaloneConfiguration(redisProperties.host(), redisProperties.port());
        return new JedisConnectionFactory(configuration);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        redisTemplate.setDefaultSerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    MessageListenerAdapter fundRaisedListener(FundRaisedEventListener fundRaisedEventListener) {
        return new MessageListenerAdapter(fundRaisedEventListener);
    }

    @Bean
    ChannelTopic topic() {
        return new ChannelTopic(fundRaisedTopic);
    }

    @Bean
    RedisMessageListenerContainer redisMessageListenerContainer(JedisConnectionFactory jedisConnectionFactory,
                                                                MessageListenerAdapter fundRaisedListener) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory);
        eventListeners.forEach(listener ->
                container.addMessageListener(listener.getListenerAdapter(), listener.getChannelTopic()));
        container.addMessageListener(fundRaisedListener, topic());
        return container;
    }
}