package com.empujecomunitario.rest_server.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.empujecomunitario.rest_server.entity.Event;
import com.empujecomunitario.rest_server.entity.EventReportResponse;
import com.empujecomunitario.rest_server.repository.IEventRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@ComponentScan(basePackages = "com.empujecomunitario.rest_server")
@RequestMapping("/events")
@Tag(name = "Reportes de Eventos")
public class EventReportController {

        private final IEventRepository eventRepository;

        public EventReportController(IEventRepository eventRepository) {
                this.eventRepository = eventRepository;
        }

        @Operation(summary = "Generar informe de eventos", description = "Retorna participación en eventos aplicando filtros")
        @GetMapping("/report")
        public ResponseEntity<List<EventReportResponse>> generateEventReport(
                        @RequestParam(required = false) String fechaDesde,
                        @RequestParam(required = false) String fechaHasta,
                        @RequestParam(required = false) Boolean repartoDonaciones,
                        @RequestParam(required = false) Boolean completados) {

                try {
                        LocalDateTime fechaDesdeDateTime = fechaDesde != null
                                        ? LocalDate.parse(fechaDesde).atStartOfDay()
                                        : null;
                        LocalDateTime fechaHastaDateTime = fechaHasta != null
                                        ? LocalDate.parse(fechaHasta).atTime(23, 59, 59)
                                        : null;

                        List<Event> eventosFiltrados = eventRepository.findWithFilters(
                                        fechaDesdeDateTime, fechaHastaDateTime, repartoDonaciones, completados);

                        List<EventReportResponse> report = eventosFiltrados.stream()
                                        .map(this::convertToResponse)
                                        .collect(Collectors.toList());

                        return ResponseEntity.ok(report);

                } catch (Exception e) {
                        e.printStackTrace();
                        return ResponseEntity.status(500).build();
                }
        }

        private EventReportResponse convertToResponse(Event event) {
                return new EventReportResponse(
                                event.getId(),
                                event.getName(),
                                event.getDescription(),
                                event.getDate().toLocalDate().toString(),
                                event.getHasDonationDistribution() != null ? event.getHasDonationDistribution() : false,
                                event.getIsCompleted() != null ? event.getIsCompleted() : false);
        }

}