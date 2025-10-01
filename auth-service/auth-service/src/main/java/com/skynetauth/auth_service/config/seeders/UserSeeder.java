package com.skynetauth.auth_service.config.seeders;

import java.util.Arrays;
import java.util.Collections;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.skynetauth.auth_service.Enum.UserType;
import com.skynetauth.auth_service.models.Permission;
import com.skynetauth.auth_service.models.Role;
import com.skynetauth.auth_service.models.User;
import com.skynetauth.auth_service.repositories.PermissionRepository;
import com.skynetauth.auth_service.repositories.RoleRepository;
import com.skynetauth.auth_service.repositories.UserRepository;

@Component
public class UserSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(UserSeeder.class);

    @Autowired
    public UserSeeder(UserRepository userRepository, RoleRepository roleRepository,
            PermissionRepository permissionRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (args.getOptionValues("seeder") != null) {
            List<String> seeder = Arrays.asList(args.getOptionValues("seeder").get(0).split(","));
            if (seeder.contains("user")) {
                userSeeder();
            }
            if (seeder.contains("role")) {
                roleSeeder();
            }
            if (seeder.contains("permission")) {
                permissionSeeder();
            }
            System.exit(0);
        } else {
            logger.info("No seeders ran!");
        }
    }

    private void userSeeder() {
        try {
            Role superAdmin = roleRepository.findByName("SUPERADMIN").orElse(new Role());
            Role admin = roleRepository.findByName("ADMIN").orElse(new Role());
            Role userRole = roleRepository.findByName("USER").orElse(new Role());

            User user1 = new User();
            user1.setFirstName("Super");
            user1.setLastName("Admin");
            user1.setEmail("superadmin@example.com");
            user1.setPassword(passwordEncoder.encode("securepassword"));
            user1.setRoles(Collections.singletonList(superAdmin));
            user1.setPermissions(superAdmin.getPermissions());

            User user2 = new User();
            user2.setFirstName("Admin");
            user2.setLastName("User");
            user2.setEmail("admin@example.com");
            user2.setPassword(passwordEncoder.encode("securepassword"));
            user2.setRoles(Collections.singletonList(admin));
            user2.setPermissions(admin.getPermissions());

            User user3 = new User();
            user3.setFirstName("Normal");
            user3.setLastName("User");
            user3.setEmail("user@example.com");
            user3.setPassword(passwordEncoder.encode("securepassword"));
            user3.setRoles(Collections.singletonList(userRole));
            user3.setPermissions(userRole.getPermissions());

            userRepository.save(user1);
            userRepository.save(user2);
            userRepository.save(user3);

            logger.info("Seeded users: superadmin, admin, user");
        } catch (Exception e) {
            logger.error("Failed to seed users", e);
        }
    }

    private void roleSeeder() {
        try {
            Permission read = permissionRepository.findByName("READ");
            Permission write = permissionRepository.findByName("WRITE");
            Permission createUsers = permissionRepository.findByName("CAN_CREATE_USERS");

            Role superAdmin = new Role();
            superAdmin.setName("SUPERADMIN");
            superAdmin.setPermissions((Arrays.asList(read, write, createUsers)));
            superAdmin.setUserType(UserType.ADMIN);

            Role admin = new Role();
            admin.setName("ADMIN");
            admin.setPermissions((Arrays.asList(read, write)));
            admin.setUserType(UserType.ADMIN);
            
            Role user = new Role();
            user.setName("USER");
            user.setPermissions((Collections.singletonList(read)));
            user.setUserType(UserType.USER);

            roleRepository.save(superAdmin);
            roleRepository.save(admin);
            roleRepository.save(user);

            logger.info("Seeded roles: SUPERADMIN, ADMIN, USER");
        } catch (Exception e) {
            logger.error("Failed to seed roles", e);
        }
    }

    private void permissionSeeder() {
        try {
            List<Permission> permissions = Arrays.asList(
                new Permission("READ", UserType.USER),
                new Permission("WRITE", UserType.ADMIN),
                new Permission("CAN CREATE USERS", UserType.ADMIN)
            );

            for (Permission permission : permissions) {
                permissionRepository.save(permission);
                logger.info("Seeded permission: {}", permission.getName());
            }
        } catch (Exception e) {
            logger.error("Failed to seed permissions", e);
        }
    }

}
