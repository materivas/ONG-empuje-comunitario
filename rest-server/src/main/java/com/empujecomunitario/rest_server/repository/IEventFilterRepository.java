package com.empujecomunitario.rest_server.repository;

import com.empujecomunitario.rest_server.entity.EventFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IEventFilterRepository extends JpaRepository<EventFilter, Long> {
    List<EventFilter> findByUserId(Long userId);
}