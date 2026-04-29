package org.example.caffe.service;

import org.example.caffe.domain.Inventory;
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
    public Inventory addInventory(Inventory inventory) {
        inventoryRepository.findByMaterialNameIgnoreCase(inventory.getMaterialName())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Inventory item with name '" + inventory.getMaterialName() + "' already exists");
                });
        inventory.setIsActive(true);
        return inventoryRepository.save(inventory);
    }

    // READ – single
    @Cacheable(value = "inventory", key = "#id")
    public Inventory getInventoryById(Long id) {
        return inventoryRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found with id: " + id));
    }

    // READ – all active
    @Cacheable(value = "inventoryList")
    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAllByIsActiveTrue();
    }

    // UPDATE
    @CacheEvict(value = {"inventoryList", "inventory"}, allEntries = true)
    public Inventory updateInventory(Inventory inventory) {
        if (inventory.getId() == null) {
            throw new IllegalArgumentException("Inventory ID must not be null for update");
        }
        // Ensure record exists and is active
        inventoryRepository.findByIdAndIsActiveTrue(inventory.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found with id: " + inventory.getId()));

        // Check uniqueness against other records
        inventoryRepository.findByMaterialNameIgnoreCase(inventory.getMaterialName())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(inventory.getId())) {
                        throw new IllegalArgumentException(
                                "Another inventory item with name '" + inventory.getMaterialName() + "' already exists");
                    }
                });

        inventory.setIsActive(true);
        return inventoryRepository.save(inventory);
    }

    // DELETE – soft delete
    @CacheEvict(value = {"inventoryList", "inventory"}, allEntries = true)
    public String deleteInventory(Long id) {
        Inventory inventory = inventoryRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found with id: " + id));
        inventory.setIsActive(false);
        inventoryRepository.save(inventory);
        return "Inventory item deleted successfully";
    }
}
