package com.example.couponmanagement.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CartItem {
    private Long productId;
    private int quantity;
    private double price;
}