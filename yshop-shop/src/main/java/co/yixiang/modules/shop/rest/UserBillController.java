/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co
 * 注意：
 * 本软件为www.yixiang.co开发研制
 */
package co.yixiang.modules.shop.rest;

import co.yixiang.dozer.service.IGenerator;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.shop.service.YxUserBillService;
import co.yixiang.modules.shop.service.dto.YxUserBillDto;
import co.yixiang.modules.shop.service.dto.YxUserBillQueryCriteria;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author hupeng
 * @date 2019-11-06
 */
@Api(tags = "商城:用户账单管理")
@RestController
@RequestMapping("api")
public class UserBillController {

    private final YxUserBillService yxUserBillService;
    private final IGenerator generator;


    public UserBillController(YxUserBillService yxUserBillService,IGenerator generator) {

        this.yxUserBillService = yxUserBillService;
        this.generator = generator;

    }



    @Log("查询")
    @ApiOperation(value = "查询")
    @GetMapping(value = "/yxUserBill")
    @PreAuthorize("hasAnyRole('admin','YXUSERBILL_ALL','YXUSERBILL_SELECT')")
    public ResponseEntity getYxUserBills(YxUserBillQueryCriteria criteria, Pageable pageable) {
        return new ResponseEntity(yxUserBillService.queryAll(criteria, pageable), HttpStatus.OK);
    }


    @DeleteMapping(value = "/yxUserBill/{id}")
    @PreAuthorize("hasAnyRole('admin','YXUSERBILL_ALL','YXUSERBILL_DELETE')")
    public ResponseEntity delete(@PathVariable Integer id) {

        yxUserBillService.removeById(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    //    @Log("删除用户账单")
//    @ApiOperation(value = "删除用户账单")
//    @PreAuthorize("@el.check('yxUserBill:del')")
//    @DeleteMapping
//    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
//        yxUserBillService.removeByIds(new ArrayList<>(Arrays.asList(ids)));
//        return new ResponseEntity<>(HttpStatus.OK);
//    }

//    @Log("删除所有用户账单")
//    @ApiOperation("删除所有用户账单")
//    @DeleteMapping(value = "/yxUserBill/1")
//    @PreAuthorize("hasAnyRole('admin','YXUSERBILL_ALL','YXUSERBILL_DELETE')")
//    public ResponseEntity<Object> delAllByStatus() {
//        yxUserBillService.delAll();
//        return new ResponseEntity<>(HttpStatus.OK);
//    }

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/yxUserBill/download")
    @PreAuthorize("@el.check('bill:list')")
    public void download(HttpServletResponse response, YxUserBillQueryCriteria criteria) throws IOException {
        yxUserBillService.download(generator.convert(yxUserBillService.queryAll(criteria), YxUserBillDto.class),response);
    }




}
