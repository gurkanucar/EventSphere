package com.gucardev.eventsphere.domain.auth.role.entity;

import com.gucardev.eventsphere.domain.auth.permission.entity.Permission;
import com.gucardev.eventsphere.domain.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
public class Role extends BaseEntity {

    public static final String ROLE_PREFIX = "ROLE_";

    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    public UUID id;

    @Column(unique = true, nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String displayName;

    @Column(length = 255)
    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_permissions", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions = new HashSet<>();

    public void setName(String name) {
        if (name == null) {
            this.name = null;
            return;
        }

        String upperName = name.toUpperCase();

        if (upperName.startsWith(ROLE_PREFIX)) {
            this.name = upperName.substring(ROLE_PREFIX.length());
        } else {
            this.name = upperName;
        }
    }
}
