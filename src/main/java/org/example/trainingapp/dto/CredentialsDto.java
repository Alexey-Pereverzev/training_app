package org.example.trainingapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User credentials for login or registration")
public class CredentialsDto {
    @Schema(description = "Username of the user", example = "Dina.Aliyeva", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(description = "Password of the user", example = "secret123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
