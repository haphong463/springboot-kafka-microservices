package net.javaguides.order_service.controller;

import jakarta.persistence.OptimisticLockException;
import jakarta.servlet.http.HttpServletRequest;
import net.javaguides.common_lib.dto.ApiResponse;
import net.javaguides.common_lib.dto.order.OrderDTO;
import net.javaguides.order_service.dto.OrderRequestDto;
import net.javaguides.order_service.dto.OrderResponseDto;
import net.javaguides.order_service.dto.UserDto;
import net.javaguides.order_service.exception.OrderException;
import net.javaguides.order_service.service.AuthenticationAPIClient;
import net.javaguides.order_service.service.OrderService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("api/v1/order")
public class OrderController {
    private final OrderService orderService;
    private final AuthenticationAPIClient authenticationAPIClient;

    public OrderController(OrderService orderService, AuthenticationAPIClient authenticationAPIClient) {
        this.orderService = orderService;
        this.authenticationAPIClient = authenticationAPIClient;
    }



    /**
     * Endpoint to place a new order
     * @param order: Order request DTO containing order details
     * @param request: The HTTP request to extract the cookie for user authentication
     * @return ResponseEntity<ApiResponse<?>>: Response with order details or error message
     */
    @PostMapping
    public ResponseEntity<ApiResponse<?>> placeOrder(@RequestBody OrderRequestDto order, HttpServletRequest request) {
        try {
            // Extract cookie from the request header
            String cookie = request.getHeader(HttpHeaders.COOKIE);

            // Send cookie in Feign request to authenticate user
            ApiResponse<UserDto> user = authenticationAPIClient.getCurrentUser(cookie).getBody();
            if (user != null && user.getData() != null) {
                // Place order with authenticated user's ID
                return new ResponseEntity<>(new ApiResponse<>(orderService.placeOrder(order, user.getData().getId(), user.getData().getEmail()), HttpStatus.CREATED.value()), HttpStatus.CREATED);
            }
            // Return if user not found
            return new ResponseEntity<>(new ApiResponse<>("User not found!", HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
        } catch (OrderException e) {
            // Handle custom order exceptions
            return new ResponseEntity<>(new ApiResponse<>(e.getMessage(), e.getStatus().value()), e.getStatus());
        } catch (Exception e) {
            // Handle general exceptions
            return new ResponseEntity<>(new ApiResponse<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint to cancel an order
     * @param orderId: The ID of the order to cancel
     * @param request: The HTTP request to extract the cookie for user authentication
     * @return ResponseEntity<ApiResponse<?>>: Response with cancellation details or error message
     */
    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<ApiResponse<?>> cancelOrder(@PathVariable("orderId") String orderId, HttpServletRequest request) {
        try {
            // Extract cookie for user authentication
            String cookie = request.getHeader(HttpHeaders.COOKIE);

            ApiResponse<UserDto> user = authenticationAPIClient.getCurrentUser(cookie).getBody();

            if (user != null && user.getData() != null) {
                // Cancel order using user's ID and orderId
                OrderDTO existingOrder = orderService.cancelOrder(orderId, user.getData().getId());

                if (existingOrder != null) {
                    return new ResponseEntity<>(new ApiResponse<>(existingOrder, HttpStatus.OK.value()), HttpStatus.OK);
                }
                // Return if order is not found
                return new ResponseEntity<>(new ApiResponse<>("Order not found!", HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
            }
            // Return if unauthorized request
            return new ResponseEntity<>(new ApiResponse<>("Unauthorized request!", HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // Handle general exceptions
            return new ResponseEntity<>(new ApiResponse<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint to get order status
     * @param orderId: The ID of the order to check
     * @return ResponseEntity<ApiResponse<?>>: Response with order status or error message
     */
    @GetMapping("{orderId}")
    public ResponseEntity<ApiResponse<?>> getOrderStatus(@PathVariable("orderId") String orderId) {
        try {
            // Fetch order status based on orderId
            OrderResponseDto existingOrder = orderService.checkOrderStatusByOrderId(orderId);

            if (existingOrder != null) {
                return new ResponseEntity<>(new ApiResponse<>(existingOrder, HttpStatus.OK.value()), HttpStatus.OK);
            }
            // Return if order not found
            return new ResponseEntity<>(new ApiResponse<>("Order not found!", HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // Handle general exceptions
            return new ResponseEntity<>(new ApiResponse<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getOrders(HttpServletRequest request, @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        try {
            // Extract cookie from the request header
            String cookie = request.getHeader(HttpHeaders.COOKIE);

            // Send cookie in Feign request to authenticate user
            ApiResponse<UserDto> user = authenticationAPIClient.getCurrentUser(cookie).getBody();

            return new ResponseEntity<>(new ApiResponse<>(orderService.getAllOrders(user.getData().getId(), page, size), HttpStatus.OK.value()), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint to update the order status
     * @param orderId: The ID of the order to update
     * @param version: Optimistic locking version for concurrency control
     * @return ResponseEntity<ApiResponse<?>>: Response with updated order status or error message
     */
    @PatchMapping("/update/status/{orderId}")
    public ResponseEntity<ApiResponse<?>> updateOrderStatus(@PathVariable("orderId") String orderId, @RequestHeader(HttpHeaders.IF_MATCH) int version) {
        try {
            // Update order status using orderId and version for concurrency control
            OrderResponseDto orderDTO = orderService.updateOrderStatus(orderId, version);
            return orderDTO != null ? new ResponseEntity<>(new ApiResponse<>(orderDTO, HttpStatus.OK.value()), HttpStatus.OK)
                    : new ResponseEntity<>(new ApiResponse<>("Not found order!", HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
        } catch (IllegalStateException | OptimisticLockException e) {
            // Handle optimistic locking and state-related exceptions
            return new ResponseEntity<>(new ApiResponse<>(e.getMessage(), HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Handle general exceptions
            return new ResponseEntity<>(new ApiResponse<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
