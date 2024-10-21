package com.titanium.data.jpa.entity;

import com.titanium.common.user.UserContextHolder;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

/**
 * 审计实体监听器
 */
public class BaseEntityListener implements AuditorAware<Long> {
    /**
     * Returns the current auditor of the application.
     * @return the current auditor.
     */
    @Override
    public Optional<Long> getCurrentAuditor() {
        return Optional.of(UserContextHolder.get().getUserId());
    }

    @PrePersist
    public void prePersist(Object entity) {
        if (entity instanceof NamedBaseEntity) {
            NamedBaseEntity base = (NamedBaseEntity) entity;
            base.setCreatorName(UserContextHolder.get().getUserName());
        }
    }

    @PreUpdate
    public void preUpdate(Object entity) {
        if (entity instanceof NamedBaseEntity) {
            NamedBaseEntity base = (NamedBaseEntity) entity;
            base.setReviserName(UserContextHolder.get().getUserName());
        }
    }
}
