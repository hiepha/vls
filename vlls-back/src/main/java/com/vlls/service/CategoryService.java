package com.vlls.service;

import com.vlls.exception.*;
import com.vlls.exception.UnsupportedOperationException;
import com.vlls.jpa.domain.Category;
import com.vlls.jpa.repository.CategoryRepository;
import com.vlls.web.model.CategoryPageResponse;
import com.vlls.web.model.CategoryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class CategoryService extends AbstractService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private CategoryRepository cateRepo;

    @Autowired
    private SecurityService securityService;

    @Transactional
    public CategoryResponse create(String name, String description) throws
            InvalidRequestItemException, NoInstanceException, UnauthorizedException, ServerTechnicalException,
            UnauthenticatedException {
        securityService.checkAdmin();
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        cateRepo.save(category);

        //Generating response
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setData(category);
        return categoryResponse;

    }

    @Transactional
    public CategoryResponse update(Integer Id, String name, String description) throws
            InvalidRequestItemException, NoInstanceException, UnauthorizedException, ServerTechnicalException,
            UnauthenticatedException {
        securityService.checkAdmin();
        Category category = cateRepo.findOne(Id);
        if (category == null) {
            throw new NoInstanceException("Category", Id);
        } else {
            category.setName(name);
            category.setDescription(description);
        }
        //Generating response
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setData(category);
        return categoryResponse;

    }

    public CategoryPageResponse get(Integer id, String name) {

        name = isEmpty(name) ? "%" : name;
        if (id > 0) {// case 1
            Category category = cateRepo.findOne(id);
            CategoryPageResponse CategoryPageResponse = new CategoryPageResponse();
            CategoryPageResponse.from(category);
            return CategoryPageResponse;
        } else {// case 2 3
            List<Category> categories = cateRepo.findByNameLike(name);
            CategoryPageResponse CategoryPageResponse = new CategoryPageResponse();
            CategoryPageResponse.from(categories);
            return CategoryPageResponse;
        }

    }

    @Transactional
    public boolean delete(Integer Id) throws UnsupportedOperationException, UnauthorizedException,
            ServerTechnicalException, UnauthenticatedException {
        securityService.checkAdmin();
        cateRepo.delete(Id);
        return true;

    }

    Category get0(Integer Id) throws NoInstanceException {
        Category category = cateRepo.findOne(Id);
        if (category == null) {
            throw new NoInstanceException("Category", Id);
        } else {
            return category;
        }
    }
}
