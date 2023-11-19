package org.kirkiano.rpg.bank.model;

import java.time.Instant;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


/**
 * Abstract base class of JPA entities
 */
@SuppressWarnings("NotNullFieldNotInitialized")
@Getter
@MappedSuperclass
public abstract class BaseEntity {

    /**
     * Unique numeric identifier
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    protected Long id; // boxed for nullability


    /**
     * Instant of creation
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    protected Instant createdAt;


    /**
     * Instant of last modification
     */
    @UpdateTimestamp
    @Column(name = "modified_at", nullable = false)
    protected Instant modifiedAt;


    /**
     * Version number (in case optimistic locking is used in future)
     */
    @Column(nullable = false)
    @Version
    protected int version;
}
