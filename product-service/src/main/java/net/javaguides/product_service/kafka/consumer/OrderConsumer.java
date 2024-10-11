package net.javaguides.product_service.kafka.consumer;

import lombok.RequiredArgsConstructor;
import net.javaguides.common_lib.dto.order.OrderDTO;
import net.javaguides.common_lib.dto.order.OrderEvent;
import net.javaguides.common_lib.dto.order.OrderItemDTO;
import net.javaguides.product_service.dto.product_variant.ProductVariantResponseDto;
import net.javaguides.product_service.dto.product_variant.UpdateProductVariantRequestDto;
import net.javaguides.product_service.entity.ProductVariant;
import net.javaguides.product_service.service.ProductVariantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderConsumer.class);
    private final ProductVariantService productVariantService;
    @KafkaListener(topics = "${spring.kafka.create-order-topic.name}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consume(OrderEvent orderEvent) {
        try {
            LOGGER.info(String.format("OrderDTO event received in product service -> %s", orderEvent.toString()));

            OrderDTO orderDTO = orderEvent.getOrderDTO();
            Set<Long> variantIds = orderDTO.getOrderItems().stream()
                    .map(OrderItemDTO::getVariantId)
                    .collect(Collectors.toSet());

            // Lấy danh sách các ProductVariant theo variantIds
            Map<Long, ProductVariant> variantMap = productVariantService.getProductVariantByIds(variantIds).stream()
                    .collect(Collectors.toMap(ProductVariant::getId, variant -> variant));

            // Cập nhật từng sản phẩm
            for (OrderItemDTO orderItemDTO : orderDTO.getOrderItems()) {
                ProductVariant productVariant = variantMap.get(orderItemDTO.getVariantId());

                if (productVariant != null) {
                    int updatedQuantity = productVariant.getStockQuantity() - orderItemDTO.getQuantity();
                    if (updatedQuantity < 0) {
                        LOGGER.warn(String.format("Stock quantity for variant %s is insufficient.", orderItemDTO.getVariantId()));
                        continue; // Nếu không đủ số lượng thì bỏ qua
                    }

                    UpdateProductVariantRequestDto productVariantRequestDto = new UpdateProductVariantRequestDto();
                    productVariantRequestDto.setStockQuantity(updatedQuantity);

                    productVariantService.updateProductVariant(orderItemDTO.getVariantId(), productVariantRequestDto);
                } else {
                    LOGGER.warn(String.format("Product variant %s not found.", orderItemDTO.getVariantId()));
                }
            }
        } catch (Exception e) {
            LOGGER.warn(String.format("Error message -> %s", e.getMessage()));
        }
    }

}
