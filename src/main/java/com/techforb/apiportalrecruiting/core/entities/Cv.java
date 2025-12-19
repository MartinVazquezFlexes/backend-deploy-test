package com.techforb.apiportalrecruiting.core.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@Table(name = "cvs")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Cv {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cv_id")
	private Long id;

	@Column(nullable = false)
	private String version;

	@Column(name = "public_id", nullable = false)
	private String publicId;

	private String name;

	@Column(name = "is_last")
	private Boolean isLast;

	@Column(updatable = false, name = "creation_date")
	@CreationTimestamp
	private LocalDateTime creationDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "person_id")
	private Person person;

}
