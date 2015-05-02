package com.vlls.jpa.repository;

import com.vlls.jpa.domain.LikedPicture;
import com.vlls.jpa.domain.Picture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by HoangPhi on 10/13/2014.
 */
public interface LikedPictureRepository extends JpaRepository<LikedPicture, Integer> {
    LikedPicture findOneByPictureIdAndUserId (Integer pictureId, Integer userId);
    Page<LikedPicture> findByUserId (Integer userId, Pageable pageable);

    @Query("select count(p.picture.id) from LikedPicture p where p.picture.id = :id")
    Integer getLikeCount(@Param(value = "id") Integer pictureId);

    @Query("select p from LikedPicture p where p.picture = ?1")
    List<LikedPicture> findByPictureId(Picture picture);
}
