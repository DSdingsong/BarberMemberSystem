/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co
 * 注意：
 * 本软件为www.yixiang.co开发研制
 */
package co.yixiang.modules.shop.service.mapper;

import co.yixiang.common.mapper.CoreMapper;
import co.yixiang.modules.shop.domain.YxUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
 * @author hupeng
 * @date 2020-05-12
 */
@Repository
@Mapper
public interface UserMapper extends CoreMapper<YxUser> {

    @Select("select count(uid) from yx_user ")
    Integer memberCount();

    @Select("select IFNULL(sum(number),0)  from yx_user_bill where type='system_add' and  uid <> '9999999' ")
    Integer memberAdd();

    @Select("select IFNULL(sum(number),0)   from yx_user_bill where type='system_sub'  and  uid <> '9999999' ")
    Integer memberSub();

    @Select("select IFNULL(sum(now_money),0)  from yx_user where nickname='非会员消费记录' ")
    Integer nonmemberSub();

    @Select("SELECT IFNULL(sum(number),0)  FROM yx_user_bill WHERE type='system_add'  and  uid <> '9999999'  and add_time >= ${today}")
    Integer memberAddTime(@Param("today") int today);

    @Select("SELECT IFNULL(sum(number),0) FROM yx_user_bill WHERE   add_time < ${today}  and add_time >= ${yesterday} and  type='system_add'  and  uid <> '9999999'  ")
    Integer memberAddTimeY(@Param("today") int today, @Param("yesterday") int yesterday);


    @Select("SELECT IFNULL(sum(number),0) FROM yx_user_bill WHERE type='system_sub'  and  uid <> '9999999' and add_time >= ${today}")
    Integer memberSubTime(@Param("today") int today);

    @Select("SELECT IFNULL(sum(number),0) FROM yx_user_bill WHERE   add_time < ${today}  and  add_time >= ${yesterday} and type='system_sub'  and  uid <> '9999999' ")
    Integer memberSubTimeY(@Param("today") int today, @Param("yesterday") int yesterday);

    @Select("select discount from yx_system_user_level  where level=#{level} ")
    Double levelByDiscount(@Param("level") int level);


    @Update("update yx_user set now_money = now_money + ${money},level = #{level}, " +
            "discount =(select discount from yx_system_user_level where level = #{level}) where uid = #{id}")
    void updateMoney(@Param("money") double money, @Param("level") int level,  @Param("id") int id);

}
