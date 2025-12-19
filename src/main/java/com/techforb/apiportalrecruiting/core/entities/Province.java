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
@Table(name = "provinces")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Province {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "province_id")
	private Long id;

	private String name;

	@JoinColumn(name = "country_id",nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private Country country;
}
