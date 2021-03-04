/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co
 * 注意：
 * 本软件为www.yixiang.co开发研制
 */
package co.yixiang.modules.shop.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import co.yixiang.common.service.impl.BaseServiceImpl;
import co.yixiang.common.utils.QueryHelpPlus;
import co.yixiang.dozer.service.IGenerator;
import co.yixiang.exception.BadRequestException;
import co.yixiang.modules.shop.domain.YxUser;
import co.yixiang.modules.shop.domain.YxUserBill;
import co.yixiang.modules.shop.service.YxUserBillService;
import co.yixiang.modules.shop.service.YxUserService;
import co.yixiang.modules.shop.service.dto.*;
import co.yixiang.modules.shop.service.mapper.UserMapper;
import co.yixiang.utils.FileUtil;
import co.yixiang.utils.OrderUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

// 默认不使用缓存
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;

/**
 * @author hupeng
 * @date 2020-05-12
 */
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "yxUser")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class YxUserServiceImpl extends BaseServiceImpl<UserMapper, YxUser> implements YxUserService {

    private final IGenerator generator;

    private final UserMapper yxUserMapper;

    private final YxUserBillService yxUserBillService;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(YxUserQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<YxUser> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), YxUserDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }

    @Override
    //@Cacheable
    public List<YxUser> queryAll(YxUserQueryCriteria criteria) {
            return baseMapper.selectList(QueryHelpPlus.getPredicate(YxUser.class, criteria));

    }



    @Override
    public void download(List<YxUserDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (YxUserDto yxUser : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("会员姓名", yxUser.getNickname());
            map.put("手机号码", yxUser.getPhone());
            map.put("会员余额", yxUser.getNowMoney());
            map.put("会员等级", yxUser.getLevel());
            map.put("会员备注", yxUser.getMark());
            map.put("创建时间", yxUser.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    /**
     * 首页显示
     * @return
     */
    @Override
    public MemberDto memberCount() {
        int today = OrderUtil.dateToTimestampT(DateUtil.beginOfDay(new Date()));
        int yesterday = OrderUtil.dateToTimestampT(DateUtil.beginOfDay(DateUtil.
                yesterday()));
        int lastWeek = OrderUtil.dateToTimestampT(DateUtil.beginOfDay(DateUtil.lastWeek()));
        int nowMonth = OrderUtil.dateToTimestampT(DateUtil
                .beginOfMonth(new Date()));
        MemberDto memberCountDto = new MemberDto();
        memberCountDto.setMemberCount(yxUserMapper.memberCount());
        memberCountDto.setMemberAdd(yxUserMapper.memberAdd());
        memberCountDto.setMemberSub(yxUserMapper.memberSub());
        memberCountDto.setNonmemberSub(yxUserMapper.nonmemberSub());

        memberCountDto.setMemberAddDay(yxUserMapper.memberAddTime(today));
        memberCountDto.setMemberAddWeek(yxUserMapper.memberAddTime(lastWeek));
        memberCountDto.setMemberAddMonth(yxUserMapper.memberAddTime(nowMonth));
        memberCountDto.setMemberAddYesterday(yxUserMapper.memberAddTimeY(today, yesterday));

        memberCountDto.setMemberSubDay(yxUserMapper.memberSubTime(today));
        memberCountDto.setMemberSubWeek(yxUserMapper.memberSubTime(lastWeek));
        memberCountDto.setMemberSubMonth(yxUserMapper.memberSubTime(nowMonth));
        memberCountDto.setMemberSubYesterday(yxUserMapper.memberSubTimeY(today, yesterday));


        return memberCountDto;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMoney(UserMoneyDto param) {
        YxUserDto userDTO = generator.convert(getById(param.getUid()), YxUserDto.class);
        Double newMoney = 0d;
        Double discount = yxUserMapper.levelByDiscount(param.getLevel());
        Double money = param.getMoney();
        String type = "system_add";
        String mark = "";
        Integer pm = 1;
        String title = "会员充值余额";

        if (param.getPtype() == 1) {
            mark = "会员充值了" + param.getMoney() + "余额";
            newMoney = NumberUtil.add(userDTO.getNowMoney(), param.getMoney()).doubleValue();
        } else {
            title = "会员消费余额";
            type = "system_sub";
            pm = 0;
            newMoney = NumberUtil.sub(userDTO.getNowMoney(), param.getMoney()).doubleValue();
            money =  discount / 10 * money;
            newMoney = newMoney + param.getMoney() - money;
            mark = "会员折扣后消费了" +  money + "余额";
            if (newMoney < 0) {
                throw new BadRequestException("会员余额不足，请充值 ！！！");
            }

            if(param.getLevel() == 1 && pm == 0){
                throw new BadRequestException("该用户为非会员，不可减少余额，请选择新增余额 ！！！");
            }
        }

        YxUser user = new YxUser();
        user.setUid(userDTO.getUid());
        user.setNowMoney(BigDecimal.valueOf(newMoney));
        user.setLevel(param.getLevel());
        user.setDiscount(BigDecimal.valueOf(discount));
        saveOrUpdate(user);

        YxUserBill userBill = new YxUserBill();
        userBill.setUid(userDTO.getUid());
        userBill.setLinkId("0");
        userBill.setPm(pm);
        userBill.setPhone(userDTO.getPhone());
        userBill.setTitle(title);
        userBill.setCategory("now_money");
        userBill.setType(type);
        userBill.setNumber(BigDecimal.valueOf(money));
        userBill.setBalance(BigDecimal.valueOf(newMoney));
        userBill.setMark(mark);
        userBill.setAddTime(OrderUtil.getSecondTimestampTwo());
        userBill.setCreateTime(OrderUtil.getStringToday());
        userBill.setStatus(1);
        yxUserBillService.save(userBill);
    }


    /**
     * 新增用户
     *
     * @param resources /
     * @return /
     */
    @Override
    //@CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public YxUserDto create(YxUser resources) {
        if (this.getOne(new LambdaQueryWrapper<YxUser>().eq(YxUser::getPhone, resources.getPhone())) != null) {
            throw new BadRequestException("该手机号已存在！！！");
        }
        this.save(resources);
        return generator.convert(resources, YxUserDto.class);
    }



    /**
     * 编辑用户
     *
     * @param resources /
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(YxUser resources) {
        YxUser yxUser = this.getById(resources.getUid());
        YxUser yxStoreOrder1 = this.getOne(new LambdaQueryWrapper<YxUser>().eq(YxUser::getPhone, resources.getPhone()));
        if (yxStoreOrder1 != null && !yxStoreOrder1.getUid().equals(yxUser.getUid())) {
            throw new BadRequestException("该手机号已存在！！！");
        }


        yxUser.copy(resources);
        this.saveOrUpdate(yxUser);
    }


}
