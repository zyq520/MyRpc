package com.myrpc.service.impl;

import com.myrpc.service.IStrService;

/**
 * @author zhuangyq
 * @create 2018-04-27 上午 11:07
 **/
public class StrServiceImpl implements IStrService{
    @Override
    public String getString(String a, String b) {
        return a+"-helloRpc-"+b;
    }
}
