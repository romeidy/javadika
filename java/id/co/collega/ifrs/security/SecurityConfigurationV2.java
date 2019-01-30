package id.co.collega.ifrs.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import id.co.collega.v7.security.AuthenticationProvider;

@EnableWebSecurity
@Order(1)
public class SecurityConfigurationV2 extends WebSecurityConfigurerAdapter {

	@Autowired
	AuthenticationProvider authenticationProvider;

//	@Autowired
//	SimpleUrlLogoutSuccessHandler LogoutSuccessHandler;

//	@Autowired
//	private SessionRegistry sessionRegistry;

//	@Autowired
//	AbstractAuthenticationProcessingFilter authenticationFilter;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http 
			.headers()
			.frameOptions().sameOrigin()
			.and()
			.authorizeRequests()
			.antMatchers("/index.zul**",
						"/zkau/**",
						"/asset/**",
						"/api/**").permitAll()
        	.anyRequest()
        	.authenticated()
        	.and()
//        	.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
        	.formLogin()
        	.loginPage("/index.zul")
        	.permitAll().defaultSuccessUrl("/main.zul", true)
        	.and()
   			.logout()
//        	.logoutSuccessHandler(LogoutSuccessHandler)
        	.permitAll()
        	.and()
        	.csrf()
        	.disable()
        	.sessionManagement()
        	.invalidSessionUrl("/index.zul?error=Session expired")
        	;
//        	.maximumSessions(2)
//        	.sessionRegistry(sessionRegistry)

	}
}
