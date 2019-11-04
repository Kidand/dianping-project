package com.kidand.dianping.service;

import com.kidand.dianping.common.BusinessException;
import com.kidand.dianping.model.ShopModel;

import java.math.BigDecimal;
import java.util.List;

public interface ShopService {
    ShopModel create(ShopModel shopModel) throws BusinessException;

    ShopModel get(Integer id);

    List<ShopModel> selectAll();

    List<ShopModel> recommend(BigDecimal longitude, BigDecimal latitude);

    Integer countAllShop();

    List<ShopModel> search(BigDecimal longitude, BigDecimal latitude, String keyword, Integer orderby, Integer categoryId);
}
