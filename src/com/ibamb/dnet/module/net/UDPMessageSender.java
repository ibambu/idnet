package com.ibamb.dnet.module.net;

import com.ibamb.dnet.module.constants.Constants;
import com.ibamb.dnet.module.log.UdmLog;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;


public class UDPMessageSender {
    /**
     * 发送数据包
     *
     * @param sendData
     * @param expectReplyLenth
     * @return
     */
    public byte[] sendByBroadcast(byte[] sendData, int expectReplyLenth) {

        InetAddress address;
        byte[] recevBuffer = new byte[expectReplyLenth];
        byte[] recevData = null;
        DatagramPacket recevPacket = new DatagramPacket(recevBuffer, recevBuffer.length);
        DatagramSocket datagramSocket = null;
        try {
            datagramSocket = new DatagramSocket();
            address = InetAddress.getByName(Constants.UDM_BROADCAST_IP);
            DatagramPacket sendDataPacket = new DatagramPacket(sendData, sendData.length, address, Constants.UDM_UDP_SERVER_PORT);
            datagramSocket.setBroadcast(true);
            // 发送数据
            datagramSocket.send(sendDataPacket);
            // 接收数据
            Thread.sleep(200);//延迟200ms，然后再读取数据。
            datagramSocket.receive(recevPacket);
            recevData = recevPacket.getData();
        }catch (Exception ex) {
            UdmLog.error(ex);
        }
        return recevData;
    }

    /**
     * 单播方式发送数据
     *
     * @param sendData
     * @param expectReplyLenth
     * @param ip
     * @return
     */
    public byte[] sendByUnicast(byte[] sendData, int expectReplyLenth,String ip) {
        InetAddress address;
        byte[] recevBuffer = new byte[expectReplyLenth];
        byte[] recevData = null;
        DatagramPacket recevPacket = new DatagramPacket(recevBuffer, recevBuffer.length);
        DatagramSocket datagramSocket = null;
        try {
            datagramSocket = new DatagramSocket();
            datagramSocket.setSoTimeout(3000);//一秒后无返回则超时处理，避免任务无法终止导致内存泄露。
            address = InetAddress.getByName(Constants.UDM_BROADCAST_IP);

            DatagramPacket sendDataPacket = new DatagramPacket(sendData, sendData.length, address, Constants.UDM_UDP_SERVER_PORT);
            // 发送数据
            datagramSocket.send(sendDataPacket);
            // 接收数据
//            Thread.sleep(200);//延迟200ms，然后再读取数据。
            datagramSocket.receive(recevPacket);
            recevData = recevPacket.getData();

        } catch (Exception ex) {
            UdmLog.error(ex);
        } finally {
            if (datagramSocket != null) {
                datagramSocket.close();
                datagramSocket.disconnect();
            }
        }
        return recevData;
    }

    /**
     * 组播方式发送数据
     * @param sendData
     * @param expectReplyLenth
     * @param ip
     * @return
     */
    public byte[] sendByMulticast(byte[] sendData,int expectReplyLenth,String ip){
        MulticastSocket multicastSocket = null;
        DatagramPacket dataPacket = null;
        byte[] recevData = null;
        byte[] recevBuffer = new byte[expectReplyLenth];
        DatagramPacket recevPacket = new DatagramPacket(recevBuffer, recevBuffer.length);
        try{

            multicastSocket = new MulticastSocket(5);
            InetAddress address = InetAddress.getByName(ip);
            multicastSocket.joinGroup(address);
            dataPacket = new DatagramPacket(sendData, sendData.length, address,5);
            multicastSocket.send(dataPacket);
            multicastSocket.receive(recevPacket);
            recevData = recevPacket.getData();
        }catch (Exception e){
            UdmLog.error(e);
        }finally {
            if(multicastSocket!=null){
                multicastSocket.close();
            }
        }
        return recevData;
    }
}
