package com.example.couponmanagement.controller;

import com.example.couponmanagement.dto.CartDTO;
import com.example.couponmanagement.dto.CouponDTO;
import com.example.couponmanagement.model.Coupon;
import com.example.couponmanagement.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {
    private final CouponService cpnService;

    @PostMapping
    public ResponseEntity<Coupon> createCoupon(@RequestBody CouponDTO couponDTO) {
        return ResponseEntity.ok(cpnService.createCoupon(couponDTO));
    }

    @GetMapping
    public ResponseEntity<List<Coupon>> getAllCoupons() {
        return ResponseEntity.ok(cpnService.getAllCoupons());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Coupon> getCouponById(@PathVariable Long id) {
        return ResponseEntity.ok(cpnService.getCouponById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Coupon> updateCoupon(@PathVariable Long id, @RequestBody CouponDTO couponDTO) {
        return ResponseEntity.ok(cpnService.updateCoupon(id, couponDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        cpnService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/applicable-coupons")
    public ResponseEntity<List<Coupon>> getApplicableCoupons(@RequestBody CartDTO cartDTO) {
        return ResponseEntity.ok(cpnService.getApplicableCoupons(cartDTO));
    }

    @PostMapping("/apply-coupon/{id}")
    public ResponseEntity<CartDTO> applyCoupon(@PathVariable Long id, @RequestBody CartDTO cartDTO) {
        return ResponseEntity.ok(cpnService.applyCoupon(id, cartDTO));
    }
}