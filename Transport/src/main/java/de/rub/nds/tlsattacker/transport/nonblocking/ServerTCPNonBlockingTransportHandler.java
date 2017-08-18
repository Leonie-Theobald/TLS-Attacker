/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2017 Ruhr University Bochum / Hackmanit GmbH
 *
 * Licensed under Apache License 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlsattacker.transport.nonblocking;

import de.rub.nds.tlsattacker.transport.ConnectionEndType;
import de.rub.nds.tlsattacker.transport.TransportHandler;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Robert Merget <robert.merget@rub.de>
 */
public class ServerTCPNonBlockingTransportHandler extends TransportHandler {

    private final int port;

    private ServerSocket serverSocket;

    private Socket clientSocket;

    private AcceptorCallable callable;

    private FutureTask<Socket> task;

    private Thread thread;

    public ServerTCPNonBlockingTransportHandler(long timeout, int port) {
        super(timeout, ConnectionEndType.SERVER);
        this.port = port;
    }

    @Override
    public void closeConnection() throws IOException {
        if (serverSocket == null) {
            throw new IOException("TransportHandler is not initialised!");
        }
        serverSocket.close();
        if (clientSocket != null) {
            clientSocket.close();
        }
    }

    @Override
    public void initialize() throws IOException {
        serverSocket = new ServerSocket(port);
        callable = new AcceptorCallable(serverSocket);
        task = new FutureTask(callable);
        thread = new Thread(task);
        thread.start();
        recheck();
    }

    public void recheck() throws IOException {
        if (task != null) {
            if (task.isDone()) {
                try {
                    clientSocket = task.get();
                    setStreams(clientSocket.getInputStream(), clientSocket.getOutputStream());
                } catch (InterruptedException | ExecutionException ex) {
                    LOGGER.warn("Could not retrieve clientSocket");
                    LOGGER.debug(ex);
                }
            } else {
                LOGGER.debug("TransportHandler not yet connected");
            }
        } else {
            throw new IOException("Transporthandler is not initalized!");
        }
    }

    public void recheck(long timeout) throws IOException {
        try {
            if (task != null) {
                clientSocket = task.get(timeout, TimeUnit.MILLISECONDS);
                if (clientSocket != null) {
                    setStreams(clientSocket.getInputStream(), clientSocket.getOutputStream());
                }
            }
        } catch (InterruptedException | ExecutionException ex) {
            LOGGER.warn("Could not retrieve clientSocket");
            LOGGER.debug(ex);
        } catch (TimeoutException ex) {
            Logger.getLogger(ServerTCPNonBlockingTransportHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}