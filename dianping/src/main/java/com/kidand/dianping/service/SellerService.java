package com.kidand.dianping.service;

import com.kidand.dianping.common.BusinessException;
import com.kidand.dianping.model.SellerModel;

import java.util.List;

public interface SellerService {

    SellerModel create(SellerModel sellerModel);

    SellerModel get(Integer id);

    List<SellerModel> selectAll();

    SellerModel changestatus(Integer id, Integer disabledFlag) throws BusinessException;
}
