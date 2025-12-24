package com.techforb.apiportalrecruiting.core.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "categories")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Category {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "category_id")
	private Long id;

	private String name;

}
