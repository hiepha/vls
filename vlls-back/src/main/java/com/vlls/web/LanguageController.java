package com.vlls.web;

import com.vlls.exception.*;
import com.vlls.service.LanguageService;
import com.vlls.web.model.LanguagePageResponse;
import com.vlls.web.model.LanguageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/language")
public class LanguageController implements ControllerConstant {

    @Autowired
    private LanguageService languageService;

    /**
     * Only admin can create Language.
     *
     * @param name name
     * @param description description
     * @return create language
     */
    @RequestMapping(method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public LanguageResponse create(
            // MANDATORY
            @RequestParam("name") String name,
            // OPTIONAL
            @RequestParam(value = "description", required = false, defaultValue = EMPTY) String description)
            throws DuplicatedItemException, UnauthorizedException, ServerTechnicalException, UnauthenticatedException {

        LanguageResponse languageResponse = languageService.create(name, description);
        return languageResponse;
    }

    /**
     * Only admin can update Language.
     *
     * @param id id
     * @param name name
     * @param description description
     * @return updated language
     */
    @RequestMapping(method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public LanguageResponse update(
            @RequestParam("id") Integer id,
            @RequestParam("name") String name,
            @RequestParam("description") String description
    ) throws NoInstanceException, DuplicatedItemException, UnauthorizedException, ServerTechnicalException, UnauthenticatedException {
        LanguageResponse languageResponse = languageService.update(id, name, description);
        return languageResponse;
    }

    /**
     * Public API.
     *
     * 1/ If id is present. Return page with only 1 language
     *
     * 2/ If only name is present. Return page of languages that have name in equal.
     *
     * 3/ If none of parameter is present. Return page of all languages.
     *
     * @param id id
     * @param name name
     * @return languages
     */
    @RequestMapping(method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public LanguagePageResponse get(
            @RequestParam(value = "id", required = false, defaultValue = NEGATIVE) Integer id,
            @RequestParam(value = "name", required = false, defaultValue = EMPTY) String name) {
        LanguagePageResponse languagePageResponse = languageService.get(id, name);
        return languagePageResponse;
    }

    /**
     * Only admin can delete language
     *
     * @param id id
     * @return operation status
     */
    @RequestMapping(method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String del(@RequestParam("id") Integer id) {
        return OK_STATUS;
    }
}
