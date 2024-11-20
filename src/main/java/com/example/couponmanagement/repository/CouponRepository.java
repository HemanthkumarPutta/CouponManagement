package com.example.couponmanagement.repository;

import com.example.couponmanagement.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByType(String type); //Finding a coupon by type

    boolean existsById(Long id);// CHecking coupin exists with Id
}