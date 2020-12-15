package com.example.bookfinder.config;

import com.example.bookfinder.model.login.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        //http.authorizeRequests().antMatchers("/").permitAll();
        http.authorizeRequests().antMatchers("/login", "login/createuser").permitAll().antMatchers("/").access("hasAuthority('1')")
                .and().formLogin().loginPage("/login").usernameParameter("username").passwordParameter("password").defaultSuccessUrl("/listen")
                .and().logout().logoutSuccessUrl("/login");

    }

    @Autowired
    public void login(AuthenticationManagerBuilder am) throws Exception {
        am.jdbcAuthentication().dataSource(dataSource)
                .usersByUsernameQuery("SELECT username, password, active FROM user where username = ?").authoritiesByUsernameQuery("select username, role from user where username = ?");
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
