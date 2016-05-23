package no.priv.garshol.duke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = {"no.priv.garshol.duke"})
public class SpringApplicationTest {

    public static void main(String[] args) throws Exception {

        try (ConfigurableApplicationContext context = SpringApplication.run(SpringApplicationTest.class, args)) {

            DatasourceTest test= context.getBean(DatasourceTest.class);
            test.launch();
        }
    }
}

@Component
class DatasourceTest{
    @Autowired
    javax.sql.DataSource dataSource;

    public void launch(){
        // check that datasource is loaded
    };
}
