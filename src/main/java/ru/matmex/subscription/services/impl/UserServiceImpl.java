package ru.matmex.subscription.services.impl;

import com.google.api.client.auth.oauth2.Credential;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.matmex.subscription.entities.GoogleCredential;
import ru.matmex.subscription.entities.User;
import ru.matmex.subscription.models.security.Crypto;
import ru.matmex.subscription.models.user.Role;
import ru.matmex.subscription.models.user.UserModel;
import ru.matmex.subscription.models.user.UserRegistrationModel;
import ru.matmex.subscription.models.user.UserUpdateModel;
import ru.matmex.subscription.repositories.CredentialRepository;
import ru.matmex.subscription.repositories.UserRepository;
import ru.matmex.subscription.services.UserService;
import ru.matmex.subscription.services.notifications.NotificationService;
import ru.matmex.subscription.services.utils.mapping.CategoryModelMapper;
import ru.matmex.subscription.services.utils.mapping.UserModelMapper;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для операций с пользователем
 */
@Service
public class UserServiceImpl implements UserService {

    public static final Random RANDOM = new Random();

    private final UserRepository userRepository;
    private final CredentialRepository credentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserModelMapper userModelMapper;
    private final Crypto crypto;
    private final NotificationService notificationService;

    @Autowired
    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            Crypto crypto,
            CredentialRepository credentialRepository, NotificationService notificationService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.notificationService = notificationService;
        this.userModelMapper = new UserModelMapper(new CategoryModelMapper());
        this.credentialRepository = credentialRepository;
        createAdmin();
        this.crypto = crypto;
    }

    /**
     * Загрузить пользователя по имени
     *
     * @param username - имя пользователя
     * @return авторизовачная информация о пользователе
     * @throws UsernameNotFoundException пользователь не найден
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getUser(username);
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), mapRolesToAuthorities(user.getRoles()));
    }

    @Override
    public UserModel adduser(UserRegistrationModel userRegistrationModel) {
        if (userRepository.existsByUsername(userRegistrationModel.username())) {
            throw new AuthenticationServiceException("Пользователь с таким именем уже существует");
        }
        String secretKey = createSecretTelegramKey();
        User user = new User(userRegistrationModel.username(),
                userRegistrationModel.email(),
                passwordEncoder.encode(userRegistrationModel.password()),
                crypto.encrypt(secretKey.getBytes(StandardCharsets.UTF_8)));
        userRepository.save(user);
        notificationService.registerNotification(
                "Вы успешно зарегистрировались в приложении! \n Ваш секретный ключ для тг: " + secretKey,
                userRegistrationModel.email());
        return userModelMapper.map(user);
    }

    @Override
    public UserModel updateUser(UserUpdateModel userUpdateModel) {
        User user = getUser(userUpdateModel.id());
        user.setUsername(userUpdateModel.name());
        user.setEmail(userUpdateModel.email());
        userRepository.save(user);
        return userModelMapper.map(user);
    }

    @Override
    public UserModel getUserModel(Long id) {
        return userModelMapper
                .map(getUser(id));
    }

    @Override
    public UserModel getUserModel(String username) {
        return userModelMapper
                .map(getUser(username));
    }

    public User getUser(String username) throws UsernameNotFoundException {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with name:" + username + "not found"));
    }

    @Override
    public User getUser(Long id) throws UsernameNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Преобразовать роль к авторизационной роли spring-security
     *
     * @param roles список ролей
     * @return список авторизационных ролей
     */
    private List<? extends GrantedAuthority> mapRolesToAuthorities(Set<Role> roles) {
        return roles
                .stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.name()))
                .collect(Collectors.toList());
    }

    @Override
    public User getCurrentUser() {
        Authentication user = SecurityContextHolder.getContext().getAuthentication();
        return getUser(user.getName());
    }

    @Override
    public void setTelegramChatId(User user, long telegramChatId) {
        user.setTelegramChatId(telegramChatId);
        userRepository.save(user);
    }

    private String createSecretTelegramKey() {
        StringBuilder sb = new StringBuilder();
        int length = 16;
        while (sb.length() < length) {
            sb.append(Integer.toHexString(RANDOM.nextInt()));
        }
        return sb.toString();
    }

    @Override
    public String delete(String username) {
        if (!userRepository.existsByUsername(username)) {
            throw new UsernameNotFoundException("User with" + username + " not found");
        }
        User user = getUser(username);
        userRepository.delete(user);
        notificationService.registerNotification("Пользователь " + username + " успешно удален", username);
        return "Пользователь успешно удален!";
    }

    @Override
    public List<UserModel> getUsers() {
        return userRepository
                .findAll()
                .stream().map(userModelMapper::map).toList();
    }

    @Override
    public GoogleCredential getGoogleCredential() {
        User currentUser = getCurrentUser();
        return currentUser.getGoogleCredential();
    }

    @Override
    public GoogleCredential getGoogleCredential(String username) {
        User user = getUser(username);
        return user.getGoogleCredential();
    }

    @Override
    public void setGoogleCredential(Credential credential) {
        User currentUser = getCurrentUser();
        GoogleCredential newCredential = new GoogleCredential(credential.getAccessToken(), credential.getExpirationTimeMilliseconds(), credential.getRefreshToken());
        currentUser.setGoogleCredential(newCredential);
        credentialRepository.save(newCredential);
    }

    @Override
    public String getInformationAboutGoogle() {
        return getCurrentUser().getGoogleCredential() == null ? "гугл аккаунт не привязан" : "гугл аккаунт успешно привязан";
    }

    @Override
    public boolean checkIntegrationWithTelegram() {
        return Optional.ofNullable(getCurrentUser().getTelegramChatId()).isPresent();
    }

    /**
     * Создать администратора
     */
    public void createAdmin() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User("admin", "admin@mail.ru", passwordEncoder.encode("admin"), Role.ADMIN);
            admin.setRoles(Set.of(Role.ADMIN));
            userRepository.save(admin);
        }
    }
}
