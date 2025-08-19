package com.example.integration.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SlowTransactionConfig {

	@Bean
	@Primary
	public PlatformTransactionManager transactionManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource) {
			@Override
			protected void doBegin(Object transaction, TransactionDefinition definition) {
				log.debug("[TX] 트랜잭션 시작: {}", definition.getName());
				super.doBegin(transaction, definition);
			}

			@Override
			protected void doCommit(DefaultTransactionStatus status) {
				log.debug("[TX] 커밋 시작: {}", status.getTransaction());
				try {
					Thread.sleep(50);
					log.debug("커밋 전 지연 완료: {}", Thread.currentThread().getName());
					super.doCommit(status);
					log.debug("[TX] 커밋 완료");
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					log.debug("[TX] 지연 중 인터럽트");
				} catch (Exception e) {
					log.debug("[TX] 커밋 중 예외: {}", e.getMessage());
					throw e;
				}
			}

			@Override
			protected void doRollback(DefaultTransactionStatus status) {
				log.debug("[TX] 롤백 시작: {}", status.getTransaction());
				super.doRollback(status);
				log.debug("[TX] 롤백 완료");
			}
		};
	}
}
