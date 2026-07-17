package com.grupo_c.SistemasDistribuidosTP.service;

import com.grupo_c.SistemasDistribuidosTP.entity.EventInventory;

import java.util.List;

public interface IEventInventoryService {
    List<EventInventory> findByEventId(Long eventId);
    List<EventInventory> findByEventIdOrdered(Long eventId);
}
