package com.techforb.apiportalrecruiting.core.dtos.users;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailChangePasswordRequestDTO {
	@Schema(description = "User Email", example = "user@example.com")
	@NotBlank(message = "Email is mandatory")
	@Email(message = "Email must be valid")
	private String email;
	private String oldPassword;
	@NotBlank(message = "Password is mandatory")
	@Size(min = 8, message = "Password must have at least 8 characters")
	@Schema(description = "User Password", example = "contraseña123")
	private String newPassword;
	@NotBlank(message = "Password is mandatory")
	@Size(min = 8, message = "Password must have at least 8 characters")
	@Schema(description = "Confirm password must be equal to the new password", example = "contraseña123")
	private String confirmPassword;
}
