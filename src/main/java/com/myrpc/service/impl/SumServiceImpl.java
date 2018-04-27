package com.myrpc.service.impl;

import com.myrpc.service.ISumService;

/**
 * @author zhuangyq
 * @create 2018-04-27 上午 11:07
 **/
public class SumServiceImpl implements ISumService{
    @Override
    public Integer sum(Integer a, Integer b) {
        return a+b;
    }
}
