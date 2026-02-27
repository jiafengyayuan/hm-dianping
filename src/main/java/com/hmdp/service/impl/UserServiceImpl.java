package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.LoginFormDTO;
import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.User;
import com.hmdp.mapper.UserMapper;
import com.hmdp.service.IUserService;
import com.hmdp.utils.RegexUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Random;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public Result sendCode(String phone, HttpSession session) {
        //检查手机号
        if (RegexUtils.isPhoneInvalid(phone)){
            //错误手机号
            return Result.fail("手机号错误");
        }

        //生成验证码
        String code = RandomUtil.randomNumbers(6);
        //保存验证码
        session.setAttribute("code",code);
        //返回验证码
        log.debug("发送验证码成功"+code);
        return Result.ok();
    }

    @Override
    public Result login(LoginFormDTO loginForm, HttpSession session) {
        String phone = loginForm.getPhone();
        //校验手机号和code
        if (RegexUtils.isPhoneInvalid(phone)){
            //错误手机号
            return Result.fail("手机号错误");
        }
        //校验code
        Object obcode = session.getAttribute("code");
        String code = loginForm.getCode();
        //不一zhi
        if (obcode == null || !obcode.toString().equals(code)){
            return Result.fail("验证code错误");
        }
        //手机号查用户
        User user = query().eq("phone",phone).one();

        //没有用户
        if (user == null){
            user = createUserwithPhone(phone);
        }
        //保存用户到session
        session.setAttribute("user", BeanUtil.copyProperties(user, UserDTO.class));

        return Result.ok();
    }

    private User createUserwithPhone(String phone) {
        User user = new User();
        user.setPhone(phone);
        user.setNickName("游客"+RandomUtil.randomString(10));
        save(user);
        return user;
    }
}
