package com.gizwits.lease.wallet.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gizwits.boot.dto.Pageable;
import com.gizwits.lease.wallet.dto.UserWalletUseRecordDto;
import com.gizwits.lease.wallet.entity.UserWallet;
import com.gizwits.lease.wallet.entity.UserWalletUseRecord;
import com.baomidou.mybatisplus.service.IService;
import com.gizwits.lease.wallet.dto.UserWalletUseDto;

import java.util.List;

/**
 * <p>
 * 用户钱包操作记录表 服务类
 * </p>
 *
 * @author yinhui
 * @since 2017-07-28
 */
public interface UserWalletUseRecordService extends IService<UserWalletUseRecord> {

    /**
     * 添加用户钱包操作记录
     */
    boolean insertUserWalletUseRecord(UserWallet userWallet, Double fee, Integer opeartorType, String tradeNo);

    /**
     * 通过openId查询钱包操作记录(卡励）只需查询充值记录
     *@param pageable
     * @Param operatorType
     */
    Page<UserWalletUseRecordDto> listPage(Pageable<String> pageable, Integer operatorType);

    /**
     * 通过openId查询钱包操作（充值／支付）记录
     *@param pageable
     * @Param operatorType
     */
    Page<UserWalletUseRecordDto> listPage(Pageable pageable);


    /**
     * 统计 kali
     */
    Integer countNum(Integer userId, Integer operationType);

    /**
     * 统计
     * @param userId
     * @return
     */
    Integer countNum(Integer userId);

    /**
     * 分页查询 kali
     * @param userId
     * @param operationType
     * @param pagesize
     * @param begin
     * @return
     */
    List<UserWalletUseRecordDto> listUserWalletUseRecord(Integer userId, Integer operationType, Integer pagesize, Integer begin);

    /**
     * 重载
     * @param userId
     * @param pagesize
     * @param begin
     * @return
     */
    List<UserWalletUseRecordDto> listUserWalletUseRecord(Integer userId, Integer pagesize, Integer begin);

}
