package com.badsector.qerb.modules.user.domain.port.in;

import com.badsector.qerb.modules.user.domain.model.User;
import com.badsector.qerb.modules.user.domain.port.in.result.AuthResult;

public interface UserUseCase {
    AuthResult register(User user);
    AuthResult login(User user);
}
