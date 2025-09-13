package com.coffee.coffee_api;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users", indexes = { @Index(columnList = "email") })
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name="full_name", nullable=false, length=120)
  private String fullName;

  @Column(nullable=false, unique=true, length=191)
  private String email;

  @Column(name="password_hash", nullable=false, length=255)
  private String passwordHash;

  @Enumerated(EnumType.STRING)              
  @Column(nullable=false, length=16)
  @Builder.Default
  private Role role = Role.CUSTOMER;        

  @Column(name="is_active")
  @Builder.Default
  private Boolean isActive = true;
}
