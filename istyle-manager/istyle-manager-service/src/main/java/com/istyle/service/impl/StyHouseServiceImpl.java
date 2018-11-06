package com.istyle.service.impl;

import com.exception.AppAuthException;
import com.exception.AppUnknownException;
import com.istyle.mapper.TbStyHouseMapper;
import com.istyle.pojo.TbStyHouse;
import com.istyle.service.StyHouseService;
import com.util.JWT;
import com.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 造型屋实现接口
 * @author 黄文伟
 */
@Service
public class StyHouseServiceImpl implements StyHouseService {
    @Autowired
    private TbStyHouseMapper styHouseMapper;

    /**
     * 造型屋注册
     * @param StyHouse 造型屋数据，包括名字、账号、密码、负责人姓名、负责人身份证、负责人电话、地理位置
     */
    @Override
    public void styHouseRegister(TbStyHouse StyHouse) {
        if (StringUtil.isEmpty(StyHouse.getStyHouseName())) {
            throw new AppAuthException("造型屋名为空");
        }
        if (StringUtil.isEmpty(StyHouse.getStyHouseAccount())) {
            throw new AppAuthException("造型屋账号为空");
        }
        if (StringUtil.isEmpty(StyHouse.getStyHousePassword())) {
            throw new AppAuthException("造型屋密码为空");
        }
        if (StringUtil.isEmpty(StyHouse.getHeadName())) {
            throw new AppAuthException("造型屋负责人姓名为空");
        }
        if (StringUtil.isEmpty(StyHouse.getHeadId())) {
            throw new AppAuthException("造型屋负责人身份证为空");
        }
        if (StringUtil.isEmpty(StyHouse.getHeadPhone())) {
            throw new AppAuthException("造型屋负责人号码为空");
        }
        if (StringUtil.isEmpty(StyHouse.getStyHousePosition())) {
            throw new AppAuthException("造型屋位置为空");
        }
        if (styHouseMapper.isStyHouseAccount(StyHouse.getStyHouseAccount()) != 0) {
            throw new AppAuthException("该账号已注册");
        }

        styHouseMapper.addStyHouse(StyHouse);

        if (styHouseMapper.isAccountAndPassword(StyHouse) == 0) {
            throw new AppUnknownException("造型屋注册失败");
        }
    }

    @Override
    public Map styHouseLogin(TbStyHouse styHouse) {
        Map<String, String> stokenMap = null;

        if (StringUtil.isEmpty(styHouse.getStyHouseAccount())) {
            throw new AppAuthException("账号为空");
        }
        if (StringUtil.isEmpty(styHouse.getStyHousePassword())) {
            throw new AppAuthException("密码为空");
        }
        if (styHouseMapper.isAccountAndPassword(styHouse) != 1) {
            throw new AppAuthException("账号密码错误，登录失败");
        }
        if (styHouseMapper.isStyHouseAccount(styHouse.getStyHouseAccount()) != 1) {
            throw new AppAuthException("号码错误");
        }

        TbStyHouse tbStyHouse = styHouseMapper.selectStyHouseByAccountAndPassword(styHouse);
        if (tbStyHouse == null) {
            throw new AppUnknownException("登录错误");
        }

        String stoken = JWT.sign(tbStyHouse, 24L * 3600L * 30L);

        if (StringUtil.isNotEmpty(stoken)) {
            stokenMap = new HashMap<>(16);
            stokenMap.put("stoken", stoken);

            return stokenMap;
        } else {
            throw new AppUnknownException("stoken获取失败，造型屋登录错误");
        }
    }
}
