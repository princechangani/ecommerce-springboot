package com.project.service;

import com.project.entity.*;
import com.project.enums.InventoryTransactionType;
import com.project.enums.OrderStatus;
import com.project.enums.PaymentStatus;
import com.project.exception.ResourceNotFound;
import com.project.repository.*;
import com.project.util.PaginatedResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private InventoryTransactionRepository inventoryTransactionRepository;

    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Order> getUserOrdersByStatus(Long userId, OrderStatus status) {
        return orderRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status);
    }

    public PaginatedResult<Order> getUserOrdersPaginated(Long userId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Order> orderPage = orderRepository.findByUserId(userId, pageRequest);
        
        return new PaginatedResult<>(
                orderPage.getContent(),
                orderPage.getTotalElements(),
                page,
                size
        );
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFound("Order not found"));
    }

    public Order getOrderByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFound("Order not found"));
    }

    @Transactional
    public Order createOrder(Long userId, List<CartItem> cartItems, Address shippingAddress, Address billingAddress) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setOrderNumber(generateOrderNumber());
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);

        // Copy shipping address
        copyAddressToOrder(shippingAddress, order, "shipping");

        // Copy billing address
        copyAddressToOrder(billingAddress, order, "billing");

        // Calculate totals
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            subtotal = subtotal.add(cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        order.setSubtotal(subtotal);
        order.setTotalAmount(subtotal); // Simplified - add tax, shipping, etc. as needed
        order = orderRepository.save(order);

        // Create order items
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(cartItem.getProduct().getPrice());
            orderItem.setTotalPrice(cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));

            // Product snapshot
            orderItem.setProductName(cartItem.getProduct().getName());
            orderItem.setProductSku(cartItem.getProduct().getSku());
            orderItem.setProductDescription(cartItem.getProduct().getDescription());

            orderItemRepository.save(orderItem);

            // Update inventory
            InventoryTransaction transaction = new InventoryTransaction();
            transaction.setProduct(cartItem.getProduct());
            transaction.setType(InventoryTransactionType.SALE);
            transaction.setQuantityChange(-cartItem.getQuantity());
            transaction.setReferenceId(order.getId());
            transaction.setReferenceType("order");
            transaction.setNotes("Order: " + order.getOrderNumber());
            transaction.setCreatedBy(user);
            
            inventoryTransactionRepository.save(transaction);
        }

        // Clear cart
        cartItemRepository.deleteByUserId(userId);

        return order;
    }

    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        
        if (status == OrderStatus.SHIPPED) {
            order.setShippedAt(LocalDateTime.now());
        } else if (status == OrderStatus.DELIVERED) {
            order.setDeliveredAt(LocalDateTime.now());
        }
        
        return orderRepository.save(order);
    }

    public Order updatePaymentStatus(Long orderId, PaymentStatus status) {
        Order order = getOrderById(orderId);
        order.setPaymentStatus(status);
        return orderRepository.save(order);
    }

    public List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(startDate, endDate);
    }

    public List<Order> getOrdersByDateRangeAndStatus(LocalDateTime startDate, LocalDateTime endDate, OrderStatus status) {
        return orderRepository.findByCreatedAtBetweenAndStatusOrderByCreatedAtDesc(startDate, endDate, status);
    }

    public BigDecimal getTotalSalesAmount(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.getTotalSalesAmount(startDate, endDate, OrderStatus.CANCELLED, OrderStatus.RETURNED);
    }

    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis();
    }

    private void copyAddressToOrder(Address address, Order order, String type) {
        if ("shipping".equals(type)) {
            order.setShippingFirstName(address.getFirstName());
            order.setShippingLastName(address.getLastName());
            order.setShippingCompany(address.getCompany());
            order.setShippingAddressLine1(address.getAddressLine1());
            order.setShippingAddressLine2(address.getAddressLine2());
            order.setShippingCity(address.getCity());
            order.setShippingState(address.getState());
            order.setShippingPostalCode(address.getPostalCode());
            order.setShippingCountry(address.getCountry());
            order.setShippingPhone(address.getPhone());
        } else if ("billing".equals(type)) {
            order.setBillingFirstName(address.getFirstName());
            order.setBillingLastName(address.getLastName());
            order.setBillingCompany(address.getCompany());
            order.setBillingAddressLine1(address.getAddressLine1());
            order.setBillingAddressLine2(address.getAddressLine2());
            order.setBillingCity(address.getCity());
            order.setBillingState(address.getState());
            order.setBillingPostalCode(address.getPostalCode());
            order.setBillingCountry(address.getCountry());
            order.setBillingPhone(address.getPhone());
        }
    }
}