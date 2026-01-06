package com.techforb.apiportalrecruiting.modules.portal.applications.controllers;

import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.application.ApplicationDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.application.ApplicationModified;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.application.ApplicationStateUpdateDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.services.ApplicationEditorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portal/applications")
@RequiredArgsConstructor
public class ApplicationEditorController {
    private final ApplicationEditorService applicationEditorService;


    @GetMapping("/{id}")
    public ResponseEntity<List<ApplicationDTO>> getApplicationsByUserId (@PathVariable Long id){
        return ResponseEntity.ok(applicationEditorService.getApplicationByApplicantId(id));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApplicationDTO> updateApplication(@PathVariable Long id, @RequestBody ApplicationModified applicationModified) {
        return ResponseEntity.ok(applicationEditorService.modifyApplication(id, applicationModified));
    }

    @PutMapping("/{id}/modify")
    public ResponseEntity<ApplicationDTO> updateState(@PathVariable Long id, @RequestBody ApplicationStateUpdateDTO dto) {
        return ResponseEntity.ok(applicationEditorService.modifyStateApplication(id, dto.getApplicationState()));
    }
}
