package com.grupo_c.SistemasDistribuidosTP.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.grupo_c.SistemasDistribuidosTP.entity.EventInventory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IEventInventoryRepository extends JpaRepository<EventInventory, Long> {
    
    List<EventInventory> findByEventId(Long eventId);
    
    List<EventInventory> findByInventoryId(Long inventoryId);
    
    @Query("SELECT ei FROM EventInventory ei WHERE ei.event.id = :eventId AND ei.inventory.id = :inventoryId")
    Optional<EventInventory> findByEventAndInventory(@Param("eventId") Long eventId, 
                                                   @Param("inventoryId") Long inventoryId);
    
    @Query("SELECT ei FROM EventInventory ei WHERE ei.distributionDate BETWEEN :startDate AND :endDate")
    List<EventInventory> findByDistributionDateBetween(@Param("startDate") LocalDateTime startDate, 
                                                     @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(ei.quantityDistributed) FROM EventInventory ei WHERE ei.inventory.id = :inventoryId")
    Integer sumDistributedQuantityByInventory(@Param("inventoryId") Long inventoryId);
    
    @Query("SELECT ei FROM EventInventory ei WHERE ei.event.id = :eventId ORDER BY ei.distributionDate DESC")
    List<EventInventory> findByEventIdOrdered(@Param("eventId") Long eventId);
    
    @Query("SELECT ei.inventory.id, SUM(ei.quantityDistributed) " +
           "FROM EventInventory ei " +
           "WHERE ei.distributionDate BETWEEN :startDate AND :endDate " +
           "GROUP BY ei.inventory.id")
    List<Object[]> sumDistributedByInventoryAndDateRange(@Param("startDate") LocalDateTime startDate,
                                                       @Param("endDate") LocalDateTime endDate);
}