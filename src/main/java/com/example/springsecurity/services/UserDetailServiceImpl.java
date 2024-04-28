package com.example.springsecurity.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.springsecurity.controllers.dto.AuthLoginRequest;
import com.example.springsecurity.controllers.dto.AuthRegisterRequest;
import com.example.springsecurity.controllers.dto.AuthResponse;
import com.example.springsecurity.models.RoleEntity;
import com.example.springsecurity.models.UserEntity;
import com.example.springsecurity.repositories.RoleRepository;
import com.example.springsecurity.repositories.UserRepository;
import com.example.springsecurity.utilities.JwtUlils;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUlils jwtUlils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        userEntity.getRoles()
                .forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName().name())));

        userEntity.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.getName())));


        return new User(
            userEntity.getUsername(),
            userEntity.getPassword(),
            userEntity.isActive(),
            !userEntity.isAccountExpired(),
            !userEntity.isCredentialsExpired(),
            !userEntity.isLocked(),
            authorities);
    }

    public AuthResponse register(AuthRegisterRequest authRegisterRequest) {
        String username = authRegisterRequest.getUsername();
        String password = authRegisterRequest.getPassword();
        List<String> roleList = authRegisterRequest.getRoles().getRoleList();

        Set<RoleEntity> roleEntitiesSet = roleRepository.findRoleEntitiesByRoleEnumIn(roleList).stream().collect(Collectors.toSet());

        if (roleEntitiesSet.isEmpty()) {
            throw new IllegalArgumentException("Invalid roles");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setPassword(passwordEncoder.encode(password));
        userEntity.setRoles(roleEntitiesSet);
        userEntity.setActive(true);
        userEntity.setAccountExpired(false);
        userEntity.setCredentialsExpired(false);
        userEntity.setLocked(false);
        UserEntity userCreated = userRepository.save(userEntity);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        userCreated.getRoles()
                .forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName().name())));

        userCreated.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.getName())));

        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userCreated.getUsername(),
                userCreated.getPassword(), authorities);
        context.setAuthentication(authentication);

        String token = jwtUlils.createToken(authentication);
        AuthResponse authResponse = new AuthResponse();
        authResponse.setUsername(username);
        authResponse.setJwt(token);
        authResponse.setSuccess(true);
        authResponse.setMessage("Registration successful");

        return null;
    }

    public AuthResponse login(AuthLoginRequest authLoginRequest) {
                String username = authLoginRequest.getUsername();
        String password = authLoginRequest.getPassword();

        Authentication authentication = authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtUlils.createToken(authentication);
        AuthResponse authResponse = new AuthResponse();
        authResponse.setUsername(username);
        authResponse.setJwt(token);
        authResponse.setSuccess(true);
        authResponse.setMessage("Login successful");

        return authResponse;
    }

    private Authentication authenticate(String username, String password) {
        UserDetails userDetails = loadUserByUsername(username);

        if (userDetails == null) {
            throw new UsernameNotFoundException("Invalid user or password");
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new UsernameNotFoundException("Invalid user or password");
        }

        return new UsernamePasswordAuthenticationToken(username, userDetails.getPassword(), userDetails.getAuthorities());
    }
    
}
