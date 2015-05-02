package com.vlls.jpa.repository;

import com.vlls.jpa.domain.Picture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Created by hiephn on 2014/10/01.
 */
public interface PictureRepository extends JpaRepository<Picture, Integer> {

    @Query("select p from Picture p where p.uploader.name = :uploader")
    Page<Picture> findByUploader(@Param(value = "uploader") String username, Pageable pageable);

    @Query("select p from Picture p, LikedPicture lp where p.id = lp.picture.id and lp.user.name = :user")
    Page<Picture> findPicturesLikedByUser(@Param(value = "user") String username, Pageable pageable);

    @Query("select distinct p.source from Picture p where p.item.id = ?1")
    Page<String> getAllPictureSources(Integer itemId, Pageable pageable);

    @Query("select p, count(lp.pictureId) as c, lp.userId from LikedPicture lp right outer join lp.picture p where p.item.id = ?1 and p.id != ?2 group by p.id order by c desc")
    Page<Object[]> getAllPicturesByItemId(Integer itemId, Integer pictureId, Pageable pageable);

    @Query("select p, count(lp.pictureId) as num from LikedPicture lp, Picture p where lp.picture.id = p.id and p.uploader.name = ?1 group by p.id order by num desc")
    Page<Object> getPopularPicture(String username, Pageable pageable);

    @Query("select p from Picture p, LearningItem li where li.memPicture.id = p.id and li.id = ?1")
    Page<Picture> getSelectedPictureByLearningItemId(Integer learningItemId, Pageable pageable);
}
