
package com.kakaopay.investment.config;

import com.kakaopay.investment.repository.redis.BaseRedisRepositoryPackageLocation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

/**
 * The type Redis repository config.
 */
@Configuration
@EnableRedisRepositories(basePackageClasses = BaseRedisRepositoryPackageLocation.class)
public class RedisRepositoryConfig {
	
	@Value("${spring.redis.host}")
	private String redisHost;

	@Value("${spring.redis.port}")
	private int redisPort;

	/**
	 * Redis connection factory lettuce connection factory.
	 *
	 * @return the lettuce connection factory
	 */
	@Bean
	public LettuceConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory(redisHost, redisPort);
	}

	/**
	 * Redis template redis template.
	 *
	 * @return the redis template
	 */
	@Bean
	public RedisTemplate<?, ?> redisTemplate() {
		RedisTemplate<byte[], byte[]> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory());
		return redisTemplate;
	}
}