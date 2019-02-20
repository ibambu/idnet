package com.ibamb.dnet.module.api;

import com.ibamb.dnet.module.beans.*;
import com.ibamb.dnet.module.core.ParameterMapping;
import com.ibamb.dnet.module.instruct.*;
import com.ibamb.dnet.module.instruct.beans.Parameter;
import com.ibamb.dnet.module.search.DeviceSearch;
import com.ibamb.dnet.module.security.AESUtil;
import com.ibamb.dnet.module.security.UserAuth;
import com.ibamb.dnet.module.sync.DeviceParamSynchronize;
import com.ibamb.dnet.module.sys.SysManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 与设备信息交互的客户端，通过调用该类的实例可以读/写设备参数。
 */
public class UdmClient {
    private static UdmClient udmClient;

    public static UdmClient getInstance() {
        if (udmClient == null) {
            udmClient = new UdmClient();
        }
        return udmClient;
    }

    /**
     * 创建UdmClient对象实例，加载设备参数ID配置表。
     */
    private UdmClient() {
        ParameterMapping.getInstance();
    }

    /**
     * 获取设备基本信息，包括设备各个通道是否开启，通道是否支持DNS信息。
     *
     * @param mac 设备物理地址
     * @return DeviceBaseInfo 设备基本信息，包括所有通道状态，是否支持DNS等信息。
     */
    public DeviceBaseInfo getDeviceBaseInfo(String mac) {
        return DeviceBaseInfoManager.getDeviceBaseInfo(mac);
    }

    /**
     * 探测设备支持最大的通道，通道从1开始递增编号，可以用于获知设备支持多少个通道。
     *
     * @param mac 设备物理地址
     * @return channelNum 设备支持的最大通道数。
     */
    public int detectMaxSupportedChannel(String mac) {
        DeviceBaseInfo deviceBaseInfo = DeviceBaseInfoManager.getDeviceBaseInfo(mac);
        int channelNum = 0;
        if (deviceBaseInfo != null) {
            for (DeviceBaseInfo.ComBaseInfo comBaseInfo : deviceBaseInfo.getComBaseInfoList()) {
                if (comBaseInfo.isComEabled()) {
                    channelNum++;
                }
            }
        }

        return channelNum == 0 ? 1 : channelNum;
    }

    /**
     * 登录设备,注意用户名和密码之间要留一个空格，加密时需将空格和用户名一起BASE64编码。
     *
     * @param userName 登录设备的用户名(BASE64编码后的用户名)
     * @param password 登录设备的密码 (BASE64编码后的密码）
     * @param mac      设备物理地址
     * @param ip       设备IP地址，非必须。
     * @return 登录成功返回 true，登录失败返回false。
     */
    public boolean login(String userName, String password, String mac, String ip) {
        return UserAuth.login(userName, password, mac, ip);
    }

    /**
     * 退出登录
     *
     * @param mac 设备物理地址
     * @return 退出成功返回true，退出失败返回false。
     */
    public boolean logout(String mac) {
        return SysManager.logout(mac);
    }

    /**
     * 保存更新并重启设备
     *
     * @param mac 设备物理地址
     * @return 成功返回true，失败返回false。
     */
    public boolean saveAndReboot(String mac) {
        return SysManager.saveAndReboot(mac);
    }

    /**
     * 重启设备
     *
     * @param mac 设备物理地址
     * @return 成功返回true，失败返回false。
     */
    public boolean reboot(String mac) {
        return SysManager.reboot(mac);
    }

    /**
     * 搜索设备，采用UDP广播合UDP组播两种方式搜索。
     *
     * @param keyword 搜索关键字，支持匹配IP地址、设备MAC和设备名称。
     * @return DeviceModel 返回设备列表，返回信息包括设备IP地址、设备名称、版本号。
     */
    public ArrayList<DeviceModel> searchDevice(String keyword) {
        return DeviceSearch.searchDevice(keyword);
    }

    /**
     * 同步设备参数，将一设备的参数配置同步到另外一设备，网路配置类的参数除外。
     * 如果同步的参数中有不可以修改参数值，则同步会出现失败。
     *
     * @param srcDeviceParameter  源设备参数
     * @param distDeviceParameter 目标设备
     */

    public void syncDeviceParameter(DeviceParameter srcDeviceParameter, DeviceParameter distDeviceParameter) {
        DeviceParamSynchronize synchronize = new DeviceParamSynchronize();
        synchronize.syncDeviceChannelParam(srcDeviceParameter, distDeviceParameter);
    }

    /**
     * 读取设备参数
     *
     * @param deviceParameter 只包设备参数ID的对象，传入时DeviceParameter对象中的paramItems包含的ParameterItem只有参数ID，不需要设置参数值。
     * @return DeviceParameter 返回设备参数对象是输入时传入的同一个对象实例，此时返回的 ParameterItem 已经赋值，包括转译后的值 paramValue 和 转译前的值 byteValue。
     * 如果写入成功择 DeviceParameter 的isSuccessful() 返回 true，否则isSuccessful() 返回false。
     * 如果是没有登录，则 DeviceParameter 的 isNoPermission() 方法返回 true。
     */
    public DeviceParameter readDeviceParameter(DeviceParameter deviceParameter) {
        IParamReader reader = new ParamReader();
        return reader.readDeviceParam(deviceParameter);
    }

    /**
     * 将参数值写入设备
     *
     * @param deviceParameter 设备参数对象，包含需要修改的参数值集。
     * @return 返回写入设备的参数对象。如果写入成功择 DeviceParameter 的isSuccessful() 返回 true，否则isSuccessful() 返回false。
     * 如果是没有登录，则 DeviceParameter 的 isNoPermission() 方法返回 true。
     */
    public DeviceParameter writeDeviceParameter(DeviceParameter deviceParameter) {
        IParamWriter paramWriter = new ParamWriter();
        return paramWriter.writeDeviceParam(deviceParameter);
    }

    /**
     * 导出设置
     *
     * @param mac 设备物理地址
     * @return 返回设备参数密文
     */
    public DataModel<String> exportDeviceParameters(String mac) {

        DataModel<String> dataModel = new DataModel<>();

        int maxChannel = detectMaxSupportedChannel(mac);
        StringBuilder paramBuffer = new StringBuilder();
        DeviceParameter deviceParameter = new DeviceParameter(mac, String.valueOf(-1));
        List<ParameterItem> items = new ArrayList<>();
        for (int i = 0; i < maxChannel + 1; i++) {
            /**
             * 一个个通道读取，0通道表示非通道参数。
             */
            List<Parameter> parameters = ParameterMapping.getInstance().getChannelParamDef(i);
            for (Parameter parameter : parameters) {
                /**
                 * 部分私有参数不能导出，例如IP地址，MAC地址。
                 */
                if (parameter.isPublic()) {
                    items.add(new ParameterItem(parameter.getId(), null));
                }
            }
            deviceParameter.setParamItems(items);
        }
        /**
         * 读取通道参数值
         */
        readDeviceParameter(deviceParameter);
        if (deviceParameter.isSuccessful()) {
            for (ParameterItem item : items) {
                paramBuffer.append(item.getParamId() + "~" + item.getParamValue()).append('\001');
            }
            String content = AESUtil.aesEncrypt(paramBuffer.toString(), "8@jvcvIWun4SlA3!");
            dataModel.setCode(1);
            dataModel.setData(content);
        } else {
            dataModel.setCode(0);
        }
        return dataModel;
    }

    /**
     * 导入设置
     *
     * @param mac               设备物理地址
     * @param paramEncodeString 设备参数密文
     * @return 成功返回 true,失败返回false
     */
    public boolean importDeviceParameters(String mac, String paramEncodeString) {
        DeviceParameter deviceParameter = new DeviceParameter(mac, "-1");
        deviceParameter.setParamItems(new ArrayList<>());
        String data = AESUtil.aesDecrypt(paramEncodeString, "8@jvcvIWun4SlA3!");

        String[] parameterItems = data.split(String.valueOf('\001'));
        for (String item : parameterItems) {
            String[] paramItem = item.split("~");
            if (paramItem.length > 1) {
                deviceParameter.getParamItems().add(new ParameterItem(paramItem[0], paramItem[1]));
            }
        }
        writeDeviceParameter(deviceParameter);
        return deviceParameter.isSuccessful();
    }


    public static void main(String[] args) {

        String mac = "2c.ac:44:00:17:c5";
        System.out.println(mac.replaceAll("\\.","-"));
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        System.out.println(sdf.format(new Date()));
//        ParameterMapping.getInstance();
//        String userName = Base64.encode("admin ".getBytes());
//        String password = Base64.encode("admin".getBytes());
////        boolean isSuccess = UdmClient.getInstance().login(userName, password, mac, null);
////        System.out.println(isSuccess);
//        if (true) {
//            DataModel<String> content = UdmClient.getInstance().exportDeviceParameters(mac);
//            System.out.println(content.getData());
//            System.out.println(AESUtil.aesDecrypt(content.getData(), "8@jvcvIWun4SlA3!"));
//
////            boolean importReusult = UdmClient.getInstance().importDeviceParameters(mac,aa);
////            System.out.println(importReusult);
//        }
//        UdmClient.getInstance().searchDevice(null);

    }

}
