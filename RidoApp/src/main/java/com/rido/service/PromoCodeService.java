package com.rido.service;

import java.math.BigDecimal;

import com.rido.entity.Booking;
import com.rido.entity.RentalBooking;

public interface PromoCodeService {
	 double applyPromoCode(String promoCode, double originalAmount);

	BigDecimal applyPromoCode1(String promoCode, BigDecimal originalAmount);


	//AADARSH KAUSHIK
	BigDecimal applyPromocode(String promoCode, BigDecimal baseAmount);

}