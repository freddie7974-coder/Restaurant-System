package com.example.server.controller;

import com.example.server.model.Order;
import com.example.server.model.Tab;
import com.example.server.service.OrderService;
import com.example.server.service.TabService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Waiter-facing endpoints.
 *
 * The waiter's JavaFX app uses these to:
 *   - See which orders in their section are ready for delivery
 *   - See which tables have open tabs (still ordering)
 *   - See which tables have closed tabs (want to pay)
 *   - Get a table's full bill before collecting payment
 *
 * WebSocket subscriptions for waiters:
 *   /topic/waiter/all           — ORDER_READY, TAB_CLOSED, PAYMENT_COMPLETE
 *   /topic/waiter/{section}     — section-specific order events
 */
@RestController
@RequestMapping("/api/waiter")
@CrossOrigin
public class WaiterController {

    private final OrderService orderService;
    private final TabService   tabService;

    public WaiterController(OrderService orderService, TabService tabService) {
        this.orderService = orderService;
        this.tabService   = tabService;
    }

    /**
     * All orders currently READY for delivery in a section.
     * GET /api/waiter/ready/{section}
     *
     * The waiter sees this list and knows which tables to visit.
     * When they deliver, they call PUT /api/orders/{id} with COMPLETED.
     */
    @GetMapping("/ready/{section}")
    public List<Order> getReadyOrdersForSection(@PathVariable String section) {
        return orderService.getActiveOrdersBySection(section)
                .stream()
                .filter(o -> o.getStatus().name().equals("READY"))
                .toList();
    }

    /**
     * All active orders for a waiter's section (any status).
     * GET /api/waiter/section/{section}
     */
    @GetMapping("/section/{section}")
    public List<Order> getSectionOrders(@PathVariable String section) {
        return orderService.getActiveOrdersBySection(section);
    }

    /**
     * All tables with open tabs — still ordering.
     * GET /api/waiter/tabs/open
     */
    @GetMapping("/tabs/open")
    public List<Tab> getOpenTabs() {
        return tabService.getAllOpenTabs();
    }

    /**
     * Get the full bill for a specific table.
     * GET /api/waiter/table/{tableNumber}/bill
     *
     * Returns tab details + all orders + total amount.
     * Waiter shows this to the customer before payment.
     */
    @GetMapping("/table/{tableNumber}/bill")
    public ResponseEntity<Map<String, Object>> getTableBill(
            @PathVariable int tableNumber) {

        return tabService.getOpenTab(tableNumber)
                .map(tab -> {
                    double total = tabService.calculateTotal(tab.getId());
                    List<Order> orders = orderService.getOrdersByTable(tableNumber);
                    return ResponseEntity.ok(Map.of(
                        "tabId",       tab.getId(),
                        "tableNumber", tableNumber,
                        "totalAmount", total,
                        "status",      tab.getStatus().name(),
                        "orders",      orders,
                        "openedAt",    tab.getOpenedAt().toString()
                    ));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Overview of all tables — useful for the waiter's dashboard.
     * GET /api/waiter/tables/overview
     *
     * Shows each table's current status at a glance.
     */
    @GetMapping("/tables/overview")
    public List<Map<String, Object>> getTablesOverview() {
        return tabService.getAllOpenTabs().stream()
                .map(tab -> Map.<String, Object>of(
                    "tableNumber", tab.getTableNumber(),
                    "tabId",       tab.getId(),
                    "tabStatus",   tab.getStatus().name(),
                    "totalAmount", tabService.calculateTotal(tab.getId()),
                    "openedAt",    tab.getOpenedAt().toString()
                ))
                .toList();
    }
}
