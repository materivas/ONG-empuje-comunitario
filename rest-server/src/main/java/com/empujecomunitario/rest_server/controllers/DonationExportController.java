package com.empujecomunitario.rest_server.controllers;
import com.empujecomunitario.rest_server.entity.Donation;
import com.empujecomunitario.rest_server.service.IDonationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter; // Para formatear LocalDate
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/export")
@Tag(name = "Donation Export Controller", description = "Endpoint para exportar donaciones a Excel")
public class DonationExportController {

    private final IDonationService donationService;
    // Formateador para la fecha que sí tenés (LocalDate)
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public DonationExportController(IDonationService donationService) {
        this.donationService = donationService;
    }

    @Operation(summary = "Exportar donaciones a Excel",
               description = "Genera un archivo .xlsx con el detalle de las donaciones activas, usando los campos disponibles en la entidad. " +
                             "Opcionalmente puede filtrar por 'received' (recibidas) o 'made' (realizadas). " +
                             "El archivo tiene hojas separadas por categoría.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Archivo Excel generado y descargado exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno al generar el archivo")
    })
    @GetMapping("/donations")
    public ResponseEntity<byte[]> exportDonationsToExcel(
            @Parameter(description = "Tipo de donaciones a exportar ('received', 'made', u omitir para activas).")
            @RequestParam(value = "type", required = false) String type) throws IOException {

        try (Workbook workbook = new XSSFWorkbook()) {
            List<Donation> allDonations = donationService.findAll();
            List<Donation> filteredDonations;

            // Filtrar según el parámetro 'type' y que no estén borradas (isDeleted == false)
            if (type != null) {
                if (type.equalsIgnoreCase("received")) {
                    filteredDonations = allDonations.stream()
                            .filter(d -> !d.getIsDeleted() && d.getMadeByOurselves() != null && !d.getMadeByOurselves())
                            .collect(Collectors.toList());
                } else if (type.equalsIgnoreCase("made")) {
                    filteredDonations = allDonations.stream()
                            .filter(d -> !d.getIsDeleted() && d.getMadeByOurselves() != null && d.getMadeByOurselves())
                            .collect(Collectors.toList());
                } else {
                    // Si el tipo no es válido, exportar solo activas
                    filteredDonations = allDonations.stream()
                            .filter(d -> !d.getIsDeleted())
                            .collect(Collectors.toList());
                }
            } else {
                // Si no se especifica 'type', exportar solo activas
                 filteredDonations = allDonations.stream()
                         .filter(d -> !d.getIsDeleted())
                         .collect(Collectors.toList());
            }

            // Agrupar por categoría
            Map<Donation.Category, List<Donation>> donationsByCategory = filteredDonations.stream()
                    .collect(Collectors.groupingBy(Donation::getCategory));

            // Crear hojas por categoría
             for (Map.Entry<Donation.Category, List<Donation>> entry : donationsByCategory.entrySet()) {
                Donation.Category category = entry.getKey();
                List<Donation> categoryDonations = entry.getValue();

                if(categoryDonations.isEmpty()) continue;

                Sheet sheet = workbook.createSheet(category.name());

                
                Row header = sheet.createRow(0);
                // header.createCell(0).setCellValue("Categoría"); // Redundante si está en hoja separada
                header.createCell(0).setCellValue("Descripción");
                header.createCell(1).setCellValue("Cantidad");
                header.createCell(2).setCellValue("Eliminado"); 
                header.createCell(3).setCellValue("Fecha Última Donación");
                header.createCell(4).setCellValue("Hecha por Nosotros");

                int rowIdx = 1;
                for (Donation donation : categoryDonations) {
                    Row row = sheet.createRow(rowIdx++);
                    
                    // row.createCell(0).setCellValue(donation.getCategory().name()); // Redundante
                    row.createCell(0).setCellValue(donation.getDescription() != null ? donation.getDescription() : "");
                    row.createCell(1).setCellValue(donation.getQuantity());
                    row.createCell(2).setCellValue(donation.getIsDeleted() ? "SI" : "NO");
                    row.createCell(3).setCellValue(donation.getLastDonationDate() != null ? donation.getLastDonationDate().format(dateFormatter) : "N/A");
                    row.createCell(4).setCellValue(donation.getMadeByOurselves() != null && donation.getMadeByOurselves() ? "SI" : "NO");
                    
                }

                // Ajustar anchos para 5 columnas
                 sheet.setColumnWidth(0, 10000); // Descripcion
                 sheet.setColumnWidth(1, 3000); // Cantidad
                 sheet.setColumnWidth(2, 3000); // Eliminado
                 sheet.setColumnWidth(3, 5000); // Fecha Ultima Donacion
                 sheet.setColumnWidth(4, 5000); // Hecha por Nosotros
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);

            String filename = "donaciones_activas.xlsx";
            if (type != null) {
                if (type.equalsIgnoreCase("received")) filename = "donaciones_recibidas.xlsx";
                else if (type.equalsIgnoreCase("made")) filename = "donaciones_realizadas.xlsx";
            }

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + filename)
                    .contentType(MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(out.toByteArray());
        } catch (Exception e) {
             // logger.error("Error al exportar donaciones a Excel", e);
             throw new IOException("Error al generar el archivo Excel", e);
        }
    }
}