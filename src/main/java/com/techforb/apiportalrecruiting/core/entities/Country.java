package com.techforb.apiportalrecruiting.core.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "countries")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Country {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "country_id")
	private Long id;

	private String name;
}
