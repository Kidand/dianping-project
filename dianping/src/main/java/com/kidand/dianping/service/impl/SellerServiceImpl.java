package com.kidand.dianping.service.impl;

import com.kidand.dianping.dal.SellerModelMapper;
import com.kidand.dianping.model.SellerModel;
import com.kidand.dianping.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SellerServiceImpl implements SellerService {

    @Autowired
    private SellerModelMapper sellerModelMapper;

    @Override
    public SellerModel create(SellerModel sellerModel) {
        return null;
    }

    @Override
    public SellerModel get(Integer id) {
        return null;
    }

    @Override
    public List<SellerModel> selectAll() {
        return sellerModelMapper.selectAll();
    }

    @Override
    public SellerModel changestatus(Integer id, Integer disabledFlag) {
        return null;
    }
}
