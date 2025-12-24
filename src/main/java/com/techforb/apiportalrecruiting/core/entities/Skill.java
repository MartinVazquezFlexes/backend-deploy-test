package com.techforb.apiportalrecruiting.core.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "skills")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Skill {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String description;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "category_id")
	private Category category;

	@ManyToMany(mappedBy = "skills")
	List<Person> persons;
}
