package com.ibamb.dnet.module.instruct;

import com.ibamb.dnet.module.beans.DeviceParameter;


public interface IParamWriter {
    /**
     * 写参数值
     * @param deviceParameter
     * @return
     */
    public DeviceParameter writeDeviceParam(DeviceParameter deviceParameter);
}
