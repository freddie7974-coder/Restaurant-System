package com.example.server.controller;

import com.example.server.model.OrderStatus;
import com.example.server.service.OrderService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class WebSocketController {

    private final OrderService orderService;

    public WebSocketController(OrderService orderService) {
        this.orderService = orderService;
    }

    @MessageMapping("/orders/status/{orderId}")
    @SendTo("/topic/orders/kitchen")
    public Map<String, Object> updateOrderStatus(
            @DestinationVariable Long orderId,
            Map<String, Object> message) {

        String statusStr = (String) message.get("status");
        try {
            OrderStatus newStatus = OrderStatus.valueOf(statusStr);
            orderService.updateOrderStatus(orderId, newStatus);
            return Map.of("eventType","STATUS_UPDATE_ACK","orderId",orderId,
                          "status",statusStr,"success",true);
        } catch (IllegalArgumentException e) {
            return Map.of("eventType","ERROR","message",
                          "Invalid status: " + statusStr,"success",false);
        }
    }

    @MessageMapping("/ping")
    @SendTo("/topic/pong")
    public Map<String, Object> ping(Map<String, Object> message) {
        return Map.of("eventType","PONG","timestamp",
                      System.currentTimeMillis(),"echo",message);
    }
}
