package net.javaguides.order_service.controller;

import net.javaguides.base_domains.dto.order.OrderDTO;
import net.javaguides.order_service.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping()
    public ResponseEntity<OrderDTO> placeOrder(@RequestBody OrderDTO order){
        try {
            OrderDTO createOrder = orderService.placeOrder(order);
            return new ResponseEntity<>(createOrder, HttpStatus.CREATED);
        }catch(Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
