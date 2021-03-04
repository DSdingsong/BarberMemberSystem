/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co
 * 注意：
 * 本软件为www.yixiang.co开发研制
 */
package co.yixiang.modules.shop.rest;

import cn.hutool.core.util.ObjectUtil;
import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.constant.SystemConfigConstants;
import co.yixiang.dozer.service.IGenerator;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.shop.domain.YxUser;
import co.yixiang.modules.shop.service.YxSystemConfigService;
import co.yixiang.modules.shop.service.YxUserService;
import co.yixiang.modules.shop.service.dto.UserMoneyDto;
import co.yixiang.modules.shop.service.dto.YxUserBillQueryCriteria;
import co.yixiang.modules.shop.service.dto.YxUserDto;
import co.yixiang.modules.shop.service.dto.YxUserQueryCriteria;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author hupeng
 * @date 2019-10-06
 */
@Api(tags = "商城:会员管理")
@RestController
@RequestMapping("api")
public class MemberController {

    private final YxUserService yxUserService;
    private final YxSystemConfigService yxSystemConfigService;
    private final IGenerator generator;


    public MemberController(YxUserService yxUserService, YxSystemConfigService yxSystemConfigService,IGenerator generator) {
        this.yxUserService = yxUserService;
        this.yxSystemConfigService = yxSystemConfigService;
        this.generator = generator;

    }

    @Log("查询用户")
    @ApiOperation(value = "查询用户")
    @GetMapping(value = "/yxUser")
    @PreAuthorize("hasAnyRole('admin','YXUSER_ALL','YXUSER_SELECT')")
    public ResponseEntity getYxUsers(YxUserQueryCriteria criteria, Pageable pageable) {
        if (ObjectUtil.isNotNull(criteria.getIsPromoter())) {
            if (criteria.getIsPromoter() == 1) {
                String key = yxSystemConfigService.findByKey(SystemConfigConstants.STORE_BROKERAGE_STATU)
                        .getValue();
                if (Integer.valueOf(key) == 2) {
                    return new ResponseEntity(null, HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity(yxUserService.queryAll(criteria, pageable), HttpStatus.OK);
    }



    @Log("新增用户")
    @ApiOperation(value = "新增用户")
    @PostMapping(value = "/yxUser")
    @PreAuthorize("hasAnyRole('admin','YXUSER_ALL','YXUSER_CREATE')")
    public ResponseEntity create(@Validated @RequestBody YxUser resources) {
            return new ResponseEntity(yxUserService.create(resources),HttpStatus.CREATED);
    }

    @Log("修改用户")
    @ApiOperation(value = "修改用户")
    @PutMapping(value = "/yxUser")
    @PreAuthorize("hasAnyRole('admin','YXUSER_ALL','YXUSER_EDIT')")
    public ResponseEntity update(@Validated @RequestBody YxUser resources) {
        yxUserService.update(resources);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Log("删除用户")
    @ApiOperation(value = "删除用户")
    @DeleteMapping(value = "/yxUser/{uid}")
    @PreAuthorize("hasAnyRole('admin','YXUSER_ALL','YXUSER_DELETE')")
    public ResponseEntity delete(@PathVariable Integer uid) {

        yxUserService.removeById(uid);
        return new ResponseEntity(HttpStatus.OK);
    }



    @ApiOperation(value = "修改余额")
    @PostMapping(value = "/yxUser/money")
    @PreAuthorize("hasAnyRole('admin','YXUSER_ALL','YXUSER_EDIT')")
    public ResponseEntity updatePrice(@Validated @RequestBody UserMoneyDto param) {
        yxUserService.updateMoney(param);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }



    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/yxUser/download")
    @PreAuthorize("@el.check('yxUser:list')")
    public void download(HttpServletResponse response, YxUserQueryCriteria criteria) throws IOException {
        yxUserService.download(generator.convert(yxUserService.queryAll(criteria), YxUserDto.class), response);
    }

    @GetMapping(value = "/yxUser/membercount")
    @AnonymousAccess
    public ResponseEntity getMemberCount() {
        return new ResponseEntity(yxUserService.memberCount(), HttpStatus.OK);
    }

}
