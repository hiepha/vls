package com.vlls.jpa.repository;

import com.vlls.jpa.domain.Role;
import com.vlls.jpa.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by hiephn on 2014/06/28.
 */
public interface RoleRepository extends JpaRepository<Role, Integer> {

    Role findOneByNameIgnoreCase(String name);

    @Query("select r from Role r where UPPER(r.name) = UPPER('mem')")
    Role findMemRole();
}
