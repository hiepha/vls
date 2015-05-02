package com.vlls.web.model;

import com.vlls.jpa.domain.CourseRatingRate;

/**
 * Created by HoangPhi on 11/8/2014.
 */
public class CourseRatingRatePageResponse extends AbstractDataPageResponse<CourseRatingRate, CourseRatingRateResponse>  {
    @Override
    public CourseRatingRateResponse instantiateResponse() {
        return new CourseRatingRateResponse();
    }
}
