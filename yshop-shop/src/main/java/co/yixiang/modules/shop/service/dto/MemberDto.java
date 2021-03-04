/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co
 * 注意：
 * 本软件为www.yixiang.co开发研制
 */
package co.yixiang.modules.shop.service.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName UserBillDTO
 * @Author hupeng <610796224@qq.com>
 * @Date 2019/12/11
 **/
@Data
public class MemberDto implements Serializable {
    /**
     * 会员总数
     * */
    private Integer memberCount;

    /**
     * 会员总充值
     * */
    private double memberAdd;

    /**
     * 会员总消费
     * */
    private double memberSub;

    /**
     * 非会员消费
     * */
    private double nonmemberSub;

    /**
     *今日充值金额
     */
    private double memberAddDay;

    /**
     *昨日充值金额
     */
    private double memberAddYesterday;


    /**
     *本周充值金额
     */
    private double memberAddWeek;


    /**
     *本月充值金额
     */
    private double memberAddMonth;


    /**
     *今日消费金额
     */
    private double memberSubDay;


    /**
     *昨日消费金额
     */

    private double memberSubYesterday;

    /**
     *本周消费金额
     */
    private double memberSubWeek;


    /**
     *本月消费金额
     */
    private double memberSubMonth;

}
