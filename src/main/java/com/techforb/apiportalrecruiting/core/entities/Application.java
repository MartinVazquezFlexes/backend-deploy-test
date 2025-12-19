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
@Table(name = "applications")
@AllArgsConstructor
@NoArgsConstructor
public class Application {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "person_id", nullable = false)
	private Person person;

	private String comments;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(updatable = false, name = "application_date")
	private LocalDateTime applicationDate;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "update_date")
	private LocalDateTime updateDate;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, name = "application_state")
	private ApplicationState applicationState;

	@ManyToOne
	@JoinColumn(name = "vacancy_id", nullable = false)
	private Vacancy vacancy;

	@ManyToOne
	@JoinColumn(name = "cv_id")
	private Cv cv;

	@OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DetailSkill> detailSkills;
}
