package org.example.caffe.repository;

import org.example.caffe.domain.MaterialInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<MaterialInventory, Long> {

    Optional<MaterialInventory> findByMaterialNameIgnoreCase(String materialName);

    List<MaterialInventory> findAllByIsActiveTrue();

    Optional<MaterialInventory> findByIdAndIsActiveTrue(Long id);
}
