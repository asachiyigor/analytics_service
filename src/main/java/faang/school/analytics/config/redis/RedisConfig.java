package faang.school.analytics.config.redis;

import faang.school.analytics.listener.BoughtPremiumEventListener;
import faang.school.analytics.listener.RecommendationEventListener;
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

  @Value("${spring.data.redis.channel.bought-premium}")
  private String boughtPremiumChannelTopic;

  @Value("${spring.data.redis.channel.recommendation}")
  private String recommendationChannelTopic;


    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration configuration =
                new RedisStandaloneConfiguration(redisProperties.host(), redisProperties.port());
        return new JedisConnectionFactory(configuration);
    }

  @Value("${spring.data.redis.channels.name}")
  private String fundRaisedTopic;


    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        redisTemplate.setDefaultSerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

  @Bean
  MessageListenerAdapter boughtPremiumListener(
      BoughtPremiumEventListener boughtPremiumEventListener) {
    return new MessageListenerAdapter(boughtPremiumEventListener);
  }

  @Bean
  ChannelTopic boughtPremiumChannelTopic() {
    return new ChannelTopic(boughtPremiumChannelTopic);
  }

  @Bean
  MessageListenerAdapter recommendationListener(
      RecommendationEventListener recommendationEventListener) {
    return new MessageListenerAdapter(recommendationEventListener);
  }

  @Bean
  ChannelTopic recommendationChannelTopic() {
    return new ChannelTopic(recommendationChannelTopic);
  }

  @Bean
  RedisMessageListenerContainer redisMessageListenerContainer(
      RecommendationEventListener recommendationEventListener,
      BoughtPremiumEventListener boughtPremiumEventListener,
      MessageListenerAdapter fundRaisedEventListener) {
    RedisMessageListenerContainer container = new RedisMessageListenerContainer();
    container.setConnectionFactory(jedisConnectionFactory());
      eventListeners.forEach(listener ->
              container.addMessageListener(listener.getListenerAdapter(), listener.getChannelTopic()));
    container.addMessageListener(boughtPremiumListener(boughtPremiumEventListener),
        boughtPremiumChannelTopic());
    container.addMessageListener(recommendationListener(recommendationEventListener),
        recommendationChannelTopic());
    container.addMessageListener(fundRaisedEventListener, topic());
    return container;
  }

  @Bean
  MessageListenerAdapter fundRaisedListener(FundRaisedEventListener fundRaisedEventListener) {
    return new MessageListenerAdapter(fundRaisedEventListener);
  }

  @Bean
  ChannelTopic topic() {
    return new ChannelTopic(fundRaisedTopic);
  }

}