package com.techforb.apiportalrecruiting.core.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "languages")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Language {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "language_id")
	private Long id;

	@Column(name = "language_level")
	private String languageLevel;

	private String name;
}
