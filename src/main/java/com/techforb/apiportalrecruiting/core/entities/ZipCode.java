package com.techforb.apiportalrecruiting.core.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "zip_codes")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ZipCode {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "zip_code_id")
	private Long id;

	private String name;

}
