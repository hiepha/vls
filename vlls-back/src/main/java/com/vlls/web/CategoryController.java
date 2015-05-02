package com.vlls.web;

import com.vlls.exception.*;
import com.vlls.exception.UnsupportedOperationException;
import com.vlls.service.CategoryService;
import com.vlls.web.model.CategoryPageResponse;
import com.vlls.web.model.CategoryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/category")
public class CategoryController implements ControllerConstant {

    @Autowired
    private CategoryService categoryService;
    /**
     * Only admin can create Category.
     *
     * @param name name
     * @param description description
     * @return create category
     */
    @RequestMapping(method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CategoryResponse create(
            // MANDATORY
            @RequestParam("name") String name,
            // OPTIONAL
            @RequestParam(value = "description", required = false, defaultValue = EMPTY) String description)
            throws NoInstanceException, InvalidRequestItemException, ServerTechnicalException,
            UnauthorizedException, UnauthenticatedException {
        CategoryResponse categoryResponse = categoryService.create(name, description);
        return categoryResponse;
    }

    /**
     * Only admin can update Category.
     *
     * @param Id id
     * @param name name
     * @param description description
     * @return updated category
     */
    @RequestMapping(method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CategoryResponse update(
            @RequestParam("Id") Integer Id,
            @RequestParam("name") String name,
            @RequestParam(value = "description",required = false) String description)
            throws NoInstanceException, InvalidRequestItemException, UnauthorizedException,
            ServerTechnicalException, UnauthenticatedException {
        CategoryResponse categoryResponse = categoryService.update(Id, name, description);
        return categoryResponse;
    }

    /**
     * Public API.
     *
     * 1/ If id is present. Return page with only 1 category
     *
     * 2/ If only name is present. Return page of categories that have name in equal.
     *
     * 3/ If none of parameter is present. Return page of all categories.
     *
     * @param Id id
     * @param name name
     * @return categories
     */
    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CategoryPageResponse get(
            @RequestParam(value = "Id", required = false, defaultValue = NEGATIVE) Integer Id,
            @RequestParam(value = "name", required = false, defaultValue = EMPTY) String name) {

        CategoryPageResponse categoryPageResponse = categoryService.get(Id, name);
        return categoryPageResponse;
    }

    /**
     * Only admin can delete category
     *
     * @param Id id
     * @return operation status
     */
    @RequestMapping(method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String del(@RequestParam("Id") Integer Id) throws
            UnsupportedOperationException, UnauthorizedException, ServerTechnicalException, UnauthenticatedException {
        categoryService.delete(Id);
        return OK_STATUS;
    }
}
