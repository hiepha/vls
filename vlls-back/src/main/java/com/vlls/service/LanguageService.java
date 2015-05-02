package com.vlls.service;

import com.vlls.exception.*;
import com.vlls.jpa.domain.Language;
import com.vlls.jpa.repository.LanguageRepository;
import com.vlls.web.model.LanguagePageResponse;
import com.vlls.web.model.LanguageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by hiephn on 2014/09/27.
 */
@Service
public class LanguageService extends AbstractService{

    @Autowired
    private LanguageRepository langRepo;

    @Autowired
    private SecurityService securityService;

    @Transactional
    public LanguageResponse create(String name, String description) throws DuplicatedItemException,
            UnauthorizedException, ServerTechnicalException, UnauthenticatedException {
        securityService.checkAdmin();
        // Check if language name is already exist
        Language language = langRepo.findOneByNameIgnoreCase(name);
        if (language != null) {
            throw new DuplicatedItemException("Language name already exists");
        } else {
            language = new Language();
            language.setName(name);
            language.setDescription(description);
            langRepo.save(language);

            // Generate response
            LanguageResponse languageResponse = new LanguageResponse();
            languageResponse.setData(language);
            return languageResponse;
        }
    }

    @Transactional
    public LanguageResponse update(Integer id, String name, String description) throws NoInstanceException,
            DuplicatedItemException, UnauthorizedException, ServerTechnicalException, UnauthenticatedException {
        securityService.checkAdmin();
        //Check duplicate language name
        Language language = langRepo.findOneByNameIgnoreCase(name);
        if (language != null) {
            throw new DuplicatedItemException("Language name already exist");
        } else {
            language = get0(id);
            language.setName(name);
            language.setDescription(description);

            // Generate response
            LanguageResponse languageResponse = new LanguageResponse();
            languageResponse.setData(language);

            return languageResponse;
        }
    }


    public LanguagePageResponse get(Integer id, String name) {
        name = this.wildcardSearchKey(name);
        if (id > 0) {
            Language language = langRepo.findOne(id);
            LanguagePageResponse languagePageResponse = new LanguagePageResponse();
            languagePageResponse.from(language);
            return languagePageResponse;
        } else {
            List<Language> languages = langRepo.findByNameLike(name);
            LanguagePageResponse languagePageResponse = new LanguagePageResponse();
            languagePageResponse.from(languages);
            return languagePageResponse;
        }
    }

    @Transactional
    public void delete(Integer id) throws UnauthorizedException, ServerTechnicalException, UnauthenticatedException {
        securityService.checkAdmin();
        langRepo.delete(id);
    }

    Language get0(Integer id) throws NoInstanceException {
        Language language = langRepo.getOne(id);
        if (language == null) {
            throw new NoInstanceException("Language", id);
        } else {
            return language;
        }
    }
}
