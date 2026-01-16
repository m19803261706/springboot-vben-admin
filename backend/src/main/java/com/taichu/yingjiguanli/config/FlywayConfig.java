package com.taichu.yingjiguanli.config;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Flyway 数据库迁移配置
 *
 * 显式配置 Flyway 确保迁移正确执行
 *
 * @author CX
 * @since 2026-01-16
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "spring.flyway.enabled", havingValue = "true", matchIfMissing = true)
public class FlywayConfig {

    @Value("${spring.flyway.baseline-version:1}")
    private String baselineVersion;

    @Value("${spring.flyway.locations:classpath:db/migration}")
    private String[] locations;

    @Value("${spring.flyway.table:flyway_schema_history}")
    private String table;

    /**
     * 显式配置 Flyway Bean
     *
     * 使用 baseline-on-migrate 确保在空数据库上也能正确迁移
     */
    @Bean(initMethod = "migrate")
    public Flyway flyway(DataSource dataSource) {
        log.info("【Flyway】开始配置 Flyway...");
        log.info("【Flyway】baseline-version: {}", baselineVersion);
        log.info("【Flyway】locations: {}", String.join(",", locations));
        log.info("【Flyway】table: {}", table);

        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations(locations)
                .table(table)
                .baselineOnMigrate(true)
                .baselineVersion(baselineVersion)
                .validateOnMigrate(false)
                .outOfOrder(true)
                .load();

        log.info("【Flyway】配置完成，准备执行迁移...");
        return flyway;
    }
}
