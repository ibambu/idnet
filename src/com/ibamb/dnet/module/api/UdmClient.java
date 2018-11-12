package com.ibamb.dnet.module.api;

import com.ibamb.dnet.module.beans.DeviceModel;
import com.ibamb.dnet.module.beans.DeviceParameter;
import com.ibamb.dnet.module.core.ParameterMapping;
import com.ibamb.dnet.module.instruct.IParamReader;
import com.ibamb.dnet.module.instruct.IParamWriter;
import com.ibamb.dnet.module.instruct.ParamReader;
import com.ibamb.dnet.module.instruct.ParamWriter;
import com.ibamb.dnet.module.search.DeviceSearch;
import com.ibamb.dnet.module.security.UserAuth;
import com.ibamb.dnet.module.sync.DeviceParamSynchronize;
import com.ibamb.dnet.module.sys.SysManager;

import java.util.ArrayList;

/**
 * 设备配置客户端，通过调用该类的实例可以读/写设备参数。
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
     * 加载设备参数ID配置表
     */
    private UdmClient() {
        ParameterMapping.getInstance();
    }


    /**
     * 是否启用DNS
     *
     * @param mac 设备物理地址
     * @return 返回true 表示已经启用DNS，支持域名解析; 返回false 表示不支持DNS。
     */
    public boolean isDNSEnabled(String mac) {
        return false;
    }

    /**
     * 探测设备支持最大的通道，通道从1开始递增编号，可以用于获知设备支持多少个通道。
     *
     * @param mac
     * @return
     */
    public int detectMaxSupportedChannel(String mac) {
        return 0;
    }

    /**
     * 登录设备
     *
     * @param mac      设备物理地址
     * @param userName 登录设备的用户名(BASE64编码)
     * @param password 登录设备的密码 (BASE64编码）
     * @return 登录成功返回 true，登录失败返回false。
     */
    public boolean login(String mac, String ip, String userName, String password) {
        return UserAuth.login(userName, password, mac, ip);
    }

    /**
     * 退出登录
     *
     * @param mac 设备物理地址
     * @return 退出成功返回true，退出失败返回false。
     */
    public boolean logout(String mac) {
        return false;
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
     * 搜索设备，采用UDP广播合UDP组播两种方式搜索。
     *
     * @return 返回设备列表
     */
    public ArrayList<DeviceModel> searchDevice(String keyword) {
        return DeviceSearch.searchDevice(keyword);
    }

    /**
     * 同步设备参数，将一设备的参数配置同步到另外一设备，网路配置类的参数除外。
     * 如果同步的参数种有只读参数，则同步会出现失败。
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
     * @param deviceParameter 只包设备参数ID的对象
     * @return 返回设备参数对象，包含参数ID和参数值的对象。如果写入成功择 DeviceParameter 的isSuccessful() 返回 true，否则isSuccessful() 返回false。
     * 如果是没有登录，则 DeviceParameter 的 isNoPermission() 方法返回 true。
     */
    public DeviceParameter readDeviceParameter(DeviceParameter deviceParameter) {
        IParamReader reader = new ParamReader();
        return reader.readDeviceParam(deviceParameter);
    }

    /**
     * 将参数值写入设备
     *
     * @param deviceParameter
     * @return 返回写入设备的参数对象。如果写入成功择 DeviceParameter 的isSuccessful() 返回 true，否则isSuccessful() 返回false。
     * 如果是没有登录，则 DeviceParameter 的 isNoPermission() 方法返回 true。
     */
    public DeviceParameter writeDeviceParameter(DeviceParameter deviceParameter) {
        IParamWriter paramWriter = new ParamWriter();
        return paramWriter.writeDeviceParam(deviceParameter);
    }

}