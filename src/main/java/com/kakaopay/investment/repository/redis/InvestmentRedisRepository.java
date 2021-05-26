package com.kakaopay.investment.repository.redis;

import com.kakaopay.investment.entity.ProductEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Repository
public class InvestmentRedisRepository {
	final long REDIS_SYNC_MILLISENCONDS = 2000;
	final String REDIS_SYNC_KEY_PREFIX = "LOCK";
	final String REDIS_REMAINAMOUNT_KEY_PREFIX = "";

	private RedisTemplate<String, String> redisTemplate;

	@Autowired
	public InvestmentRedisRepository(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}


	/**
	 * 동시성 방지를 위한 LOCK
	 *
	 * @param productId
	 * @return
	 */
	public Boolean lockInvestment(Long productId) {
		String key = REDIS_SYNC_KEY_PREFIX + productId;
		ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
		return opsForValue.setIfAbsent(key, "1", Duration.ofMillis(REDIS_SYNC_MILLISENCONDS));
	}

	/**
	 * 동시성 방지를 위한 Lock해제
	 *
	 * @param productId
	 * @return
	 */
	public Boolean releaseInvestmentLock(Long productId){
		String key = REDIS_SYNC_KEY_PREFIX + productId;
		return redisTemplate.delete(key);
	}

	/**
	 * 투자가능 (남은) 금액 정보 set
	 *
	 * @param productEntity
	 * @param remainAmount
	 */
	public void setProductRemainAmount(ProductEntity productEntity, Long remainAmount) {
		String key = REDIS_REMAINAMOUNT_KEY_PREFIX + productEntity.getId();
		long remainAmountTtl = productEntity.getFinishedAt().getTime() - new Date().getTime();

		ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
		opsForValue.set(key, String.valueOf(remainAmount), remainAmountTtl, TimeUnit.MILLISECONDS);
	}

	/**
	 * 투자가능 (남은) 금액 차감
	 *
	 * @param prductId
	 * @param amount
	 */
	public Long decreaseProductRemainAmount(Long prductId, Long amount) {
		String key = REDIS_REMAINAMOUNT_KEY_PREFIX + prductId;
		ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
		return opsForValue.decrement(key,amount.longValue());
	}

	/**
	 * 투자가능 (남은) 금액 정보 get
	 *
	 * @param productId
	 * @return
	 */
	public Long getProductRemainAmount(Long productId) {
		String key = REDIS_REMAINAMOUNT_KEY_PREFIX + productId;
		ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
		String remainAmount = opsForValue.get(key);
		return (StringUtils.isBlank(remainAmount)) ? null : Long.parseLong(remainAmount);
	}
}
