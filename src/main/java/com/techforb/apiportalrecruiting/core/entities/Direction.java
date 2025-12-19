package com.techforb.apiportalrecruiting.core.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "directions")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Direction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "direction_id")
	private Long id;

	private String description;

	@JoinColumn(name = "city_id",nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private City city;

	@JoinColumn(name = "zip_code_id",nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private ZipCode zipCode;
}
