package com.chen.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.chen.gmall.bean.UmsMember;
import com.chen.gmall.bean.UmsMemberReceiveAddress;
import com.chen.gmall.service.UserService;
import com.chen.gmall.user.mapper.UmsMemberReceiveAddressMapper;
import com.chen.gmall.user.mapper.UserMapper;
import com.chen.gmall.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public List<UmsMember> getAllUser() {

        List<UmsMember> umsMemberList = userMapper.selectAll();//userMapper.selectAllUser();

        return umsMemberList;
    }

    @Override
    public List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId) {

        // 封装的参数对象
        UmsMemberReceiveAddress umsMemberReceiveAddress = new UmsMemberReceiveAddress();
        umsMemberReceiveAddress.setMemberId(memberId);
        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = umsMemberReceiveAddressMapper.select(umsMemberReceiveAddress);


//        Example example = new Example(UmsMemberReceiveAddress.class);
//        example.createCriteria().andEqualTo("memberId",memberId);
//        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = umsMemberReceiveAddressMapper.selectByExample(example);

        return umsMemberReceiveAddresses;
    }

    @Override
    public UmsMember login(UmsMember umsMember) {

        Jedis jedis = null;
        try{
            jedis = redisUtil.getJedis();
            String umsMemberInfo = jedis.get("user:" + umsMember.getPassword() + ":info");

            if(StringUtils.isNotBlank(umsMemberInfo)){
                UmsMember umsMember1 = JSON.parseObject(umsMemberInfo,UmsMember.class);
                return umsMember1;
            }else {
                //密码错误

                //缓存中没有

                //开数据库
                UmsMember umsMemberFromDb = loginFromDb(umsMember);
                if(umsMemberFromDb!=null){
                    jedis.setex("user:" + umsMember.getPassword() + ":info",60*60*3,JSON.toJSONString(umsMemberFromDb));
                }
                return umsMemberFromDb;
            }

        }finally {
            jedis.close();
        }

    }

    @Override
    public void addUserToken(String token, String userId) {
        Jedis jedis = redisUtil.getJedis();
        //给定token的过期时间，两个小时内有效，将token放入到redis缓存中保存
        jedis.setex("user"+userId+":token",60*60*2,token);

        jedis.close();
    }

    private UmsMember loginFromDb(UmsMember umsMember) {

        UmsMember umsMember1 = userMapper.selectOne(umsMember);

        return umsMember1;
    }
}
