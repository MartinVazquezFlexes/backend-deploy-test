package com.techforb.apiportalrecruiting.core.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "identification_type")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IdentificationType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "identification_type_id")
    private Long id;

    @Column(name = "description", nullable = false)
    private String description;
}