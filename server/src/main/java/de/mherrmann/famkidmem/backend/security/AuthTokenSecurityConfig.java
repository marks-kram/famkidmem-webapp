package de.mherrmann.famkidmem.backend.security;

import de.mherrmann.famkidmem.backend.Application;
import de.mherrmann.famkidmem.backend.utils.Bcrypt;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
@EnableWebSecurity
@Order(1)
public class AuthTokenSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception
    {
        PreAuthTokenHeaderFilter filter = new PreAuthTokenHeaderFilter("CCMS-AUTH-TOKEN");

        filter.setAuthenticationManager(authentication -> {
            String principal = (String) authentication.getPrincipal();
            String principalHash = Bcrypt.hash("wrong");
            try {
               principalHash = new String(Files.readAllBytes(Paths.get(Application.filesDir+"ccms_auth_token_hash")));
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (!Bcrypt.check(principal, principalHash))
            {
                throw new BadCredentialsException("The API key was not found "
                        + "or not the expected value.");
            }
            authentication.setAuthenticated(true);
            return authentication;
        });

        httpSecurity.
                antMatcher("/ccms/**")
                .csrf()
                .disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(filter)
                .addFilterBefore(new ExceptionTranslationFilter(
                                new Http403ForbiddenEntryPoint()),
                        filter.getClass()
                )
                .authorizeRequests()
                .anyRequest()
                .authenticated();
    }

}
