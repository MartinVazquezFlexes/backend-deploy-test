package com.techforb.apiportalrecruiting.core.entities;


import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "contact_types")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
}
