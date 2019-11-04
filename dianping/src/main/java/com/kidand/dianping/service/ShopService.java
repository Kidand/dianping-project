package com.kidand.dianping.service;

import com.kidand.dianping.common.BusinessException;
import com.kidand.dianping.model.ShopModel;

import java.util.List;

public interface ShopService {
    ShopModel create(ShopModel shopModel) throws BusinessException;

    ShopModel get(Integer id);

    List<ShopModel> selectAll();
}
