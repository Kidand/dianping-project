package com.kidand.dianping.service.impl;

import com.kidand.dianping.common.BusinessException;
import com.kidand.dianping.common.EmBusinessError;
import com.kidand.dianping.dal.CategoryModelMapper;
import com.kidand.dianping.model.CategoryModel;
import com.kidand.dianping.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryModelMapper categoryModelMapper;

    @Override
    @Transactional
    public CategoryModel create(CategoryModel categoryModel) throws BusinessException {
        categoryModel.setCreatedAt(new Date());
        categoryModel.setUpdatedAt(new Date());

        try {
            categoryModelMapper.insertSelective(categoryModel);
        } catch (DuplicateKeyException ex) {
            throw new BusinessException(EmBusinessError.CATEGORY_NAME_DUPLICATED);
        }

        return get(categoryModel.getId());
    }

    @Override
    public CategoryModel get(Integer id) {
        return categoryModelMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<CategoryModel> selectAll() {
        return categoryModelMapper.selectAll();
    }
}
