package com.example.server.controller;

import com.example.server.model.Tab;
import com.example.server.service.TabService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Handles the full payment flow from close-tab to payment confirmation.
 *
 * Flow:
 *   1. POST /api/payment/close-tab/{tableNumber}   — customer done ordering
 *   2. GET  /api/payment/tab/{tabId}               — get bill summary
 *   3. POST /api/payment/mpesa/{tabId}             — pay with M-Pesa (simulated)
 *      POST /api/payment/card/{tabId}              — pay with card (simulated)
 *      POST /api/payment/cash/{tabId}              — pay with cash
 */
@RestController
@RequestMapping("/api/payment")
@CrossOrigin
public class PaymentController {

    private final TabService tabService;

    public PaymentController(TabService tabService) {
        this.tabService = tabService;
    }

    /**
     * Customer taps "Close Tab" on the tablet.
     * POST /api/payment/close-tab/{tableNumber}
     *
     * Calculates the total, marks tab as CLOSED,
     * and sends a WebSocket notification to all waiters.
     */
    @PostMapping("/close-tab/{tableNumber}")
    public ResponseEntity<Tab> closeTab(@PathVariable int tableNumber) {
        try {
            Tab closed = tabService.closeTab(tableNumber);
            return ResponseEntity.ok(closed);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get tab details and total bill.
     * GET /api/payment/tab/{tabId}
     */
    @GetMapping("/tab/{tabId}")
    public ResponseEntity<Tab> getTab(@PathVariable Long tabId) {
        return tabService.getTabById(tabId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Simulate M-Pesa STK Push payment.
     * POST /api/payment/mpesa/{tabId}
     * Body: {"phoneNumber": "0712345678"}
     *
     * Simulates sending an STK push to the customer's phone.
     * In production this calls the Safaricom Daraja API.
     * Response tells the tablet to show "Enter your M-Pesa PIN".
     */
    @PostMapping("/mpesa/{tabId}")
    public ResponseEntity<Map<String, Object>> payWithMpesa(
            @PathVariable Long tabId,
            @RequestBody MpesaRequest request) {

        try {
            Map<String, Object> result =
                    tabService.simulateMpesaPayment(tabId, request.getPhoneNumber());
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * Simulate card payment.
     * POST /api/payment/card/{tabId}
     * Body: {"cardLastFour": "4242"}
     *
     * Simulates a card charge via Flutterwave/Stripe.
     * In production this calls the payment gateway API.
     */
    @PostMapping("/card/{tabId}")
    public ResponseEntity<Map<String, Object>> payWithCard(
            @PathVariable Long tabId,
            @RequestBody CardRequest request) {

        try {
            Map<String, Object> result =
                    tabService.simulateCardPayment(tabId, request.getCardLastFour());
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * Cash payment — waiter collects and confirms.
     * POST /api/payment/cash/{tabId}
     */
    @PostMapping("/cash/{tabId}")
    public ResponseEntity<Map<String, Object>> payWithCash(@PathVariable Long tabId) {
        try {
            Map<String, Object> result = tabService.processCashPayment(tabId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // DTOs
    public static class MpesaRequest {
        private String phoneNumber;
        public String getPhoneNumber()          { return phoneNumber; }
        public void setPhoneNumber(String p)    { this.phoneNumber = p; }
    }

    public static class CardRequest {
        private String cardLastFour;
        public String getCardLastFour()         { return cardLastFour; }
        public void setCardLastFour(String c)   { this.cardLastFour = c; }
    }
}
