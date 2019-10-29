package com.kidand.dianping.service.impl;

import com.kidand.dianping.dal.UserModelMapper;
import com.kidand.dianping.model.UserModel;
import com.kidand.dianping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserModelMapper userModelMapper;

    @Override
    public UserModel getUser(Integer id) {
        return userModelMapper.selectByPrimaryKey(id);
    }
}
