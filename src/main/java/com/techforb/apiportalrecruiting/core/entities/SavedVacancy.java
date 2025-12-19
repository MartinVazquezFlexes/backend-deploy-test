package com.techforb.apiportalrecruiting.core.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "saved_vacancies", uniqueConstraints = @UniqueConstraint(columnNames = {"person_id", "vacancy_id"}))
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SavedVacancy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vacancy_id", nullable = false)
    private Vacancy vacancy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "saved_date", nullable = false)
    private LocalDateTime savedDate;
    
    @PrePersist
    protected void onCreate() {
        savedDate = LocalDateTime.now();
    }
} 