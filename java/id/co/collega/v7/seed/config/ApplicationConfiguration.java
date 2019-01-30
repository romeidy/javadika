package id.co.collega.v7.seed.config;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.google.gson.Gson;

import id.co.collega.ifrs.common.JdbcTemplate;
import id.co.collega.ifrs.security.SecurityConfigurationV2;
import id.co.collega.v7.core.config.CoreConfiguration;
import id.co.collega.v7.ui.component.MenuLoader;
import id.co.collega.v7.ui.config.UIConfiguration;
import id.co.collega.v7.ui.config.ZkossScopeConfig;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = {"id.co.collega.v7.seed","id.co.collega.ifrs"})
@PropertySource("classpath:application.properties")
//@Import({CoreConfiguration.class, UIConfiguration.class, SecurityConfiguration.class})
@Import({CoreConfiguration.class, UIConfiguration.class, SecurityConfigurationV2.class})
public class ApplicationConfiguration {
    @Autowired
    Environment env;
    
    @Bean 
    DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(env.getRequiredProperty("jdbc.url"));
        dataSource.setDriverClassName(env.getRequiredProperty("jdbc.driver"));
        dataSource.setUsername(env.getRequiredProperty("jdbc.username"));
        dataSource.setPassword(env.getRequiredProperty("jdbc.password"));
        return dataSource;
    }

    /*@Bean 
    DataSource dataSource2() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(env.getRequiredProperty("jdbc2.url"));
        dataSource.setDriverClassName(env.getRequiredProperty("jdbc2.driver"));
        dataSource.setUsername(env.getRequiredProperty("jdbc2.username"));
        dataSource.setPassword(env.getRequiredProperty("jdbc2.password"));
        return dataSource;
    }
    
    @Bean 
    DataSource dataSource3() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(env.getRequiredProperty("jdbc3.url"));
        dataSource.setDriverClassName(env.getRequiredProperty("jdbc3.driver"));
        dataSource.setUsername(env.getRequiredProperty("jdbc3.username")); 
        dataSource.setPassword(env.getRequiredProperty("jdbc3.password"));
        return dataSource;
    }*/
    
    @Bean (name="jt")
    JdbcTemplate jdbcTemplate(){
        return new JdbcTemplate(dataSource());
    }

   /* @Bean (name="jt2")
    JdbcTemplate jdbcTemplate2(){
        return new JdbcTemplate(dataSource2());
    }
    
    @Bean (name="jt3")
    JdbcTemplate jdbcTemplate3(){
        return new JdbcTemplate(dataSource3());
    }*/
   
    
    @Bean
    PlatformTransactionManager platformTransactionManager(){
        return new DataSourceTransactionManager(dataSource());
    }

//    @Bean
//    @Scope("session")
//    MenuLoader menuLoader(){
//        return new XmlBasedMenuLoader();
//    }
    
    @Bean
    @Scope("session")
    MenuLoader menuLoader(){
        return new ApplicationMenuLoader();
    }
    
    @Bean
    public CommonsMultipartResolver multipartResolver(){
        return new CommonsMultipartResolver();
    }
    
    @Bean
    public Gson gson(){
    	return new Gson();
    }
    
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate res = new RestTemplate();
        res.setRequestFactory(clientHttpRequestFactory());
        res.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        return res;
    }
    
    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(25000);
        factory.setConnectTimeout(20000);
        return factory;
    }

    @Bean
    public HttpHeaders httpHeader(){
    	HttpHeaders header = new HttpHeaders();
    	List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
        header.setAccept(acceptableMediaTypes);
        header.setContentType(MediaType.APPLICATION_JSON);
        return header;
    }
}
