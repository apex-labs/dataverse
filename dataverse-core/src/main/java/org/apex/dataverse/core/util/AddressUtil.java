package org.apex.dataverse.core.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @version : v1.0
 * @author : Danny.Huo
 * @date : 2023/4/28 10:48
 */
public class AddressUtil {

    public static void main(String[] args) throws IOException {
        System.out.println(getLocalAddress());
        System.out.println(getFreePort());
    }

    private static String localAddress = null;

    private static String localHostname = null;

    static {
        if(null == localAddress) {
            localAddress = getLocalAddress();
        }

        if(null == localHostname) {
            localHostname = getLocalHostname();
        }
    }

    /**
     * 获取本机地址
     *
     * @return
     * @throws UnknownHostException
     */
    public static String getLocalAddress() {
        if(null != localAddress) {
            return localAddress;
        }

        InetAddress local;
        try {
            local = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        return local.getHostAddress();
    }

    /**
     * 获取本机Hostname
     *
     * @return
     * @throws UnknownHostException
     */
    public static String getLocalHostname() {
        if(null != localHostname) {
            return localHostname;
        }

        InetAddress local;
        try {
            local = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        return local.getHostName();
    }

    /**
     * 获取空闲端口
     * @return
     * @throws IOException
     */
    public static Integer getFreePort() throws IOException {
        InetSocketAddress addr = new InetSocketAddress(0);
        try (Socket socket = new Socket()){
            socket.bind(addr);
            socket.close();
            return socket.getLocalPort();
        }
    }
}
