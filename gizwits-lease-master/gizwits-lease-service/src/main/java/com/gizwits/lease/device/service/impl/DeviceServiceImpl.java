package com.gizwits.lease.device.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gizwits.boot.dto.Pageable;
import com.gizwits.boot.enums.DeleteStatus;
import com.gizwits.boot.enums.SysUserType;
import com.gizwits.boot.sys.entity.SysUser;
import com.gizwits.boot.sys.entity.SysUserExt;
import com.gizwits.boot.sys.service.SysUserExtService;
import com.gizwits.boot.sys.service.SysUserService;
import com.gizwits.boot.utils.CommonEventPublisherUtils;
import com.gizwits.boot.utils.ParamUtil;
import com.gizwits.boot.utils.QueryResolverUtils;
import com.gizwits.boot.utils.SysConfigUtils;
import com.gizwits.lease.benefit.entity.ShareBenefitRuleDetailDevice;


import com.gizwits.lease.config.CommonSystemConfig;
import com.gizwits.lease.constant.*;
import com.gizwits.lease.device.dao.DeviceDao;
import com.gizwits.lease.device.dao.DevicePlanDao;
import com.gizwits.lease.device.entity.*;
import com.gizwits.lease.device.entity.dto.*;
import com.gizwits.lease.device.service.*;
import com.gizwits.lease.device.vo.BatchDeviceDetailWebSocketVo;
import com.gizwits.lease.device.vo.BatchDevicePageDto;
import com.gizwits.lease.device.vo.BatchDeviceWebSocketVo;
import com.gizwits.lease.device.vo.DeviceAuth;
import com.gizwits.lease.device.vo.DeviceDetailWebSocketVo;
import com.gizwits.lease.device.vo.DevicePageDto;
import com.gizwits.lease.device.vo.DeviceWebSocketVo;
import com.gizwits.lease.enums.*;
import com.gizwits.lease.event.BindGizwitsDeviceEvent;
import com.gizwits.lease.event.DeviceLocationEvent;
import com.gizwits.lease.exceptions.LeaseExceEnums;
import com.gizwits.lease.exceptions.LeaseException;
import com.gizwits.lease.manager.dto.OperatorAllotDeviceDto;
import com.gizwits.lease.manager.dto.OperatorExtDto;
import com.gizwits.lease.manager.entity.Agent;
import com.gizwits.lease.manager.entity.Manufacturer;
import com.gizwits.lease.manager.entity.Operator;
import com.gizwits.lease.manager.service.AgentService;
import com.gizwits.lease.manager.service.ManufacturerService;
import com.gizwits.lease.model.DeviceAddressModel;
import com.gizwits.lease.model.MapDataEntity;
import com.gizwits.lease.operator.entity.OperatorExt;
import com.gizwits.lease.operator.service.OperatorExtService;
import com.gizwits.lease.order.entity.OrderBase;
import com.gizwits.lease.order.service.OrderBaseService;
import com.gizwits.lease.product.dto.*;
import com.gizwits.lease.product.entity.*;
import com.gizwits.lease.product.service.*;
import com.gizwits.lease.redis.RedisService;
import com.gizwits.lease.stat.dto.StatDeviceDto;
import com.gizwits.lease.stat.dto.StatDeviceTrendDto;
import com.gizwits.lease.stat.vo.StatAlarmWidgetVo;
import com.gizwits.lease.stat.vo.StatDeviceWidgetVo;
import com.gizwits.lease.stat.vo.StatLocationVo;
import com.gizwits.lease.stat.vo.StatTrendVo;
import com.gizwits.lease.user.entity.User;
import com.gizwits.lease.user.service.UserService;
import com.gizwits.lease.user.service.UserWeixinService;
import com.gizwits.lease.util.*;

import com.google.gson.JsonElement;
import groovy.util.IFileNameFinder;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 设备表 服务实现类
 * </p>
 *
 * @author zhl
 * @since 2017-07-11
 */
@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceDao, Device> implements DeviceService {
    protected static Logger logger = LoggerFactory.getLogger("DEVICE_LOGGER");

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private ManufacturerService manufacturerService;

    @Autowired

    private SysUserExtService sysUserExtService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private ProductServiceModeService productServiceModeService;

    @Autowired
    private ProductServiceDetailService productServiceDetailService;

    @Autowired
    private DeviceLaunchAreaService deviceLaunchAreaService;

    @Autowired
    private ProductCommandConfigService productCommandConfigService;

    @Autowired
    private ProductDeviceChangeService productDeviceChangeService;

    @Autowired
    private OrderBaseService orderBaseService;


    @Autowired
    private ProductService productService;

    @Autowired
    private ProductDataPointService productDataPointService;

    @Autowired
    private UserWeixinService userWeixinService;

    @Autowired
    private DeviceGroupToDeviceService deviceGroupToDeviceService;

    @Autowired
    private AgentService agentService;

    @Autowired
    private UserService userService;

    @Autowired
    private DeviceServiceModeSettingService deviceServiceModeSettingService;

    @Autowired
    private DeviceToProductServiceModeService deviceToProductServiceModeService;

    @Autowired
    private ProductCategoryService productCategoryService;

    @Autowired
    private UserBindDeviceService userBindDeviceService;


    @Autowired
    private DevicePlanDao devicePlanDao;


    @Override
    public Integer countDeviceByProductId(Integer productId) {
        EntityWrapper<Device> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("product_id", productId).eq("is_deleted", DeleteStatus.NOT_DELETED.getCode());
        entityWrapper.ne("status", DeviceNormalStatus.WAIT_TO_ENTRY.getCode());
        entityWrapper.in("owner_id", sysUserService.resolveSysUserAllSubAdminIds(sysUserService.getCurrentUserOwner()));
        return selectCount(entityWrapper);

    }


    @Override
    public Integer countLeaseDeviceByProductId(Integer productId) {
        EntityWrapper<Device> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("product_id", productId).eq("is_deleted", DeleteStatus.NOT_DELETED.getCode());
        entityWrapper.ne("status", DeviceNormalStatus.WAIT_TO_ENTRY.getCode());
        entityWrapper.in("owner_id", sysUserService.resolveSysUserAllSubAdminIds(sysUserService.getCurrentUserOwner()));
        return selectCount(entityWrapper);

    }

    @Override
    public List<Integer> getProductIdByDeviceSno(List<String> snos) {
        EntityWrapper<Device> entityWrapper = new EntityWrapper<>();
        entityWrapper.in("sno", snos).eq("is_deleted", DeleteStatus.NOT_DELETED.getCode());
        List<Device> devices = selectList(entityWrapper);
        List<Integer> ids = new ArrayList<>();
        for (Device device : devices) {
            ids.add(device.getProductId());
        }
        return ids;
    }

    @Override
    public List<Integer> getDeviceLaunchAreaIdIdByDeviceSno(List<String> snos) {
        EntityWrapper<Device> entityWrapper = new EntityWrapper<>();
        entityWrapper.in("sno", snos).eq("is_deleted", DeleteStatus.NOT_DELETED.getCode());
        List<Device> devices = selectList(entityWrapper);
        List<Integer> ids = new ArrayList<>();
        for (Device device : devices) {
            ids.add(device.getLaunchAreaId());
        }
        return ids;
    }

    @Override
    public Integer countDeviceByDeviceLaunchAreaId(Integer deviceLaunchAreaId) {
        EntityWrapper<Device> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("launch_area_id", deviceLaunchAreaId)
                .eq("is_deleted", DeleteStatus.NOT_DELETED.getCode());
        return selectCount(entityWrapper);
    }


    /**
     * 通过设备ID获取设备所属的微信运营配置
     */
    public SysUserExt getWxConfigByDeviceId(String deviceId) {
        if (StringUtils.isEmpty(deviceId)) {
            return null;
        }
        Device device = selectById(deviceId);
        if (device == null) {
            device = getDeviceByMac(deviceId);
            if (device == null) {
                return null;
            }
        }

        return null;
    }

    /**
     * 检查设备是否投入运营
     */
    public boolean checkDeviceIsInOperator(String deviceId) {
        if (StringUtils.isBlank(deviceId)) {
            return false;
        }
        Device device = selectById(deviceId);
        if (device == null) {

            return false;
        }
        if (ParamUtil.isNullOrEmptyOrZero(device.getOwnerId())) {
            return false;
        }


        return false;
    }


    /**
     * 获取设备直接运营商的SysUserid
     */
    public Integer getDeviceOperatorSysuserid(String deviceId) {
        if (StringUtils.isBlank(deviceId)) {
            return null;
        }
        Device device = selectById(deviceId);
        if (device == null) {
            return null;
        }
        if (ParamUtil.isNullOrEmptyOrZero(device.getOwnerId())) {
            return null;
        }

        return null;
    }

    @Override
    public Device getDeviceInfoBySno(String deviceSno) {
        EntityWrapper<Device> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("sno", deviceSno).eq("is_deleted", DeleteStatus.NOT_DELETED.getCode());
        return selectOne(entityWrapper);
    }

    /**
     * 根据Mac和ProductKey获取设备信息,如果数据库中不存在则插入一条消息
     */
    public Device getDeviceByMacAndPk(String mac, String productKey) {
        if (StringUtils.isBlank(mac) || StringUtils.isBlank(productKey)) {
            return null;
        }
        Device device = deviceDao.findByMacAndProductKey(mac, productKey);
//        if (ParamUtil.isNullOrEmptyOrZero(device)) {
//            device = insertFromReport(mac, productKey);
//        }
        return device;
    }

    /**
     * 根据Mac和productKey插入设备，由于GDMS版的设备来源于设备上报，而不是手工录入，所以用此方法插入设备
     *
     * @param mac
     * @param productKey
     * @return
     */
    @Override
    public Device insertFromReport(String mac, String productKey) {
        if (StringUtils.isBlank(mac) || StringUtils.isBlank(productKey)) {
            return null;
        }
        Product product = productService.selectOne(new EntityWrapper<Product>().eq("gizwits_product_key", productKey).eq("status", 1).eq("is_deleted", 0));
        if (!ParamUtil.isNullOrEmptyOrZero(product)) {
            Device device = new Device();
            Date now = new Date();
            device.setSno(getSno());
            device.setCtime(now);
            device.setUtime(now);
            device.setName("设备" + mac.substring(mac.length() - 6));
            device.setStatus(DeviceNormalStatus.ENTRY.getCode());
            device.setOnlineStatus(DeviceStatus.ONLINE.getCode());
            device.setWorkStatus(DeviceStatus.ONLINE.getCode());
            device.setActivateStatus(DeviceStatus.ONLINE.getCode());
            device.setActivatedTime(now);
            device.setMac(mac);
            device.setProductId(product.getId());
            device.setProductName(product.getName());
            if (!ParamUtil.isNullOrEmptyOrZero(product.getManufacturerId())) {
                Manufacturer manufacturer = manufacturerService.selectById(product.getManufacturerId());
                if (!ParamUtil.isNullOrEmptyOrZero(manufacturer)) {
                    SysUser sysUser = sysUserService.selectById(manufacturer.getSysAccountId());
                    if (!ParamUtil.isNullOrEmptyOrZero(sysUser)) {
                        //创建二维码
                        device = createQrcodeAndAuthDevice(device, product, sysUser, null);
                    }
                }

            }

            // 上报的设备默认为厂商所有
            device.setSysUserId(product.getManufacturerId());
            device.setOwnerId(product.getManufacturerId());
            device.setOrigin(DeviceOriginType.REPORT.getCode());
            insert(device);
        }
        return null;
    }

    /**
     * 从机智云接口获取坐标
     */
    public DeviceAddressModel getDeviceAddressByGizwitsAPI(String productKey, String did, String mac) {
        DeviceAddressModel addressDTO = DeviceControlAPI.getDeviceAddress(productKey, did);
        return addressDTO;
    }

    /**
     * 根据设备上报的数据点调用高德地图接口
     */
    public DeviceAddressModel getDeviceAddressByGD(String productKey, String mac, String networkType) {
        if (StringUtils.isBlank(productKey) || StringUtils.isBlank(mac)) {
            return null;
        }
        if (!redisService.containsDeviceCurrentStatusData(productKey, mac)) {//设备还未上报数据点
            return null;
        }
        JSONObject cacheData = redisService.getDeviceCurrentStatus(productKey, mac);
        if (Objects.equals(cacheData, null)) {
            logger.warn("=====设备{}未上报相应的基站信息,无法执行定位操作======");
            return null;
        }
        MapDataEntity mapDataEntity = JSONObject.parseObject(cacheData.toJSONString(), MapDataEntity.class);
        if (mapDataEntity == null || StringUtils.isBlank(mapDataEntity.getLac1())) {
            return null;
        }
        mapDataEntity.setImei("460");
        // TODO: 2017/8/26 mnc参数通过系统配置,判断是移动还是联通,0是移动，1是联通
        mapDataEntity.setMnc(networkType);
        mapDataEntity.setLac1(Integer.parseInt(mapDataEntity.getLac1(), 16) + "");
        mapDataEntity.setCellid1(Integer.parseInt(mapDataEntity.getCellid1(), 16) + "");
        mapDataEntity.setRssi1(Integer.parseInt(mapDataEntity.getRssi1(), 16) + "");

        mapDataEntity.setLac2(Integer.parseInt(mapDataEntity.getLac2(), 16) + "");
        mapDataEntity.setCellid2(Integer.parseInt(mapDataEntity.getCellid2(), 16) + "");
        mapDataEntity.setRssi2(Integer.parseInt(mapDataEntity.getRssi2(), 16) + "");

        mapDataEntity.setLac2(Integer.parseInt(mapDataEntity.getLac2(), 16) + "");
        mapDataEntity.setCellid2(Integer.parseInt(mapDataEntity.getCellid2(), 16) + "");
        mapDataEntity.setRssi2(Integer.parseInt(mapDataEntity.getRssi2(), 16) + "");

        mapDataEntity.setLac3(Integer.parseInt(mapDataEntity.getLac3(), 16) + "");
        mapDataEntity.setCellid3(Integer.parseInt(mapDataEntity.getCellid3(), 16) + "");
        mapDataEntity.setRssi3(Integer.parseInt(mapDataEntity.getRssi3(), 16) + "");


        return DeviceControlAPI.getDeviceAddressByGDMap(mapDataEntity);
    }

    /**
     * 设备控制
     */
    @Override
    public boolean remoteDeviceControl(String deviceId, String name, Object value) {
        if (ParamUtil.isNullOrEmptyOrZero(deviceId)) {
            LeaseException.throwSystemException(LeaseExceEnums.PARAMS_ERROR);
        }
        Device device = selectById(deviceId);
        if (device == null) {
            LeaseException.throwSystemException(LeaseExceEnums.DEVICE_DONT_EXISTS);
        }
        if (StringUtils.isBlank(device.getGizDid())
                || DeviceStatus.OFFLINE.getCode().equals(device.getOnlineStatus())) {
            LeaseException.throwSystemException(LeaseExceEnums.DEVICE_OFFLINE);
        }
        Product product = productService.getProductByProductId(device.getProductId());
        return DeviceControlAPI.remoteControl(product.getGizwitsProductKey(), device.getGizDid(), name, value);

    }

    public boolean remoteDeviceControl(String deviceId, JSONObject attrs) {
        if (ParamUtil.isNullOrEmptyOrZero(deviceId)) {
            LeaseException.throwSystemException(LeaseExceEnums.PARAMS_ERROR);
        }
        Device device = selectById(deviceId);
        if (device == null) {
            LeaseException.throwSystemException(LeaseExceEnums.DEVICE_DONT_EXISTS);
        }
        if (StringUtils.isBlank(device.getGizDid())
                || DeviceStatus.OFFLINE.getCode().equals(device.getOnlineStatus())) {
            LeaseException.throwSystemException(LeaseExceEnums.DEVICE_OFFLINE);
        }
        Product product = productService.getProductByProductId(device.getProductId());

//        return DeviceControlAPI.remoteControl(product.getGizwitsProductKey(), device.getGizDid(), attrs);

        //snoti 控制方式
        ControlDto controlDto = new ControlDto()
                .setProductKey(product.getGizwitsProductKey())
                .setDid(device.getGizDid())
                .setMac(device.getMac())
                .setAttrs(attrs);
        redisService.cacheSnotiControlDevice(CommandType.CONTROL.getCode(),controlDto);
        return true;
    }

    /**
     * 获取设备的实时数据点状态
     */
    public String getDeviceNowTimeStatus(String deviceId) {
        if (ParamUtil.isNullOrEmptyOrZero(deviceId)) {
            return null;
        }
        Device device = selectById(deviceId);
        if (device == null) {
            LeaseException.throwSystemException(LeaseExceEnums.DEVICE_DONT_EXISTS);
        }
        if (StringUtils.isBlank(device.getGizDid())) {
            LeaseException.throwSystemException(LeaseExceEnums.DEVICE_OFFLINE);
        }
        Product product = productService.getProductByProductId(device.getProductId());
        return DeviceControlAPI.deviceNowTimeData(device.getGizDid(), product.getGizwitsAppId());
    }

    /**
     * 下发设备实时状态查询指令,设备在2s内上报所有数据点
     */
    public boolean sendDeviceStatusQueryCommand(String deviceId, String username) {
        if (ParamUtil.isNullOrEmptyOrZero(deviceId)) {
            return false;
        }
        Device device = selectById(deviceId);
        if (device == null) {
            LeaseException.throwSystemException(LeaseExceEnums.DEVICE_DONT_EXISTS);
            return false;
        }
        if (StringUtils.isBlank(device.getGizDid())) {
            LeaseException.throwSystemException(LeaseExceEnums.DEVICE_OFFLINE);
        }
        Product product = productService.getProductByProductId(device.getProductId());
        return DeviceControlAPI.sendDataQueryCommand(device.getGizDid(), product.getGizwitsProductKey());
    }

    /**
     * 根据订单发送控制指令
     */
    public boolean remoteDeviceControlByOrder(OrderBase orderBase) {
        if (ParamUtil.isNullOrEmptyOrZero(orderBase.getSno())
                || ParamUtil.isNullOrEmptyOrZero(orderBase.getCommand())) {
            return false;
        }

        Device device = selectById(orderBase.getSno());
        if (device == null || ParamUtil.isNullOrEmptyOrZero(device.getGizDid())) {
            //获取设备的gizDid
            return false;
        }
        Product product = productService.getProductByProductId(device.getProductId());

        return DeviceControlAPI.remoteControl(product.getGizwitsProductKey(), device.getGizDid(), orderBase.getCommand());
    }

    /**
     * 更新设备的在线状态
     */
    public boolean updateDeviceOnOffLineStatus(String mac, String productKey, String did, boolean isOnline) {
        Device device = getDeviceByMacAndPk(mac, productKey);
        if (device != null && StringUtils.isNotBlank(did)) {
            logger.info("======Device {} status is {} or GizDid is {}, update Device Status to ONLINE and GizDid====", mac, device.getOnlineStatus(), device.getGizDid());
            device.setGizDid(did);
            device.setActivateStatus(DeviceActiveStatusType.ACTIVE.getCode());
            device.setUtime(new Date());
            if (isOnline) {
                baseMapper.updateActivateStatus(device.getSno(), new Date());
            }
            if (isOnline && !DeviceStatus.ONLINE.getCode().equals(device.getOnlineStatus())) {
                device.setOnlineStatus(DeviceStatus.ONLINE.getCode());
                if (device.getWorkStatus().equals(DeviceStatus.OFFLINE.getCode())) {
                    device.setWorkStatus(DeviceStatus.ONLINE.getCode());
                }
                device.setLastOnlineTime(new Date());
                device.setActivateStatus(DeviceActiveStatusType.ACTIVE.getCode());

                /***调整设备的经纬度信息***/
                locationDeviceAddress(productKey, mac);
                //同步设备数据到天气拓展表
//                deviceExtWeatherService.save(device);
            } else if (!isOnline && !DeviceStatus.OFFLINE.getCode().equals(device.getOnlineStatus())) {
                device.setOnlineStatus(DeviceStatus.OFFLINE.getCode());
            }
            return updateById(device);
        }
        return false;
    }

    @Override
    public List<String> getSnosByProductId(List<Integer> productId) {
        SysUser current = sysUserService.getCurrentUserOwner();
        List<Integer> userIds = sysUserService.resolveSysUserAllSubIds(current);
        EntityWrapper<Device> entityWrapper = new EntityWrapper<>();
        entityWrapper.in("product_id", productId).in("owner_id", userIds).eq("is_deleted", DeleteStatus.NOT_DELETED.getCode());
        List<Device> list = selectList(entityWrapper);
        List<String> snos = new ArrayList<>();
        for (Device d : list) {
            snos.add(d.getSno());
        }
        return snos;
    }

    public List<Integer> getDeviceLaunchAreaIdByProductId(Integer productId) {
        EntityWrapper<Device> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("product_id", productId).eq("is_deleted", DeleteStatus.NOT_DELETED.getCode());
        List<Device> deviceList = selectList(entityWrapper);
        List<Integer> deviceLaunchAreaId = new ArrayList<>();
        for (Device device : deviceList) {
            deviceLaunchAreaId.add(device.getLaunchAreaId());
        }
        return deviceLaunchAreaId;
    }


    /**
     * 2	厂商管理员
     * 3	经销商
     * 4	操作员
     * 5    入库员
     */
    public String addDevice(DeviceAddDto deviceAddDto) {
        if (Objects.isNull(deviceAddDto.getDeviceAddDetailsDtos())){
            LeaseException.throwSystemException(LeaseExceEnums.WORK_ORDER_MAC_BLANK);
        }
        String result = "";
        SysUser sysUser = sysUserService.getCurrentUserOwner();
        Device device = new Device();
        Date now = new Date();

        List<String> addResult = new ArrayList();

        //厂商
        /**
         * 点击“有控制器”按钮，则开始扫码，自动录入“设备MAC”和“SN1（控制器码）”到扫码框中（关联设备MAC和SN1）；
         * 点击“无控制器”按钮，开始扫码录入，区别是“无控制器”模式只需录入“设备MAC”。
         */
        if (sysUser.getIsAdmin().equals(SysUserType.MANUFACTURER.getCode())){
            List<Device> devices = new ArrayList<>();

            for (DeviceAddDetailsDto detailsDto : deviceAddDto.getDeviceAddDetailsDtos()) {
                if (Objects.isNull(detailsDto.getMac())){
                    LeaseException.throwSystemException(LeaseExceEnums.WORK_ORDER_MAC_BLANK);
                }
                Device oldDevice = getDeviceByMac(detailsDto.getMac());
                if (oldDevice != null) {
                    //批量插入预处理
                    addResult.add(detailsDto.getMac());

                } else {
                    device.setSno(getSno());
                    device.setMac(detailsDto.getMac());
                    device.setUtime(now);
                    device.setCtime(now);
                    device.setOrigin(DeviceOriginType.INPUT.getCode());
                    device.setSysUserId(sysUser.getId());
                    device.setOwnerId(sysUser.getId());
                    device.setOwnerName(sysUser.getRealName());
                    device.setRemarks(deviceAddDto.getRemarks());
                    if (deviceAddDto.getControlType()){  //有控制器
                        device.setsN1(detailsDto.getsN1());
                    }
                    device.setControlType(deviceAddDto.getControlType());
                    devices.add(device);
                }
            }

            for (Device dev : devices) {
                insertOrUpdate(dev);
            }

            if (addResult.size()>0){
                 result = "以下设备添加失败:"+addResult.toString()+",原因："+LeaseExceEnums.DEVICE_EXISTS;
            }else {
                 result = "添加成功";

            }
            return result;

        }

        /**
         * 操作员扫码模式有两种：
         * 点击“有IMEI”按钮，则开始扫码，由操作员依次录入SN2码、IMEI码（关联SN2码和IMEI码）、再扫SN1码，同SN2码做关联；
         * 点击“无IMEI”按钮，则只需要录入SN2，同厂商录入的设备MAC码做关联。配对没有SN1的设备
         * 点击“完成”按钮，将录入的信息更新到由供应商创建在设备列表中记录中，此时设备状态由待录入变为可入库。
         */
        if (sysUser.getIsAdmin().equals(SysUserType.OPERATOR.getCode())){
            for (DeviceAddDetailsDto detailsDto : deviceAddDto.getDeviceAddDetailsDtos()) {
              if (deviceAddDto.getIMEIType()){
                  Device deviceSn1 = selectOne(new EntityWrapper<Device>().eq("sn1", detailsDto.getsN1()).eq("is_deleted", DeleteStatus.NOT_DELETED.getCode()));
                  if (Objects.isNull(deviceSn1)){
                      addResult.add(detailsDto.getsN2());
                  }else {
                      deviceSn1.setiMEI(detailsDto.getiMEI());
                      deviceSn1.setsN2(detailsDto.getsN2());
                      deviceSn1.setUtime(now);

                      deviceSn1.setOperatorId(sysUser.getJobNumber());
                      deviceSn1.setOperatorName(sysUser.getRealName());

                      insertOrUpdate(deviceSn1);
                  }
              }else{
                  Device devices = selectOne(new EntityWrapper<Device>().isNull("sn1").eq("is_deleted", DeleteStatus.NOT_DELETED.getCode()));
                  if (Objects.isNull(devices)){
                      addResult.add(detailsDto.getsN2());
                  }else {
                      devices.setsN2(detailsDto.getsN2());
                      devices.setUtime(now);

                      devices.setOperatorId(sysUser.getJobNumber());


                      insertOrUpdate(devices);
                  }
              }
            }
            if (addResult.size()>0){
                result = "以下设备添加失败:"+addResult.toString()+",原因："+LeaseExceEnums.DEVICE_DONT_EXISTS;
            }else {
                result = "添加成功";

            }
            return result;

        }

        //入库员
        /**
         * 入库员点击“开始扫码”，扫描设备的SN2码，再在扫码框填写“入库批次”；
         * 选择“产品型号”、“供应商”（由客户提供供应商，系统写死）；
         * 选择“入库仓库”、“入库时间”；填写“经办人”，
         * “备注”（例如备注“退货”，表明当前批次的设备是退货机）。
         * ”入库设备“记录在“库存管理-库存列表”中；
         * ”入库记录“在”库存管理-入库记录表“中查看。
         */
        if (sysUser.getIsAdmin().equals(SysUserType.CLERK.getCode())){
            for (DeviceAddDetailsDto detailsDto : deviceAddDto.getDeviceAddDetailsDtos()) {
                Device devicePut = selectOne(new EntityWrapper<Device>().eq("sn2", detailsDto.getsN2()).eq("is_deleted", DeleteStatus.NOT_DELETED.getCode()));
                //判断产品是否符合规则
                ProductCategory productCategory = productCategoryService.selectOne(new EntityWrapper<ProductCategory>().eq("category_type", deviceAddDto.getControlType()).eq("is_deleted", DeleteStatus.NOT_DELETED.getCode()));
                if (productCategory.getCategoryId() == null){
                    addResult.add(detailsDto.getsN2());
                    continue;
                }
                Product product = judgeProductIsExist(productCategory.getCategoryId());
                if (Objects.isNull(product)) {
                    addResult.add(detailsDto.getsN2());
                    continue;
                }
                devicePut.setUtime(now);
                insertOrUpdate(devicePut);
            }

            if (addResult.size()>0){
                result = "以下设备添加失败:"+addResult.toString()+",原因："+LeaseExceEnums.PRODUCT_DONT_EXISTS;
            }else {
                result = "添加成功";

            }

            return result;

        }
         return null;


    }

    /**
     * 返回含有二维码内容的device
     */
    @Override
    public Device createQrcodeAndAuthDevice(Device device, Product product, SysUser currentUser, DeviceQrcodeService.QrcodeListener qrcodeListener) {
        //根据当前登录用户获取公众号信息
        // SysUserExt sysUserExt = sysUserExtService.selectById(currentUser.getId());
        SysUserExt sysUserExt = new SysUserExt();
        Map<String, String> map = QrcodeUtil.createAndSaveQrcode(device, product, sysUserExt);
        if (map == null) LeaseException.throwSystemException(LeaseExceEnums.DEVICE_QRCODE_ERROR);
        //如果是生成微信二维码，则需要微信授权
        if (product.getQrcodeType().equals(QrcodeType.WEIXIN.getCode())) {
            //判断map中是否存在wxDid
            if (!map.containsKey("wxDid")) {
                LeaseException.throwSystemException(LeaseExceEnums.WEIXIN_DEVICE_ID_GET_ERROR);
            }
            device.setWxDid(String.valueOf(map.get("wxDid")));
            device.setWxTicket(String.valueOf(map.get("content")));
            deviceAuthResult(device, sysUserExt);
        } else if (product.getQrcodeType().equals(QrcodeType.WEB.getCode())) {
            device.setContentUrl(map.get("content"));
        }
        if (qrcodeListener != null && !qrcodeListener.doAfterCreateQrcode(String.valueOf(map.get("fileName")))) {
            LeaseException.throwSystemException(LeaseExceEnums.DEVICE_QRCODE_ERROR);
        }
        return device;
    }

    private Product judgeProductIsExist(Integer productId) {
        Product product = productService.selectById(productId);
        //如果产品被删除或者产品为空
        if (product.getIsDeleted().equals(1) || Objects.isNull(product)) {
            return null;
        }
        return product;
    }

    @Override
    public String getSno() {
        String sno = RandomStringUtils.random(20, false, true);
        if (judgeSnoIsExist(sno)) {
            sno = RandomStringUtils.random(20, false, true);
        }
        return sno;
    }


    private boolean judgeSnoIsExist(String sno) {
        EntityWrapper<Device> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("sno", sno).eq("is_deleted", DeleteStatus.NOT_DELETED.getCode());
        Device device = selectOne(entityWrapper);
        return !ParamUtil.isNullOrEmptyOrZero(device);
    }

    @Override
    public boolean judgeMacIsExsit(String mac) {
        return !ParamUtil.isNullOrEmptyOrZero(getDeviceByMac(mac));
    }

    /**
     * 获取指定设备的经纬度信息并更新到数据库
     */
    public Device locationDeviceAddress(String productKey, String mac) {
        Device device = getDeviceByMacAndPk(mac, productKey);
        if (Objects.isNull(device)) {
            logger.error("=====产品{}的设备{}不存在====", productKey, mac);
            return null;
        }
        Product product = productService.getProductByProductId(device.getProductId());
        if (Objects.isNull(product)) {
            logger.error("====产品ID为{}的产品PK{}不存在=====", device.getProductId(), productKey);
            return null;
        }
        DeviceAddressModel deviceAddressModel = null;
        if (product.getLocationType().equals(LocationType.GD.getCode())) {
            logger.info("====使用高德地图获取设备经纬度======");
            deviceAddressModel = getDeviceAddressByGD(product.getGizwitsProductKey(), device.getMac(), product.getNetworkType());
        } else if (product.getLocationType().equals(LocationType.GIZWITS.getCode())) {
            logger.info("====使用机智云接口获取设备经纬度======");
            if (StringUtils.isBlank(device.getGizDid())) {
                logger.error("====设备{}的GizDid为空,无法使用机智云接口获取设备经纬度====", device.getSno());
                return device;
            }
            deviceAddressModel = getDeviceAddressByGizwitsAPI(product.getGizwitsProductKey(), device.getGizDid(), device.getMac());
        }

        if (Objects.nonNull(deviceAddressModel)) {
            logger.info("====获取经纬度不为空======");
            //定位后做额外操作
            CommonEventPublisherUtils.publishEvent(new DeviceLocationEvent("DeviceLocation", deviceAddressModel, device));

            device.setLatitude(deviceAddressModel.getLatitude());
            device.setLongitude(deviceAddressModel.getLongitude());
            device.setCountry(deviceAddressModel.getCountry());
            device.setProvince(deviceAddressModel.getProvince());
            device.setCity(deviceAddressModel.getCity());

            device.setUtime(new Date());
            updateById(device);
        }

        return device;
    }

    @Override
    public DeviceForDetailDto detail1(String id) {
        DeviceForDetailDto detailDto = detail(id);
//        detailDto.setWorkStatusDesc(getDeviceWorkStatus(selectById(id)));
        return detailDto;
    }


    @Override
    public DeviceForDetailDto detail(String id) {
        Device device = selectById(id);
        if (Objects.isNull(device)) {
            LeaseException.throwSystemException(LeaseExceEnums.ENTITY_NOT_EXISTS);
        }
        Product product = productService.getProductByProductId(device.getProductId());
        device = locationDeviceAddress(product.getGizwitsProductKey(), device.getMac());

        if (Objects.isNull(device)) {
            LeaseException.throwSystemException(LeaseExceEnums.ENTITY_NOT_EXISTS);
        }
        DeviceForDetailDto result = new DeviceForDetailDto(device);
        result.setWorkStatusDesc(DeviceStatus.getShowName(device.getWorkStatus(), device.getFaultStatus(), device.getLock()));
//        if (device.getFaultStatus().equals(DeviceStatus.FAULT.getCode())) {
//            result.setWorkStatus(device.getFaultStatus());
//        }

        result.setImei(device.getiMEI());
        result.setSn1(device.getsN1());
        result.setStatus(device.getStatus());
        result.setProductKey(product.getGizwitsProductKey());
        result.setLock(device.getLock());
        result.setOnlineStatusDesc(DeviceStatus.getName(result.getOnlineStatus()));
        //获取经销商
        if(device.getAgentId()!=null) {
            Agent agent = agentService.getAgentBySysAccountId(device.getAgentId());
            if (!ParamUtil.isNullOrEmptyOrZero(agent)) {
                result.setBelongOperatorName(agent.getName());
            }
        }
//        result.setServiceMode(resolveServiceMode(device.getServiceId()));
        //查看设备的所有收费模式
//        List<Integer> modeIds = deviceToProductServiceModeService.resolveServiceModeIdBySno(id);
//        if (!ParamUtil.isNullOrEmptyOrZero(modeIds)) {
//            List<ProductServiceDetailForDeviceDto> serviceDetailForDeviceDtos = new ArrayList<>();
//            for (Integer modeId : modeIds) {
//                serviceDetailForDeviceDtos.add(resolveServiceMode(modeId));
//            }
//            result.setServiceMode(serviceDetailForDeviceDtos);
//        }
//        result.setLaunchArea(resolveLaunchArea(device.getLaunchAreaId()));
//        result.setCanModifyLaunchArea(deviceLaunchAreaAssignService.canModify(device.getLaunchAreaId()));
//        result.setBelongOperatorName(resolveOwner(device.getOwnerId()));
//        result.setControlCommands(getDeviceControlCommandWithNowStatus(product, device.getMac()));



//        //查看是否有权限修改
//        SysUser userOwner = sysUserService.getCurrentUserOwner();
//        //查看是否能够添加多个收费模式
//        result.setCanAddMoreServiceMode(sysUserService.resolveAddMoreServiceMode(userOwner));
//        if (Objects.equals(device.getOwnerId(), userOwner.getId())) {
//            result.setCanModify(true);
//            result.setCanModifyLaunchArea(true);
////            result.setCanModifyServiceMode(true);
//        }
//        result.setCanModifyServiceMode(true);
        return result;
    }


    @Override
    public ManageDeviceDetailDto getDeviceDetail(String sno) {
        Device device = selectById(sno);
        if (device == null) {
            LeaseException.throwSystemException(LeaseExceEnums.DEVICE_DONT_EXISTS);
        }
        DeviceShowDto dto = getDeviceShowDto(device);
        ManageDeviceDetailDto result = new ManageDeviceDetailDto();
        BeanUtils.copyProperties(dto, result);
        result.setLongitude(device.getLongitude());
        result.setLatitude(device.getLatitude());
        return result;
    }

    @Override
    public DeviceForDetailDto detailForApp(String id) {
        if (ParamUtil.isNullOrEmptyOrZero(id)){
            LeaseException.throwSystemException(LeaseExceEnums.PARAMS_ERROR,"未传入设备sno");
        }
        Device device = selectById(id);

        if (Objects.isNull(device)) {
            LeaseException.throwSystemException(LeaseExceEnums.ENTITY_NOT_EXISTS,"设备不存在，sno = "+id);
        }

        Product product = productService.getProductByProductId(device.getProductId());
        DeviceForDetailDto result = new DeviceForDetailDto(device);
        result.setWorkStatusDesc(DeviceStatus.getShowName(device.getWorkStatus(), device.getFaultStatus(), device.getLock()));
//        if (device.getFaultStatus().equals(DeviceStatus.FAULT.getCode())) {
//            result.setWorkStatus(device.getFaultStatus());
//        }

        result.setProductKey(product.getGizwitsProductKey());
        result.setOnlineStatusDesc(DeviceStatus.getName(result.getOnlineStatus()));
        //查看设备的所有收费模式
        List<Integer> modeIds = deviceToProductServiceModeService.resolveServiceModeIdBySno(id);
        if (!ParamUtil.isNullOrEmptyOrZero(modeIds)) {
            List<ProductServiceDetailForDeviceDto> serviceDetailForDeviceDtos = new ArrayList<>();
            for (Integer modeId : modeIds) {
                serviceDetailForDeviceDtos.add(resolveServiceMode(modeId));
            }
            result.setServiceMode(serviceDetailForDeviceDtos);
        }
        result.setLaunchArea(resolveLaunchArea(device.getLaunchAreaId()));
//        result.setCanModifyLaunchArea(deviceLaunchAreaAssignService.canModify(device.getLaunchAreaId()));
        result.setBelongOperatorName(resolveOwner(device.getOwnerId()));
        result.setControlCommands(getDeviceControlCommandWithNowStatus(product, device.getMac()));


        return result;
    }

    private List<ProductCommandForDeviceDetailDto> getDeviceControlCommandWithNowStatus(Product product, String mac) {
        List<ProductCommandConfig> commandConfigList = productCommandConfigService.selectList(
                new EntityWrapper<ProductCommandConfig>()
                        .eq("product_id", product.getId())
                        .eq("command_type", CommandType.CONTROL.getCode())
                        .eq("is_deleted", DeleteStatus.NOT_DELETED.getCode())
                        .eq("is_show", ShowType.DISPLAY.getCode()));
        if (CollectionUtils.isNotEmpty(commandConfigList)) {
            List<ProductCommandForDeviceDetailDto> result = new ArrayList<>();
            for (ProductCommandConfig commandConfig : commandConfigList) {
                ProductCommandForDeviceDetailDto deviceDetailDto = new ProductCommandForDeviceDetailDto(commandConfig);
                if (redisService.containsDeviceCurrentStatusData(product.getGizwitsProductKey(), mac)) {
                    JSONObject cacheData = redisService.getDeviceCurrentStatus(product.getGizwitsProductKey(), mac);
                    JSONObject command = JSONObject.parseObject(commandConfig.getCommand());
                    if (command.containsKey("displayName")) {
                        String displayName = command.getString("displayName");
                        deviceDetailDto.setValue(cacheData.get(displayName));
                    }
                }
                result.add(deviceDetailDto);
            }
            return result;
        }

        return null;
    }

    private void resolveModifyLaunchArea(DeviceForDetailDto result) {
        if (Objects.isNull(result.getLaunchArea())) {
            result.setCanModifyLaunchArea(true);
        } else {

        }
    }

    @Override
    public boolean update(DeviceForUpdateDto dto) {
        Device exist = selectById(dto.getSno());
        EntityWrapper<Device> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("mac", dto.getMac()).eq("is_deleted", DeleteStatus.NOT_DELETED.getCode()).ne("sno", exist.getSno());
        Device device = selectOne(entityWrapper);
        if (Objects.nonNull(device)) {
            LeaseException.throwSystemException(LeaseExceEnums.DEVICE_MAC_EXISTS);
        }

        exist.setUtime(new Date());
        resolveBasic(exist, dto);
        resolveOperation(exist, dto);
        return updateAllColumnById(exist);
    }

    @Override
    public String DoesItAlreadyExist(String sno , String mac, String sn1 , String sn2, String iMEI){
        if (Objects.isNull(sno)) {
            LeaseException.throwSystemException(LeaseExceEnums.DEVICE_DONT_EXISTS);
        }

        if (selectCount(new EntityWrapper<Device>().eq("mac",mac)
                .eq("is_deleted", DeleteStatus.NOT_DELETED.getCode()).ne("sno", sno))>0){
            return LeaseExceEnums.DEVICE_MAC_EXISTS.getMessage();
        }
        if (sn1!=null && !sn1.equals("")){
            if (selectCount(new EntityWrapper<Device>().eq("sn1",sn1)
                    .eq("is_deleted", DeleteStatus.NOT_DELETED.getCode()).ne("sno", sno))>0){
                return LeaseExceEnums.DEVICE_SN1_EXISTS.getMessage();
            }
        }
        if (sn2!=null && !sn2.equals("")){
            if (selectCount(new EntityWrapper<Device>().eq("sn2",sn2)
                    .eq("is_deleted", DeleteStatus.NOT_DELETED.getCode()).ne("sno", sno))>0){
                return LeaseExceEnums.DEVICE_SN2_EXISTS.getMessage();
            }
        }
        if (sn2!=null && !sn2.equals("")){
            if (selectCount(new EntityWrapper<Device>().eq("imei",iMEI)
                    .eq("is_deleted", DeleteStatus.NOT_DELETED.getCode()).ne("sno", sno))>0){
                return LeaseExceEnums.DEVICE_IMEI_EXISTS.getMessage();
            }
        }
        return "";
    }

    /**
     * 用于移动端设备上报经纬度信息时更新
     *
     * @param deviceLocationDto
     * @return
     */
    @Override
    public void updateDeviceLocation(AppUpdateDeviceLocationDto deviceLocationDto) {
        Device device = getDeviceByMac(deviceLocationDto.getMac());
        if (!ParamUtil.isNullOrEmptyOrZero(device)) {
            device.setLatitude(deviceLocationDto.getLatitude());
            device.setLongitude(deviceLocationDto.getLongitude());
            device.setUtime(new Date());
            updateById(device);
        } else {
            logger.info("设备 mac：{}不存在", deviceLocationDto.getMac());
        }
    }

    @Override
    public int deviceAuthResult(Device device, SysUserExt sysUserExt) {
        List<DeviceAuth> deviceAuths = new ArrayList<>();
        //由于微信绑定设备需要记录mac并且长度不能长于16的16进制的字符串
        String mac = device.getMac().substring(device.getMac().length() - 12, device.getMac().length());
        DeviceAuth deviceAuth = new DeviceAuth(device.getWxDid(), mac);
        deviceAuths.add(deviceAuth);
        String res = userWeixinService.authorizeDevice(deviceAuths, sysUserExt);
        logger.info("设备" + device.getSno() + "," + device.getWxDid() + "微信授权结果" + res);

        JSONObject jsonRes = JSON.parseObject(res);
        int errcode = 1;
        if (jsonRes.containsKey("resp")) {
            errcode = jsonRes.getJSONArray("resp").getJSONObject(0)
                    .getInteger("errcode");
        }
        return errcode;
    }

    @Override
    public List<DeviceShowDto> getDeviceByGroupId(Integer groupId) {
        List<DeviceGroupToDevice> deviceGroupToDevices = deviceGroupToDeviceService.selectList(new EntityWrapper<DeviceGroupToDevice>().eq("device_group_id", groupId));
        if (CollectionUtils.isNotEmpty(deviceGroupToDevices)) {
            Set<String> deviceSnos = deviceGroupToDevices.stream().map(DeviceGroupToDevice::getDeviceSno).collect(Collectors.toSet());
            List<Device> devices = selectBatchIds(new ArrayList<>(deviceSnos));
            return devices.stream().map(this::getDeviceShowDto).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public BatchDeviceWebSocketVo getInfoForControlBatchDevice(BatchDevicePageDto data) {
        SysUser currentUser = sysUserService.getCurrentUser();
        if (Objects.isNull(currentUser)) {
            LeaseException.throwSystemException(LeaseExceEnums.USER_DONT_EXISTS);
        }
        List<String> snoList = data.getSnoList();
        if (Objects.isNull(snoList) || snoList.size() == 0) {
            LeaseException.throwSystemException(LeaseExceEnums.DEVICE_DONT_EXISTS);
        }
        List<Device> deviceList = selectBatchIds(snoList);
        if (Objects.isNull(deviceList) || deviceList.size() == 0) {
            LeaseException.throwSystemException(LeaseExceEnums.DEVICE_DONT_EXISTS);
        }
        if (ParamUtil.isNullOrZero(deviceList.get(0).getProductId())) {
            LeaseException.throwSystemException(LeaseExceEnums.PRODUCT_DONT_EXISTS);
        }
        Product product = productService.selectById(deviceList.get(0).getProductId());
        if (Objects.isNull(product)) {
            LeaseException.throwSystemException(LeaseExceEnums.PRODUCT_DONT_EXISTS);
        }

        //根据product找到数据点
        List<ProductDataPoint> dts = productDataPointService.selectList(new EntityWrapper<ProductDataPoint>().eq("product_id", product.getId()));
        if (dts.size() == 0 || Objects.isNull(dts)) {
            LeaseException.throwSystemException(LeaseExceEnums.DATA_POINT_ILLEGAL_PARAM);
        }
        List<ProductDataPointDto> productDataPointDtos = new ArrayList<>();
        for (ProductDataPoint dt : dts) {
            productDataPointDtos.add(new ProductDataPointDto(dt));
        }

        String userToken = redisService.getUserTokenByUserName(currentUser.getId() + "");
        String uid = redisService.getUidByUsername(currentUser.getId() + "");
        if (StringUtils.isEmpty(userToken) || StringUtils.isEmpty(uid)) {//该操作只操作userToken和uid，其他passcode和host都不处理
            String res = GizwitsUtil.createUser(currentUser.getId() + "", product.getGizwitsAppId());
            JSONObject json = JSONObject.parseObject(res);
            redisService.setUserTokenByUsername(currentUser.getId() + "", String.valueOf(json.get("uid")),
                    String.valueOf(json.get("token")),
                    Long.valueOf(String.valueOf(json.get("expire_at"))));
            userToken = String.valueOf(json.get("token"));
            uid = String.valueOf(json.get("uid"));
        }
        String host = "sandbox.gizwits.com";
        String port = "8080";
        List<String> didList = new ArrayList<>();
        for (int i = 0; i < deviceList.size(); ++i) {
            Device device = deviceList.get(i);
            String res = GizwitsUtil.bindDevice(device.getMac(), product, userToken);
            JSONObject json = JSONObject.parseObject(res);
            if (!Objects.isNull(json.get("host")) && !Objects.isNull(json.get("ws_port"))) {
                host = String.valueOf(json.get("host"));
                port = String.valueOf(json.get("ws_port"));
            }
            didList.add(device.getGizDid());
        }
        BatchDeviceWebSocketVo batchDeviceWebSocketVo = new BatchDeviceWebSocketVo();
        batchDeviceWebSocketVo.setUid(uid);
        batchDeviceWebSocketVo.setToken(userToken);
        batchDeviceWebSocketVo.setBatchDeviceDetailWebSocketVo(new BatchDeviceDetailWebSocketVo(didList, port, port, host));
        batchDeviceWebSocketVo.setGizAppId(product.getGizwitsAppId());
        batchDeviceWebSocketVo.setDataPoints(productDataPointDtos);
        return batchDeviceWebSocketVo;
    }

    @Override
    public String getSnoByOpenid(String openid) {
        String sno = deviceDao.getSnoByOpenid(openid, OrderStatus.USING.getCode());
        return sno;

    }

    /**
     * 兼容续费单的判断
     *
     * @param sno
     * @param canRent
     */
    @Override
    public void checkDeviceIsRenting(String sno, boolean canRent) {
        Device device = getDeviceInfoBySno(sno);
        if (Objects.isNull(device)) {
            LeaseException.throwSystemException(LeaseExceEnums.DEVICE_DONT_EXISTS);
        }
        if (!canRent) {
            if (DeviceStatus.USING.getCode().equals(device.getWorkStatus())
                    || DeviceStatus.OFFLINE.getCode().equals(device.getOnlineStatus())) {
                LeaseException.throwSystemException(LeaseExceEnums.DEVICE_INUSING_FAIL);
            }
        } else {
            if (DeviceStatus.OFFLINE.getCode().equals(device.getOnlineStatus())) {
                LeaseException.throwSystemException(LeaseExceEnums.DEVICE_OFFLINE);
            }
        }
        //投放点判断
        DeviceLaunchArea deviceLaunchArea = deviceLaunchAreaService.getLaunchAreaInfoById(device.getLaunchAreaId());
        if (Objects.isNull(deviceLaunchArea)) {
            LeaseException.throwSystemException(LeaseExceEnums.DEVICE_WITHOUT_LAUNCH_AREA);
        }
        //运营商运营
        SysUserExt sysUserExt = getWxConfigByDeviceId(sno);
        if (Objects.isNull(sysUserExt)) {
            LeaseException.throwSystemException(LeaseExceEnums.DEVICE_NOT_IN_OPERATOR);
        }

    }


    @Override
    public Boolean checkDeviceIfUsedByOpenid(Device device, String openId) {
        if (Objects.isNull(device)) {
            LeaseException.throwSystemException(LeaseExceEnums.DEVICE_DONT_EXISTS);
        }
        OrderBase orderBase = orderBaseService.getUsingOrderByOpenid(device.getSno(), openId);
        return !Objects.isNull(orderBase) && device.getSno().equals(orderBase.getSno());
    }

    private void resolveOperation(Device exist, DeviceForUpdateDto dto) {
        boolean has = false;
        if (Objects.nonNull(dto.getServiceId()) && Objects.nonNull(dto.getServiceName())
                && !dto.getServiceId().equals(exist.getServiceId())) {
            has = true;
            checkDeviceServiceMode(exist, dto.getServiceId());
            exist.setServiceId(dto.getServiceId());
            exist.setServiceName(dto.getServiceName());
        }
        if (Objects.nonNull(dto.getLaunchAreaId()) && Objects.nonNull(dto.getLaunchAreaName())
                && !dto.getLaunchAreaId().equals(exist.getLaunchAreaId())) {
            has = true;
            exist.setLaunchAreaId(dto.getLaunchAreaId());
            exist.setLaunchAreaName(dto.getLaunchAreaName());
        }
//        if (!Objects.equals(dto.getInstallUserName(), exist.getInstallUserName())) {
//            has = true;
//            exist.setInstallUserName(dto.getInstallUserName());
//        }
//        if (!Objects.equals(dto.getInstallUserProvince(), exist.getInstallUserProvince())) {
//            has = true;
//            exist.setInstallUserProvince(dto.getInstallUserProvince());
//        }
//        if (!Objects.equals(dto.getInstallUserCity(), exist.getInstallUserCity())) {
//            has = true;
//            exist.setInstallUserCity(dto.getInstallUserCity());
//        }
//        if (!Objects.equals(dto.getInstallUserArea(), exist.getInstallUserArea())) {
//            has = true;
//            exist.setInstallUserArea(dto.getInstallUserArea());
//        }
//        if (!Objects.equals(dto.getInstallUserAddress(), exist.getInstallUserAddress())) {
//            has = true;
//            exist.setInstallUserAddress(dto.getInstallUserAddress());
//        }
//        if (!Objects.equals(dto.getInstallUserMobile(), exist.getInstallUserMobile())) {
//            has = true;
//            exist.setInstallUserMobile(dto.getInstallUserMobile());
//        }
        if (has) {
            publishEvent(exist.getSno(), ProductOperateType.DEVICE_OPERATION);
        }
    }

    private void checkDeviceServiceMode(Device device, Integer newServiceModeId) {
        //判断新的收费模式是否是免费模式
        EntityWrapper<ProductServiceMode> serviceModeEntityWrapper = new EntityWrapper<>();
        serviceModeEntityWrapper.eq("id", newServiceModeId).eq("is_deleted", DeleteStatus.NOT_DELETED.getCode());
        ProductServiceMode productServiceMode = productServiceModeService.selectOne(serviceModeEntityWrapper);
        if (Objects.isNull(productServiceMode)) {
            logger.error("=====收费模式{}不存在=====", newServiceModeId);
            LeaseException.throwSystemException(LeaseExceEnums.ENTITY_NOT_EXISTS);
        }
        if (StringUtils.isNotBlank(productServiceMode.getCommand())) {
            Product product = productService.getProductByProductId(device.getProductId());
            if (StringUtils.isBlank(device.getGizDid())
                    || !DeviceControlAPI.remoteControl(product.getGizwitsProductKey(), device.getGizDid(), productServiceMode.getCommand())) {
                logger.warn("====设备{}修改收费模式为{}并下发指令{}失败,将指令放入缓存,等设备上线后下发=====", device.getSno(), productServiceMode.getId(), productServiceMode.getCommand());
                redisService.cacheDeviceControlCommand(product.getGizwitsProductKey(), device.getMac(), productServiceMode.getCommand());
            }
        }
        if (productServiceMode.getIsFree().equals(BooleanEnum.FALSE.getCode())) {//收费的设置不需要判断上级设置情况
            return;
        }

        //对于免费的收费模式需要判断上级给当前设备设置的收费模式类型
        EntityWrapper<DeviceServiceModeSetting> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("sno", device.getSno()).eq("assign_account_id", device.getOwnerId()).eq("is_deleted", BooleanEnum.FALSE.getCode());
        DeviceServiceModeSetting deviceServiceModeSetting = deviceServiceModeSettingService.selectOne(entityWrapper);
        if (Objects.nonNull(deviceServiceModeSetting)) {
            if (deviceServiceModeSetting.getIsFree().equals(BooleanEnum.FALSE.getCode())) {//上级给设备设置的是收费类型而非免费,禁止用户修改设备收费模式
                LeaseException.throwSystemException(LeaseExceEnums.DEVICE_PARENT_SERVICE_MODE_SETTING_LIMIT);
            }
        }
    }

    private void resolveBasic(Device exist, DeviceForUpdateDto dto) {
        if (Objects.nonNull(dto.getMac()) && Objects.nonNull(dto.getName())) {
            if (!dto.getMac().equals(exist.getMac())) {
                int macCount = selectCount(new EntityWrapper<Device>().eq("is_deleted", 0).eq("mac", dto.getMac()));
                if (macCount > 0) {
                    LeaseException.throwSystemException(LeaseExceEnums.DEVICE_MAC_EXISTS);
                }
                exist.setMac(dto.getMac());
            }
            exist.setName(dto.getName());
            publishEvent(exist.getSno(), ProductOperateType.DEVICE_BASIC);
        }
    }

    private void publishEvent(String deviceSno, ProductOperateType operateType) {
        productDeviceChangeService.publishChangeEvent(deviceSno, operateType);
    }

    private DeviceLaunchAreaForDeviceDto resolveLaunchArea(Integer launchAreaId) {
        if (Objects.nonNull(launchAreaId)) {
            DeviceLaunchArea deviceLaunchArea = deviceLaunchAreaService.selectById(launchAreaId);
            if (Objects.nonNull(deviceLaunchArea)) {
                return new DeviceLaunchAreaForDeviceDto(deviceLaunchArea);
            }
        }
        return null;
    }

    private ProductServiceDetailForDeviceDto resolveServiceMode(Integer serviceId) {
        if (Objects.nonNull(serviceId)) {
            ProductServiceMode mode = productServiceModeService.selectById(serviceId);
            if (Objects.nonNull(mode)) {
                ProductServiceDetailForDeviceDto dto = new ProductServiceDetailForDeviceDto(mode);
                dto.setPriceAndNumDtoList(productServiceDetailService.getProductPriceDetailByServiceModeId(serviceId));
                return dto;
            }
        }
        return null;
    }

    @Override
    public Integer countByServiceId(Integer serviceId) {
        SysUser sysUser = sysUserService.getCurrentUserOwner();
        EntityWrapper<DeviceToProductServiceMode> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("service_mode_id", serviceId)
                .in("sys_user_id", sysUserService.resolveSysUserAllSubAdminIds(sysUser))
                .eq("is_deleted", DeleteStatus.NOT_DELETED.getCode());
        return deviceToProductServiceModeService.selectCount(entityWrapper);
    }

    @Override
    public Page<DeviceShowDto> listPage(Pageable<DeviceQueryDto> pageable) {
        DeviceQueryDto queryDto=pageable.getQuery()==null?new DeviceQueryDto():pageable.getQuery();

        Integer current=(pageable.getCurrent()-1)*pageable.getSize();
        Integer size=pageable.getSize();
        queryDto.setCurrent(current);
        queryDto.setSize(size);
        Page<DeviceShowDto> result = new Page<>();
        List<Device> devices=deviceDao.selectDevices(queryDto);
        if(ParamUtil.isNullOrEmptyOrZero(devices)){
            return result;
        }
        Integer count=deviceDao.selectDevicesCount(queryDto);
        List<DeviceShowDto> list = getDeviceShowDtos(devices);
        result.setRecords(list);
        result.setTotal(count);
        return result;
    }

    private List<DeviceShowDto> getDeviceShowDtos(List<Device> devices) {
        List<DeviceShowDto> list = new ArrayList<>(devices.size());
        for (Device device : devices) {
            DeviceShowDto showDto = getDeviceShowDto(device);
            showDto.setWorkStatusDesc(getDeviceWorkStatus(device));
            list.add(showDto);
        }
        return list;
    }

    private List<DeviceShowDto> getDeviceShowDtos2(List<Device> devices) {
        List<DeviceShowDto> list = new ArrayList<>(devices.size());
        for (Device device : devices) {
            DeviceShowDto showDto = getDeviceShowDto(device);
            showDto.setWorkStatusDesc(getGDMSDeviceWorkStatus(device));
            list.add(showDto);
        }
        return list;
    }

    @Override
    public Page<DeviceShowDto> getListForManage(SysUser sysUser, Pageable<DeviceQueryDto> pageable) {
        Page<DeviceShowDto> result = new Page<>();
        BeanUtils.copyProperties(pageable, result);

        List<DeviceLaunchArea> deviceLaunchAreaList = deviceLaunchAreaService.getListByMaintainerId(sysUser.getId());
        if (CollectionUtils.isEmpty(deviceLaunchAreaList)) {
            result.setRecords(Collections.emptyList());
            return result;
        }
        List<Integer> deviceLaunchAreaIdList = deviceLaunchAreaList.stream().map(DeviceLaunchArea::getId).collect(Collectors.toList());
        if (pageable.getQuery() == null) {
            pageable.setQuery(new DeviceQueryDto());
        }
//        pageable.getQuery().setDeviceLaunchAreaIdList(deviceLaunchAreaIdList);

        Page<Device> page = new Page<>();
        BeanUtils.copyProperties(pageable, page);
        page = selectPage(page, QueryResolverUtils.parse(pageable.getQuery(), new EntityWrapper<>()));
        BeanUtils.copyProperties(page, result);
        result.setRecords(page.getRecords().stream().map(item -> getDeviceShowDto(item)).collect(Collectors.toList()));
        return result;
    }

    @Override
    public int getDeviceCountForManage(SysUser sysUser) {
        List<DeviceLaunchArea> deviceLaunchAreaList = deviceLaunchAreaService.getListByMaintainerId(sysUser.getId());
        if (CollectionUtils.isEmpty(deviceLaunchAreaList)) {
            return 0;
        }
        List<Integer> deviceLaunchAreaIdList = deviceLaunchAreaList.stream().map(DeviceLaunchArea::getId).collect(Collectors.toList());
        Wrapper<Device> wrapper = new EntityWrapper();
        wrapper.in("launch_area_id", deviceLaunchAreaIdList);
        wrapper.eq("is_deleted", DeleteStatus.NOT_DELETED.getCode());
        wrapper.ne("status", DeviceNormalStatus.WAIT_TO_ENTRY.getCode());
        return selectCount(wrapper);
    }

    @Override
    public Page<DeviceShowDto> currentListPage(Pageable<DeviceQueryDto> pageable, Integer type) {

        Page<Device> page = new Page<>();
        BeanUtils.copyProperties(pageable, page);

        Wrapper<Device> wrapper = new EntityWrapper<>();
        wrapper.orderBy("last_online_time", false);
        switch (type) {
            case 1:
                wrapper.isNull("launch_area_name");
                break;
            case 2:
                wrapper.isNotNull("launch_area_name");
                break;
            default:
                break;
        }

        Page<Device> page1 = selectPage(page,
                QueryResolverUtils.parse(pageable.getQuery(), wrapper));
        List<Device> devices = page1.getRecords();
        Page<DeviceShowDto> result = new Page<>();
        BeanUtils.copyProperties(page1, result);
        List<DeviceShowDto> list = getDeviceShowDtos(devices);
        result.setRecords(list);
        return result;
    }

    private String getServiceModeForDevice(Device device) {
        List<Integer> modeIds = deviceToProductServiceModeService.resolveServiceModeIdBySno(device.getSno());
        if (!ParamUtil.isNullOrEmptyOrZero(modeIds)) {
            List<String> modeNameList = modeIds.stream().map(modeId -> {
                ProductServiceMode mode = productServiceModeService.selectById(modeId);
                if (mode == null) {
                    return null;
                }
                return mode.getName();
            }).collect(Collectors.toList());
            return StringUtils.join(modeNameList, ",");
        }
        return null;
    }

    private DeviceShowDto getDeviceShowDto(Device device) {
        DeviceShowDto deviceShowDto = new DeviceShowDto();
        deviceShowDto.setSno(device.getSno());
        deviceShowDto.setMac(device.getMac());
        deviceShowDto.setsN1(device.getsN1());
        deviceShowDto.setsN2(device.getsN2());
        deviceShowDto.setiMEI(device.getiMEI());
        deviceShowDto.setName(device.getName());
        deviceShowDto.setRemarks(device.getRemarks());
        deviceShowDto.setCtime(device.getCtime());
        deviceShowDto.setLastOnLineTime(device.getLastOnlineTime());
        deviceShowDto.setProduct(device.getProductName());
        deviceShowDto.setLaunchArea(device.getLaunchAreaName());
        String serviceMode = getServiceModeForDevice(device);
        if (serviceMode == null) {
            deviceShowDto.setServiceMode(device.getServiceName());
        } else {
            deviceShowDto.setServiceMode(serviceMode);
        }
        deviceShowDto.setLock(device.getLock());
        String onLineStatus = DeviceOnlineStatus.getName(device.getOnlineStatus());
        deviceShowDto.setWorkStatus(device.getWorkStatus());
        String workStatus = DeviceStatus.getShowName(device.getWorkStatus(), device.getFaultStatus(), device.getLock());
        deviceShowDto.setWorkStatusDesc(workStatus);
        String status = DeviceStatus.getName(device.getStatus());
        deviceShowDto.setOnlineStatus(onLineStatus);
        deviceShowDto.setActivateStatus(device.getActivateStatus());
        deviceShowDto.setActivateStatusDesc(DeviceActiveStatusType.getName(device.getActivateStatus()));
        deviceShowDto.setOwnerName(device.getOwnerName());

        DeviceLaunchArea deviceLaunchArea = deviceLaunchAreaService.getLaunchAreaInfoById(device.getLaunchAreaId());
        if (!ParamUtil.isNullOrEmptyOrZero(deviceLaunchArea)) {
            deviceShowDto.setProvince(deviceLaunchArea.getProvince());
            deviceShowDto.setAddress(deviceLaunchArea.getAddress());
            deviceShowDto.setCity(deviceLaunchArea.getCity());
            deviceShowDto.setArea(deviceLaunchArea.getArea());
        }

        deviceShowDto.setStatus(status);
        return deviceShowDto;
    }

    private String getDeviceWorkStatus(Device device) {
        String workStatus = DeviceStatus.getShowName(device.getWorkStatus(), device.getFaultStatus(), device.getLock());
        return workStatus;
    }

    private String getGDMSDeviceWorkStatus(Device device) {
        String workStatus = DeviceStatus.getShowName(device.getWorkStatus(), device.getFaultStatus(), device.getLock());
        if (device.getFaultStatus().equals(DeviceStatus.NORMAL.getCode())) {
            workStatus = DeviceStatus.NORMAL.getName();
        }
        return workStatus;
    }

    @Override
    public String resolveOwner(Integer ownerId) {
        if (Objects.nonNull(ownerId)) {

            Agent agent = agentService.selectOne(new EntityWrapper<Agent>().eq("sys_account_id", ownerId).eq("is_deleted", DeleteStatus.NOT_DELETED.getCode()));
            if (Objects.nonNull(agent)) {
                return agent.getName();
            }
        }
        return null;
    }


    @Override
    public String deleteDevice(List<String> snos) {
        List<String> fails = new LinkedList<>();
        StringBuilder sb = new StringBuilder();
        Date date=new Date();
        List<Device> devices = selectList(new EntityWrapper<Device>().in("sno", snos).eq("is_deleted", DeleteStatus.NOT_DELETED.getCode()));
        for (Device device : devices) {
           //判断设备是否被app用户绑定
            UserBindDevice userBindDevice=userBindDeviceService.selectOne(new EntityWrapper<UserBindDevice>().eq("mac",device.getMac()).eq("is_deleted",0));
            if(!ParamUtil.isNullOrEmptyOrZero(userBindDevice)){
                //存在就解绑
                userBindDevice.setUtime(date);
                userBindDevice.setIsDeleted(DeleteStatus.DELETED.getCode());
                userBindDeviceService.updateById(userBindDevice);
            }
            device.setUtime(date);
            device.setIsDeleted(DeleteStatus.DELETED.getCode());
            updateById(device);
        }
        sb.append("删除成功");
        return sb.toString();
    }



    @Override
    public List<Device> listDeviceByOperatorId(List<Integer> operatorIds) {
        EntityWrapper<Device> entityWrapper = new EntityWrapper<>();
        entityWrapper.in("operator_id", operatorIds).eq("is_deleted", DeleteStatus.NOT_DELETED.getCode());
        return selectList(entityWrapper);
    }

    public DeviceWebSocketVo getInfoForControlDevice(DevicePageDto data) {
        //不断的获取orderNo的订单的状态是否为USING
        OrderBase orderBase = orderBaseService.selectById(data.getOrderNo());
        //如果是退款就返回错误信息
        if (OrderStatus.REFUNDING.getCode().equals(orderBase.getOrderStatus())
                || OrderStatus.REFUND_FAIL.getCode().equals(orderBase.getOrderStatus())
                || OrderStatus.REFUNDED.getCode().equals(orderBase.getOrderStatus())) {
            LeaseException.throwSystemException(LeaseExceEnums.DEVICE_START_FAIL);
        }
        if (!orderBase.getOrderStatus().equals(OrderStatus.USING.getCode())) {
            logger.warn("===============>订单状态" + orderBase.getOrderStatus() + "不符合获取websocket信息");
            return null;
        }
        //如果为USING将设备的gizwits的内容返回出去
        Device device = getDeviceInfoBySno(data.getSno());
        if (Objects.isNull(device) || device.getIsDeleted() == 1) {
            LeaseException.throwSystemException(LeaseExceEnums.DEVICE_DONT_EXISTS);
        }
        //如订单的设备号与设备号不一样
        if (!orderBase.getSno().equals(device.getSno())) {
            LeaseException.throwSystemException(LeaseExceEnums.PARAMS_ERROR);
        }
        //根据sno查找product
        Product product = productService.getProductByProductId(device.getProductId());
        if (Objects.isNull(product) || product.getIsDeleted() == 1) {
            LeaseException.throwSystemException(LeaseExceEnums.PRODUCT_DONT_EXISTS);
        }
        //根据product找到数据点
        List<ProductDataPoint> dts = productDataPointService.selectList(new EntityWrapper<ProductDataPoint>().eq("product_id", product.getId()));
        if (dts.size() == 0 || Objects.isNull(dts)) {
            LeaseException.throwSystemException(LeaseExceEnums.DATA_POINT_ILLEGAL_PARAM);
        }
        List<ProductDataPointDto> productDataPointDtos = new ArrayList<>();
        for (ProductDataPoint dt : dts) {
            productDataPointDtos.add(new ProductDataPointDto(dt));
        }
        User user = userService.getUserByIdOrOpenidOrMobile(orderBase.getUserId() + "");
        String userToken = redisService.getUserTokenByUserName(user.getUsername());
        String uid = redisService.getUidByUsername(user.getUsername());
        //如果从redis中获取不到，则重新获取
        if (StringUtils.isEmpty(userToken) || StringUtils.isEmpty(uid)) {//该操作只操作userToken和uid，其他passcode和host都不处理
            String res = GizwitsUtil.createUser(user.getUsername(), product.getGizwitsAppId());
            JSONObject json = JSONObject.parseObject(res);
            redisService.setUserTokenByUsername(user.getUsername(), String.valueOf(json.get("uid")),
                    String.valueOf(json.get("token")),
                    Long.valueOf(String.valueOf(json.get("expire_at"))));
            userToken = String.valueOf(json.get("token"));
            uid = String.valueOf(json.get("uid"));
        }
        DeviceWebSocketVo deviceWebSocketVo = new DeviceWebSocketVo();
        deviceWebSocketVo.setUid(uid);
        deviceWebSocketVo.setToken(userToken);
        deviceWebSocketVo.setDeviceDetailWebSocketVo(new DeviceDetailWebSocketVo(device.getGizDid(), device.getGizWsPort(), device.getGizWssPort(), device.getGizHost()));
        deviceWebSocketVo.setGizAppId(product.getGizwitsAppId());
        deviceWebSocketVo.setDataPoints(productDataPointDtos);
        return deviceWebSocketVo;
    }


    @Override
    public DeviceWebSocketVo getInfoForControlDevice2(DevicePageDto data) {
        User user = userService.getCurrentUser();
        String sno = data.getSno();
        Device device = selectById(sno);
        if (Objects.isNull(device) || device.getIsDeleted() == 1) {
            LeaseException.throwSystemException(LeaseExceEnums.DEVICE_DONT_EXISTS);
        }
        Product product = productService.getProductByDeviceSno(sno);
        if (Objects.isNull(product) || product.getIsDeleted() == 1) {
            LeaseException.throwSystemException(LeaseExceEnums.PRODUCT_DONT_EXISTS);
        }

        //在机智云绑定设备
        //判断redis里面是否存在token和uid等信息
        if (StringUtils.isEmpty(redisService.getUserTokenByUserName(user.getUsername()))) {
            //TODO 家博会期间使用superAppId
//            String res = GizwitsUtil.createUser(user.getUsername(), product.getGizwitsAppId());
            String res = GizwitsUtil.createUser(user.getUsername(), "799cf934e497415eaf5b743f451b6aa6");
            JSONObject json = JSONObject.parseObject(res);
            redisService.setUserTokenByUsername(user.getUsername(), String.valueOf(json.get("uid")),
                    String.valueOf(json.get("token")),
                    Long.valueOf(String.valueOf(json.get("expire_at"))));
            //异步绑定设备
            CommonEventPublisherUtils.publishEvent(new BindGizwitsDeviceEvent("BindGizwitsDevice", product.getId(), String.valueOf(json.get("token")), sno, user.getUsername()));
        } else { //如果存在就绑定该设备
            String userToken = redisService.getUserTokenByUserName(user.getUsername());
            CommonEventPublisherUtils.publishEvent(new BindGizwitsDeviceEvent("BindGizwitsDevice", product.getId(), userToken, sno, user.getUsername()));
        }


        //根据product找到数据点
        List<ProductDataPoint> dts = productDataPointService.selectList(new EntityWrapper<ProductDataPoint>().eq("product_id", product.getId()));
        if (dts.size() == 0 || Objects.isNull(dts)) {
            LeaseException.throwSystemException(LeaseExceEnums.DATA_POINT_ILLEGAL_PARAM);
        }
        List<ProductDataPointDto> productDataPointDtos = new ArrayList<>();
        for (ProductDataPoint dt : dts) {
            productDataPointDtos.add(new ProductDataPointDto(dt));
        }

        String userToken = redisService.getUserTokenByUserName(user.getUsername());
        String uid = redisService.getUidByUsername(user.getUsername());
        //如果从redis中获取不到，则重新获取
        if (StringUtils.isEmpty(userToken) || StringUtils.isEmpty(uid)) {//该操作只操作userToken和uid，其他passcode和host都不处理
            String res = GizwitsUtil.createUser(user.getUsername(), product.getGizwitsAppId());
            JSONObject json = JSONObject.parseObject(res);
            redisService.setUserTokenByUsername(user.getUsername(), String.valueOf(json.get("uid")),
                    String.valueOf(json.get("token")),
                    Long.valueOf(String.valueOf(json.get("expire_at"))));
            userToken = String.valueOf(json.get("token"));
            uid = String.valueOf(json.get("uid"));
        }
        DeviceWebSocketVo deviceWebSocketVo = new DeviceWebSocketVo();
        deviceWebSocketVo.setUid(uid);
        deviceWebSocketVo.setToken(userToken);
        deviceWebSocketVo.setDeviceDetailWebSocketVo(new DeviceDetailWebSocketVo(device.getGizDid(), device.getGizWsPort(), device.getGizWssPort(), device.getGizHost()));
        deviceWebSocketVo.setGizAppId(product.getGizwitsAppId());
        deviceWebSocketVo.setDataPoints(productDataPointDtos);
        return deviceWebSocketVo;
    }

    @Override
    public List<String> deviceAllotOperator(OperatorAllotDeviceDto operatorAllotDeviceDto) {

        List<String> snos = new ArrayList<>();
        List<Device> devices = selectBatchIds(operatorAllotDeviceDto.getSno());
        for (Device device : devices) {
            if (judgeSnoIsAllot(device)) {
                snos.add(device.getSno());
            } else {
                device.setOwnerId(operatorAllotDeviceDto.getOwnerId());
                updateById(device);
            }
        }
        return snos;
    }

    @Override
    public boolean judgeSnoIsAllot(Device device) {
        boolean flag = false;
        SysUser sysUser = sysUserService.getCurrentUserOwner();
        if (!Objects.equals(device.getOwnerId(), sysUser.getId())) {
            flag = true;
        }
        return flag;
    }

    @Override
    public MJDeviceDetailDto deviceDetailForMahjong(String sno) {
        SysUser user = sysUserService.getCurrentUser();
        Device device = selectOne(new EntityWrapper<Device>().eq("sno", sno)
                .eq("is_deleted", DeleteStatus.NOT_DELETED.getCode()));
        if (Objects.isNull(device)) {
            LeaseException.throwSystemException(LeaseExceEnums.DEVICE_DONT_EXISTS);
        }
        MJDeviceDetailDto deviceDetailDto = new MJDeviceDetailDto(device);
        deviceDetailDto.setWorkStatus(device.getWorkStatus());
        deviceDetailDto.setWorkStatusDesc(DeviceWorkStatus.getName(device.getWorkStatus()));
        if (Objects.equals(device.getOwnerId(), user.getId())) {
            deviceDetailDto.setIsModifyLaunchArea(1);
            deviceDetailDto.setIsModifyServiceMode(1);
        }
        Product product = productService.getProductByDeviceSno(device.getSno());
        if (Objects.isNull(product)) {
            LeaseException.throwSystemException(LeaseExceEnums.PRODUCT_DONT_EXISTS);
        }

        deviceDetailDto.setOnlineStatus(device.getOnlineStatus());
        deviceDetailDto.setOnlineStatusDesc(DeviceOnlineStatus.getName(device.getOnlineStatus()));

       /* JSONObject jsonObject = redisService.getDeviceControlCommand(product.getGizwitsProductKey(), device.getMac());
        if (Objects.nonNull(jsonObject)) {
            String voltage = jsonObject.getString("Voltage");
            Boolean Switch = jsonObject.getBoolean("Switch");
            deviceDetailDto.setVoltage(voltage);
            if (Switch) {
                deviceDetailDto.setIsOpen(1);
            } else {
                deviceDetailDto.setIsOpen(0);
            }
        }*/
        DeviceLaunchArea deviceLaunchArea = deviceLaunchAreaService.getLaunchAreaInfoById(device.getLaunchAreaId());
        if (!ParamUtil.isNullOrEmptyOrZero(deviceLaunchArea)) {
            deviceDetailDto.setProvince(deviceLaunchArea.getProvince());
            deviceDetailDto.setCity(deviceLaunchArea.getCity());
            deviceDetailDto.setArea(deviceLaunchArea.getArea());
            deviceDetailDto.setAddress(deviceLaunchArea.getAddress());
        }

        return deviceDetailDto;
    }

    @Override
    public List<String> deviceDeleteLaunchArea(DeviceWithLaunchArea deviceWithLaunchArea) {
        List<String> snos = new ArrayList<>();
        Integer launchAreaId = deviceWithLaunchArea.getLaunchAreaId();
        logger.info("投放点id=" + launchAreaId + "设备序列号：" + deviceWithLaunchArea.getSno().toString());
        List<Device> devices = selectBatchIds(deviceWithLaunchArea.getSno());
        for (Device device : devices) {
            if (!Objects.equals(device.getLaunchAreaId(), launchAreaId)) {
                snos.add(device.getSno());
            } else {
                device.setLaunchAreaId(null);
                device.setLaunchAreaName(null);
                boolean flag = updateAllColumnById(device);
                if (!flag) {
                    snos.add(device.getSno());
                }
            }
        }
        return snos;
    }

    @Override
    public List<String> deviceWithLaunchArea(DeviceWithLaunchArea deviceWithLaunchArea) {
        List<String> snos = new ArrayList<>();
        Integer launchAreaId = deviceWithLaunchArea.getLaunchAreaId();
        DeviceLaunchArea deviceLaunchArea = deviceLaunchAreaService.selectById(launchAreaId);
        List<Device> devices = selectBatchIds(deviceWithLaunchArea.getSno());
        for (Device device : devices) {
            if (!ParamUtil.isNullOrEmptyOrZero(device.getLaunchAreaId())) {
                snos.add(device.getSno());
            } else {
                device.setLaunchAreaId(launchAreaId);
                device.setLaunchAreaName(deviceLaunchArea.getName());
                boolean flag = updateById(device);
                if (!flag) {
                    snos.add(device.getSno());
                }
            }
        }
        return snos;
    }

    @Override
    public List<String> getFireFailSnos(ListDeviceForFireDto dto) {
        List<String> snos = new ArrayList<>();
        for (String sno : dto.getSnos()) {
            if (ParamUtil.isNullOrEmptyOrZero(dto.getAttrs())) {
                if (!remoteDeviceControl(sno, dto.getName(), dto.getValue())) {
                    snos.add(sno);
                }
            } else {
                if (!remoteDeviceControl(sno, dto.getAttrs())) {
                    snos.add(sno);
                }
            }
        }
        return snos;
    }

    public void sendCallOutToManage(Device device) {
        if (Objects.isNull(device)) {
            logger.error("====设备信息为空====");
            return;
        }
        if (Objects.isNull(device.getLaunchAreaId())) {
            logger.error("====设备{}未设置投放点====", device.getSno());
            return;
        }
        DeviceLaunchArea deviceLaunchArea = deviceLaunchAreaService.selectById(device.getLaunchAreaId());
        if (Objects.isNull(deviceLaunchArea)) {
            logger.error("===设备{}的投放点{}不存在====", device.getSno(), device.getLaunchAreaId());
            return;
        }
        SysUserExt sysUserExt = getWxConfigByDeviceId(device.getSno());
        if (Objects.isNull(sysUserExt)) {
            logger.error("===设备{}未找到相关的微信配置=====", device.getSno());
            return;
        }
        SysUserExt toUser = sysUserExtService.selectById(deviceLaunchArea.getMaintainerId());
        if (Objects.isNull(toUser)) {
            logger.error("===设备{}的投放点{}的维护人员{}不存在=====", device.getSno(), deviceLaunchArea.getId(), deviceLaunchArea.getMaintainerId());
            return;
        }
        if (Objects.isNull(toUser.getWxOpenId())) {
            logger.error("===设备{}的投放点{}的维护人员{}未配置微信openID=====", device.getSno(), deviceLaunchArea.getId(), deviceLaunchArea.getMaintainerId());
            return;
        }

        JSONObject sendData = new JSONObject();
        sendData.put("touser", sysUserExt.getWxOpenId());
        sendData.put("template_id", SysConfigUtils.get(CommonSystemConfig.class).getMahjongCallTemplateId());

        JSONObject firstData = new JSONObject();
        firstData.put("value", "您好，有新的呼叫。");
        firstData.put("color", "#173177");

        JSONObject keyword2 = new JSONObject();
        keyword2.put("value", deviceLaunchArea.getName() + "--" + device.getMac());
        keyword2.put("color", "#173177");

        JSONObject keyword1 = new JSONObject();
        keyword1.put("value", com.gizwits.boot.utils.DateKit.getTimestampString(new Date()));
        keyword1.put("color", "#173177");

        JSONObject remarkData = new JSONObject();
        remarkData.put("value", "请及时处理。");
        remarkData.put("color", "#173177");

        JSONObject body = new JSONObject();
        body.put("first", firstData);
        body.put("keyword1", keyword1);
        body.put("keyword2", keyword2);
        body.put("remark", remarkData);

        sendData.put("data", body);
        logger.info("====发送串货信息信息-====>>>" + body);
        //发送模板消息
        WxUtil.sendTemplateNotice(sendData.toJSONString(), sysUserExt);
    }

    @Override
    public Device getDeviceByQrcode(String qrcode) {
        Wrapper<Device> wrapper = new EntityWrapper<>();
        wrapper.eq("wx_ticket", qrcode).or().eq("content_url", qrcode);
        return selectOne(wrapper);
    }

    @Override
    public Device getDeviceByMac(String mac) {
        Wrapper<Device> wrapper = new EntityWrapper<>();
        wrapper.eq("mac", mac).eq("is_deleted", DeleteStatus.NOT_DELETED.getCode());
        return selectOne(wrapper);
    }

    @Override
    public Device selectById(String sno) {
        Wrapper<Device> wrapper = new EntityWrapper<>();
        wrapper.eq("sno", sno).eq("is_deleted", DeleteStatus.NOT_DELETED.getCode());
        return selectOne(wrapper);
    }

    @Override
    public List<DeviceProductShowDto> show(String sno) {
        Device device = getDeviceInfoBySno(sno);
        if (ParamUtil.isNullOrEmptyOrZero(device)) {
            LeaseException.throwSystemException(LeaseExceEnums.DEVICE_DONT_EXISTS);
        }
        ProductCommandConfigForQueryDto queryDto = new ProductCommandConfigForQueryDto();
        queryDto.setProductId(device.getProductId());
        queryDto.setCommandType("SHOW");
        List<ProductCommandConfigForDetailDto> detailDtos = productCommandConfigService.list(queryDto);
        Product product = productService.getProductByDeviceSno(sno);
        JSONObject existDevice = redisService.getDeviceCurrentStatus(product.getGizwitsProductKey(), device.getMac());
        if (Objects.isNull(existDevice)) {
            return ListUtils.EMPTY_LIST;
        }

        logger.info("getDeviceCurrentStatus mac {} {}", device.getMac(), existDevice.toJSONString());
        List<DeviceProductShowDto> showDtos = new ArrayList<>(detailDtos.size());
        if (!ParamUtil.isNullOrEmptyOrZero(existDevice)) {
            for (ProductCommandConfigForDetailDto detailDto : detailDtos) {
                DeviceProductShowDto deviceProductShowDto = new DeviceProductShowDto();
                String identityName = detailDto.getIdentityName();
                if (StringUtils.isEmpty(identityName)) {
                    identityName = JSONObject.parseObject(detailDto.getCommand()).getString("name");
                }
                String range = JSONObject.parseObject(detailDto.getCommand()).getString("value");
                deviceProductShowDto.setRange(range);
                String value = existDevice.getString(identityName);
                logger.info("get value by identity name {} mac {} -> value {}", identityName, device.getMac(), value);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(detailDto.getName(), value);
                deviceProductShowDto.setJsonObject(jsonObject);
                deviceProductShowDto.setShowType(detailDto.getShowType());
                showDtos.add(deviceProductShowDto);
            }
        }
        return showDtos;
    }

    @Override
    public void updateLockFlag(String sno, Integer abnormalTimes, Boolean lock) {
        Device forUpdate = new Device();
        forUpdate.setSno(sno);
        forUpdate.setAbnormalTimes(abnormalTimes);
        forUpdate.setLock(lock);
        updateById(forUpdate);
    }

    @Override
    public void assingModeOrArea(DeviceAssignDto deviceAssignDto) {
        SysUser sysUser = sysUserService.getCurrentUserOwner();
        EntityWrapper<Device> entityWrapper = new EntityWrapper<>();
        Device device = new Device();

        switch (deviceAssignDto.getType()) {
            case 1:
                //做一下转换
                deviceAssignDto.setAssignId(deviceAssignDto.getAssignIdList().get(0));
                ProductServiceMode serviceMode = productServiceModeService.getProductServiceMode(deviceAssignDto.getAssignId());
                if (ParamUtil.isNullOrEmptyOrZero(serviceMode)) {
                    LeaseException.throwSystemException(LeaseExceEnums.SERVICE_MODE_NOT_EXIST);
                }
                deviceToProductServiceModeService.batchInsertBySno(deviceAssignDto);
                break;
            case 2:
                DeviceLaunchArea launchArea = deviceLaunchAreaService.getLaunchAreaInfoById(deviceAssignDto.getAssignId());
                if (ParamUtil.isNullOrEmptyOrZero(launchArea) || !sysUser.getId().equals(launchArea.getSysUserId())) {
                    LeaseException.throwSystemException(LeaseExceEnums.DEVICE_LAUNCH_AREA_CANT_ASSIGN);
                }
                entityWrapper.in("sno", deviceAssignDto.getSno());
                device.setLaunchAreaId(launchArea.getId());
                device.setLaunchAreaName(launchArea.getName());
                device.setUtime(new Date());
                update(device, entityWrapper);
                break;
            default:
                break;
        }
    }

    //===============================ADD============================================//

    @Override
    public StatDeviceWidgetVo getDeviceWidget(StatDeviceDto statDeviceDto) {
            StatDeviceWidgetVo vo=new StatDeviceWidgetVo();
             Date yesterday= DateUtil.addDay(new Date(),-1);
            //获取设备总数，昨日新增设备数，设备在线数，设备活跃数
            vo=deviceDao.selectTotalDeviceCount(statDeviceDto.getProductId(),yesterday);

            //如果设备总数为0，则在线率，活跃率为0
            if(vo.getTotalCount()==0){
                vo.setOnlineRate(0.0);
                vo.setActivatedRate(0.0);
            }else{
                vo.setOnlineRate(BigDecimal.valueOf((double) vo.getOnlineCount()/ vo.getTotalCount()).setScale(0, BigDecimal.ROUND_HALF_UP)
                        .doubleValue()*100);
                vo.setActivatedRate(BigDecimal.valueOf((double) vo.getActiveCount()/ vo.getTotalCount()).setScale(0, BigDecimal.ROUND_HALF_UP)
                        .doubleValue()*100);
            }
        return vo;
    }

    @Override
    public StatAlarmWidgetVo getAlarmAndFaultWidget(StatDeviceDto statDeviceDto) {
        StatAlarmWidgetVo vo=new StatAlarmWidgetVo();
         vo=deviceDao.getAlarmAndFaultWidget(statDeviceDto.getProductId());
         if(vo.getTotalCount()==0){
             vo.setAlarmPercent(0.0);
             vo.setFaultPercent(0.0);
         }else{
             vo.setAlarmPercent(BigDecimal.valueOf((double) vo.getWarnCount()/ vo.getTotalCount()).setScale(0, BigDecimal.ROUND_HALF_UP)
                     .doubleValue()*100);
             vo.setFaultPercent(BigDecimal.valueOf((double) vo.getFaultCount()/ vo.getTotalCount()).setScale(0, BigDecimal.ROUND_HALF_UP)
                     .doubleValue()*100);
         }
        return vo;
    }

    @Override
    public List<StatLocationVo> ditributionByProvince(StatDeviceDto statDeviceDto) {
          Integer productId=statDeviceDto.getProductId();
           final Integer allCount;
          if(productId==null){
                allCount=selectCount(new EntityWrapper<Device>().eq("is_deleted",0));
          }else{
                allCount=selectCount(new EntityWrapper<Device>().eq("product_id",productId).eq("is_deleted",0));
          }
          List<StatLocationVo> vo=deviceDao.ditributionByProvince(productId);
          if(ParamUtil.isNullOrEmptyOrZero(vo)||allCount==0){
              return new LinkedList<>();
          }
        vo.stream().forEach(item->{
            if(item.getProvince()==null||item.getProvince().equals("")){ item.setProvince("其他");}
            item.setCount(item.getDeviceCount());
            item.setProportion(BigDecimal.valueOf(item.getDeviceCount().doubleValue()/ allCount).setScale(1, BigDecimal.ROUND_HALF_UP)
                        .doubleValue());
        });

        Collections.sort(vo);
        return vo;
    }

    @Override
    public List<StatLocationVo> ditributionByCity(StatDeviceDto statDeviceDto) {
        Integer productId=statDeviceDto.getProductId();
        String province=statDeviceDto.getProvince();
        final Integer allCount;
        if(productId==null){
            allCount=selectCount(new EntityWrapper<Device>().eq("province",province).eq("is_deleted",0));
        }else{
            allCount=selectCount(new EntityWrapper<Device>().eq("province",province).eq("product_id",productId).eq("is_deleted",0));
        }
        List<StatLocationVo> vo=deviceDao.ditributionByProvince(productId);
        if(ParamUtil.isNullOrEmptyOrZero(vo)||allCount==0){
            return new LinkedList<>();
        }
        vo.stream().forEach(item->{
            if(item.getProvince()==null||item.getProvince().equals("")){ item.setProvince("其他");}
            item.setCount(item.getDeviceCount());
            item.setProportion(BigDecimal.valueOf(item.getDeviceCount().doubleValue()/ allCount).setScale(1, BigDecimal.ROUND_HALF_UP)
                    .doubleValue());
        });
        Collections.sort(vo);
        return vo;
    }

    @Override
    public User getBindUser(String sno) {
        Device device=selectById(sno);
        if(ParamUtil.isNullOrEmptyOrZero(device)){
            LeaseException.throwSystemException(LeaseExceEnums.DEVICE_DONT_EXISTS);
        }
        User user=userService.getBindUser(device.getMac());
        return user;
    }

    @Override
    public Boolean updateDeviceName(AppUpdateDeviceNameDto dto) {
         List<String> macs=dto.getMacs();
         if(ParamUtil.isNullOrEmptyOrZero(macs)){
             return false;
         }
         Device device=new Device();
         device.setUtime(new Date());
         device.setName(dto.getName());
        return update(device,new EntityWrapper<Device>().in("mac",macs).eq("is_deleted",0));
    }

    @Override
    public List<Device> getUserRoomDevices(Integer roomId) {
         List<Device> devices=deviceDao.getUserRoomDevice(roomId);
        return devices;
    }

    @Override
    public void sendPlanToDevices() {
        //获取所有在线的设备的正在开启的计划,
         List<DevicePlan> list=devicePlanDao.getAllDevicePlanByUsed();
         logger.info("获取到可能要执行计划数：{}",list.size());
         if(ParamUtil.isNullOrEmptyOrZero(list)){
             return ;
         }
         Date now=new Date();
         //获取当前时分，
        String nowTime=DateUtil.dateToString(now,"HH:mm");
         //获取当前周几,1周日，2周一，3周二 .。。以此类推
         String weekDay=DateUtil.getWeekByDate(now);
         Integer sendCount=0;
         //去掉不符合要求的计划
           for(DevicePlan item:list){
              JSONObject json=JSONObject.parseObject(item.getContent());
             String time=json.getString("time");
             String repeat=json.getString("repeat");

             //判断当前时间和计划时间是否相差1分钟
             if(DateUtil.checkWeek(weekDay,repeat)&&DateUtil.checkMinute(nowTime,time)==1){
                 Boolean status=json.getBoolean("status");
                 //todo： 发送控制
                 if(sendControl(item.getMac(),status)) sendCount++;
             }
         }
         logger.info("实际发送定时计划：{}",sendCount);
    }
    private Boolean sendControl(String mac,Boolean status){
        Device device=getDeviceByMac(mac);
        if(ParamUtil.isNullOrEmptyOrZero(device)){
            return false;
        }
        Product product=productService.getProductByProductId(device.getProductId());
        if(ParamUtil.isNullOrEmptyOrZero(product)){
            return false;
        }
        JSONObject attr=new JSONObject();
        attr.put("switch",status);
        ControlDto controlDto=new ControlDto()
                                .setMac(mac)
                                .setDid(device.getGizDid())
                                .setProductKey(product.getGizwitsProductKey())
                                .setAttrs(attr);
        redisService.cacheSnotiControlDevice(CommandType.CONTROL.getCode(),controlDto);
        return true;
    }
//===============================END==============================================//
}
