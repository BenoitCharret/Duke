package no.priv.garshol.duke.autoconf;

import no.priv.garshol.duke.JDBCComponent;
import no.priv.garshol.duke.SpringDatasourceEquivalenceClassDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@ConditionalOnClass({ DataSource.class})
@AutoConfigureAfter({DataSourceAutoConfiguration.class})
public class DukeDataSourceAutoconfiguration {

    @Configuration
    @ConditionalOnBean({DataSource.class})
    protected static class JDBCConfiguration {

        @Autowired
        DataSource dataSource;

        @Bean
        SpringDatasourceEquivalenceClassDatabase springDatasourceEquivalenceClassDatabase() {
            return new SpringDatasourceEquivalenceClassDatabase(dataSource, jdbcComponent());
        }

        @Bean
        JDBCComponent jdbcComponent() {
            return new JDBCComponent(dataSource);
        }
    }
}
