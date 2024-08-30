package net.javaguides.payment_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    private String id;

    private String orderId;
    private BigDecimal amount;
    private String paymentMethod;
    private LocalDateTime paymentDate;
    private String status;
}
