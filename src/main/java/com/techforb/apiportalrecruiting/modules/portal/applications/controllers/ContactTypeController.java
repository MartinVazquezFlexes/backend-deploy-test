package com.techforb.apiportalrecruiting.modules.portal.applications.controllers;


import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.contactTypes.RequestContactTypeDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.contactTypes.ResponseContactTypeDTO;
import com.techforb.apiportalrecruiting.modules.portal.services.ContactTypeService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/contact-type")
@AllArgsConstructor
public class ContactTypeController {

	private final ContactTypeService contactTypeService;

	@GetMapping("list")
	public ResponseEntity<List<ResponseContactTypeDTO>> getAllContactTypes() {
		return ResponseEntity.ok(contactTypeService.getAllContactTypes());
	}

	@GetMapping
	public ResponseEntity<ResponseContactTypeDTO> getContactTypeById(@RequestParam Long id) {
		return ResponseEntity.ok(contactTypeService.getContactTypeById(id));
	}

	@PostMapping("create")
	public ResponseEntity<ResponseContactTypeDTO> create(@RequestBody RequestContactTypeDTO requestContactTypeDTO) {
		return ResponseEntity.ok(contactTypeService.createContactType(requestContactTypeDTO));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ResponseContactTypeDTO> update(
			@PathVariable Long id,
			@RequestBody RequestContactTypeDTO requestContactTypeDTO
	) {
		return ResponseEntity.ok(contactTypeService.updateContactType(id, requestContactTypeDTO));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		contactTypeService.deleteContactType(id);
		return ResponseEntity.noContent().build();
	}

}
