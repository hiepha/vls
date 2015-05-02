package com.vlls.web.model;

import com.vlls.jpa.domain.User;

/**
 * Created by Hoang Phi on 10/25/2014.
 */
public class UserInfoPageResponse extends AbstractDataPageResponse<User, UserInfoResponse> {
    @Override
    public UserInfoResponse instantiateResponse() {
        return new UserInfoResponse();
    }
}
