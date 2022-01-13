package com.kidand.dianping.recommend;

import com.kidand.dianping.dal.RecommendDOMapper;
import com.kidand.dianping.model.RecommendDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * ██╗  ██╗██╗██████╗  █████╗ ███╗   ██╗██████╗
 * ██║ ██╔╝██║██╔══██╗██╔══██╗████╗  ██║██╔══██╗
 * █████╔╝ ██║██║  ██║███████║██╔██╗ ██║██║  ██║
 * ██╔═██╗ ██║██║  ██║██╔══██║██║╚██╗██║██║  ██║
 * ██║  ██╗██║██████╔╝██║  ██║██║ ╚████║██████╔╝
 * ╚═╝  ╚═╝╚═╝╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═══╝╚═════╝
 *
 * @description:RecommendService
 * @author: Kidand
 * @date: 2021/1/13 9:06 PM
 * Copyright © 2021 by Kidand
 */
@Service
public class RecommendService implements Serializable {

    @Autowired
    private RecommendDOMapper recommendDOMapper;

    /**
     * 根据 userId 召回 shopIdList
     * @param userId
     * @return
     */
    public List<Integer> recall(Integer userId){
        RecommendDO recommendDO = recommendDOMapper.selectByPrimaryKey(userId);
        if (recommendDO == null){
            // 设置默认初始化推荐
            recommendDO = recommendDOMapper.selectByPrimaryKey(0);
        }
        String[] shopIdArr = recommendDO.getRecommend().split(",");
        ArrayList<Integer> shopIdList = new ArrayList<>();
        for (int i = 0; i < shopIdArr.length; i++) {
            shopIdList.add(Integer.valueOf(shopIdArr[i]));
        }
        return shopIdList;
    }
}
