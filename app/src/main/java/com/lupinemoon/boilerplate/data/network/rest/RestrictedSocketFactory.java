package com.lupinemoon.boilerplate.data.network.rest;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import javax.net.SocketFactory;

import timber.log.Timber;

/**
 * Restricted Socket Factory
 * <p>
 * OkHttp buffers to the network interface but the network interface's default
 * buffer size is sometimes set very high e.g. 512Kb which makes tracking
 * upload progress impossible as the upload content is sitting in the network
 * interface buffer waiting to be transmitted.
 * <p>
 * So here, we create socket factory that forces all sockets to have a restricted
 * send buffer size. So that further down the chain in OkHttps' RequestBody we can
 * track the actual progress to the nearest [sendBufferSize] unit.
 */
class RestrictedSocketFactory extends SocketFactory {

    private int sendBufferSize;

    RestrictedSocketFactory(int sendBufferSize) {
        this.sendBufferSize = sendBufferSize;
        try {
            Socket socket = new Socket();
            Timber.d(
                    "Changing SO_SNDBUF on new sockets from %d to %d.",
                    socket.getSendBufferSize(),
                    sendBufferSize);
        } catch (SocketException e) {
            Timber.e(e.toString());
        }
    }

    @Override
    public Socket createSocket() throws IOException {
        return updateSendBufferSize(new Socket());
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException {
        return updateSendBufferSize(new Socket(host, port));
    }

    @Override
    public Socket createSocket(
            String host,
            int port,
            InetAddress localHost,
            int localPort) throws IOException {
        return updateSendBufferSize(new Socket(host, port, localHost, localPort));
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return updateSendBufferSize(new Socket(host, port));
    }

    @Override
    public Socket createSocket(
            InetAddress address,
            int port,
            InetAddress localAddress,
            int localPort)
            throws IOException {
        return updateSendBufferSize(new Socket(address, port, localAddress, localPort));
    }

    private Socket updateSendBufferSize(Socket socket) throws IOException {
        socket.setSendBufferSize(sendBufferSize);
        return socket;
    }
}