package ro.itschool.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ro.itschool.controller.model.AuthenticationRequest;
import ro.itschool.controller.model.AuthenticationResponse;
import ro.itschool.controller.model.RegisterRequest;
import ro.itschool.entity.MyRole;
import ro.itschool.entity.MyUser;
import ro.itschool.entity.RoleName;
import ro.itschool.repository.RoleRepository;
import ro.itschool.repository.UserRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;

    public AuthenticationResponse register(RegisterRequest request) {

        MyRole userRole = roleRepository.findByName(RoleName.ROLE_USER);
        var user = new MyUser();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of(userRole));
        user.setFullName(request.getFullName());
        repository.save(user);

        var jwtToken = jwtService.generateToken(user);

        return new AuthenticationResponse(jwtToken);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        );
        authenticationManager.authenticate(authentication);
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return new AuthenticationResponse(jwtToken);
    }
}
