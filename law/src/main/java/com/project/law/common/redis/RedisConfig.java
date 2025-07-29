package com.project.law.common.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;

@Configuration
public class RedisConfig {


        // Redis Connection Configuration
        @Bean
        @Primary
        public LettuceConnectionFactory redisConnectionFactory() {
            return new LettuceConnectionFactory(new RedisStandaloneConfiguration("localhost", 6379));
        }

        @Bean(name = "CustomStringRedisTemplate")
        @Primary
        StringRedisTemplate customStringRedisTemplate(LettuceConnectionFactory lettuceConnectionFactory){

            StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();

            // ConnectionFactory
            stringRedisTemplate.setConnectionFactory(lettuceConnectionFactory);

            stringRedisTemplate.afterPropertiesSet(); // after Properties Set, process will be on

            return stringRedisTemplate;
        }

        @Bean
        public RedisCacheManager cacheManager(LettuceConnectionFactory lettuceConnectionFactory) {
            // 기본 캐시 설정 (여기서 entryTtl로 TTL 지정)
            RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofMinutes(5)); // 예: 모든 캐시 항목의 유효기간을 5분으로 설정

            return RedisCacheManager.builder(lettuceConnectionFactory)
                    .cacheDefaults(cacheConfig)
                    .build();
        }

}
