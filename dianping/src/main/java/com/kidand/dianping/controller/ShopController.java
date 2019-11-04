package com.kidand.dianping.controller;

import com.kidand.dianping.common.BusinessException;
import com.kidand.dianping.common.CommonRes;
import com.kidand.dianping.common.EmBusinessError;
import com.kidand.dianping.model.ShopModel;
import com.kidand.dianping.service.CategoryService;
import com.kidand.dianping.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.List;

@Controller("/shop")
@RequestMapping("shop")
public class ShopController {

    @Autowired
    private ShopService shopService;

    @Autowired
    private CategoryService categoryService;

    //推荐服务V1.0
    @RequestMapping("/recommend")
    @ResponseBody
    public CommonRes recommend(@RequestParam(name="longitude")BigDecimal longitude,
                               @RequestParam(name = "latitude")BigDecimal latitude) throws BusinessException {
        if (longitude == null || latitude == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        List<ShopModel> shopModelist = shopService.recommend(longitude,latitude);
        return CommonRes.create(shopModelist);
    }

}
