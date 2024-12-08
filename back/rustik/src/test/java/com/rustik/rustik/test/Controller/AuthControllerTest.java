package com.rustik.rustik.test.Controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rustik.rustik.controller.AuthController;
import com.rustik.rustik.dto.AuthUserDTO;
import com.rustik.rustik.dto.LogInDTO;
import com.rustik.rustik.dto.UserDTO;
import com.rustik.rustik.model.User;
import com.rustik.rustik.model.UserRole;
import com.rustik.rustik.security.CustomUserDetails;
import com.rustik.rustik.security.TokenService;
import com.rustik.rustik.service.EmailService;
import com.rustik.rustik.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private TokenService tokenService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    public void testRegisterUser_Success() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");
        userDTO.setName("Test");
        userDTO.setSurname("User");
        userDTO.setPhone("123456789");
        userDTO.setPassword("1234Admin!");
        userDTO.setRepeatPassword("1234Admin!");
        userDTO.setCountry("CO");

        User user = new User("Test", "User", "test@example.com", "123456789", "CO", UserRole.ROLE_USER, "1234Admin!");
        AuthUserDTO authUserDTO = new AuthUserDTO(user, "fakeToken");

        when(userService.findUserByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userService.findUserByPhone(user.getPhone())).thenReturn(Optional.empty());
        when(userService.registerUser(user)).thenReturn(authUserDTO);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO))
                        .param("resendConfirmationEmail", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.name").value(user.getName() + " " + user.getSurname()));
    }


    @Test
    public void testRegisterUser_EmailAlreadyExists() throws Exception {

        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");
        userDTO.setName("Test");
        userDTO.setSurname("User");
        userDTO.setPhone("123456789");
        userDTO.setPassword("1234Admin!");
        userDTO.setRepeatPassword("1234Admin!");
        userDTO.setCountry("CO");

        User user = new User("Test", "User", "test@example.com", "123456789", "CO", UserRole.ROLE_USER, "1234Admin!");
        when(userService.findUserByEmail(user.getEmail())).thenReturn(Optional.of(user));

        ResultActions result = mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)));

        result.andExpect(status().isBadRequest())
                .andExpect(content().string("El correo electrónico ya está registrado."));
    }


    @Test
    public void testLogIn_Success() throws Exception {
        LogInDTO logInDTO = new LogInDTO();
        logInDTO.setEmail("test@example.com");
        logInDTO.setPassword("123456789");

        AuthUserDTO authUserDTO = new AuthUserDTO(new User("Test", "User", "test@example.com", "123456789", "CO", UserRole.ROLE_USER, "1234Admin!"), "fakeToken");

        when(userService.logIn(logInDTO)).thenReturn(authUserDTO);

        ResultActions result = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(logInDTO)));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(authUserDTO.getEmail()));
    }


    @Test
    public void testValidateToken_Success() throws Exception {
        String token = "validToken";

        User user = new User("Test", "User", "test@example.com", "123456789", "CO", UserRole.ROLE_USER, "1234Admin!");
        CustomUserDetails userDetails = new CustomUserDetails(user);

        mockMvc.perform(get("/api/v1/auth/validate-token")
                        .header("Authorization", "Bearer " + token)
                        .principal(userDetails))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }


    @Test
    public void testIsAdmin_Success() throws Exception {
        String token = "validToken";

        User user = new User("Test", "User", "test@example.com", "123456789", "CO", UserRole.ROLE_ADMIN, "1234Admin!");
        CustomUserDetails userDetails = new CustomUserDetails(user);

        when(tokenService.subjectIsAdmin(anyString())).thenReturn(true);

        ResultActions result = mockMvc.perform(get("/api/v1/auth/isAdmin")
                .header("Authorization", "Bearer " + token)
                .principal(userDetails));

        result.andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

 /*

    @Test
    public void testIsAdmin_Unauthorized() throws Exception {
        String invalidToken = "Bearer invalidToken";

        User user = new User("Test", "User", "test@example.com", "123456789", "CO", UserRole.ROLE_USER, "1234Admin!");
        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(tokenService.subjectIsAdmin(anyString())).thenReturn(false);

        ResultActions result = mockMvc.perform(get("/api/v1/auth/isAdmin")
                .header("Authorization", invalidToken));

        result.andExpect(status().isUnauthorized());

        SecurityContextHolder.clearContext();
    }
    */
}