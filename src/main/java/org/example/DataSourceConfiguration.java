package org.example;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Objects;

@Configuration
public class DataSourceConfiguration {

    @Bean
    @Primary
    DataSource firstDataSource() {
        return DataSourceBuilder.create()
                .url("jdbc:h2:mem:testdb")
                .username("h2")
                .password("h2")
                .driverClassName("org.h2.Driver")
                .build();
    }

    @Bean
    DataSource secondDataSource() {
        return DataSourceBuilder.create()
                .url("jdbc:h2:mem:seconddb")
                .username("h2")
                .password("h2")
                .driverClassName("org.h2.Driver")
                .build();
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            @Qualifier("firstDataSource") DataSource dataSource,
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(dataSource)
                .properties(Map.of("hibernate.hbm2ddl.auto", "create"))
                .packages("org.example.first", "org.springframework.modulith.events.jpa")
                .build();
    }

    @Bean
    @Primary
    public PlatformTransactionManager transactionManager(
            @Qualifier("entityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactory.getObject()));
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean secondEntityManagerFactory(
            @Qualifier("secondDataSource") DataSource dataSource,
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(dataSource)
                .properties(Map.of("hibernate.hbm2ddl.auto", "create"))
                .packages("org.example.second")
                .build();
    }

    @Bean
    public PlatformTransactionManager secondTransactionManager(
            @Qualifier("secondEntityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactory.getObject()));
    }

    @Bean
    public ChainedTransactionManager chainedTransactionManager(
            @Qualifier("transactionManager") PlatformTransactionManager cbplTransactionManager,
            @Qualifier("secondTransactionManager") PlatformTransactionManager interestPlatformTransactionManager) {
        return new ChainedTransactionManager(cbplTransactionManager, interestPlatformTransactionManager);
    }
}
