package com.techforb.apiportalrecruiting.core.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "contacts")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Contact {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "contact_type_id", nullable = false)
	private ContactType contactType;

	@Column(name = "contact_value", nullable = false)
	private String value;

	@Column(nullable = false)
	private String label;

	@JoinColumn(name = "person_id")
	@ManyToOne
	private Person person;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = createdAt;
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}
}
