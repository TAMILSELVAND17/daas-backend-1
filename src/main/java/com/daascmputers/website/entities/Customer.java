package com.daascmputers.website.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer",indexes = {
        @Index(name = "idx_name",columnList = "name"),
        @Index(name = "idx_email",columnList = "email"),
        @Index(name = "idx_mobile",columnList = "mobile")
})
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int customerID;

    @NotBlank(message = "Name is mandatory")
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is mandatory")
    @Column(unique = true)
    private String email;


    //  @Digits(integer = 15, fraction = 0, message = "Mobile number must be numeric and up to 15 digits")
    @NotNull(message = "Mobile number is required")
    private Long mobile;

    @NotBlank(message = "Message cannot be blank")
    private String message;
}
