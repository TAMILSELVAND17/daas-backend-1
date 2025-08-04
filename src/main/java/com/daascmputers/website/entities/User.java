package com.daascmputers.website.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "users",indexes = {
        @Index(name ="idx_email",columnList = "email"),
        @Index(name = "idx_mobile",columnList = "mobile"),
        @Index(name = "idx_last_login_at",columnList = "lastLoginAt")
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String mobile;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;


    @PreUpdate
    public void onLoginUpdate() {
        this.lastLoginAt = LocalDateTime.now();
    }


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Accessory> accessories= new ArrayList<>();
}

