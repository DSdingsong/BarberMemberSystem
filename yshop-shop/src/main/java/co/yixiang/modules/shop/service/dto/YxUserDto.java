/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co
 * 注意：
 * 本软件为www.yixiang.co开发研制
 */
package co.yixiang.modules.shop.service.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author hupeng
 * @date 2020-05-12
 */
@Data
public class YxUserDto implements Serializable {

    /** 用户id */
    private Integer uid;

    /** 会员姓名 */
    private String nickname;


    /** 用户备注 */
    private String mark;

    /** 手机号码 */
    private String phone;


    /** 用户余额 */
    private BigDecimal nowMoney;

    /** 1为正常，0为禁止 */
    private Integer status;

    /** 等级 */
    private Integer level;

    /** 创建时间 */
    private Timestamp createTime;

    /** 会员折扣 */
    private Double discount;


}
