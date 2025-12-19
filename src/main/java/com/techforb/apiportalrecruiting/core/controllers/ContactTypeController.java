package com.techforb.apiportalrecruiting.core.controllers;

import com.techforb.apiportalrecruiting.core.dtos.contactTypes.RequestContactTypeDTO;
import com.techforb.apiportalrecruiting.core.dtos.contactTypes.ResponseContactTypeDTO;
import com.techforb.apiportalrecruiting.core.services.ContactTypeService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
