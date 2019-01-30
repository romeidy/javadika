package id.co.collega.v7.seed.config;

import id.co.collega.v7.ui.config.MVCConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


@EnableWebMvc
@ComponentScan("id.co.collega.v7.seed.api")
public class WebConfiguration extends MVCConfiguration {
}
