package com.ultimate.wellme.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignUpRequest {
    private String email;
    private String password;
    private String password_repeat;
}
