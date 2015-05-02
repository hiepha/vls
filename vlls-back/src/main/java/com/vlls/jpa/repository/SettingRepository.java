package com.vlls.jpa.repository;

import com.vlls.jpa.domain.Setting;
import com.vlls.jpa.domain.SettingKey;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by HoangPhi on 10/11/2014.
 */
public interface SettingRepository extends JpaRepository<Setting, SettingKey> {
}
