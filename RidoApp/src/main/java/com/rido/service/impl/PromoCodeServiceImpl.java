package com.rido.service.impl;

import java.math.BigDecimal;

import java.util.Optional;

import java.util.HashMap;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rido.entity.Booking;
import com.rido.entity.PromoCode;
import com.rido.entity.UserLocation;
import com.rido.repository.BookingRepository;
import com.rido.repository.PromoCodeRepository;
import com.rido.service.PromoCodeService;
import com.rido.utils.CalculateDistance;

@Service
public class PromoCodeServiceImpl implements PromoCodeService {

    @Autowired	
	private BookingRepository bookingRepository;
   
    private CalculateDistance calculateDistance ;
    @Autowired	
   	private PromoCodeRepository promoCodeRepository;
	@Override
	public double applyPromoCode(String promoCode, double originalAmount) {
		// Implement logic to apply promo code and calculate discount
		// For simplicity, let's assume a fixed discount amount for each promo code
		if ("PROMO10".equals(promoCode)) {
			return originalAmount * 0.10; // 10% discount
		} else if ("PROMO20".equals(promoCode)) {
			return originalAmount * 0.20; // 20% discount
		} else {
			return 0; // No discount for invalid promo codes
		}
	}

	@Override
	public BigDecimal applyPromoCode1(String promoCode, BigDecimal originalAmount) {
		// Implement logic to apply promo code and calculate discount
		// For simplicity, let's assume a fixed discount amount for each promo code
		BigDecimal discount = BigDecimal.ZERO; // Initialize discount to zero

		if ("PROMO10".equals(promoCode)) {
			BigDecimal discountPercentage = new BigDecimal("0.10"); // 10% discount
			discount = originalAmount.multiply(discountPercentage);
		} else if ("PROMO20".equals(promoCode)) {
			BigDecimal discountPercentage = new BigDecimal("0.20"); // 20% discount
			discount = originalAmount.multiply(discountPercentage);
		}

		return discount;
	}

	//AADARSH KAUSHIK
	@Override
	public BigDecimal applyPromocode(String promoCode, BigDecimal baseAmount) {
		// Map promo codes to their corresponding discount percentages
		Map<String, BigDecimal> promoCodeDiscounts = new HashMap<>();
		promoCodeDiscounts.put("PROMO10", new BigDecimal("0.10")); // 10% discount
		promoCodeDiscounts.put("PROMO20", new BigDecimal("0.20")); // 20% discount

		// Look up the discount percentage for the given promo code
		BigDecimal discountPercentage = promoCodeDiscounts.getOrDefault(promoCode, BigDecimal.ZERO);

		// Calculate discount amount
		BigDecimal discount = baseAmount.multiply(discountPercentage);

		return discount;
	}
}