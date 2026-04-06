package com.example.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Living API reference — teammates call GET /api/docs to see
 * every available endpoint, its method, body format, and what
 * it returns. No external Swagger dependency needed.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiDocsController {

    @GetMapping("/docs")
    public ResponseEntity<Map<String, Object>> getDocs() {
        return ResponseEntity.ok(Map.of(
            "service",  "Restaurant Self-Ordering System",
            "version",  "2.0",
            "baseUrl",  "http://localhost:8080",
            "webSocket", Map.of(
                "connectUrl",   "ws://localhost:8080/ws/restaurant",
                "connectHeaders", Map.of(
                    "tableNumber", "integer — the table number",
                    "clientType",  "TABLET | KITCHEN | WAITER"
                ),
                "subscriptions", Map.of(
                    "kitchen",  "/topic/orders/kitchen",
                    "tablet",   "/topic/orders/table/{tableNumber}",
                    "waiter",   "/topic/waiter/all  or  /topic/waiter/{section}"
                ),
                "eventTypes", List.of(
                    "NEW_ORDER", "STATUS_UPDATE", "ORDER_READY",
                    "TAB_CLOSED", "PAYMENT_COMPLETE", "HEARTBEAT"
                )
            ),
            "endpoints", List.of(

                // --- HEALTH ---
                Map.of("method","GET","path","/api/health",
                    "description","Server health check",
                    "body","none",
                    "returns","{ status, service, timestamp }"),

                // --- MENU ---
                Map.of("method","GET","path","/api/menu",
                    "description","Available menu items (tablet view)",
                    "body","none",
                    "returns","[ { id, name, description, price, available, category } ]"),

                Map.of("method","GET","path","/api/menu/all",
                    "description","All items including unavailable (kitchen view)",
                    "body","none",
                    "returns","[ MenuItem ]"),

                Map.of("method","POST","path","/api/menu",
                    "description","Kitchen adds a new menu item",
                    "body","{ name*, description, price*, category }",
                    "returns","201 Created — MenuItem"),

                Map.of("method","PUT","path","/api/menu/{id}",
                    "description","Kitchen updates an existing item",
                    "body","{ name, description, price, category }",
                    "returns","200 OK — updated MenuItem"),

                Map.of("method","DELETE","path","/api/menu/{id}",
                    "description","Kitchen permanently removes an item",
                    "body","none",
                    "returns","204 No Content"),

                Map.of("method","PATCH","path","/api/menu/{id}/toggle",
                    "description","Kitchen marks item available/unavailable",
                    "body","none",
                    "returns","200 OK — MenuItem with new available status"),

                // --- ORDERS ---
                Map.of("method","POST","path","/api/orders",
                    "description","Tablet places a new order",
                    "body","{ tableNumber*, items*: [ { name*, quantity*, customization, price } ] }",
                    "returns","201 Created — Order with id, status=PENDING, tabId"),

                Map.of("method","GET","path","/api/orders",
                    "description","Full order history",
                    "body","none",
                    "returns","[ Order ]"),

                Map.of("method","GET","path","/api/orders/active",
                    "description","Active orders only (PENDING/PREPARING/READY)",
                    "body","none",
                    "returns","[ Order ]"),

                Map.of("method","GET","path","/api/orders/table/{tableNumber}",
                    "description","All orders for a specific table",
                    "body","none",
                    "returns","[ Order ]"),

                Map.of("method","GET","path","/api/orders/section/{section}",
                    "description","Active orders for a waiter section",
                    "body","none",
                    "returns","[ Order ]"),

                Map.of("method","PUT","path","/api/orders/{id}",
                    "description","Kitchen updates order status",
                    "body","{ status: PENDING|PREPARING|READY|COMPLETED|CANCELLED }",
                    "returns","200 OK — updated Order"),

                // --- WAITER ---
                Map.of("method","GET","path","/api/waiter/ready/{section}",
                    "description","Orders ready for delivery in a section",
                    "body","none",
                    "returns","[ Order ] — all with status=READY"),

                Map.of("method","GET","path","/api/waiter/section/{section}",
                    "description","All active orders for a section",
                    "body","none",
                    "returns","[ Order ]"),

                Map.of("method","GET","path","/api/waiter/tabs/open",
                    "description","All tables with open tabs",
                    "body","none",
                    "returns","[ Tab ]"),

                Map.of("method","GET","path","/api/waiter/table/{tableNumber}/bill",
                    "description","Full bill for a table",
                    "body","none",
                    "returns","{ tabId, tableNumber, totalAmount, orders, openedAt }"),

                Map.of("method","GET","path","/api/waiter/tables/overview",
                    "description","Dashboard overview of all open tables",
                    "body","none",
                    "returns","[ { tableNumber, tabId, tabStatus, totalAmount, openedAt } ]"),

                // --- PAYMENT ---
                Map.of("method","POST","path","/api/payment/close-tab/{tableNumber}",
                    "description","Customer closes tab and requests bill",
                    "body","none",
                    "returns","Tab with status=CLOSED and totalAmount"),

                Map.of("method","GET","path","/api/payment/tab/{tabId}",
                    "description","Get tab details",
                    "body","none",
                    "returns","Tab"),

                Map.of("method","POST","path","/api/payment/mpesa/{tabId}",
                    "description","Simulate M-Pesa STK Push payment",
                    "body","{ phoneNumber: '0712345678' }",
                    "returns","{ success, message, checkoutRequestId, amount, simulated }"),

                Map.of("method","POST","path","/api/payment/card/{tabId}",
                    "description","Simulate card payment",
                    "body","{ cardLastFour: '4242' }",
                    "returns","{ success, message, amount, simulated }"),

                Map.of("method","POST","path","/api/payment/cash/{tabId}",
                    "description","Record cash payment",
                    "body","none",
                    "returns","{ success, message, amount }")
            )
        ));
    }
}
