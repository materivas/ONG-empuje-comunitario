package com.grupo_c.SistemasDistribuidosTP.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.grupo_c.SistemasDistribuidosTP.entity.Event;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IEventRepository extends JpaRepository<Event, Long> {
    
    List<Event> findByNameContainingIgnoreCase(String name);
    
    List<Event> findByIsCompletedTrue();
    
    List<Event> findByIsCompletedFalse();
    
    List<Event> findByDateBefore(LocalDateTime date);
    
    List<Event> findByDateAfter(LocalDateTime date);
    
    @Query("SELECT e FROM Event e WHERE e.date BETWEEN :startDate AND :endDate")
    List<Event> findByDateBetween(@Param("startDate") LocalDateTime startDate, 
                                 @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT e FROM Event e WHERE e.isCompleted = false AND e.date > CURRENT_TIMESTAMP")
    List<Event> findUpcomingEvents();
    
    @Query("SELECT e FROM Event e WHERE e.isCompleted = true")
    List<Event> findPastEvents();

    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.participants")
    List<Event> findAllJoinParticipants();

    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.participants WHERE e.id = :eventId ")
    Optional<Event> findByIdJoinParticipants(@Param("eventId") Long eventId);

    @Query("SELECT e FROM Event e JOIN e.participants u WHERE u.id = :userId")
    List<Event> findByParticipantId(@Param("userId") Long userId);
    
    @Query("SELECT e FROM Event e JOIN e.participants u JOIN FETCH e.participants WHERE u.id = :userId AND e.isCompleted = false")
    List<Event> findUpcomingEventsByParticipant(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(e) FROM Event e WHERE e.isCompleted = false AND e.date > CURRENT_TIMESTAMP")
    long countUpcomingEvents();
    
    @Query("SELECT e FROM Event e ORDER BY e.date ASC")
    List<Event> findAllOrderByDate();

    @Modifying
    @Transactional
    @Query("UPDATE Event e SET e.isCompleted = true WHERE e.date < CURRENT_TIMESTAMP AND e.isCompleted = false")
    void markPastEventsAsCompleted();

    @Modifying
    @Transactional
    @Query("UPDATE Event e SET e.isCompleted = true WHERE e.date < :dateTime AND e.isCompleted = false")
    void markPastEventsAsCompletedWithDateTime(@Param("dateTime") LocalDateTime dateTime);

    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.distributedInventories WHERE e.id = :eventId")
    Event findByIdJoinEventInventory(@Param("eventId") Long eventId);
}