package com.ibamb.dnet.module.search;

import com.ibamb.dnet.module.beans.DeviceModel;
import com.ibamb.dnet.module.constants.Constants;
import com.ibamb.dnet.module.constants.Control;
import com.ibamb.dnet.module.core.ContextData;
import com.ibamb.dnet.module.core.SpecialParams;
import com.ibamb.dnet.module.instruct.beans.Parameter;
import com.ibamb.dnet.module.log.UdmLog;
import com.ibamb.dnet.module.util.Convert;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;


public class DeviceSearch {
    /**
     * 搜索设备(包含广播和组播两种方式搜索)
     * @param keyword
     * @return
     */
    public static ArrayList<DeviceModel> searchDevice(String keyword) {
        ArrayList<DeviceModel> deviceList1 = searchByBroadcast(keyword);
        ArrayList<DeviceModel> deviceList2 = searchByMulticast(keyword);

        for(DeviceModel deviceModel2:deviceList2){
            boolean isExists = false;
            for(DeviceModel deviceModel1:deviceList1 ){
                if(deviceModel1.getMac().equalsIgnoreCase(deviceModel2.getMac())){
                    isExists = true;
                    break;
                }
            }
            if(isExists){
                deviceList1.add(deviceModel2);
            }
        }
        //将搜索到的内容存放到上下文数据中对象中。
        ContextData contextData = ContextData.getInstance();
        contextData.cleanDeviceList();
        contextData.addAllDevice(deviceList1);
        return deviceList1;
    }

    /**
     * 构造搜索指令
     * @return
     */
    private static byte[] makeSearchByteData() {
        byte[] serachBytes = new byte[26];
        serachBytes[0] = Convert.intToUnsignedByte(Control.SEARCH_DEVICE);
        serachBytes[1] = Convert.intToUnsignedByte(0xff);
        serachBytes[2] = Convert.intToUnsignedByte(0x00);
        serachBytes[3] = Convert.intToUnsignedByte(0x1a);//根据参数个数修改
        serachBytes[4] = Convert.intToUnsignedByte(0xff);
        serachBytes[5] = Convert.intToUnsignedByte(0xff);
        serachBytes[6] = Convert.intToUnsignedByte(0xff);
        serachBytes[7] = Convert.intToUnsignedByte(0xff);
        //IP地址参数ID
        serachBytes[8] = Convert.intToUnsignedByte(0x00);
        serachBytes[9] = Convert.intToUnsignedByte(0x02);
        //IP地址子报文长度
        serachBytes[10] = Convert.intToUnsignedByte(0x03);
        //MAC地址参数ID
        serachBytes[11] = Convert.intToUnsignedByte(0x00);
        serachBytes[12] = Convert.intToUnsignedByte(0x21);
        //MAC地址子报文长度
        serachBytes[13] = Convert.intToUnsignedByte(0x03);
        //Device Name
        serachBytes[14] = Convert.intToUnsignedByte(0x00);
        serachBytes[15] = Convert.intToUnsignedByte(0xbf);
        serachBytes[16] = Convert.intToUnsignedByte(0x03);
        //Device SN
        serachBytes[17] = Convert.intToUnsignedByte(0x01);
        serachBytes[18] = Convert.intToUnsignedByte(0x61);
        serachBytes[19] = Convert.intToUnsignedByte(0x03);
        //Device FIRMWARE_VER
        serachBytes[20] = Convert.intToUnsignedByte(0x00);
        serachBytes[21] = Convert.intToUnsignedByte(0x00);
        serachBytes[22] = Convert.intToUnsignedByte(0x03);
        //Product Name
        serachBytes[23] = Convert.intToUnsignedByte(0x00);
        serachBytes[24] = Convert.intToUnsignedByte(0x07);
        serachBytes[25] = Convert.intToUnsignedByte(0x03);

        //hardware version
//       serachBytes[26] = Convert.intToUnsignedByte(0x01);
//       serachBytes[27] = Convert.intToUnsignedByte(0xb1);
//       serachBytes[28] = Convert.intToUnsignedByte(0x03);
        return serachBytes;
    }

    /**
     * UDP 广播方式搜索设备
     * @param keyword
     * @return
     */

    public static ArrayList<DeviceModel> searchByBroadcast(String keyword) {
        DatagramSocket datagramSocket = null;
        ArrayList<DeviceModel> deviceList = new ArrayList<>();
        try {
            datagramSocket = new DatagramSocket();
            datagramSocket.setBroadcast(true);
            datagramSocket.setSoTimeout(2000);
            byte[] serachBytes = makeSearchByteData();
            /**
             * 发送报文
             */
            InetAddress address = InetAddress.getByName(Constants.UDM_BROADCAST_IP);
            DatagramPacket sendDataPacket = new DatagramPacket(serachBytes, serachBytes.length, address, Constants.UDM_UDP_SERVER_PORT);
            datagramSocket.send(sendDataPacket);
            /**
             * 接收报文
             */
            byte[] recevBuffer = new byte[126];//如果需要返回更多信息需要重新拼帧。期望返回的长度也需要改变。
            DatagramPacket recevPacket = new DatagramPacket(recevBuffer, recevBuffer.length);
            //因有一定数量的设备会响应，所以只要连接没有关闭，就一直读取。
            while (!datagramSocket.isClosed()) {
                datagramSocket.receive(recevPacket);
                byte[] replyData = recevPacket.getData();
                DeviceModel deviceInfo = parse(replyData);
                if (deviceInfo == null) {
                    continue;
                }
                deviceInfo.setIndex(deviceList.size() + 1);
                boolean isExists = false;
                for (DeviceModel deviceInfo1 : deviceList) {
                    //如果重复搜索到设备则过滤掉。
                    if (deviceInfo1.getMac().equalsIgnoreCase(deviceInfo.getMac())) {
                        isExists = true;
                        break;
                    }
                }
                if (!isExists) {
                    if (keyword != null && keyword.trim().length() > 0) {
                        //如果ip 或者mac ,device name匹配上关键字，则返回该设备。
                        if ((deviceInfo.getIp().contains(keyword)
                                || deviceInfo.getMac().contains(keyword))
                                || deviceInfo.getDeviceName().contains(keyword)) {
                            deviceList.add(deviceInfo);
                        }
                    } else {
                        deviceList.add(deviceInfo);
                    }
                }
            }

        } catch (SocketException ex) {
            UdmLog.error(ex);

        } catch (UnknownHostException e) {
            UdmLog.error(e);

        } catch (IOException e) {
            UdmLog.error(e);

        } finally {
            if (datagramSocket != null) {
                datagramSocket.close();
            }
        }
        return deviceList;
    }

    /**
     * UDP 组播方式 搜索设备
     * @param keyword
     * @return
     */

    public static ArrayList<DeviceModel> searchByMulticast(String keyword) {


        MulticastSocket multicastSocket = null;
        ArrayList<DeviceModel> deviceList = new ArrayList<>();
        try {
            multicastSocket = new MulticastSocket(5);
            multicastSocket.setSoTimeout(2000);
            byte[] serachBytes = makeSearchByteData();
            /**
             * 发送报文
             */
            InetAddress address = InetAddress.getByName("224.149.237.193");
            multicastSocket.setLoopbackMode(false);
            multicastSocket.joinGroup(address);
            DatagramPacket sendDataPacket = new DatagramPacket(serachBytes, serachBytes.length, address, 27473);
            multicastSocket.send(sendDataPacket);
            /**
             * 接收报文
             */
            byte[] recevBuffer = new byte[126];//如果需要返回更多信息需要重新拼帧。期望返回的长度也需要改变。
            DatagramPacket recevPacket = new DatagramPacket(recevBuffer, recevBuffer.length);
            //因有一定数量的设备会响应，所以只要连接没有关闭，就一直读取。
            while (!multicastSocket.isClosed()) {
                multicastSocket.receive(recevPacket);
                byte[] replyData = recevPacket.getData();
                DeviceModel deviceInfo = parse(replyData);
                if (deviceInfo == null) {
                    continue;
                }
                deviceInfo.setIndex(deviceList.size() + 1);
                boolean isExists = false;
                for (DeviceModel deviceInfo1 : deviceList) {
                    //如果重复搜索到设备则过滤掉。
                    if (deviceInfo1.getMac().equalsIgnoreCase(deviceInfo.getMac())) {
                        isExists = true;
                        break;
                    }
                }
                if (!isExists) {
                    if (keyword != null && keyword.trim().length() > 0) {
                        //如果ip 或者mac ,device name匹配上关键字，则返回该设备。
                        if ((deviceInfo.getIp().contains(keyword)
                                || deviceInfo.getMac().contains(keyword))
                                || deviceInfo.getDeviceName().contains(keyword)) {
                            deviceList.add(deviceInfo);
                        }
                    } else {
                        deviceList.add(deviceInfo);
                    }
                }
            }

        } catch (SocketException ex) {
            UdmLog.error(ex);

        } catch (UnknownHostException e) {
            UdmLog.error(e);

        } catch (IOException e) {
            UdmLog.error(e);

        } finally {
            if (multicastSocket != null) {
                multicastSocket.close();
            }
        }
        return deviceList;
    }


    /**
     * 解析帧获取设备信息。
     *
     * @param data
     * @return
     */
    private static DeviceModel parse(byte[] data) {
        //解析一帧
        DeviceModel devinfo = null;
        try {
            byte control = data[0];//返回值
            if (control == Control.ACKNOWLEDGE) {
                byte id = data[1];//通信编号
                byte[] length = Arrays.copyOfRange(data, 2, 4);//报文总长度
                if (Convert.bytesToShort(length) > 0) {
//              byte[] ipTypeId = Arrays.copyOfRange(replyData, dPos + 4, dPos + 6);//ip参数ID
//              byte subFrameLength = replyData[dPos + 6];//子帧长度
                    byte[] ipValue = Arrays.copyOfRange(data, 7, 11);//IP地址
                    StringBuilder ipBuffer = new StringBuilder();
                    for (int i = 0; i < ipValue.length; i++) {
                        String tempIp = String.valueOf(((int) ipValue[i]) & 0xff);
                        ipBuffer.append(tempIp).append(".");
                    }
                    ipBuffer.deleteCharAt(ipBuffer.length() - 1);
//                byte[] macTypeId = Arrays.copyOfRange(data, 11, 13);//mac参数ID
                    byte[] macValue = Arrays.copyOfRange(data, 14, 20);//mac地址
                    StringBuilder macBuffer = new StringBuilder();
                    for (int i = 0; i < macValue.length; i++) {
                        String macbit = Convert.toHexString(macValue[i]);
                        macbit = macbit.length() < 2 ? "0" + macbit : macbit;
                        macBuffer.append(macbit).append(":");
                    }
                    macBuffer.deleteCharAt(macBuffer.length() - 1);
                    //device name
                    byte[] deviceNameBytes = Arrays.copyOfRange(data, 23, 53);
                    StringBuilder deviceNameBuffer = new StringBuilder();
                    for (int k = 0; k < deviceNameBytes.length; k++) {
                        if (deviceNameBytes[k] != 0) {
                            char c = (char) deviceNameBytes[k];
                            deviceNameBuffer.append(c);
                        }
                    }
                    String deviceName = deviceNameBuffer.toString().trim();
                    deviceName = deviceName.length() == 0 ? "No Device Name" : deviceName;
                    //device sn
                    byte[] deviceSNBytes = Arrays.copyOfRange(data, 56, 80);//device sn
                    StringBuilder deviceSnBuffer = new StringBuilder();
                    for (int k = 0; k < deviceSNBytes.length; k++) {
                        if (deviceSNBytes[k] != 0) {
                            char c = (char) deviceSNBytes[k];
                            deviceSnBuffer.append(c);
                        }
                    }
                    devinfo = new DeviceModel(ipBuffer.toString(), macBuffer.toString());
                    devinfo.setDeviceName(deviceName);
                    //device firmware version
                    byte[] firmwareVersion = Arrays.copyOfRange(data, 83, 103);//firmware version
                    StringBuilder versionBuffer = new StringBuilder();
                    for (int k = 0; k < firmwareVersion.length; k++) {
                        // if (firmwareVersion[k] != 0) {
                        char c = (char) firmwareVersion[k];
                        versionBuffer.append(c);
                        //  }
                    }
                    devinfo.setFirmwareVersion(versionBuffer.toString().trim());
                    //Product Name
                    byte[] productName = Arrays.copyOfRange(data, 106, 126);//firmware version
                    StringBuilder productNameBuffer = new StringBuilder();
                    for (int k = 0; k < productName.length; k++) {

                        char c = (char) productName[k];
                        productNameBuffer.append(c);

                    }
                    devinfo.setPruductName(productNameBuffer.toString().trim());
                    //hardware version
//                    byte[] hardwareVersion = Arrays.copyOfRange(data, 129, 149);//firmware version
//                    StringBuilder hardwareVersionBuffer = new StringBuilder();
//                    for (int k = 0; k < hardwareVersion.length; k++) {
//                        char c = (char) hardwareVersion[k];
//                        hardwareVersionBuffer.append(c);
//
//                    }
//                    devinfo.setHardwareVersion(hardwareVersionBuffer.toString());
                }

            }
        } catch (Exception e) {
            UdmLog.error(e);
        }
        return devinfo;
    }

    public static void main(String[] args){
        DeviceSearch.searchDevice(null);
    }
}

