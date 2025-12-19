package com.techforb.apiportalrecruiting.core.controllers;

import com.techforb.apiportalrecruiting.core.dtos.contacts.RequestContactDTO;
import com.techforb.apiportalrecruiting.core.dtos.contacts.ResponseContactDTO;
import com.techforb.apiportalrecruiting.core.services.ContactService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/contact")
@AllArgsConstructor
public class ContactController {

	private final ContactService contactService;

	@GetMapping("list")
	public ResponseEntity<List<ResponseContactDTO>> getAllContactsByPerson() {
		return ResponseEntity.ok(contactService.getContactsByPersonId());
	}

	@GetMapping("/{id}")
	public ResponseEntity<ResponseContactDTO> getById(@PathVariable Long id) {
		return ResponseEntity.ok(contactService.getContactById(id));
	}

	@PostMapping("create")
	public ResponseEntity<ResponseContactDTO> create(@RequestBody RequestContactDTO dto) {
		return ResponseEntity.ok(contactService.createContact(dto));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ResponseContactDTO> update(
			@PathVariable Long id,
			@RequestBody RequestContactDTO dto
	) {
		ResponseContactDTO updated = contactService.updateContact(id, dto);
		return ResponseEntity.ok(updated);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		contactService.deleteContactById(id);
		return ResponseEntity.noContent().build();
	}

}
