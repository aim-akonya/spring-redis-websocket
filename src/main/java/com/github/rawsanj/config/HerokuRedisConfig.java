package com.github.rawsanj.config;

import com.github.rawsanj.messaging.RedisChatMessageListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import static com.github.rawsanj.config.ChatConstants.ACTIVE_USER_KEY;
import static com.github.rawsanj.config.ChatConstants.MESSAGE_COUNTER_KEY;

@Configuration(proxyBeanMethods = false)
@Profile("heroku")
public class HerokuRedisConfig {

	private static Logger log = LoggerFactory.getLogger(HerokuRedisConfig.class);

	@Bean
	ReactiveRedisConnectionFactory reactiveRedisConnectionFactory(Environment environment) throws URISyntaxException {
		return lettuceConnectionFactory(environment);
	}

	@Bean
	RedisConnectionFactory redisConnectionFactory(Environment environment) throws URISyntaxException {
		return lettuceConnectionFactory(environment);
	}

	
	private LettuceConnectionFactory lettuceConnectionFactory(Environment environment) throws URISyntaxException {
		String redisUrlEnvName = environment.getProperty("HEROKU_REDIS_URL_ENV_NAME");
		String redisStringUrl = environment.getProperty(Objects.requireNonNull(redisUrlEnvName,
				"Environment variable HEROKU_REDIS_URL_ENV_NAME cannot be null & should point to Redis ENV URL."));
		URI redisUrl = new URI(Objects.requireNonNull(redisStringUrl,
				"Environment variable " + redisUrlEnvName + "cannot not be null"));
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisUrl.getHost(),
				redisUrl.getPort());
		redisStandaloneConfiguration.setPassword(redisUrl.getUserInfo().split(":", 2)[1]);
		return new LettuceConnectionFactory(redisStandaloneConfiguration);
	}

	@Bean
	ReactiveStringRedisTemplate template(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
		return new ReactiveStringRedisTemplate(reactiveRedisConnectionFactory);
	}

	// Redis Atomic Counter to store no. of total messages sent from multiple app
	// instances.
	@Bean
	RedisAtomicInteger chatMessageCounter(RedisConnectionFactory redisConnectionFactory) {
		return new RedisAtomicInteger(MESSAGE_COUNTER_KEY, redisConnectionFactory);
	}

	// Redis Atomic Counter to store no. of Active Users.
	@Bean
	RedisAtomicLong activeUserCounter(RedisConnectionFactory redisConnectionFactory) {
		return new RedisAtomicLong(ACTIVE_USER_KEY, redisConnectionFactory);
	}

	@Bean
	ApplicationRunner applicationRunner(RedisChatMessageListener redisChatMessageListener) {
		return args -> {
			redisChatMessageListener.subscribeMessageChannelAndPublishOnWebSocket()
					.doOnSubscribe(subscription -> log.info("Redis Listener Started"))
					.doOnError(throwable -> log.error("Error listening to Redis topic.", throwable))
					.doFinally(signalType -> log.info("Stopped Listener. Signal Type: {}", signalType)).subscribe();
		};
	}

}
