package com.williammedina.forohub.domain.user.service.profile;

import com.williammedina.forohub.domain.user.dto.UpdateCurrentUserPasswordDTO;
import com.williammedina.forohub.domain.user.dto.UpdateUsernameDTO;
import com.williammedina.forohub.domain.user.dto.UserDTO;
import com.williammedina.forohub.domain.user.entity.UserEntity;
import com.williammedina.forohub.domain.user.repository.UserRepository;
import com.williammedina.forohub.domain.user.service.context.AuthenticatedUserProvider;
import com.williammedina.forohub.domain.user.service.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService{

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final UserValidator validator;

    @Override
    @Transactional
    public UserDTO updateCurrentUserPassword(UpdateCurrentUserPasswordDTO request) {
        validator.validatePasswordsMatch(request.password(), request.password_confirmation());

        UserEntity currentUser = authenticatedUserProvider.getAuthenticatedUser();
        validator.validateCurrentPassword(currentUser, request.current_password());

        currentUser.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(currentUser);
        log.info("Password successfully changed for user ID: {}", currentUser.getId());

        return UserDTO.fromEntity(currentUser);
    }

    @Override
    @Transactional
    public UserDTO updateUsername(UpdateUsernameDTO request) {
        UserEntity currentUser = authenticatedUserProvider.getAuthenticatedUser();

        validator.ensureNewUsername(currentUser, request.username());
        validator.validateUsernameContent(request.username()); // Validate the new username with AI
        validator.existsByUsername(request.username());

        currentUser.setUsername(request.username());
        userRepository.save(currentUser);
        log.info("Username updated to '{}' for user ID: {}", request.username(), currentUser.getId());

        return new UserDTO(currentUser.getId(), currentUser.getUsername(), currentUser.getProfile().getName());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getCurrentUser() {
        UserEntity currentUser = authenticatedUserProvider.getAuthenticatedUser();
        log.debug("Fetching current user info - ID: {}", currentUser.getId());
        return UserDTO.fromEntity(currentUser);
    }
}
