package com.techforb.apiportalrecruiting.core.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "identifications")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Identification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "identification_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "person_id", nullable = false)
	private Person person;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "document_type_id")
	private IdentificationType documentType;

	@Column(name = "description", nullable = false)
	private String description;
}