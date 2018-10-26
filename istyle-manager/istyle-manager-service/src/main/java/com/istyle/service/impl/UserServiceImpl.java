package com.istyle.service.impl;

import com.exception.AppAuthException;
import com.istyle.mapper.TbSubmissionMapper;
import com.istyle.mapper.TbUserMapper;
import com.istyle.mapper.TbUserUserMapper;
import com.istyle.pojo.TbSubmission;
import com.istyle.pojo.TbUser;
import com.istyle.pojo.TbUserUser;
import com.istyle.service.UserService;
import com.util.JWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author 黄文伟
 */
@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private TbUserMapper tbUserMapper;
    @Autowired
    private TbUserUserMapper tbUserUserMapper;
    @Autowired
    private TbSubmissionMapper tbSubmissionMapper;

    /**
     * 注册用户
     * @param user
     */
    @Override
    public void insertUser(TbUser user){
        if (user == null) {
            System.out.println("user为空");
        }
        System.out.println(user.getUserName());
        System.out.println(user.getUserPassword());
            tbUserMapper.insertUser(user);
    }

    /**
     * 判断号码是否存在
     * @param userName
     * @return
     */
    @Override
    public boolean isUserName(String userName) {
        if (tbUserMapper.isUserName(userName) == 0) {
            //存在
            return false;
        } else {
            //不存在
            return true;
        }
    }

    /**
     * 用户登录
     * @param user
     * @return
     */
    @Override
    public TbUser loginUser(TbUser user){
        if (tbUserMapper.isNameAndPassword(user) != 1) {
            throw new AppAuthException("号码和密码错误");
        }
        if (tbUserMapper.isUserName(user.getUserPhone()) != 1) {
            throw new AppAuthException("号码错误");
        }
        TbUser tbUser = tbUserMapper.loginUser(user);
        if (tbUser == null) {
            throw new AppAuthException("登录错误");
        }
        String stoken = JWT.sign(user, 24L * 3600L * 30);
        tbUser.setUserPassword(null);
        tbUser.setStoken(stoken);
        return tbUser;
    }

    /**
     * 修改用户信息
     * @param user
     */
    @Override
    public void updateUser(TbUser user) {
        tbUserMapper.updateById(user);
    }

    /**
     * 通过id查询用户
     * @param userId
     * @return
     */
    @Override
    public TbUser selectUserById(Long userId) {
        return tbUserMapper.selectUserById(userId);
    }

    /**
     * 通过id查询关注
     * @param userId
     * @return
     */
    @Override
    public List<TbUser> selectFollersById(Long userId) {
        List<TbUser> users = tbUserMapper.selectPhotoNameWordById(userId);
        return users;
    }

    /**
     * 通过id查询用户数量
     * @param userId
     * @return
     */
    @Override
    public Long selectUserCountById(Long userId) {
        return tbUserMapper.selectUserCountById(userId);
    }

    /**
     * 取消关注
     * @param userId
     * @param userId2
     * @return
     */
    @Override
    public int unFoller(Long userId, Long userId2) {
        TbUserUser tbUserUser = new TbUserUser();
        tbUserUser.setUserId(userId);
        tbUserUser.setUserId2(userId2);
         tbUserUserMapper.updateUsersStateTo1(tbUserUser);
         int flag = tbUserUserMapper.selectUsersStateById(tbUserUser);

         if (flag == 1){
             return 0;
         }
         else {
             return 1;
         }
    }

    /**
     * 我的粉丝页面展示
     * @param userId2
     * @return fanCount
     * @return users
     * @return usersState
     * @return fans
     */
    @Override
    public Map myFansPage(Long userId2) {
        Long fanCount;
        List<TbUser> users ;
        List<Integer> usersState = new ArrayList<>();
        Map<String, List> fans = new HashMap<>(16);
        TbUserUser tbUserUser = new TbUserUser();
        List<Long> userId;
        Integer flag;

//        获取粉丝数量
        fanCount = tbUserUserMapper.selectFanCountByUserId2(userId2);
//        获取粉丝
        users =tbUserUserMapper.selectUsersByUserId2(userId2);
//        获取粉丝id
        userId = tbUserUserMapper.selectUserIdByUserId2(userId2);

        tbUserUser.setUserId(userId2);
        for (Long id :
                userId) {
                tbUserUser.setUserId2(id);
                flag = tbUserUserMapper.selectUsersStateById(tbUserUser);
                if (flag == null) {
                    usersState.add(1);
                } else {
                    usersState.add(flag);
                }

        }

        fans.put("fanCount", Collections.singletonList(fanCount));
        fans.put("users", users);
        fans.put("usersState", usersState);

        for (Integer state :
                usersState) {
        }

        return fans;
    }

    /**
     * 粉丝关注功能
     * @param userUser
     * @return 0; 表示关注成功
     * @return 1; 表示关注失败
     */
    @Override
    public int addFoller(TbUserUser userUser) {
        Integer flag;

//        判断用户是否曾关注过，如果没有，则增加关注；如果关注后取关，则修改关注
        flag = tbUserUserMapper.selectUsersStateById(userUser);
        if (flag == null){
            tbUserUserMapper.addUserStateTo0(userUser);
        } else {
            tbUserUserMapper.updateUsersStateTo0(userUser);
        }
        flag = tbUserUserMapper.selectUsersStateById(userUser);

        if (flag == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    /**
     * 我的投稿界面展示
     * @param userId
     * @return map
     */
    @Override
    public Map mySubmission(Long userId) {
        HashMap<String, List> map = new HashMap<>(16);
        List<TbSubmission> submissions;
        Long subCount;

        if (userId != null) {

            subCount = tbSubmissionMapper.selectSubCountByUserId(userId);
            submissions = tbSubmissionMapper.findSubmissionIdByUserId(userId);

            map.put("submissionCount", Collections.singletonList(subCount));
            map.put("submissions", submissions);
        }

        return map;
    }
}
