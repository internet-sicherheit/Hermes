package net.ifis.ites.hermes

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@ComponentScan
@EnableJpaRepositories
@EnableAutoConfiguration
@EnableTransactionManagement
@EnableAspectJAutoProxy(proxyTargetClass=true)
class HermesApplication {

    static void main(String[] args) {
        SpringApplication.run HermesApplication, args
    }
}