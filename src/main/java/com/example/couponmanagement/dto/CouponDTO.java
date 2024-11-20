package com.example.couponmanagement.dto;

import lombok.Data;
import com.example.couponmanagement.model.CouponType;

@Data
public class CouponDTO {
    private CouponType type;
    private String details; // This is for the JSON details
    private Integer repetitionLimit;
    private String expirationDate;
}