package com.projeto.projeto_final.spring.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.projeto.projeto_final.spring.role.RoleRepository;
import com.projeto.projeto_final.spring.role.Role;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void saveUser(LoginRegister loginRegister) {
        User user = new User();

        user.setName(loginRegister.getName());
        user.setUsername(loginRegister.getUsername());

        //encrypt the password once we integrate spring security
        user.setPassword(passwordEncoder.encode(loginRegister.getPassword()));

        Set<Role> roles= new HashSet<>();
        roles.add(roleRepository.findByName(loginRegister.getRole()));

        user.setRoles(roles);

        userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findById(int id) {
        return userRepository.findById(id);
    }

    public List<UserDTO> findAllUsers() {
        List<User> users = userRepository.findAllOrderByName();
        return users.stream().map(this::convertEntityToDto)
                .collect(Collectors.toList());
    }

    public List<String> getListAllUsernames () {
        List<User> users = userRepository.findAllOrderByName();
        List<String> listUsernames = new ArrayList<>();

        for (User user : users) {
            listUsernames.add(user.getUsername());
        }

        return listUsernames;
    }

    public void connect(UserDTO user) {
        User storedUser = userRepository.findByUsername(user.getUsername());
        if (storedUser != null) {
            storedUser.setStatus(Status.ONLINE);
            userRepository.save(storedUser);
        }
    }

    public void disconnect(UserDTO user) {
        User storedUser = userRepository.findByUsername(user.getUsername());
        if (storedUser != null) {
            storedUser.setStatus(Status.OFFLINE);
            userRepository.save(storedUser);
        }
    }

    public void setAllUsersOffline() {
        List<User> userList = userRepository.findAll();
        for (User user : userList) {
            user.setStatus(Status.OFFLINE);
        }

        userRepository.saveAll(userList);
    }

    public UserDTO convertEntityToDto(User user) {
        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setUsername(user.getUsername());
        userDTO.setStatus(user.getStatus());

        return userDTO;
    }
}