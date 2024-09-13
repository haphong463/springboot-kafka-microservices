package net.javaguides.payment_service.entity.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import net.javaguides.payment_service.entity.PaymentStatus;

@Converter(autoApply = true)
public class PaymentStatusConverter implements AttributeConverter<PaymentStatus, String> {

    @Override
    public String convertToDatabaseColumn(PaymentStatus status) {
        if(status == null){
            return null;
        }
        return status.getLabel();
    }

    @Override
    public PaymentStatus convertToEntityAttribute(String label) {
        if(label == null){
            return null;
        }
        return switch(label) {
            case "Pending" -> PaymentStatus.PENDING;
            case "Success" -> PaymentStatus.SUCCESS;
            case "Failed" -> PaymentStatus.FAILED;
            default -> throw new IllegalArgumentException("Unknown payment status: " + label);
        };
    }
}
