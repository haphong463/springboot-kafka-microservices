package net.javaguides.order_service.controller;


import io.github.haphong463.dto.ApiResponse;
import io.github.haphong463.dto.order.OrderDTO;
import net.javaguides.order_service.dto.StockDto;
import net.javaguides.order_service.dto.UserDto;
import net.javaguides.order_service.exception.OrderException;
import net.javaguides.order_service.interceptor.FeignClientInterceptor;
import net.javaguides.order_service.service.AuthenticationAPIClient;
import net.javaguides.order_service.service.OrderService;
import net.javaguides.order_service.service.StockAPIClient;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<ApiResponse<?>> placeOrder(@RequestBody OrderDTO order) {
        try {
            ApiResponse<UserDto> user = authenticationAPIClient.getCurrentUser().getBody();

            if (user != null) {
                OrderDTO createOrder = orderService.placeOrder(order, user.getData().getId());
                ApiResponse<OrderDTO> response = new ApiResponse<>(createOrder, HttpStatus.CREATED.value());
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }

            ApiResponse<String> response = new ApiResponse<>("User not found!", HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (OrderException e) {
            ApiResponse<String> response = new ApiResponse<>(e.getMessage(), e.getStatus().value());
            return new ResponseEntity<>(response, e.getStatus());
        } catch (Exception e) {
            ApiResponse<String> response = new ApiResponse<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("{orderId}")
    public ResponseEntity<OrderDTO> getOrderStatus(@RequestParam("orderId") String orderId) {
        try {
            OrderDTO existingOrder = orderService.checkOrderStatusByOrderId(orderId);
            if (existingOrder != null) {
                return new ResponseEntity<>(existingOrder, HttpStatus.OK);
            }
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
