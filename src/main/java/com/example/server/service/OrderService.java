package com.example.server.service;

import com.example.server.model.Order;
import com.example.server.model.OrderStatus;
import com.example.server.model.Tab;
import com.example.server.repository.OrderRepository;
import com.example.server.repository.TabRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository       orderRepository;
    private final TabRepository         tabRepository;
    private final OrderMessagingService messagingService;

    public OrderService(OrderRepository orderRepository,
                        TabRepository tabRepository,
                        OrderMessagingService messagingService) {
        this.orderRepository  = orderRepository;
        this.tabRepository    = tabRepository;
        this.messagingService = messagingService;
    }

    /**
     * Places a new order.
     * Automatically links it to the open tab for this table,
     * creating one if it doesn't exist yet.
     */
    public Order placeOrder(Order newOrder) {
        newOrder.setStatus(OrderStatus.PENDING);

        // Find or create an open tab for this table
        Tab tab = tabRepository
                .findByTableNumberAndStatus(newOrder.getTableNumber(), Tab.TabStatus.OPEN)
                .orElseGet(() -> tabRepository.save(new Tab(newOrder.getTableNumber())));

        newOrder.setTabId(tab.getId());

        Order saved = orderRepository.save(newOrder);
        messagingService.notifyKitchenNewOrder(saved);
        return saved;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getActiveOrders() {
        return orderRepository.findByStatusIn(List.of(
                OrderStatus.PENDING, OrderStatus.PREPARING, OrderStatus.READY));
    }

    public List<Order> getOrdersByTable(int tableNumber) {
        return orderRepository.findByTableNumber(tableNumber);
    }

    public List<Order> getActiveOrdersBySection(String section) {
        return orderRepository.findByStatusInAndWaiterSection(
                List.of(OrderStatus.PENDING, OrderStatus.PREPARING, OrderStatus.READY),
                section);
    }

    public Order updateOrderStatus(Long id, OrderStatus newStatus) {
        return orderRepository.findById(id)
                .map(existing -> {
                    existing.setStatus(newStatus);
                    Order saved = orderRepository.save(existing);
                    messagingService.notifyStatusUpdate(saved);
                    return saved;
                })
                .orElse(null);
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }
}
