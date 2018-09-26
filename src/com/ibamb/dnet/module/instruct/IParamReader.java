package com.ibamb.dnet.module.instruct;

import com.ibamb.dnet.module.beans.DeviceParameter;


public interface IParamReader {
    /**
     * 读取参数值
     * @param deviceParameter
     * @return
     */
    public DeviceParameter readDeviceParam(DeviceParameter deviceParameter);
}
