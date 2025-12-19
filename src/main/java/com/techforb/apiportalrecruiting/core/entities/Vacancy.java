package com.techforb.apiportalrecruiting.core.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "vacancies")
@AllArgsConstructor
@NoArgsConstructor

public class Vacancy {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "company_id", nullable = false)
	private Company company;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "recruiter_id", nullable = false)
	private UserEntity recruiter;

	@JoinColumn(name = "direction_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Direction direction;

	private String role;

	private String description;

	private Boolean active;

	@Column(nullable = false, name = "years_experience_required")
	private Integer yearsExperienceRequired;

	@Column(name = "work_modality")
	@Enumerated(EnumType.STRING)
	private WorkModality workModality;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(updatable = false, name = "creation_date")
	private LocalDateTime creationDate;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(updatable = false, name = "update_date")
	private LocalDateTime updateDate;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(updatable = false, name = "expiration_date")
	private LocalDateTime expirationDate;

	@OneToMany(mappedBy = "vacancy", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DetailSkill> detailSkills;
}
