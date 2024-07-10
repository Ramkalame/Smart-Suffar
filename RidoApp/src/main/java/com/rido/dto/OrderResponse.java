package com.rido.dto;
import com.rido.entity.enums.CourierOrderStatus;
import lombok.Data;
import java.util.Map;

@Data
public class OrderResponse {
    private String orderTrackingNumber;
    private CourierOrderStatus status;
    private String message;
    private Map<String,Object> orderData;
}