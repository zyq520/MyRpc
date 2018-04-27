package com.myrpc.service.impl;

import com.myrpc.service.IDiffService;

/**
 * @author zhuangyq
 * @create 2018-04-27 上午 11:06
 **/
public class DiffServiceImpl implements IDiffService {
    @Override
    public double diff(double a, double b) {
        return a-b;
    }
}
