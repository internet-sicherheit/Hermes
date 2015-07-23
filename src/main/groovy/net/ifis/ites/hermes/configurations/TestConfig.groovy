package net.ifis.ites.hermes.configurations

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.JpaVendorAdapter
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor
import org.springframework.web.servlet.i18n.SessionLocaleResolver

import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

/**
 * Class to create an test configuration for Hermes.
 *
 * @author Andreas Sekulski
 */
@Configuration
@Profile("test")
@EnableTransactionManagement
class TestConfig {

    /** Root from all entity classes from hemers **/
    private static final String DEFAULT_CLASSES_PACKAGE = "net.ifis.ites.hermes.domains.management"

    /** Database driver **/
    private static final String DB_DRIVER = "org.postgresql.Driver"

    /** Database connection **/
    private static final String DB_CONNECTION = "jdbc:postgresql://localhost:5432/hermesTest"

    /** Database username **/
    private static final String DB_USERNAME = "hermes"

    /** Database password **/
    private static final String DB_PASSWORD = "pinguin"

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.GERMANY);
        return slr;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean()
        em.setDataSource(dataSource())
        em.setPackagesToScan(DEFAULT_CLASSES_PACKAGE)

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter()
        em.setJpaVendorAdapter(vendorAdapter)
        em.setJpaProperties(additionalProperties())

        return em
    }

    @Bean
    public DataSource dataSource(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource()
        dataSource.setDriverClassName(DB_DRIVER)
        dataSource.setUrl(DB_CONNECTION)
        dataSource.setUsername(DB_USERNAME)
        dataSource.setPassword(DB_PASSWORD)
        return dataSource
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf){
        JpaTransactionManager transactionManager = new JpaTransactionManager()
        transactionManager.setEntityManagerFactory(emf)

        return transactionManager
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation(){
        return new PersistenceExceptionTranslationPostProcessor()
    }

    /**
     * Method to add additional jpa properties in jpa bean
     * @return Additional properties to add to his responding jpa bean
     */
    private Properties additionalProperties() {
        Properties properties = new Properties()
        properties.setProperty("hibernate.hbm2ddl.auto", "create-drop")
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
        return properties
    }
}