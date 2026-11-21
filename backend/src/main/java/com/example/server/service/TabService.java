package com.example.server.service;

import com.example.server.model.Order;
import com.example.server.model.Tab;
import com.example.server.repository.OrderRepository;
import com.example.server.repository.TabRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Manages the full tab lifecycle:
 *   open tab → add orders → close tab → simulate payment
 */
@Service
public class TabService {

    private final TabRepository        tabRepository;
    private final OrderRepository      orderRepository;
    private final OrderMessagingService messagingService;

    public TabService(TabRepository tabRepository,
                      OrderRepository orderRepository,
                      OrderMessagingService messagingService) {
        this.tabRepository    = tabRepository;
        this.orderRepository  = orderRepository;
        this.messagingService = messagingService;
    }

    /**
     * Opens a new tab for a table.
     * Called automatically when the first order is placed,
     * or manually by the waiter.
     */
    public Tab openTab(int tableNumber) {
        // Only one open tab per table at a time
        Optional<Tab> existing = tabRepository
                .findByTableNumberAndStatus(tableNumber, Tab.TabStatus.OPEN);
        if (existing.isPresent()) {
            return existing.get();
        }
        Tab tab = new Tab(tableNumber);
        return tabRepository.save(tab);
    }

    /**
     * Returns the current open tab for a table,
     * including all orders and running total.
     */
    public Optional<Tab> getOpenTab(int tableNumber) {
        return tabRepository
                .findByTableNumberAndStatus(tableNumber, Tab.TabStatus.OPEN);
    }

    /**
     * Calculates and returns the total bill for a tab
     * by summing all order items.
     */
    public double calculateTotal(Long tabId) {
        List<Order> orders = orderRepository.findByTabId(tabId);
        return orders.stream()
                .flatMap(o -> o.getItems().stream())
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    /**
     * Customer closes the tab — requests the bill.
     * Updates total and marks tab as CLOSED.
     * Notifies waiter via WebSocket.
     */
    public Tab closeTab(int tableNumber) {
        Tab tab = tabRepository
                .findByTableNumberAndStatus(tableNumber, Tab.TabStatus.OPEN)
                .orElseThrow(() -> new RuntimeException("No open tab for table " + tableNumber));

        double total = calculateTotal(tab.getId());
        tab.setTotalAmount(total);
        tab.setStatus(Tab.TabStatus.CLOSED);
        tab.setClosedAt(LocalDateTime.now());
        Tab saved = tabRepository.save(tab);

        // Notify waiter that this table wants to pay
        messagingService.notifyTabClosed(saved);
        return saved;
    }

    /**
     * Simulates an M-Pesa STK Push payment.
     *
     * In a real implementation this would call the Safaricom
     * Daraja API and wait for the callback. Here we simulate:
     *   - Generate a fake transaction ID
     *   - Mark payment as PENDING (waiting for PIN)
     *   - After 3 seconds simulate success
     */
    public Map<String, Object> simulateMpesaPayment(Long tabId, String phoneNumber) {
        Tab tab = tabRepository.findById(tabId)
                .orElseThrow(() -> new RuntimeException("Tab not found"));

        // Set payment method and phone
        tab.setPaymentMethod(Tab.PaymentMethod.MPESA);
        tab.setMpesaPhone(phoneNumber);
        tab.setPaymentStatus(Tab.PaymentStatus.PENDING);
        tabRepository.save(tab);

        // Simulate STK Push sent to phone
        // In real Daraja integration this is where you call:
        // POST https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/processrequest
        String simulatedCheckoutId = "ws_CO_" + System.currentTimeMillis();

        // Simulate successful payment after short delay
        simulatePaymentSuccess(tabId, simulatedCheckoutId);

        return Map.of(
            "success",         true,
            "message",         "STK Push sent to " + phoneNumber + ". Enter your M-Pesa PIN.",
            "checkoutRequestId", simulatedCheckoutId,
            "amount",          tab.getTotalAmount(),
            "simulated",       true
        );
    }

    /**
     * Simulates a card payment.
     *
     * In a real implementation this would call Flutterwave or Stripe.
     * Here we simulate a successful charge.
     */
    public Map<String, Object> simulateCardPayment(Long tabId, String cardLastFour) {
        Tab tab = tabRepository.findById(tabId)
                .orElseThrow(() -> new RuntimeException("Tab not found"));

        tab.setPaymentMethod(Tab.PaymentMethod.CARD);
        tab.setPaymentStatus(Tab.PaymentStatus.PENDING);
        tabRepository.save(tab);

        // Simulate card charge success
        // In real Flutterwave integration:
        // POST https://api.flutterwave.com/v3/charges?type=card
        simulatePaymentSuccess(tabId, "FLW_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        return Map.of(
            "success",    true,
            "message",    "Card ending in " + cardLastFour + " charged successfully.",
            "amount",     tab.getTotalAmount(),
            "simulated",  true
        );
    }

    /**
     * Cash payment — waiter collects payment and marks as paid.
     */
    public Map<String, Object> processCashPayment(Long tabId) {
        Tab tab = tabRepository.findById(tabId)
                .orElseThrow(() -> new RuntimeException("Tab not found"));

        tab.setPaymentMethod(Tab.PaymentMethod.CASH);
        tab.setPaymentStatus(Tab.PaymentStatus.PAID);
        tab.setStatus(Tab.TabStatus.PAID);
        Tab saved = tabRepository.save(tab);

        messagingService.notifyPaymentComplete(saved);

        return Map.of(
            "success", true,
            "message", "Cash payment recorded. Table " + tab.getTableNumber() + " cleared.",
            "amount",  tab.getTotalAmount()
        );
    }

    /**
     * Internal helper — simulates payment confirmation arriving.
     * In production this would be triggered by a webhook callback
     * from Daraja or Flutterwave, not called directly.
     */
    private void simulatePaymentSuccess(Long tabId, String transactionId) {
        Tab tab = tabRepository.findById(tabId).orElse(null);
        if (tab == null) return;

        tab.setPaymentStatus(Tab.PaymentStatus.PAID);
        tab.setStatus(Tab.TabStatus.PAID);
        tab.setMpesaTransactionId(transactionId);
        Tab saved = tabRepository.save(tab);

        // Notify waiter and kitchen that table is paid and ready to clear
        messagingService.notifyPaymentComplete(saved);
    }

    public List<Tab> getAllOpenTabs() {
        return tabRepository.findByStatus(Tab.TabStatus.OPEN);
    }

    public Optional<Tab> getTabById(Long id) {
        return tabRepository.findById(id);
    }
}
