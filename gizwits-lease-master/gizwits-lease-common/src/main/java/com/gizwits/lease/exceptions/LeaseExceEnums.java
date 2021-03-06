package com.gizwits.lease.exceptions;

/**
 * Created by rongmc on 2017/7/15.
 */
public enum LeaseExceEnums {

    //1XXXXXX user message
    USER_DONT_EXISTS("1000000", "用户不存在"),
    OPERATOR_DONT_EXISTS("1000001", "运营商不存在"),
    PRODUCT_DONT_EXISTS("1000002", "该产品不存在"),
    PRODUCT_IS_EXISTS("10000021", "该产品已存在"),
    PRODUCTCATEGORY_IS_EXISTS("10000022", "品类不存在"),
    USERNAME_EXISTS("1000003", "用户名已存在"),
    FROZEN_OPERATION("1000004", "冻结运营状态"),
    OPERATOR_IS_NOT_CHILDREN("1000005", "运营商非直接子用户"),
    AGENT_IS_NOT_CHILDREN("1000006", "代理商非直接子用户"),
    USER_IS_BINDING("1000007", "用户已经绑定微信用户"),
    WEIXXIN_USER_DONT_EXISTS("1000008", "微信用户不存在"),
    WEIXIN_OPENID_IS_NULL("1000009", "微信Openid为空"),
    USER_IN_BLACK("1000010", "用户在黑名单"),
    USER_PHONE_EXISTS("1000011", "手机号已存在"),
    USER_IS_NOT_OPERATOR("1000012","用户不是运营商"),
    UNSUPPORTED_OPERATION("1000013","不支持当前操作"),

    UNSUPPORT_OPERATOIN("1000014","不支持的操作"),


    USER_EXIST("1000011","用户已存在"),
    OPERATION_FAIL("1000012","操作失败"),


    VERSION_WRONG("1000013","API版本号错误"),

    PICTURE_FAIL("1000017", "图形验证码生成失败"),
    PICTURE_CODE_ERROR("1000018", "图形验证码错误"),

    MOBILE_BINDED("1000019"," 手机号已绑定"),
    MOBILE_NOT_EXIT("1000020","账号不存在"),
    MOBILE_ILLEGAL("1000021","手机号不合法"),
    CANT_BIND("1000022","此第三方账号已被绑定"),
    REDIS_WITHOUT_DEVICE_INFO("1000023","请检查redis缓存是否存在设备数据点信息"),

    PASSWORD("1000019","用户密码不匹配"),
    NO_TOKEN("1000020","请求头未传入Authorization"),
    ERROE_MOBILE("1000021","手机号与用户不匹配"),
    IMAGE_SUFFIX_NOT_SUPPORT("1000024", "图片格式不支持"),
    UPLOAD_IMAGE_FAIL("1000025", "图片上传失败"),
    USER_NOT_BIND_WEIXIN("1000026", "用户未绑定微信"),

    MOBILE_CODE_ERROR_OR_EXPIRE("1000024", "手机验证码错误或已失效"),
    LOGIN_ERROR("1000028","用户登录失败"),
    SNOTI_PARAM_ERROR("1000029", "snoti相关参数存在错误"),
    AGENT_NOT_EXISTS("1000030","代理商不存在"),
    REQUEST_FAILTURE("1000031","请求微信access_token失败"),
    Recommender_Does_Not_Exist("1000032","推荐人不存在"),

    //2XXXXXX device message
    DEVICE_OFFLINE("2000000","设备离线"),
    DEVICE_DONT_EXISTS("2000001","设备不存在"),
    DEVICE_EXISTS("2000002","设备已存在"),
    UNBIND_DEVICE("2000003","用户未绑定设备"),
    DEVICE_CONTROL_FAIL("2000004","设备操作失败"),
    DEVICE_INUSING_FAIL("2000005","设备占用中"),
    DEVICE_INFAULT("2000006","设备故障中"),
    DEVICE_SAVE_ERROR("2000007","设备保存出错"),
    DEVICE_NOT_IN_OPERATOR("2000008","设备未投入运营中"),
    DEVICE_SERVICE_MODEL_FREE("2000009","设备免费"),
    DEVICE_HAS_ORDER_NOT_SHARE_BENEFIT("2000010","设备有订单未分润"),
    DEVICE_SERVICE_MODEL_DETAIL_NOT_EXIST("2000011","收费价格详情不存在"),
    DEVICE_WITHOUT_LAUNCH_AREA("2000012","设备投放点不存在"),
    DEVICE_CANNOT_ACCESS("2000013", "无权限访问设备"),
    DEVICE_CANNOT_ASSIGN("2000014", "无法分配设备"),
    DEVICE_CANNOT_ACCESS_GROUP("2000015", "设备组不存在"),
    DEVICE_LAUNCH_AREA_NOT_EXIST("2000016", "投放点不存在"),
    CANNOT_UNBIND_DEVICE("2000017", "设备无法解绑"),
    DEVICE_ASSIGN_OPERATOR_NOT_EXIST("2000018", "分配的运营商不存在"),
    DEVICE_ASSIGN_AGENT_NOT_EXIST("2000019", "分配的经销商不存在"),



    DEVICE_NOT_IN_LAUNCH_AREA("2000022", "投放点下无设备"),
    DEVICE_LAUNCH_AREA_CANT_ASSIGN("2000023", "投放点不允许分配"),
    DEVICE_LAUNCH_AREA_CANT_UNBIND("2000024", "投放点不允许解绑"),
    DEVICE_LAUNCH_AREA_ALREADY_BELONG_SELF("2000025", "该操作无意义"),

    DEVICE_PARENT_SERVICE_MODE_SETTING_LIMIT("2000026","设备分配时设置为付费模式,不允许修改为免费"),

    DEVICE_UNRENT("2000027","设备不能被租赁，可能设备被使用中，或者离线"),
    DEVICE_CONFIG_ERROR("2000028","设备支付信息配置异常"),
    DEVICE_NOT_SUPPORT_ALIPAY("2000029","设备暂时未支持支付宝"),
    DEVICE_START_FAIL("2000030","您好，设备启动失败,\n" +
            "您的支付款将原路返回到账号，\n" +
            "请重新扫码使用，或联系客服"),


    DEVICE_WITHOUT_BENEFIT_RULE("2000031","未分配分润规则"),
    DEVICE_MAC_EXISTS("2000032","设备MAC已存在"),
    DEVICE_SN1_EXISTS("200003201","设备SN1已存在"),
    DEVICE_SN2_EXISTS("200003202","设备SN2已存在"),
    DEVICE_IMEI_EXISTS("200003203","设备IMEI已存在"),
    DEVICE_NOT_BELONG_DIRECT_USER("2000033","设备不是在子用户下"),

    DEVICE_NOT_MATCH_DEVICE_LAUNCH_AREA("2000034", "设备与投放点不匹配"),
    DEVICE_NOT_MATCH_AGENT("2000035", "设备与运营商不匹配"),
    DEVICE_IS_SET_TIMING_RULE("2000036", "设备已关联定时规则"),

    DEVICE_QRCODE_ERROR("2000037","二维码生成失败"),
    DEVICE_BIND_USER("2000038","您已绑定该设备，无需再次绑定"),
    PLEASE_CHOOSE_SHARE_BENEFIT("2000039","请选择分润订单"),
    SERVICE_MODE_NAME_EXITS("2000040","收费模式名称已存在"),
    QECODE_NOT_EXIST("2000041","二维码信息不存在"),


    //3XXXXXX order message
    REPEAT_ORDER("3000000", "重复下单"),
    ORDER_DONT_EXISTS("3000001", "订单不存在"),
    ORDER_HAS_REFUND("3000002", "订单已退款"),
    ORDER_NOT_CURRENT_USER("3000003", "非法操作订单"),
    ORDER_IS_FREE("3000004", "免费订单"),
    CREATE_ORDER_ERROR("3000005", "创建订单失败"),
    HAS_UNFINISH_ORDER("3000006", "有未完成订单"),
    RENT_RULE_CONFIG_ERROR("3000007", "租金规则配置错误"),
    UNFREE_ORDER_BUT_GET_ZERO_MONEY("3000008", "非免费订单,计算金额小于0.01"),
    ORDER_UNLIMITED_DONT_EXISTS("3000009", "不定时规则订单不存在"),
    WEIXIN_PREPAYID_ERROR("3000010", "微信统一下单失败"),
    SERVICE_MODE_CONFIG_ERROR("3000011", "收费模式配置出错"),
    ORDER_STATUS_ERROR("3000012", "订单状态不正确"),
    ORDER_REFUND_FAIL("3000013", "订单退款失败"),
    ORDER_REFUND_FAIL_WITH_INCOMPLATE_INFORMATION("3000013_1","订单退款失败,找不到微信配置信息"),
    ORDER_REFUND_FAIL_WITH_RESULT_ID_EMPTY("3000013_2","退款失败，请求接口失败"),
    ORDER_REFUND_FAIL_WITH_INCOMPLATE_ALIPAY_INFORMATION("3000013_2","退款失败，原商户交易单号和支付宝交易号不能同时为空"),
    ORDER_CHANGE_STATUS_FAIL("3000020", "订单状态修改失败"),
    ORDER_EXT_QUANTITY_NOT_EXIST("3000021", "订单数量获取失败"),
    ORDER_IN_USING_REPEAT("3000022", "一个公众号对应的用户不能有多个使用中的订单"),
    
    SHARE_PERCENTAGE_RANG_ERROR("3000023", "分润比例不正确,请确认每个层级都有值且分润比例总和为100"),
    SHARE_RULE_SET_ERROR("3000024", "分润规则设置失败"),
    SHARE_PARENT_RULE_SET_ERROR("3000026", "父级分润单设置异常"),
    SHARE_PARENT_NOT_SET_RULE_FOR_DEVICE("3000027", "父级分润规则中未对当前设备设置分润比例,即父级不参与该设备分润"),
    SHARE_DEVICE_PERCENTAGE_BIG_THAN_PARENT("3000028", "当前设备设置的分润比例大于父级设置的比例"),
    SHARE_DEVICE_PERCENTAGE_LITTLE_THAN_CHILDREN("3000029", "当前设备设置的分润比例小于子级设置的比例"),
    SHARE_ADD_ERROR("3000040","该分配对象没有设备，无法创建分润规则"),
    USER_WITHOUT_DEVICE("3000041"," 修改失败，该分配对象没有设备无法修改分润比例"),

    WX_ORDER_QUERY_FAIL("3000030", "微信订单查询失败"),
    ORDER_TYPE_NOT_EXIST("3000031", "订单类型不存在"),
    HAS_NO_DEVICE_CANT_SET_SHARE_RULE("3000032","当前对象没有设备,无法设置分润规则"),
	SERVICE_MODE_CANT_MODIFY("3000033","只能修改自己的收费模式"),
    SHARE_RULE_MODIFY_LIMIT("3000034", "分润规则修改受到限制"),
    MANUFACTURER_NOT_EXIST("3000035", "厂商不存在"),
    MANUFACTURER_TOO_MANY("3000037", "企业id对应多个厂商，请检查数据库"),

    CARD_CODE_NOT_AVAILABLE("3000038", "卡券不可用"),
    CARD_CONSUME_FAIL("3000039", "卡券使用失败"),
    ORDER_USED_CARD("3000042", "订单已使用卡券"),
    CARD_RECEIVED("3000043", "卡券已领取"),
    CARD_EXCEEDED("3000044", "卡券使用次数超出限制"),
    CARD_EXPIRED("3000045", "卡券已过期"),
    CARD_OUT_OF_STOCK("3000046", "卡券已领完"),
    card_Is_The_Same("3000050", "卡券名称已存在"),

    SHARE_PROFIT_MONEY_IS_NOT_ENOUGH("3000047", "分润金额不足"),
    SHARE_BENEFIT_RULE_NOT_EXIST("3000048","分润规则不存在"),
    GENERATER_SHARE_BENEFIT_FAIL("3000049","生成分润单失败，更新分润记录状态时出错"),


    //4XXXXXX config message
    WEIXIN_GET_JSTICKET_ERROR("4000001", "获取微信JSTicket失败"),
    PARAMS_ERROR("4000002", "参数错误"),
    WEIXIN_GET_TOKEN_ERROR("4000003", "获取微信token失败"),
    WEIXIN_GET_QR_TICKET_ERROR("4000004", "获取qrcode的ticket接口失败"),
    UPLOAD_TOO_MANY_PICTURES("4000005", "上传太多图片"),
    PICTURE_SUFFIX_ERROR("4000006", "图片格式有误,只可以用jpg"),
    WX_UNBIND_FAIL("4000007", "微信解绑失败"),
    WEIXIN_SYS_USER_EXT_NOT_EXIST("4000008", "微信公众号不存在"),
    WEIXIN_PRODUCT_ID("4000009", "微信硬件产品id不存在"),
    WEIXIN_DEVICE_ID_GET_ERROR("40000010", "微信did获取失败"),
    WEIXIN_ID_IS_NULL("4000011", "微信ID不存在"),
    PHONE_OR_PASSWORD_ERROR("4000012", "手机号或密码错误"),
    PHONE_ERROR("4000014", "手机号错误"),
    PHONE_NOT_REGISTER("4000013", "手机号未注册"),
    PHONE_BIND_USER_ERROR("4000015", "手机号与用户绑定的手机号不符\n"),
    PHONE_REGISTERED("4000016", "手机号已被注册"),
    UPLOAD_ERROR("4000017", "图片上传失败"),
    FILE_SUFFIX_ERROT("4000018","文件格式错误"),
    ENTITY_NOT_EXISTS("300", "实体不存在"),
    ENTITY_EXISTS("301", "实体已存在"),
    DEVICE_NOT_FREE("303", "设备非空闲"),
    EXISTS_UNFINISH_ORDER("304", "存在未完成订单"),
    DEVICE_LOCKED("305", "设备已被锁定，请联系管理员"),
    PARAM_ILLAGEL("306","参数不合法，请输入不小于0的数字"),

    //5,数据分析数据错误
    STAT_ORDER_WIDGET("501", "订单看板统计出现重复记录或者没有记录"),

    //5xxxxxx product message
    DATA_POINT_ILLEGAL_PARAM("5000001", "获取数据点参数错误"),

    //6.用户钱包，用户余额订单错误
    BALANCE_NOT_ENOUGH("601", "钱包余额不足以支付"),
    WALLET_NOT_EXIST("602", "钱包不存在"),
    CHARGE_ORDER_NOT_EXIST("603", "充值单不存在"),
    CHARGE_ORDER_CHANGE_STATUS_FAIL("604", "充值单修改失败"),
    WALLET_OPERATE_FAIL("605", "钱包操作失败"),
    INPUT_FEE("606", "请输入充值金额"),

    CHARGE_CARD_WAS_BOUND("6001001", "该充值卡已经绑定过"),
    CHARGE_CARD_DONT_EXISTS("6001002", "卡券不存在"),
    Recharge_Times_Have_Reached_The_Maximum("6001003", "代充值次数已达上限,请联系上级管理员"),


    //7.交易单错误
    TRADE_NOT_EXIST("701", "交易单不存在"),
    TRADE_WEIXIN_NOT_EXIST("702", "微信交易单不存在"),
    PAYTYPE_NULL("703", "支付方式为空"),
    CALLBACK_FAILURE("704", "回调出现错误"),
    Callback_Failed_Refund_Succeeded("705", "回调出现错误,退款成功"),


    //6xxxxxx operator message
    ALREADY_BINDED_OPERATOR("6000001", "该账号已绑定运营商"),
    OPERATOR_NAME_DUP("6000002", "运营商名称重复"),
    AGENT_NOT_SHARE_BENEFIT_RULE("6000003","请联系设备代理商是否设置分润规则"),

    //7xxxxxx alipay message
    ALIPAY_PAY_FAILUR("7000001", "支付宝支付失败"),
    ALIPAY_NOTIFY_FAILUR("7000002", "支付宝回调失败"),
    USER_NOT_MATCH_ORDER("7000003","用户与订单不匹配"),
    TRADE_BASE_NOT_EXIST("7000004","交易单不存在"),
    TRADEALIPAY_NOT_EXIST("7000005","支付宝交易单不存在"),
    ALIPAY_ORDER_QUERY_FAIL("7000006", "支付宝订单查询失败"),
    ALIPAY_PARAM_IS_NULL("7000007", "所属运营商的支付宝配置为空"),

    //8xxxxx device group
    DEVICE_GROUP_NAME_DUP("8000001", "设备组名称重复"),
    //充值卡
    CARD_NOT_EXIST("8000002","充值卡不存在"),
    SEND_MESSAGE_FAIL("8000003", "发送消息失败"),

    //9xxxxxx agent message
    AGENT_NAME_DUP("9000001", "代理商名称重复"),


    //9xxxxxx import excel message
    EXCEL_FILE_FORMAT_ERROR("9000001", "文件格式错误"),
    EXCEL_PARSE_ERROR("9000002", "解析失败"),
    EXCEL_NO_DATA("9000003", "模板没有数据"),
    CHARGE_SETTING_EXIST("9000004", "该值已经存在"),
    CHARGE_SETTING_OVERFLOW("9000005", "设定超过限定条数"),
    SERVICE_MODE_NOT_EXIST("3000033","收费模式不存在"),
    SERVICE_MODE_DETAIL_NOT_EXIST("3000034","收费模式详情不存在"),

    COMMAND_NOT_EXISTS("930000", "指令不存在"),
    IMPORT_FAIL("900006", ""),

    // 54xxx 退款
    REFUND_METHOD_NOT_SUPPORT("54000001", "不支持退款"),
    REFUND_FAIL("54000002", "退款失败"),
    REFUND_MONEY_EXCEED("54000003", "退款金额错误"),
    REFUND_ORDER_STATUS("54000004", "订单当前状态不能申请退款"),
    REFUND_EXIST("54000005", "这张订单已经申请过退款"),
    CERT_LOSE("54000006","退款失败，请检查是否已设置好退款证书"),
    TRADE_NO_LOSE("54000007","退款交易记录不存在"),

    //11xxxxx 工单
    WORK_ORDER_CANT_EDIT("1100001", "只有待受理的工单才能编辑"),
    WORK_ORDER_HAS_MAINTAINER("1100002", "工单已有维护人员"),
    WORK_ORDER_CANT_ACCEPT("1100003", "工单非待受理状态"),
    WORK_ORDER_MAINTAINER_NOT_MATCH("1100004", "您不是该工单的维护人员"),
    WORK_ORDER_NOT_ACCEPTED("1100005", "工单未受理或已经结束"),
    WORK_ORDER_END("1100005", "工单已经结束"),
    WORK_ORDER_DEVICE_NOT_IN_LAUNCH_AREA("1100006", "当前设备不在此工单的投放点内"),
    MAINTAINER_NOT_EXIT("1100007","维护人员不存在"),
    WORK_ORDER_CANT_ALLOT("1100008", "工单非受理中状态"),
    WORK_ORDER_CANT_PROCESS("1100009", "工单非待处理状态"),
    WORK_ORDER_NOT_PROCESSING("1100010", "工单非处理中状态"),
    WORK_ORDER_MAC_BLANK("1100011", "设备MAC必填"),
    WORK_ORDER_OWNER_DIFF("1100012", "所有设备必须是同一个拥有者"),
    WORK_ORDER_LAUNCH_DIFF("1100013", "所有设备必须是同一个投放点"),
    WORK_ORDER_CANT_CHANGE_STATUS("1100014", "工单状态修复失败"),
    WORK_ORDER_CANT_CLOSE("1100015", "工单非待受理状态"),
    WORK_ORDER_STATUS_WRONG("1100016", "工单状态有误"),
    WORK_ORDER_SCAN_FAIL_LAUNCH_DIFF("1100017", "设备已被分配到别的投放点"),
    WORK_ORDER_SCAN_FAIL_OWNER_DIFF("1100018", "设备已被分配到别的代理商或运营商"),

    //
    INSTALL_FEE_NO_RULE("1200001", "此设备不需要支付初装费"),
    INSTALL_FEE_NEED("1200002", "请先支付初装费"),
    INSTALL_FEE_RULE_LINK_FAIL("1200003", ""),
    INSTALL_FEE_ORDER_EXIST("1200004", "此设备已支付初装费"),

    // 13xxxx  分润
    SHARE_BENEFIT_INFORMATION_LOSE("1300001","分润单的收款信息不完整，无法执行分润"),
    SHARE_ORDER_STATUS_WRONG("1300002", "分润单的状态不正确"),
    SHARE_ACTION_FAIL("1300003", "分润操作失败"),
    SHARE_ACTION_FAIL_WITH_UPDATE_STATUS("1300003", "分润操作失败,更新分润单状态失败，此账单已执行分润"),
    SHARE_ACTION_SUCCESS("1300004", "分润操作成功"),
    SHARE_FAIL_BUT_NOT_CHANGE("1300005", "上次分润失败,但不允许修改分润内容,请直接执行分润"),
    SHARE_PAY_TYPE_NOT_SUPPORT("1300006", "分润单支付类型不支持"),
    GENERATE_SHARE_ORDER_FAIL("1300007", "生成分润单失败"),
    SHARE_BENEFIT_ALIPAY_INFORMATION_LOSE("1300008","厂商支付宝信息不完整，无法执行分润"),

    //14xxxx 操作提示
    DONOT_REPAET_OPERATION("1400001","请勿重复操作"),
    SUCCESSFUL_OPERATION("1400002","更新成功"),
    DELETE_USER_ERROR("1400003","部分用户删除失败，可能不存在或已有设备"),
    ADVERTISEMENT_NOT_EXIST("1400004","广告不存在"),
    DEVICE_PLAN_PARAM_ERROR("1400005","定时内容参数不合法"),
    PLAN_REPEAT_ERROR("1400006","定时内容重复"),
    PLAN_NOT_EXIST("1400007","定时计划不存在"),
    USER_ROOM_NOT_EXIST("1400008","用户房间不存在"),
    ;
    private String code;
    private String message;

    LeaseExceEnums(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
