package com.vlls.service;

import com.vlls.exception.NoInstanceException;
import com.vlls.exception.ServerTechnicalException;
import com.vlls.exception.UnauthenticatedException;
import com.vlls.jpa.domain.Setting;
import com.vlls.jpa.domain.SettingKey;
import com.vlls.jpa.domain.User;
import com.vlls.jpa.repository.SettingRepository;
import com.vlls.service.model.UserSessionInfo;
import com.vlls.web.model.SettingPageResponse;
import com.vlls.web.model.SettingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by HoangPhi on 10/11/2014.
 */
@Service
public class SettingService extends AbstractService{
    @Autowired
    private SettingRepository settingRepo;

    @Autowired
    private  SecurityService securityService;

    public SettingPageResponse get() throws UnauthenticatedException, ServerTechnicalException {
        User user = securityService.retrieveCurrentUser0();

        List<Setting> settings = user.getSettings();
        SettingPageResponse settingPageResponse = new SettingPageResponse();
        settingPageResponse.from(settings);

        return settingPageResponse;
    }

    @Transactional
    public SettingResponse create(String name, String value) throws UnauthenticatedException, ServerTechnicalException {
        User user = securityService.retrieveCurrentUser0();

        Setting setting = new Setting();
        setting.setName(name);
        setting.setValue(value);
        setting.setUserId(user.getId());
        setting.setUser(user);
        settingRepo.save(setting);

        //Generate response
        SettingResponse settingResponse = new SettingResponse();
        settingResponse.setData(setting);
        return settingResponse;
    }

    @Transactional
    public SettingResponse update(String name, String value) throws
            UnauthenticatedException, ServerTechnicalException, NoInstanceException {
        UserSessionInfo user = securityService.retrieveCurrentUserSession();
        Setting setting = get0(name, user.getId());

        setting.setValue(value);

        SettingResponse settingResponse = new SettingResponse();
        settingResponse.setData(setting);
        return settingResponse;
    }

    @Transactional
    public void delete(String name) throws UnauthenticatedException, ServerTechnicalException {
        UserSessionInfo user = securityService.retrieveCurrentUserSession();
        SettingKey settingKey = new SettingKey();
        settingKey.setName(name);
        settingKey.setUserId(user.getId());
        Setting setting = settingRepo.findOne(settingKey);
        settingRepo.delete(setting);
    }

    Setting get0(String name, Integer userId) throws NoInstanceException {
        SettingKey settingKey = new SettingKey();
        settingKey.setName(name);
        settingKey.setUserId(userId);
        Setting setting = settingRepo.getOne(settingKey);
        if (setting == null) {
            throw new NoInstanceException("Setting", name);
        } else {
            return setting;
        }
    }

}
