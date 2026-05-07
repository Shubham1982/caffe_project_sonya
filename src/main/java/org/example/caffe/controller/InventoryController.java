package org.example.caffe.controller;

import org.example.caffe.domain.MaterialInventory;
import org.example.caffe.service.InventoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // CREATE
    @PostMapping("/add")
    public MaterialInventory addInventory(@RequestBody MaterialInventory materialInventory) {
        return inventoryService.addInventory(materialInventory);
    }

    // UPDATE
    @PutMapping("/update")
    public MaterialInventory updateInventory(@RequestBody MaterialInventory materialInventory) {
        return inventoryService.updateInventory(materialInventory);
    }

    // GET by ID
    @GetMapping("/{id}")
    public MaterialInventory getInventoryById(@PathVariable Long id) {
        return inventoryService.getInventoryById(id);
    }

    // GET all active
    @GetMapping("/getall")
    public List<MaterialInventory> getAllInventory() {
        return inventoryService.getAllInventory();
    }

    // DELETE (soft)
    @DeleteMapping("/delete/{id}")
    public String deleteInventory(@PathVariable Long id) {
        return inventoryService.deleteInventory(id);
    }
}
