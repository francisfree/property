package com.castle.property.application.config.audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Supplies the current user for JPA auditing (@CreatedBy / @LastModifiedBy).
 *
 * <p>Authentication is not yet implemented, so this returns a fixed "system"
 * identifier. When authentication is added, replace this with the authenticated
 * principal (e.g. a username or UUID from Spring Security).</p>
 */
@Component("auditorProvider")
public class AuditorAwareImpl implements AuditorAware<String> {

    private static final String SYSTEM_USER = "system";

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(SYSTEM_USER);
    }
}
