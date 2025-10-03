package com.williammedina.forohub.controller;

import com.williammedina.forohub.config.TestConfig;
import com.williammedina.forohub.config.TestUtil;
import com.williammedina.forohub.domain.user.entity.UserEntity;
import com.williammedina.forohub.domain.user.repository.UserRepository;
import com.williammedina.forohub.domain.user.dto.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ActiveProfiles("test")
@Transactional
@Import(TestConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<CreateUserDTO> createUserDTOJacksonTester;

    @Autowired
    private JacksonTester<LoginUserDTO> loginUserDTOJacksonTester;

    @Autowired
    private JacksonTester<EmailUserDTO> emailUserDTOJacksonTester;

    @Autowired
    private JacksonTester<UpdatePasswordWithTokenDTO> updatePasswordWithTokenDTOJacksonTester;

    @Autowired
    private JacksonTester<UpdateCurrentUserPasswordDTO> updateCurrentUserPasswordDTOJacksonTester;

    @Autowired
    private JacksonTester<UpdateUsernameDTO> updateUsernameDTOJacksonTester;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TestUtil testUtil;

    @Test
    @DisplayName("Debería devolver HTTP 201 cuando la cuenta de usuario se crea exitosamente")
    void createAccount_Success() throws Exception {
        CreateUserDTO createUserDTO = new CreateUserDTO("newuser@example.com", "newuser", "password", "password");
        var mvcResponse = mvc.perform(post("/api/auth/create-account")
                        .contentType("application/json")
                        .content(createUserDTOJacksonTester.write(createUserDTO).getJson()))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("Debería devolver HTTP 400 cuando los datos de solicitud son inválidos")
    void createAccount_InvalidData() throws Exception {
        CreateUserDTO createUserDTO = new CreateUserDTO("", "", "", "");
        var mvcResponse = mvc.perform(post("/api/auth/create-account")
                        .contentType("application/json")
                        .content(createUserDTOJacksonTester.write(createUserDTO).getJson()))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Debería devolver HTTP 409 cuando el nombre de usuario o el email ya están registrados")
    void createAccount_UsernameOrEmailConflict() throws Exception {
        testUtil.createUser("newuser@example.com", "newuser");
        CreateUserDTO createUserDTO = new CreateUserDTO("newuser@example.com", "newuser", "password", "password");
        var mvcResponse = mvc.perform(post("/api/auth/create-account")
                        .contentType("application/json")
                        .content(createUserDTOJacksonTester.write(createUserDTO).getJson()))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    @DisplayName("Debería devolver HTTP 200 cuando la cuenta es confirmada exitosamente")
    void confirmAccount_Success() throws Exception {
        UserEntity user = testUtil.createUser("newuser@example.com", "newuser");
        var mvcResponse = mvc.perform(get("/api/auth/confirm-account/" + user.getToken()))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Debería devolver HTTP 400 cuando el token es inválido")
    void confirmAccount_InvalidToken() throws Exception {
        String invalidToken = "invalid-token";
        var mvcResponse = mvc.perform(get("/api/auth/confirm-account/" + invalidToken))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Debería devolver HTTP 410 cuando el token ha expirado")
    void confirmAccount_ExpiredToken() throws Exception {
        UserEntity user = testUtil.createUser("newuser@example.com", "newuser");
        user.setTokenExpiration(user.getTokenExpiration().minusDays(1));
        var mvcResponse = mvc.perform(get("/api/auth/confirm-account/" + user.getToken()))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.GONE.value());
    }

    @Test
    @DisplayName("Debería devolver HTTP 200 cuando el inicio de sesión es exitoso")
    void login_Success() throws Exception {
        UserEntity user = testUtil.createUser("user@example.com", "user");
        user.setAccountConfirmed(true);
        LoginUserDTO loginData = new LoginUserDTO("user", "password");
        var mvcResponse = mvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(loginUserDTOJacksonTester.write(loginData).getJson()))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Debería devolver HTTP 400 cuando los datos de solicitud son inválidos")
    void login_InvalidRequestData() throws Exception {
        LoginUserDTO loginData = new LoginUserDTO("", "");
        var mvcResponse = mvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(loginUserDTOJacksonTester.write(loginData).getJson()))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Debería devolver HTTP 401 cuando las credenciales son inválidas")
    void login_InvalidCredentials() throws Exception {
        UserEntity user = testUtil.createUser("user@example.com", "user");
        user.setAccountConfirmed(true);
        LoginUserDTO loginData = new LoginUserDTO("user", "wrongpassword");
        var mvcResponse = mvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(loginUserDTOJacksonTester.write(loginData).getJson()))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("Debería devolver HTTP 403 cuando la cuenta no está confirmada")
    void login_UnconfirmedAccount() throws Exception {
        LoginUserDTO loginData = new LoginUserDTO("unconfirmed@example.com", "password");
        var mvcResponse = mvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(loginUserDTOJacksonTester.write(loginData).getJson()))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("Debería devolver HTTP 200 cuando el email es válido y el código de confirmación se envía exitosamente")
    void requestConfirmationCode_Success() throws Exception {
        UserEntity user = testUtil.createUser("user@example.com", "user");
        user.clearTokenData();
        EmailUserDTO emailUserDTO = new EmailUserDTO("user@example.com");
        var mvcResponse = mvc.perform(post("/api/auth/request-code")
                        .contentType("application/json")
                        .content(emailUserDTOJacksonTester.write(emailUserDTO).getJson()))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Debería devolver HTTP 400 cuando hay demasiadas solicitudes recientes o el token es inválido")
    void requestConfirmationCode_TooManyRequestsOrInvalidToken() throws Exception {
        testUtil.createUser("user@example.com", "user");
        EmailUserDTO emailUserDTO = new EmailUserDTO("user@example.com");
        var mvcResponse = mvc.perform(post("/api/auth/request-code")
                        .contentType("application/json")
                        .content(emailUserDTOJacksonTester.write(emailUserDTO).getJson()))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Debería devolver HTTP 409 cuando la cuenta ya está confirmada")
    void requestConfirmationCode_AlreadyConfirmed() throws Exception {
        UserEntity user = testUtil.createUser("user@example.com", "user");
        user.setAccountConfirmed(true);
        EmailUserDTO emailUserDTO = new EmailUserDTO("user@example.com");
        var mvcResponse = mvc.perform(post("/api/auth/request-code")
                        .contentType("application/json")
                        .content(emailUserDTOJacksonTester.write(emailUserDTO).getJson()))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    @DisplayName("Debería devolver HTTP 404 cuando el email no está registrado")
    void requestConfirmationCode_EmailNotRegistered() throws Exception {
        EmailUserDTO emailUserDTO = new EmailUserDTO("nonexistent@example.com");
        var mvcResponse = mvc.perform(post("/api/auth/request-code")
                        .contentType("application/json")
                        .content(emailUserDTOJacksonTester.write(emailUserDTO).getJson()))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Debería devolver HTTP 200 cuando el email se envía exitosamente")
    void forgotPassword_Success() throws Exception {
        UserEntity user = testUtil.createUser("user@example.com", "user");
        user.clearTokenData();
        user.setAccountConfirmed(true);
        EmailUserDTO request = new EmailUserDTO("user@example.com");
        var mvcResponse = mvc.perform(post("/api/auth/forgot-password")
                        .contentType("application/json")
                        .content(emailUserDTOJacksonTester.write(request).getJson()))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Debería devolver HTTP 400 cuando se realizan demasiadas solicitudes recientes o el token es inválido")
    void forgotPassword_TooManyRequestsOrInvalidToken() throws Exception {
        UserEntity user = testUtil.createUser("user@example.com", "user");
        user.setAccountConfirmed(true);
        EmailUserDTO request = new EmailUserDTO("user@example.com");
        var mvcResponse = mvc.perform(post("/api/auth/forgot-password")
                        .contentType("application/json")
                        .content(emailUserDTOJacksonTester.write(request).getJson()))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Debería devolver HTTP 404 cuando el email no está registrado")
    void forgotPassword_EmailNotRegistered() throws Exception {
        EmailUserDTO request = new EmailUserDTO("nonexistent@example.com");
        var mvcResponse = mvc.perform(post("/api/auth/forgot-password")
                        .contentType("application/json")
                        .content(emailUserDTOJacksonTester.write(request).getJson()))
                .andReturn().getResponse();

        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Debería devolver HTTP 409 cuando la cuenta no está confirmada")
    void forgotPassword_UnconfirmedAccount() throws Exception {
        UserEntity user = testUtil.createUser("user@example.com", "user");
        EmailUserDTO request = new EmailUserDTO("user@example.com");
        var mvcResponse = mvc.perform(post("/api/auth/forgot-password")
                        .contentType("application/json")
                        .content(emailUserDTOJacksonTester.write(request).getJson()))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    @DisplayName("Debería devolver HTTP 200 cuando el password se actualiza exitosamente")
    void updatePasswordWithToken_Success() throws Exception {
        UserEntity user = testUtil.createUser("user@example.com", "user");
        user.setAccountConfirmed(true);
        UpdatePasswordWithTokenDTO request = new UpdatePasswordWithTokenDTO("newPassword", "newPassword");
        var mvcResponse = mvc.perform(post("/api/auth/update-password/" + user.getToken())
                        .contentType("application/json")
                        .content(updatePasswordWithTokenDTOJacksonTester.write(request).getJson()))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Debería devolver HTTP 400 cuando el token o los datos son inválidos")
    void updatePasswordWithToken_InvalidTokenOrData() throws Exception {
        UserEntity user = testUtil.createUser("user@example.com", "user");
        user.setAccountConfirmed(true);
        UpdatePasswordWithTokenDTO request = new UpdatePasswordWithTokenDTO("", "");
        var mvcResponse = mvc.perform(post("/api/auth/update-password/" + user.getToken())
                        .contentType("application/json")
                        .content(updatePasswordWithTokenDTOJacksonTester.write(request).getJson()))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Debería devolver HTTP 403 cuando la cuenta no está confirmada")
    void updatePasswordWithToken_UnconfirmedAccount() throws Exception {
        UserEntity user = testUtil.createUser("user@example.com", "user");
        UpdatePasswordWithTokenDTO request = new UpdatePasswordWithTokenDTO("newPassword", "newPassword");
        var mvcResponse = mvc.perform(post("/api/auth/update-password/" + user.getToken())
                        .contentType("application/json")
                        .content(updatePasswordWithTokenDTOJacksonTester.write(request).getJson()))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    @DisplayName("Debería devolver HTTP 410 cuando el token ha expirado")
    void updatePasswordWithToken_ExpiredToken() throws Exception {
        UserEntity user = testUtil.createUser("user@example.com", "user");
        user.setAccountConfirmed(true);
        user.setTokenExpiration(user.getTokenExpiration().minusDays(1));
        UpdatePasswordWithTokenDTO request = new UpdatePasswordWithTokenDTO("newPassword", "newPassword");
        var mvcResponse = mvc.perform(post("/api/auth/update-password/" + user.getToken())
                        .contentType("application/json")
                        .content(updatePasswordWithTokenDTOJacksonTester.write(request).getJson()))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.GONE.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 200 cuando el password se actualiza exitosamente")
    void updateCurrentUserPassword_Success() throws Exception {
        UserEntity user = testUtil.getAuthenticatedUser("William");
        UpdateCurrentUserPasswordDTO request = new UpdateCurrentUserPasswordDTO("password", "newPassword", "newPassword");
        var mvcResponse = mvc.perform(
                testUtil.withAuth(
                        patch("/api/auth/update-password")
                                .contentType("application/json")
                                .content(updateCurrentUserPasswordDTOJacksonTester.write(request).getJson()),
                        user
                )
        ).andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 400 cuando los datos de la solicitud son inválidos")
    void updateCurrentUserPassword_InvalidData() throws Exception {
        UserEntity user = testUtil.getAuthenticatedUser("William");
        UpdateCurrentUserPasswordDTO request = new UpdateCurrentUserPasswordDTO("", "" , "");
        var mvcResponse = mvc.perform(
                testUtil.withAuth(
                        patch("/api/auth/update-password")
                                .contentType("application/json")
                                .content(updateCurrentUserPasswordDTOJacksonTester.write(request).getJson()),
                        user
                )
        ).andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 401 cuando el password actual es incorrecto o el token es inválido")
    void updateCurrentUserPassword_Unauthorized() throws Exception {
        UserEntity user = testUtil.getAuthenticatedUser("William");
        UpdateCurrentUserPasswordDTO request = new UpdateCurrentUserPasswordDTO("wrongpassword", "newPassword", "newPassword");
        var mvcResponse = mvc.perform(
                testUtil.withAuth(
                        patch("/api/auth/update-password")
                                .contentType("application/json")
                                .content(updateCurrentUserPasswordDTOJacksonTester.write(request).getJson()),
                        user
                )
        ).andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 200 cuando el nombre de usuario se actualiza exitosamente")
    void updateUsername_Success() throws Exception {
        UserEntity user = testUtil.getAuthenticatedUser("William");
        UpdateUsernameDTO request = new UpdateUsernameDTO("newUsername");
        var mvcResponse = mvc.perform(
                testUtil.withAuth(
                        patch("/api/auth/update-username")
                                .contentType("application/json")
                                .content(updateUsernameDTOJacksonTester.write(request).getJson()),
                        user
                )
        ).andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 400 cuando los datos de la solicitud son inválidos")
    void updateUsername_InvalidData() throws Exception {
        UserEntity user = testUtil.getAuthenticatedUser("William");
        UpdateUsernameDTO request = new UpdateUsernameDTO("");
        var mvcResponse = mvc.perform(
                testUtil.withAuth(
                        patch("/api/auth/update-username")
                                .contentType("application/json")
                                .content(updateUsernameDTOJacksonTester.write(request).getJson()),
                        user
                )
        ).andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 409 cuando el nombre de usuario ya están registrados")
    void updateUsername_UsernameConflict() throws Exception {
        UserEntity user = testUtil.getAuthenticatedUser("William");
        testUtil.createUser("user@example.com", "user");

        UpdateUsernameDTO request = new UpdateUsernameDTO("user");
        var mvcResponse = mvc.perform(
                testUtil.withAuth(
                        patch("/api/auth/update-username")
                                .contentType("application/json")
                                .content(updateUsernameDTOJacksonTester.write(request).getJson()),
                        user
                )
        ).andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 200 cuando se obtienen las estadísticas del usuario correctamente")
    void getUserStats_Success() throws Exception {
        UserEntity user = testUtil.getAuthenticatedUser("William");
        var mvcResponse = mvc.perform(
                testUtil.withAuth(get("/api/auth/stats").contentType("application/json"), user)
        ).andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 200 cuando se obtienen los detalles del usuario correctamente")
    void getCurrentUser_Success() throws Exception {
        UserEntity user = testUtil.getAuthenticatedUser("William");
        var mvcResponse = mvc.perform(
                testUtil.withAuth(get("/api/auth/me").contentType("application/json"), user)
        ).andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Debería devolver HTTP 401 si el usuario no está autenticado")
    void getCurrentUser_Unauthorized() throws Exception {
        var mvcResponse = mvc.perform(get("/api/auth/me")
                        .contentType("application/json"))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("Debería devolver HTTP 200 cuando el token de actualización es válido")
    void refreshToken_Success() throws Exception {
        UserEntity user = testUtil.getAuthenticatedUser("William");
        var mvcResponse = mvc.perform(post("/api/auth/token/refresh")
                        .cookie(testUtil.createCookie(user, "refresh_token", "/", 20000)))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Debería devolver HTTP 401 cuando el token de actualización es inválido o no está presente")
    void refreshToken_Unauthorized() throws Exception {
        var mvcResponse = mvc.perform(post("/api/auth/token/refresh"))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 200 cuando la sesión se cierra exitosamente")
    void logout_Success() throws Exception {
        UserEntity user = testUtil.getAuthenticatedUser("William");
        var mvcResponse = mvc.perform(
                testUtil.withAuth(post("/api/auth/token/logout"), user)
        ).andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Debería devolver HTTP 401 si el token de acceso es inválido o no está presente al cerrar sesión")
    void logout_Unauthorized() throws Exception {
        var mvcResponse = mvc.perform(post("/api/auth/token/logout"))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

}