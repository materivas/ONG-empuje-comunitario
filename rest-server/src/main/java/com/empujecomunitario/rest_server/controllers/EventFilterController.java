package com.empujecomunitario.rest_server.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.empujecomunitario.rest_server.dto.EventFilterRequest;
import com.empujecomunitario.rest_server.dto.EventFilterResponse;
import com.empujecomunitario.rest_server.serviceImpl.EventFilterService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/filters")
@Tag(name = "Gestión de Filtros", description = "Operaciones para guardar, editar y eliminar filtros personalizados de eventos")
public class EventFilterController {

    private final EventFilterService service;

    public EventFilterController(EventFilterService service) {
        this.service = service;
    }

    @Operation(summary = "Obtener filtros del usuario")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EventFilterResponse>> getFiltersByUser(@PathVariable Long userId) {
        List<EventFilterResponse> filters = service.getFiltersByUser(userId);
        return ResponseEntity.ok(filters);
    }

    @Operation(summary = "Obtener filtro por ID")
    @GetMapping("/{id}")
    public ResponseEntity<EventFilterResponse> getFilterById(@PathVariable Long id) {
        EventFilterResponse filter = service.getFilterById(id);
        return ResponseEntity.ok(filter);
    }

    @Operation(summary = "Obtener todos los filtros")
    @GetMapping
    public ResponseEntity<List<EventFilterResponse>> getAllFilters() {
        List<EventFilterResponse> filters = service.getAllFilters();
        return ResponseEntity.ok(filters);
    }

    @Operation(summary = "Crear nuevo filtro")
    @PostMapping
    public ResponseEntity<EventFilterResponse> createFilter(@Valid @RequestBody EventFilterRequest request) {
        EventFilterResponse createdFilter = service.createFilter(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFilter);
    }

    @Operation(summary = "Actualizar filtro")
    @PutMapping("/{id}")
    public ResponseEntity<EventFilterResponse> updateFilter(
            @PathVariable Long id,
            @Valid @RequestBody EventFilterRequest request) {
        EventFilterResponse updatedFilter = service.updateFilter(id, request);
        return ResponseEntity.ok(updatedFilter);
    }

    @Operation(summary = "Eliminar filtro")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFilter(@PathVariable Long id) {
        service.deleteFilter(id);
        return ResponseEntity.noContent().build();
    }
}
