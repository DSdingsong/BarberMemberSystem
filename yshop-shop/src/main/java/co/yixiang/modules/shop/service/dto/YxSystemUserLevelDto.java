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

/**
 * @author hupeng
 * @date 2020-05-12
 */
@Data
public class YxSystemUserLevelDto implements Serializable {

    private Integer id;

    /** 会员名称 */
    private String name;

    /** 购买金额 */
    private BigDecimal money;

    /** 会员等级 */
    private Integer level;

    /** 享受折扣 */
    private BigDecimal discount;

    /** 说明 */
    private String explain;

    /** 添加时间 */
    private Integer addTime;

}
