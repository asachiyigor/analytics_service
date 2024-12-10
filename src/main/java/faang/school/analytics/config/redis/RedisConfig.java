package faang.school.analytics.config.redis;

import faang.school.analytics.listener.BoughtPremiumEventListener;
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
  RedisMessageListenerContainer redisBoughtPremiumListenerContainer(
      MessageListenerAdapter adapter) {
    return redisContainer(adapter, boughtPremiumChannelTopic());
  }

  private RedisMessageListenerContainer redisContainer(
      MessageListenerAdapter adapter, ChannelTopic topic) {
    RedisMessageListenerContainer container = new RedisMessageListenerContainer();
    container.setConnectionFactory(jedisConnectionFactory());
    container.addMessageListener(adapter, topic);
    return container;
  }

}