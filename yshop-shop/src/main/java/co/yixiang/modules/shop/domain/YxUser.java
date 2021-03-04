/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co
 * 注意：
 * 本软件为www.yixiang.co开发研制
 */
package co.yixiang.modules.shop.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author hupeng
 * @date 2020-05-12
 */
@Data
public class YxUser implements Serializable {

    /** 用户id */
    @TableId(value = "uid", type = IdType.AUTO)
    private Integer uid;

    @NotBlank(message = "会员姓名不可为空")
    /** 用户昵称 */
    private String nickname;

    /** 用户备注 */
    private String mark;

    @NotNull(message = "会员折扣不可为空")
    /** 手机号码 */
    private BigDecimal discount;

    @NotBlank(message = "手机号码不可为空")
    /** 手机号码 */
    private String phone;

    @NotNull(message = "会员余额不可为空")
    /** 用户余额 */
    private BigDecimal nowMoney;

    /** 1为正常，0为禁止 */
    private Integer status;

    @NotNull(message = "会员等级不可为空")
    /** 等级 */
    private Integer level;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private Timestamp createTime;



    public void copy(YxUser source) {
        BeanUtil.copyProperties(source, this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
