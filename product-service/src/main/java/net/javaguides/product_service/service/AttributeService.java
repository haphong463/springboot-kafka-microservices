package net.javaguides.product_service.service;

import net.javaguides.product_service.entity.Attribute;

import java.util.List;

public interface AttributeService {
    Attribute createAttribute(String name, String dataType);
    Attribute updateAttribute(Long id, String newName, String newDataType);
    List<Attribute> getAllAttributes();
    Attribute getAttributeByName(String name);
    void deleteAttribute(Long id);
}
