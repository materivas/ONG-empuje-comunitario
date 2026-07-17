package com.grupo_c.SistemasDistribuidosTP.service;
import com.grupo_c.SistemasDistribuidosTP.entity.Inventory;
import com.grupo_c.SistemasDistribuidosTP.entity.User;
import java.util.List;

public interface IInventoryService {
    List<Inventory> findAll();
    Inventory findById(Long id);
    List<Inventory> findByCategory(Inventory.Category category);
    List<Inventory> findByisDeleted(Boolean isDeleted);
    List<Inventory> findByDescriptionContaining(String description);
    Inventory save(Inventory inventory, User currentUser);
    void deleteById(Long id, User currentUser);
    List<Inventory> findAvailableInventory();
    void decreaseStock(Inventory inventory, User user, int quantityToDecrease);
    void addOrUpdateStock(List<Inventory> inventories, User currentUser);
}