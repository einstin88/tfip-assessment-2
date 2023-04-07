package assessment.batch3.authserver.model;

import jakarta.validation.constraints.Size;

public record LoginForm(
    @Size(min = 2, message = "Username must be at least 2 characters")
    String username,
    
    @Size(min = 2, message = "Password must be at least 2 characters")
    String password,

    Double captcha
) {
    
}
