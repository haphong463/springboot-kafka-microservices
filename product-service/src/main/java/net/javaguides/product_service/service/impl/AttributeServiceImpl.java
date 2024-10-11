package net.javaguides.product_service.service.impl;

import lombok.RequiredArgsConstructor;
import net.javaguides.product_service.entity.Attribute;
import net.javaguides.product_service.repository.AttributeRepository;
import net.javaguides.product_service.service.AttributeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttributeServiceImpl implements AttributeService {

    private final AttributeRepository attributeRepository;

    @Transactional
    public Attribute createAttribute(String name, String dataType) {
        Optional<Attribute> existingAttribute = attributeRepository.findByName(name);
        if (existingAttribute.isPresent()) {
            throw new IllegalArgumentException("Attribute with name " + name + " already exists.");
        }
        Attribute attribute = new Attribute();
        attribute.setName(name);
        attribute.setDataType(dataType);
        return attributeRepository.save(attribute);
    }

    @Transactional
    public Attribute updateAttribute(Long id, String newName, String newDataType) {
        Attribute attribute = attributeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attribute not found."));
        attribute.setName(newName);
        attribute.setDataType(newDataType);
        return attributeRepository.save(attribute);
    }

    @Transactional
    public void deleteAttribute(Long id) {
        attributeRepository.deleteById(id);
    }

    public List<Attribute> getAllAttributes() {
        return attributeRepository.findAll();
    }

    public Attribute getAttributeByName(String name) {
        return attributeRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Attribute not found."));
    }
}
