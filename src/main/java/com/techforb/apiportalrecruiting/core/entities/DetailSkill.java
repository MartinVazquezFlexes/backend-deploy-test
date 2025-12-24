package com.techforb.apiportalrecruiting.core.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "detail_skills")
@AllArgsConstructor
@NoArgsConstructor
@org.hibernate.annotations.Check(constraints = "skill_id IS NOT NULL OR language_id IS NOT NULL")
public class DetailSkill {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "detail_skill_id")
	private Long id;

	@JoinColumn(name = "vacancy_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	private Vacancy vacancy;

	@JoinColumn(name = "skill_id")
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	private Skill skill;

	@JoinColumn(name = "application_id")
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Application application;

	@JoinColumn(name = "language_id")
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	private Language language;

	@Column(name = "is_obligatory")
	private Boolean isObligatory;

	private Integer priority;

	@Column(name = "years_experience")
	private Integer yearsExperience;
}
