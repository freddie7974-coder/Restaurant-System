package com.example.server.service;

import com.example.server.model.Order;
import com.example.server.model.Tab;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Handles all real-time WebSocket messaging.
 *
 * Destinations:
 *   /topic/orders/kitchen          — kitchen display
 *   /topic/orders/table/{N}        — specific tablet
 *   /topic/waiter/{section}        — waiter for that section
 *   /topic/waiter/all              — all waiters
 */
@Service
public class OrderMessagingService {

    private final SimpMessagingTemplate messagingTemplate;

    private static final String KITCHEN_DEST  = "/topic/orders/kitchen";
    private static final String TABLE_DEST    = "/topic/orders/table/%s";
    private static final String WAITER_DEST   = "/topic/waiter/%s";
    private static final String WAITER_ALL    = "/topic/waiter/all";

    public OrderMessagingService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /** New order — notify kitchen */
    public void notifyKitchenNewOrder(Order order) {
        Map<String, Object> payload = Map.of(
            "eventType", "NEW_ORDER",
            "order",      order
        );
        messagingTemplate.convertAndSend(KITCHEN_DEST, payload);

        // Also notify the waiter for this section
        if (order.getWaiterSection() != null) {
            messagingTemplate.convertAndSend(
                String.format(WAITER_DEST, order.getWaiterSection()), payload);
        }
        System.out.println("WebSocket: NEW_ORDER — table " + order.getTableNumber());
    }

    /** Status update — notify kitchen + tablet + waiter */
    public void notifyStatusUpdate(Order order) {
        Map<String, Object> payload = Map.of(
            "eventType", "STATUS_UPDATE",
            "order",      order
        );
        messagingTemplate.convertAndSend(KITCHEN_DEST, payload);
        messagingTemplate.convertAndSend(
            String.format(TABLE_DEST, order.getTableNumber()), payload);

        // Notify waiter when order is READY for collection
        if (order.getStatus() != null &&
                order.getStatus().name().equals("READY")) {
            messagingTemplate.convertAndSend(WAITER_ALL, Map.of(
                "eventType",   "ORDER_READY",
                "orderId",     order.getId(),
                "tableNumber", order.getTableNumber(),
                "message",     "Order for table " + order.getTableNumber() + " is ready!"
            ));
        }
        System.out.println("WebSocket: STATUS_UPDATE ("
                + order.getStatus() + ") — table " + order.getTableNumber());
    }

    /** Tab closed — notify all waiters to go collect payment */
    public void notifyTabClosed(Tab tab) {
        messagingTemplate.convertAndSend(WAITER_ALL, Map.of(
            "eventType",   "TAB_CLOSED",
            "tabId",       tab.getId(),
            "tableNumber", tab.getTableNumber(),
            "totalAmount", tab.getTotalAmount(),
            "message",     "Table " + tab.getTableNumber()
                           + " wants to pay. Total: KES " + tab.getTotalAmount()
        ));
        System.out.println("WebSocket: TAB_CLOSED — table "
                + tab.getTableNumber() + " KES " + tab.getTotalAmount());
    }

    /** Payment confirmed — notify waiter to clear the table */
    public void notifyPaymentComplete(Tab tab) {
        Map<String, Object> payload = Map.of(
            "eventType",     "PAYMENT_COMPLETE",
            "tabId",         tab.getId(),
            "tableNumber",   tab.getTableNumber(),
            "totalAmount",   tab.getTotalAmount(),
            "paymentMethod", tab.getPaymentMethod() != null
                             ? tab.getPaymentMethod().name() : "UNKNOWN",
            "message",       "Table " + tab.getTableNumber()
                             + " paid via " + tab.getPaymentMethod()
                             + ". Please clear the table."
        );
        messagingTemplate.convertAndSend(WAITER_ALL, payload);
        messagingTemplate.convertAndSend(
            String.format(TABLE_DEST, tab.getTableNumber()), payload);
        System.out.println("WebSocket: PAYMENT_COMPLETE — table " + tab.getTableNumber());
    }

    /** Heartbeat every 20 seconds */
    @Scheduled(fixedRate = 20_000)
    public void sendHeartbeat() {
        Map<String, Object> ping = Map.of(
            "eventType", "HEARTBEAT",
            "timestamp",  System.currentTimeMillis()
        );
        messagingTemplate.convertAndSend(KITCHEN_DEST, ping);
        messagingTemplate.convertAndSend(WAITER_ALL,   ping);
    }
}
