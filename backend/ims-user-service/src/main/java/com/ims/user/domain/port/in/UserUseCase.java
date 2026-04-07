package com.ims.user.domain.port.in;

import com.ims.common.dto.PageResponse;
import com.ims.user.application.command.*;
import com.ims.user.application.query.*;
import com.ims.user.domain.model.User;

public interface UserUseCase {

    User createUser(CreateUserCommand command);

    User updateUser(UpdateUserCommand command);

    void deactivateUser(DeactivateUserCommand command);

    void changePassword(ChangePasswordCommand command);

    User assignRole(AssignRoleCommand command);

    User getUserById(GetUserByIdQuery query);

    PageResponse<User> listUsers(ListUsersQuery query);

    String authenticate(AuthenticateUserQuery query);
}
