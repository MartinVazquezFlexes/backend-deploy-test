package com.techforb.apiportalrecruiting.modules.portal.applications.controllers;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.vacancies.VacancyRequestUpdateDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.vacancies.RequestFullVacancyDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.vacancies.VacancyDetailsDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.vacancies.VacancyNotActiveDTO;
import com.techforb.apiportalrecruiting.modules.portal.services.VacancyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.vacancies.VacancyDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing vacancy-related API endpoints.
 */
@RestController
@RequestMapping("api/portal/vacancies")
@AllArgsConstructor
@Tag(name = "Vacancies")
@Lazy
public class VacancyController {
    /**
     * Service for handling vacancy-related business logic.
     */
    private final VacancyService vacancyService;

    @ApiResponse(responseCode="200",description="Peticion Exitosa")
    @ApiResponse(responseCode="400",description="No existe la vacante")
    @ApiResponse(responseCode="410",description="La vacante Esta inactiva")
    @GetMapping("/public/{id}/details")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<VacancyDetailsDTO> getDetailsVacancyById(@PathVariable Long id){

        return ResponseEntity.ok(vacancyService.getDetailsVacancyById(id));

    }


     /**
     * Retrieves all active vacancies with pagination and sorting.
     *
     * @param pageable Pagination and sorting parameters
     * @return ResponseEntity containing a page of active vacancies
     */
    @Operation(summary = "Get all vacancies actives ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched vacancies"),
            @ApiResponse(responseCode = "400", description = "No vacancies actives found")
    })
    @GetMapping("/public/actives")
    public ResponseEntity<Page<VacancyDTO>> getAllVacanciesActive(
            @PageableDefault(page = 0, size = 10, sort = {"creationDate"},
                    direction = Sort.Direction.DESC) Pageable pageable) {
        Page<VacancyDTO> result = vacancyService.getVacanciesActiveWithLanguage(pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Update a vacancy",
            description = "Updates an existing vacancy with the given ID using the provided details."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Vacancy updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = VacancyDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Vacancy not found",
                    content = @Content)
    })
    @PutMapping("/update/{id}")
    public ResponseEntity<VacancyDTO> updateVacancy(@Valid @RequestBody VacancyRequestUpdateDTO requestData, @PathVariable(name = "id") Long vacancyId){
        VacancyDTO updatedVacancy = vacancyService.updateVacancy(requestData, vacancyId);
        return ResponseEntity.status(201).body(updatedVacancy);
    }
    /**
     * Create the vacancy with your previously created foreign keys
     *
     * @RequestBody RequestFullVacancyDTO
     * @return VacancyDetailsDTO
     */
    @Operation(summary = "CreateVacancy ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "vacancy successfully created"),
            @ApiResponse(responseCode = "403", description = "user without role allowed"),
            @ApiResponse(responseCode = "404", description = "Entity not Fount")
    })
    @PostMapping("/create")
    public ResponseEntity<VacancyDetailsDTO> createVacancy(@RequestBody RequestFullVacancyDTO newVacancy){
        return ResponseEntity.ok(vacancyService.create(newVacancy));
    }

    /**
     * Endpoint to deactivate an active vacancy.
     * If the vacancy is already disabled, it returns a 404 response.
     * If the vacancy is not found, it returns a 400 response.
     *
     * @param vacancyId the unique identifier of the vacancy to disable.
     * @return a {@code ResponseEntity<VacancyNotActiveDTO>} containing the disabled vacancy details.
     */
    @Operation(summary = "Deactivate an active vacancy")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Successfully disabled"),
            @ApiResponse(responseCode = "404",description = "The vacancy its already disabled"),
            @ApiResponse(responseCode = "400",description = "No vacancy found")
    })
    @PatchMapping("/disable/{vacancyId}")
    public ResponseEntity<VacancyNotActiveDTO> disableVacancy(@PathVariable Long vacancyId){
        return  ResponseEntity.ok(this.vacancyService.disableVacancy(vacancyId));

    }
}
