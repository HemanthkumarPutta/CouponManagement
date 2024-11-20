package com.example.couponmanagement.service;

import com.example.couponmanagement.dto.CartDTO;
import com.example.couponmanagement.dto.CouponDTO;
import com.example.couponmanagement.exception.ResourceNotFoundException;
import com.example.couponmanagement.model.Coupon;
import com.example.couponmanagement.repository.CouponRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import com.example.couponmanagement.model.CartItem;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepo;
    private final ObjectMapper objMapper;

    public Coupon createCoupon(CouponDTO cpnDto) {
        Coupon coupon = new Coupon();
        coupon.setType(cpnDto.getType());
        coupon.setDetails(cpnDto.getDetails());
        coupon.setRepetitionLimit(cpnDto.getRepetitionLimit());
        coupon.setExpirationDate(LocalDateTime.parse(cpnDto.getExpirationDate()));
        return couponRepo.save(coupon);
    }

    public List<Coupon> getAllCoupons() {//Gets all the available coupons from Repo
        return couponRepo.findAll();
    }

    public Coupon getCouponById(Long id) {//Get the coupon by Id
        return couponRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon did not found with the ID: " + id));
    }

    public Coupon updateCoupon(Long id, CouponDTO couponDTO) {//Update coupon details
        Coupon coupon = getCouponById(id);
        coupon.setType(couponDTO.getType());
        coupon.setDetails(couponDTO.getDetails());
        coupon.setRepetitionLimit(couponDTO.getRepetitionLimit());
        coupon.setExpirationDate(LocalDateTime.parse(couponDTO.getExpirationDate()));
        return couponRepo.save(coupon);
    }

    public void deleteCoupon(Long id) {//Delete coupon with id
        Coupon cpn = getCouponById(id);
        couponRepo.delete(cpn);
    }

    public List<Coupon> getApplicableCoupons(CartDTO cartDTO) {
        return couponRepo.findAll().stream()
                .filter(cpn -> isCouponApplicable(cpn, cartDTO))//Check applicable or not
                .toList();
    }

    public CartDTO applyCoupon(Long id, CartDTO cartDTO) {
        Coupon cpn = getCouponById(id);
        if (!isCouponApplicable(cpn, cartDTO)) {
            throw new IllegalArgumentException("THis Coupon is not applicable");
        }

        switch (cpn.getType()) {
            case CART_WISE:
                applyCartWise(cpn, cartDTO);
                break;
            case PRODUCT_WISE:
                applyProductWise(cpn, cartDTO);
                break;
            case BXGY:
                applyBxGy(cpn, cartDTO);
                break;
        }

        return cartDTO;
    }

    private boolean isCouponApplicable(Coupon cpn, CartDTO cartDTO) {
        // Check the expiration and return
        if (cpn.getExpirationDate().isBefore(LocalDateTime.now())) {
            return false;
        }
        return true;
    }

    private void applyCartWise(Coupon cpn, CartDTO cartDTO) {
        try {
            JsonNode details = objMapper.readTree(cpn.getDetails());
            double threshold = details.get("threshold").asDouble();
            double discountPercentage = details.get("discount").asDouble();

            if (cartDTO.getTotal() > threshold) {
                double discount = cartDTO.getTotal() * (discountPercentage / 100);
                cartDTO.setTotal(cartDTO.getTotal() - discount);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid cartwise coupon details");
        }
    }

    private void applyProductWise(Coupon cpn, CartDTO cartDTO) {
        try {
            JsonNode details = objMapper.readTree(cpn.getDetails());
            long productId = details.get("product_id").asLong();
            double discountPercentage = details.get("discount").asDouble();

            for (CartItem item : cartDTO.getItems()) {
                if (item.getProductId() == productId) {
                    double discount = item.getPrice() * (discountPercentage / 100);
                    item.setPrice(item.getPrice() - discount);
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid productwise coupon details");
        }
    }

    private void applyBxGy(Coupon cpn, CartDTO cartDTO) {
        try {
            JsonNode details = objMapper.readTree(cpn.getDetails());
            JsonNode buyProducts = details.get("buy_products");
            JsonNode getProducts = details.get("get_products");
            int repetitionLimit = details.get("repition_limit").asInt();

            int maxApplicableTimes = Integer.MAX_VALUE;

            // Calculating how many times the "buy" condition is satisfied
            for (JsonNode buyProduct : buyProducts) {
                long productId = buyProduct.get("product_id").asLong();
                int requiredQuantity = buyProduct.get("quantity").asInt();

                int availableQuantity = cartDTO.getItems().stream()
                        .filter(item -> item.getProductId() == productId)
                        .mapToInt(CartItem::getQuantity)
                        .sum();

                maxApplicableTimes = Math.min(maxApplicableTimes, availableQuantity / requiredQuantity);
            }

            // The Limit to the repetition limit
            maxApplicableTimes = Math.min(maxApplicableTimes, repetitionLimit);

            // Apply the "get" products for free
            for (JsonNode getProduct : getProducts) {
                long productId = getProduct.get("product_id").asLong();
                int freeQuantity = getProduct.get("quantity").asInt() * maxApplicableTimes;

                cartDTO.getItems().stream()
                        .filter(item -> item.getProductId() == productId)
                        .findFirst()
                        .ifPresentOrElse(
                                item -> item.setQuantity(item.getQuantity() + freeQuantity),
                                () -> {
                                    CartItem newItem = new CartItem();
                                    newItem.setProductId(productId);
                                    newItem.setQuantity(freeQuantity);
                                    newItem.setPrice(0); // Free items
                                    cartDTO.getItems().add(newItem);
                                }
                        );
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid BxGy coupon details");
        }
    }
}