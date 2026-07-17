package com.grupo_c.SistemasDistribuidosTP.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.grupo_c.SistemasDistribuidosTP.entity.Inventory;
import com.grupo_c.SistemasDistribuidosTP.entity.Inventory.Category;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional; 

@Repository
public interface IInventoryRepository extends JpaRepository<Inventory, Long> {
    
    List<Inventory> findByCategory(Category category);
    
    List<Inventory> findByisDeletedTrue();
    
    List<Inventory> findByisDeletedFalse();
    
    List<Inventory> findByQuantityGreaterThan(Integer quantity);
    
    List<Inventory> findByQuantityLessThan(Integer quantity);
    
    List<Inventory> findByDescriptionContainingIgnoreCase(String description);
    
    List<Inventory> findByUserCreatorId(Long userId);
    
    List<Inventory> findByUserModifyId(Long userId);
    
    @Query("SELECT i FROM Inventory i WHERE i.creationDate BETWEEN :startDate AND :endDate")
    List<Inventory> findByCreationDateBetween(@Param("startDate") LocalDateTime startDate, 
                                               @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT i FROM Inventory i WHERE i.category = :category AND i.isDeleted = false")
    List<Inventory> findAvailableByCategory(@Param("category") Category category);
    
    @Query("SELECT i FROM Inventory i WHERE i.quantity > 0 AND i.isDeleted = false")
    List<Inventory> findAvailableInventory();
    
    @Query("SELECT SUM(i.quantity) FROM Inventory i WHERE i.category = :category")
    Integer sumQuantityByCategory(@Param("category") Category category);
    
    @Query("SELECT i FROM Inventory i WHERE i.isDeleted = false ORDER BY i.category, i.description")
    List<Inventory> findAllAvailableOrdered();
    
    @Query("SELECT i FROM Inventory i WHERE i.category = :category AND i.isDeleted = :isDeleted")
    List<Inventory> findByCategoryAndisDeleted(@Param("category") Category category, 
                                               @Param("isDeleted") Boolean isDeleted);

    // Permite buscar un item de inventario por su categoría y descripción.
    // Devuelve un Optional para manejar de forma segura si el item no existe.
    Optional<Inventory> findByCategoryAndDescription(Category category, String description);
    
}