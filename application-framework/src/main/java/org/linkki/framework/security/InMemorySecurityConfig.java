/*******************************************************************************
 * Copyright (c) 2014 Faktor Zehn AG.
 *
 * Alle Rechte vorbehalten.
 *******************************************************************************/

package org.linkki.framework.security;

import javax.enterprise.util.AnnotationLiteral;

import org.linkki.util.cdi.BeanInstantiator;
import org.linkki.util.cdi.qualifier.InMemory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Class to easily provide a login for demo purpose.
 *
 * User and password are stored hard coded (see method
 * {@link #configure(AuthenticationManagerBuilder)}).
 */

@Configuration
@EnableWebSecurity
public class InMemorySecurityConfig extends AbstractSecurityConfig {

    /** The {@code @InMemory} annotation. */
    private static final AnnotationLiteral<InMemory> IN_MEMORY_ANNOTATION = new AnnotationLiteral<InMemory>() {
        private static final long serialVersionUID = 1L;
    };

    @Override
    public void configure(AuthenticationManagerBuilder auth) {
        // It seems that we cannot use auth.inMemoryAuthentication() if we need a special
        // UserDetailsSercive, thus we have to implement and use our own
        // InMemoryAuthenticationProvider...
        auth.authenticationProvider(inMemoryAuthenticationProvider());
    }

    private AuthenticationProvider inMemoryAuthenticationProvider() {
        InMemoryAuthenticationProvider authenticationProvider = new InMemoryAuthenticationProvider();
        authenticationProvider.setUserDetailsService(inMemoryUserDetailsService());
        return authenticationProvider;
    }

    private UserDetailsService inMemoryUserDetailsService() {
        return BeanInstantiator.getCDIInstance(UserDetailsService.class, IN_MEMORY_ANNOTATION);
    }
}