package faang.school.analytics.config.redis;

import faang.school.analytics.listener.BoughtPremiumEventListener;
import faang.school.analytics.listener.RecommendationEventListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

  @Value("${spring.data.redis.host}")
  private String host;

  @Value("${spring.data.redis.port}")
  private int port;

  @Value("${spring.data.redis.channel.bought-premium}")
  private String boughtPremiumChannelTopic;

  @Value("${spring.data.redis.channel.recommendation}")
  private String recommendationChannelTopic;

  @Bean
  public JedisConnectionFactory jedisConnectionFactory() {
    RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
    return new JedisConnectionFactory(config);
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate() {
    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(jedisConnectionFactory());
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setValueSerializer(new StringRedisSerializer());
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
      BoughtPremiumEventListener boughtPremiumEventListener) {
    RedisMessageListenerContainer container = new RedisMessageListenerContainer();
    container.setConnectionFactory(jedisConnectionFactory());
    container.addMessageListener(boughtPremiumListener(boughtPremiumEventListener),
        boughtPremiumChannelTopic());
    container.addMessageListener(recommendationListener(recommendationEventListener),
        recommendationChannelTopic());
    return container;
  }

}