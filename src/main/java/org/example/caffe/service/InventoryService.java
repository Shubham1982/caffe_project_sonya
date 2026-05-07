package org.example.caffe.service;

import org.example.caffe.domain.MaterialInventory;
import org.example.caffe.error.ResourceNotFoundException;
import org.example.caffe.repository.InventoryRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    // CREATE
    @CacheEvict(value = {"inventoryList", "inventory"}, allEntries = true)
    public MaterialInventory addInventory(MaterialInventory materialInventory) {
        inventoryRepository.findByMaterialNameIgnoreCase(materialInventory.getMaterialName())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Inventory item with name '" + materialInventory.getMaterialName() + "' already exists");
                });
        materialInventory.setIsActive(true);
        return inventoryRepository.save(materialInventory);
    }

    // READ – single
    @Cacheable(value = "inventory", key = "#id")
    public MaterialInventory getInventoryById(Long id) {
        return inventoryRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found with id: " + id));
    }

    // READ – all active
    @Cacheable(value = "inventoryList")
    public List<MaterialInventory> getAllInventory() {
        return inventoryRepository.findAllByIsActiveTrue();
    }

    // UPDATE
    @CacheEvict(value = {"inventoryList", "inventory"}, allEntries = true)
    public MaterialInventory updateInventory(MaterialInventory materialInventory) {
        if (materialInventory.getId() == null) {
            throw new IllegalArgumentException("Inventory ID must not be null for update");
        }
        // Ensure record exists and is active
        inventoryRepository.findByIdAndIsActiveTrue(materialInventory.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found with id: " + materialInventory.getId()));

        // Check uniqueness against other records
        inventoryRepository.findByMaterialNameIgnoreCase(materialInventory.getMaterialName())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(materialInventory.getId())) {
                        throw new IllegalArgumentException(
                                "Another inventory item with name '" + materialInventory.getMaterialName() + "' already exists");
                    }
                });

        materialInventory.setIsActive(true);
        return inventoryRepository.save(materialInventory);
    }

    // DELETE – soft delete
    @CacheEvict(value = {"inventoryList", "inventory"}, allEntries = true)
    public String deleteInventory(Long id) {
        MaterialInventory materialInventory = inventoryRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found with id: " + id));
        materialInventory.setIsActive(false);
        inventoryRepository.save(materialInventory);
        return "Inventory item deleted successfully";
    }
}
