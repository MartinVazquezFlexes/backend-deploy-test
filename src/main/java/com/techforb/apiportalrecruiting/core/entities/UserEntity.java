package com.techforb.apiportalrecruiting.core.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;

	@Column(nullable = false, unique = true)
	private String email;

	private String password;

	@Column(name = "is_enabled")
	private boolean isEnabled;

	@Column(name = "is_account_not_expired")
	private Boolean isAccountNotExpired;

	@Column(name = "is_account_not_locked")
	private Boolean isAccountNotLocked;

	@Column(name = "is_credential_not_expired")
	private Boolean isCredentialNotExpired;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "users_roles",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "role_id")
	)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private List<Role> roles;

	@OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
	private Person person;
}
