package com.robotech.web.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class Security {

	private final CASH successHandler;

	private final CADH accessDeniedHandler;

	public Security(CASH successHandler, CADH accessDeniedHandler) {
		this.successHandler = successHandler;
		this.accessDeniedHandler = accessDeniedHandler;
	}

	@Bean
	AuthenticationManager authManger(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	PasswordEncoder passwordEn() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	WebSecurityCustomizer webCus() {
		return (web) -> web.ignoring().requestMatchers("/css/**", "/js/**", "/images/**", "/bootstrap-5.3.3/**",
				"/imagenes/**","/img/**");
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests((auth) -> auth
				.requestMatchers("/home/**", "/registrar", "/login", "/forgot-password", "/reset-password").permitAll()
				.requestMatchers("/admin/**").hasAuthority("Administrador").anyRequest().authenticated())
				.formLogin((login) -> login.loginPage("/login").permitAll().successHandler(successHandler)
						.failureUrl("/login?error=true"))
				.logout((logout) -> logout.logoutUrl("/logout").logoutSuccessUrl("/home").invalidateHttpSession(true)
						.deleteCookies("JSESSIONID").permitAll())
				.exceptionHandling(handling -> handling.accessDeniedHandler(accessDeniedHandler));
		return http.build();
	}

}
