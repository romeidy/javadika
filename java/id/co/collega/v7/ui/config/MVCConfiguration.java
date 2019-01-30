package id.co.collega.v7.ui.config;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

public class MVCConfiguration extends WebMvcConfigurerAdapter {

	  @Override
	    public void addResourceHandlers(ResourceHandlerRegistry registry) {
	        super.addResourceHandlers(registry);
	        registry.addResourceHandler("/asset/**")
	                .addResourceLocations("asset/");
	    }

	    @Override
	    public void addViewControllers(ViewControllerRegistry registry) {
	        super.addViewControllers(registry);
	        registry.addRedirectViewController("/", "/index.zul");
	    }

	
}
