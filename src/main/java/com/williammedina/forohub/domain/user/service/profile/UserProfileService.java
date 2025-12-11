package com.williammedina.forohub.domain.user.service.profile;

import com.williammedina.forohub.domain.user.dto.UpdateCurrentUserPasswordDTO;
import com.williammedina.forohub.domain.user.dto.UpdateUsernameDTO;
import com.williammedina.forohub.domain.user.dto.UserDTO;

public interface UserProfileService {

    UserDTO updateCurrentUserPassword(UpdateCurrentUserPasswordDTO request);
    UserDTO updateUsername(UpdateUsernameDTO request);
    UserDTO getCurrentUser();

}
