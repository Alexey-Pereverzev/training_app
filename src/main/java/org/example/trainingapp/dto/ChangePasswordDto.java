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
@Schema(description = "DTO for changing user password")
public class ChangePasswordDto {
    @Schema(description = "Username of the account", example = "Dina.Aliyeva", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(description = "Old password of the user", example = "oldPass123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String oldPassword;

    @Schema(description = "New password to be set", example = "newPass456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newPassword;
}
