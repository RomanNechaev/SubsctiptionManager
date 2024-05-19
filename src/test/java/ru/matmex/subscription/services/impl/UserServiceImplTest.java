package ru.matmex.subscription.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import ru.matmex.subscription.entities.GoogleCredential;
import ru.matmex.subscription.entities.User;
import ru.matmex.subscription.models.security.Crypto;
import ru.matmex.subscription.models.user.UserModel;
import ru.matmex.subscription.models.user.UserRegistrationModel;
import ru.matmex.subscription.models.user.UserUpdateModel;
import ru.matmex.subscription.repositories.CredentialRepository;
import ru.matmex.subscription.repositories.UserRepository;
import ru.matmex.subscription.services.UserService;
import ru.matmex.subscription.services.notifications.NotificationService;
import ru.matmex.subscription.services.utils.mapping.CategoryModelMapper;
import ru.matmex.subscription.services.notifications.email.UserEmailNotificationSender;
import ru.matmex.subscription.services.utils.mapping.UserModelMapper;
import ru.matmex.subscription.utils.UserBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@ContextConfiguration(classes = {UserServiceImpl.class, PasswordEncoder.class})
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
    private final UserModelMapper userModelMapper = new UserModelMapper(new CategoryModelMapper());
    private final CredentialRepository credentialRepository = Mockito.mock(CredentialRepository.class);
    private final NotificationService notificationService = Mockito.mock(NotificationService.class);
    private final Crypto crypto = Mockito.mock(Crypto.class);
    private final UserService userService = new UserServiceImpl(
            userRepository,
            passwordEncoder,
            crypto,
            credentialRepository,
            notificationService
    );
    private final User defaultUser = UserBuilder.anUser().defaultUser();

    /**
     * Тестирование авторизации пользователя
     */
    @Test
    void testCanLoadUserByUsername() {
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(defaultUser));

        UserDetails user = new org.springframework.security.core.userdetails
                .User(defaultUser.getUsername(), defaultUser.getPassword(), defaultUser.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.name()))
                .collect(Collectors.toList()));

        UserDetails userResult = userService.loadUserByUsername("test");
        assertThat(user).isEqualTo(userResult);
    }

    /**
     * Тестирование добавления пользователя
     */
    @Test
    void testCanAddUser() {
        UserRegistrationModel userFromContext = new UserRegistrationModel("Test", "test@gmail.com", "123456");

        userService.adduser(userFromContext);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository, times(2)).save(userArgumentCaptor.capture());

        User userCaptured = userArgumentCaptor.getValue();

        assertThat(userCaptured.getUsername()).isSameAs(userFromContext.username());
    }

    /**
     * Тестирование добавления пользователя, если никнейм уже занят
     */
    @Test
    void testWillThrowWhenUsernameIsTaken() {
        UserRegistrationModel userFromContext = new UserRegistrationModel("Test", "test@gmail.com", "123456");

        given(userRepository.existsByUsername(userFromContext.username())).willReturn(true);

        assertThatThrownBy(() -> userService.adduser(userFromContext))
                .isInstanceOf(AuthenticationServiceException.class)
                .hasMessageContaining("Пользователь с таким именем уже существует");
    }

    /**
     * Тестирование обновления пользователя
     */
    @Test
    void testCanUpdateUser() {
        String newEmail = "test@yandex.ru";
        UserUpdateModel userUpdateModel = new UserUpdateModel(12L, "test", newEmail);
        String oldEmail = "test@gmail.com";
        User user = new User("test", oldEmail, "123", "123".getBytes());

        when(userRepository.findById(12L)).thenReturn(Optional.of(user));

        userService.updateUser(userUpdateModel);

        String updatedEmail = user.getEmail();

        assertThat(updatedEmail).isSameAs("test@yandex.ru");
    }

    /**
     * Тестирование получения пользователя
     */
    @Test
    void canGetUser() {

        when(userRepository.findByUsername("test")).thenReturn(Optional.of(defaultUser));

        UserModel user = userService.getUserModel("test");

        assertThat(userModelMapper.map(defaultUser)).isEqualTo(user);
    }

    /**
     * Тестирование получения несуществующего пользователя
     */
    @Test
    void willThrownWhenGetUserReturnEmptyOptional() {
        String username = defaultUser.getUsername();
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserModel(username))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User with name:"+username+ "not found");
    }

    /**
     * Тестирование удаления пользователя
     */
    @Test
    void testCanDeleteUser() {
        String username = defaultUser.getUsername();
        given(userRepository.existsByUsername(username)).willReturn(true);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(defaultUser));
        userService.delete(username);

        verify(userRepository).delete(defaultUser);
    }

    /**
     * Тестирование удаления несуществущего пользователя
     */
    @Test
    void testWillThrowWhenDeleteUserNotFound() {
        String username = defaultUser.getUsername();
        given(userRepository.existsByUsername(username)).willReturn(false);

        assertThatThrownBy(() -> userService.delete(username))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User with" + username + " not found");
        verify(userRepository, never()).delete(any());

    }

    /**
     * Тестирование на получения всех пользователей
     */
    @Test
    void testCanGetAllUsers() {
        List<User> usersList = List
                .of(defaultUser,
                        UserBuilder.anUser()
                                .withUsername("test2")
                                .withEmail("test@gmail.com")
                                .withPassword("123")
                                .build());

        when(userRepository.findAll()).thenReturn(usersList);

        List<UserModel> allUsers = userService.getUsers();

        assertThat(usersList.size()).isEqualTo(allUsers.size());

        assertThat(allUsers.get(0).username()).isEqualTo(usersList.get(0).getUsername());
        assertThat(allUsers.get(1).username()).isEqualTo(usersList.get(1).getUsername());

        verify(userRepository).findAll();
    }

    /**
     * Тестирование получение учетных данных для гугл-аккаунта пользователя
     */
    @Test
    void testGetGoogleCredential() throws IOException {
        String accessToken = "some access token";
        Long expirationTimeMilliseconds = 10L;
        String refreshToken = "some refresh token";
        User user = UserBuilder.anUser().defaultUser();
        user.setGoogleCredential(new GoogleCredential(accessToken, expirationTimeMilliseconds, refreshToken));

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        GoogleCredential googleCredential = userService.getGoogleCredential(user.getUsername());

        assertThat(googleCredential.getAccessToken()).isEqualTo("some access token");
        assertThat(googleCredential.getExpirationTimeMilliseconds()).isEqualTo(10L);
        assertThat(googleCredential.getRefreshToken()).isEqualTo("some refresh token");

    }
}