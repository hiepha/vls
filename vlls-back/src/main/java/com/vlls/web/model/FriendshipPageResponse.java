package com.vlls.web.model;

import com.vlls.jpa.domain.Friendship;

/**
 * Created by Hoang Phi on 10/18/2014.
 */
public class FriendshipPageResponse extends AbstractDataPageResponse<Friendship, FriendshipResponse> {
    @Override
    public FriendshipResponse instantiateResponse() {
        return new FriendshipResponse();
    }
}
