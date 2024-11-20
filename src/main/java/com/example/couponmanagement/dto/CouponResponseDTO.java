package com.example.couponmanagement.dto;

import com.example.couponmanagement.model.CouponType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponResponseDTO {
    private Long couponId;
    private CouponType type;
    private double discount; // The total amount of discount applied
    private String message; // Other Additional details regarding the discount application
}