package net.javaguides.order_service.controller;

import jakarta.persistence.OptimisticLockException;
import jakarta.servlet.http.HttpServletRequest;
import net.javaguides.common_lib.dto.ApiResponse;
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

@RestController
@RequestMapping("api/v1/order")
public class OrderController {
    private final OrderService orderService;
    private final AuthenticationAPIClient authenticationAPIClient;

    public OrderController(OrderService orderService, AuthenticationAPIClient authenticationAPIClient) {
        this.orderService = orderService;
        this.authenticationAPIClient = authenticationAPIClient;
    }


    @PostMapping
    public ResponseEntity<ApiResponse<?>> placeOrder(@RequestBody OrderRequestDto order, HttpServletRequest request) {
        try {
            String cookie = request.getHeader(HttpHeaders.COOKIE);

            // Gửi cookie vào request Feign
            ApiResponse<UserDto> user = authenticationAPIClient.getCurrentUser(cookie).getBody();
            if (user != null && user.getData() != null) {
                return new ResponseEntity<>(new ApiResponse<>(orderService.placeOrder(order, user.getData().getId()), HttpStatus.CREATED.value()), HttpStatus.CREATED);
            }
            return new ResponseEntity<>(new ApiResponse<>("User not found!", HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
        } catch (OrderException e) {
            return new ResponseEntity<>(new ApiResponse<>(e.getMessage(), e.getStatus().value()), e.getStatus());
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("{orderId}")
    public ResponseEntity<ApiResponse<?>> getOrderStatus(@PathVariable("orderId") String orderId) {
        try {
            OrderResponseDto existingOrder = orderService.checkOrderStatusByOrderId(orderId);

            if (existingOrder != null) {
                return new ResponseEntity<>(new ApiResponse<>(existingOrder, HttpStatus.OK.value()), HttpStatus.OK);
            }
            return new ResponseEntity<>(new ApiResponse<>("Order not found!", HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/update/status/{orderId}")
    public ResponseEntity<ApiResponse<?>> updateOrderStatus(@PathVariable("orderId") String orderId, @RequestHeader(HttpHeaders.IF_MATCH) int version) {
        try {
            OrderResponseDto orderDTO = orderService.updateOrderStatus(orderId, version);
            return orderDTO != null ? new ResponseEntity<>(new ApiResponse<>(orderDTO, HttpStatus.OK.value()), HttpStatus.OK)
                    :
                    new ResponseEntity<>(new ApiResponse<>("Not found order!", HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
        } catch (IllegalStateException | OptimisticLockException e) {
            return new ResponseEntity<>(new ApiResponse<>(e.getMessage(), HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
