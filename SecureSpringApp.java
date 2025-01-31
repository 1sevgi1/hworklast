/*
 * Java Spring Security Application - Lab 14
 * Features Implemented:
 * - Database Migration using Flyway
 * - User Authentication & Authorization with Spring Security
 * - Secure Password Management
 * - Input Validation & Access Control
 */

// Main Application
@SpringBootApplication
public class SecureSpringApp {
    public static void main(String[] args) {
        SpringApplication.run(SecureSpringApp.class, args);
    }
}

// Entity for User
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Enumerated(EnumType.STRING)
    private Role role;
}

// Enum for Roles
public enum Role {
    ROLE_USER, ROLE_ADMIN
}

// Entity for Data Storage (e.g., Books)
@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false)
    private String author;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}

// User Repository
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}

// Book Repository
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByUser(User user);
}

// Service for User Authentication
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(String username, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(username, encodedPassword, Role.ROLE_USER);
        return userRepository.save(user);
    }
}

// Security Configuration
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests()
            .antMatchers("/auth/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
