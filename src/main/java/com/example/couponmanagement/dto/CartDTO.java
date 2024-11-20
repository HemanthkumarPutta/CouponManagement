package com.example.couponmanagement.dto;

import com.example.couponmanagement.model.CartItem;
import lombok.Data;
import java.util.List;

@Data
public class CartDTO {
    private List<CartItem> items; //List of all the Cart Items
    private double total;
}
