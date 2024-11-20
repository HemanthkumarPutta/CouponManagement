package com.example.couponmanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CouponType type;//Enum of all the coupon types available

    @Column(columnDefinition = "TEXT")
    private String details;

    private Integer repetitionLimit;

    private LocalDateTime expirationDate;
}
