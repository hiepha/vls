package com.vlls.web.model;

import com.vlls.jpa.domain.User;

/**
 * Created by hiephn on 2014/09/24.
 */
public class UserPageResponse extends AbstractDataPageResponse<User, UserResponse> {
    @Override
    public UserResponse instantiateResponse() {
        return new UserResponse();
    }
}
