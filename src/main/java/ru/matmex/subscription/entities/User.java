package ru.matmex.subscription.entities;

import jakarta.persistence.*;
import ru.matmex.subscription.models.user.Role;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Сущность пользователя
 */
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private Long telegramChatId;
    @OneToOne
    @JoinColumn(name = "google_credential_id")
    private GoogleCredential googleCredential;
    private String password;
    /**
     * Секретный ключ пользователя для привязки его телеграмм аккаунта
     * Генерируется один раз при привязки телеграмм аккаунта
     */
    private byte[] telegramSecretKey;

    private String email;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Category> categories;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public GoogleCredential getGoogleCredential() {
        return googleCredential;
    }

    public void setGoogleCredential(GoogleCredential googleCredential) {
        this.googleCredential = googleCredential;
    }

    public Long getTelegramChatId() {
        return telegramChatId;
    }

    public void setTelegramChatId(long telegramChatId) {
        this.telegramChatId = telegramChatId;
    }

    public byte[] getTelegramSecretKey() {
        return telegramSecretKey;
    }

    public User() {
    }

    public User(String username, String email, String password, byte[] telegramSecretKey) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.categories = new ArrayList<>();
        this.telegramSecretKey = telegramSecretKey;
        roles.add(Role.USER);
    }

    public User(String username, String email, String password, Role role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.categories = new ArrayList<>();
        roles.add(role);
    }
}
