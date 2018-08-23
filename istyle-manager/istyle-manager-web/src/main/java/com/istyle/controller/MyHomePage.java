package com.istyle.controller;

import com.alibaba.druid.support.json.JSONUtils;
import com.istyle.pojo.TbEvaluation;
import com.istyle.pojo.TbStyHouse;
import com.istyle.pojo.TbStylist;
import com.istyle.pojo.TbUser;
import com.istyle.service.EvaluationService;
import com.istyle.service.StyHouseService;
import com.istyle.service.StylistService;
import com.istyle.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping("/myHome")
public class MyHomePage {
    @Autowired
    private UserService userService;
    @Autowired
    private StylistService stylistService;
    @Autowired
    private StyHouseService styHouseService;
    @Autowired
    private EvaluationService evaluationService;

//    打开编辑页面发送用户数据
    @ResponseBody
    @RequestMapping(value="/updateUserPage", method= RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    public String updatePage(HttpServletRequest request){
        Long userId;
        Map<String, String> users = new HashMap<>();
        String json;
        userId = (Long) request.getSession().getAttribute("userId");
        TbUser user = userService.selectUserById(userId);
        users.put("userPhoto", user.getUserPhoto());
        users.put("userName", user.getUserName());
        users.put("userWord", user.getUserWord());
        users.put("userSex", user.getUserSex());
        json = JSONUtils.toJSONString(users);

        return json;
    }

//    编辑信息
    @ResponseBody
    @RequestMapping("/updateMessage")
    public String updateUser(HttpServletRequest request){
        TbUser user = new TbUser();
        Map<String, String> map = new HashMap<>();
        String json;

        user.setUserId((Long) request.getSession().getAttribute("userId"));

        if (request.getSession().getAttribute("userId") != null) {
//            user.setUserPhoto(request.getParameter("userPhoto"));
            user.setUserName(request.getParameter("userName"));
            user.setUserWord(request.getParameter("userWord"));
            user.setUserSex(request.getParameter("userSex"));

            userService.updateUser(user);

            map.put("isOpen", "1");
            json = JSONUtils.toJSONString(map);
            return json;
        }
        else {
            map.put("isOpen", "0");
            System.out.println("test4");
            json = JSONUtils.toJSONString(map);
            return json;
        }
    }

//    我的主页跳转至我的信息
    @ResponseBody
    @RequestMapping(value="/index", method= RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    public String myHomePage(HttpServletRequest request){
        System.out.println("message page success");
        Long userId = (Long) request.getSession().getAttribute("userId");
        Map<String, String> users = new HashMap<String, String>();
        String json;
        if (userId != null){
            TbUser user = userService.selectUserById(userId);
            users.put("isOpen", "1");
            users.put("userPhoto", user.getUserPhoto());
            users.put("userName", user.getUserName());
            users.put("userWord", user.getUserWord());
            users.put("userSex", user.getUserSex());
            json = JSONUtils.toJSONString(users);
        }
        else {
            users.put("isOpen", "0");
            json = JSONUtils.toJSONString(users);
        }
        return json;
    }

//    我的收藏
    @ResponseBody
    @RequestMapping(value="/userCollection", method= RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    public String myCollection(HttpServletRequest request){
        System.out.println("连接成功");
        Long userId = (Long) request.getSession().getAttribute("userId");
        Map<String, List> map = new HashMap<>();
        String json;
        Long styCount; //造型师收藏数
        Long styHouseCount; //造型师收藏数
        Long evalCount; //测评数
        List<TbStylist> stylists; //造型师
        List<TbStyHouse> styHouses; //造型屋
        List<TbEvaluation> evaluations; //测评

        if (userId != null){
            styCount = stylistService.selectStylistCountByUserId(userId);
            stylists = stylistService.selectStylistByUserId(userId);

            styHouseCount = styHouseService.selectStyHouseCountByUserId(userId);
            styHouses = styHouseService.selectStyHouseByUserId(userId);

            evalCount = evaluationService.selectEvaluationCountByUserId(userId);
            evaluations = evaluationService.selectEvaluationByUserId(userId);

            map.put("styCount", Collections.singletonList(styCount));
            map.put("stylist", stylists);
            map.put("styHouseCount", Collections.singletonList(styHouseCount));
            map.put("styHouse", styHouses);
            map.put("evalCount", Collections.singletonList(evalCount));
            map.put("evaluation", evaluations);
            map.put("isOpen", Collections.singletonList("1"));

            json = JSONUtils.toJSONString(map);
        }
        else {
            map.put("isOpen", Collections.singletonList("0"));
            json = JSONUtils.toJSONString(map);
        }

        System.out.println("发送成功");
        return json;
    }
}
