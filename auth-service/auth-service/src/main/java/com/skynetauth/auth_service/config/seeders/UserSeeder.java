package com.skynetauth.auth_service.config.seeders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.skynetauth.auth_service.Enum.UserType;
import com.skynetauth.auth_service.models.Distribution;
import com.skynetauth.auth_service.models.Permission;
import com.skynetauth.auth_service.models.Role;
import com.skynetauth.auth_service.models.ShipTo;
import com.skynetauth.auth_service.models.SoldTo;
import com.skynetauth.auth_service.models.User;
import com.skynetauth.auth_service.repositories.DistributionRepository;
import com.skynetauth.auth_service.repositories.PermissionRepository;
import com.skynetauth.auth_service.repositories.RoleRepository;
import com.skynetauth.auth_service.repositories.SoldToRepository;
import com.skynetauth.auth_service.repositories.UserRepository;

@Component
public class UserSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final DistributionRepository distributionRepository;
    private final SoldToRepository soldToRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserSeeder.class);

    @Autowired
    public UserSeeder(UserRepository userRepository, RoleRepository roleRepository,
            PermissionRepository permissionRepository, DistributionRepository distributionRepository,
            PasswordEncoder passwordEncoder, SoldToRepository soldToRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.distributionRepository = distributionRepository;
        this.passwordEncoder = passwordEncoder;
        this.soldToRepository = soldToRepository;
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
            if (seeder.contains("distribution")) {
                distributionSeeder();
            }
            if (seeder.contains("soldto")) {
                soldToSeeder();
            }
            System.exit(0);
        } else {
            logger.info("No seeders ran!");
        }
    }

    private List<User> userSeeder() {
        try {
            Role superAdmin = roleRepository.findByName("SUPERADMIN").orElse(new Role());
            Role admin = roleRepository.findByName("ADMIN").orElse(new Role());
            Role userRole = roleRepository.findByName("USER").orElse(new Role());

            if (superAdmin.getName() == null || admin.getName() == null || userRole.getName() == null) {
                List<Role> roles = roleSeeder();
                if (superAdmin.getName() == null) {
                    superAdmin = roles.get(0);
                }
                if (admin.getName() == null) {
                    admin = roles.get(1);
                }
                if (userRole.getName() == null) {
                    userRole = roles.get(2);
                }
            }
            
            User user1 = userRepository.findByEmailIgnoreCase("superadmin@example.com").orElse(new User());
            user1.setFirstName("Super");
            user1.setLastName("Admin");
            user1.setEmail("superadmin@example.com");
            user1.setPassword(passwordEncoder.encode("securepassword"));
            user1.setRoles(Arrays.asList(superAdmin));
            user1.setPermissions(new ArrayList<>(superAdmin.getPermissions()));
            user1.setUserType(UserType.ADMIN);
            user1 = userRepository.save(user1);
            

            User user2 = userRepository.findByEmailIgnoreCase("admin@example.com").orElse(new User());
            user2.setFirstName("Admin");
            user2.setLastName("User");
            user2.setEmail("admin@example.com");
            user2.setPassword(passwordEncoder.encode("securepassword"));
            user2.setRoles(Arrays.asList(admin));
            user2.setPermissions(new ArrayList<>(admin.getPermissions()));
            user2.setUserType(UserType.ADMIN);
            user2 = userRepository.save(user2);
            

            User user3 = userRepository.findByEmailIgnoreCase("user@example.com").orElse(new User());
            user3.setFirstName("Normal");
            user3.setLastName("User");
            user3.setEmail("user@example.com");
            user3.setPassword(passwordEncoder.encode("securepassword"));
            user3.setRoles(Arrays.asList(userRole));
            user3.setPermissions(new ArrayList<>(userRole.getPermissions()));
            user3.setUserType(UserType.USER);
            user3 = userRepository.save(user3);
            

            logger.info("Seeded users: superadmin, admin, user");
            return Arrays.asList(user1, user2, user3);
        } catch (Exception e) {
            logger.error("Failed to seed users", e);
            return new ArrayList<>();
        }
    }

    private List<Role> roleSeeder() {
        try {
            Permission read = permissionRepository.findByName("READ").orElse(new Permission());
            Permission write = permissionRepository.findByName("WRITE").orElse(new Permission());
            Permission createUsers = permissionRepository.findByName("CAN_CREATE_USERS").orElse(new Permission());
            
            if (read.getName() == null || write.getName() == null || createUsers.getName() == null) {
                List<Permission> permissions = permissionSeeder();
                read = permissions.get(0);
                write = permissions.get(1);
                createUsers = permissions.get(2);
            }

            Role superAdmin = roleRepository.findByName("SUPERADMIN").orElse(new Role());
            superAdmin.setName("SUPERADMIN");
            superAdmin.setPermissions(Arrays.asList(read, write, createUsers));
            superAdmin.setUserType(UserType.ADMIN);
            superAdmin = roleRepository.save(superAdmin);

            Role admin = roleRepository.findByName("ADMIN").orElse(new Role());
            admin.setName("ADMIN");
            admin.setPermissions(Arrays.asList(read, write));
            admin.setUserType(UserType.ADMIN);
            admin = roleRepository.save(admin);
            
            Role user = roleRepository.findByName("USER").orElse(new Role());
            user.setName("USER");
            user.setPermissions(Arrays.asList(read));
            user.setUserType(UserType.USER);
            user = roleRepository.save(user);
            

            logger.info("Seeded roles: SUPERADMIN, ADMIN, USER");
            return Arrays.asList(superAdmin, admin, user);
        } catch (Exception e) {
            logger.error("Failed to seed roles", e);
            return new ArrayList<>();
        }
    }

    private List<Permission> permissionSeeder() {
        try {
            Permission read = permissionRepository.findByName("READ").orElse(new Permission("READ", UserType.USER));
            Permission write = permissionRepository.findByName("WRITE").orElse(new Permission("WRITE", UserType.ADMIN));
            Permission canCreateUsers = permissionRepository.findByName("CAN_CREATE_USERS").orElse(new Permission("CAN_CREATE_USERS", UserType.ADMIN));

            logger.info("Seeded permissions.");
            return permissionRepository.saveAll(Arrays.asList(read, write, canCreateUsers));

        } catch (Exception e) {
            logger.error("Failed to seed permissions", e);
        }
        return new ArrayList<>();
    }

    private List<Distribution> distributionSeeder() {
        try {
            // Fetch existing users to assign to distributions
            User user1 = userRepository.findByEmailIgnoreCase("superadmin@example.com").orElse(new User());
            User user2 = userRepository.findByEmailIgnoreCase("admin@example.com").orElse(new User());
            User user3 = userRepository.findByEmailIgnoreCase("user@example.com").orElse(new User());

            if (user1.getEmail() == null || user1.getEmail() == null) {
                List<User> users = userSeeder();
                user1 = users.get(0);
                user2 = users.get(1);
                user3 = users.get(2);
            }

            Distribution dist1 = distributionRepository.findByName("Distribution A").orElse(new Distribution());
            dist1.setName("Distribution A");
            dist1.setUsers(new HashSet<>(Arrays.asList(user1, user2)));
            user1.setDistributions(Arrays.asList(dist1));

            Distribution dist2 = distributionRepository.findByName("Distribution B").orElse(new Distribution());
            dist2.setName("Distribution B");
            dist2.setUsers(new HashSet<>(Collections.singletonList(user2)));
            user2.setDistributions(Arrays.asList(dist1, dist2));

            Distribution dist3 = distributionRepository.findByName("Distribution C").orElse(new Distribution());
            dist3.setName("Distribution C");
            dist3.setUsers(new HashSet<>(Collections.singletonList(user3)));
            user3.setDistributions(Arrays.asList(dist3));

            dist1 = distributionRepository.save(dist1);
            dist2 = distributionRepository.save(dist2);
            dist3 = distributionRepository.save(dist3);

            userRepository.save(user1);
            userRepository.save(user2);
            userRepository.save(user3);

            logger.info("Seeded distributions: Distribution A, Distribution B, and Distribution C");

            return Arrays.asList(dist1, dist2, dist3);
        } catch (Exception e) {
            logger.error("Failed to seed distributions", e);
            return new ArrayList<>();
        }
    }

    private void soldToSeeder() {
        Distribution distributionA = distributionRepository.findByName("Distribution A").orElse(new Distribution());
        Distribution distributionB = distributionRepository.findByName("Distribution B").orElse(new Distribution());

        if (distributionA.getName() == null || distributionB.getName() == null) {
            List<Distribution> distributions = distributionSeeder();
            distributionA = distributions.get(0);
            distributionB = distributions.get(1);
        }
        
        SoldTo AsoldTo = soldToRepository.findByName("Sold To A").orElse(new SoldTo());
        AsoldTo.setName("Sold To A");
        AsoldTo.setDistributions(distributionA);
        
        SoldTo BsoldTo = new SoldTo();
        BsoldTo.setName("Sold To B");
        BsoldTo.setDistributions(distributionB);
        
        List<ShipTo> AshipTos;
        List<ShipTo> BshipTos;

        if (AsoldTo.getShipTos().isEmpty()) {
            AshipTos = Arrays.asList(new ShipTo("Ship To 1", AsoldTo), new ShipTo("Ship To 2", AsoldTo), new ShipTo("Ship To 3", AsoldTo));
        } else {
            AshipTos = AsoldTo.getShipTos();
        }

        if (BsoldTo.getShipTos().isEmpty()) {
            BshipTos = Arrays.asList(new ShipTo("Ship To 4", BsoldTo), new ShipTo("Ship To 5", BsoldTo), new ShipTo("Ship To 6", BsoldTo));
        } else {
            BshipTos = AsoldTo.getShipTos();
        }

        for (ShipTo sh : AshipTos) {
            sh.setSoldTos(AsoldTo);
        }
        AsoldTo.setShipTos(AshipTos);
        distributionA.setSoldTos(Arrays.asList(AsoldTo));

        for (ShipTo sh : BshipTos) {
            sh.setSoldTos(BsoldTo);
        }
        BsoldTo.setShipTos(BshipTos);
        distributionB.setSoldTos(Arrays.asList(BsoldTo));

        distributionRepository.save(distributionA);
        soldToRepository.save(AsoldTo);

        distributionRepository.save(distributionB);
        soldToRepository.save(BsoldTo);
    }

}
