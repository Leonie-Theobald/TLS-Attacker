/*
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tlsattacker.transport.file;

import de.rub.nds.tlsattacker.transport.Connection;
import de.rub.nds.tlsattacker.transport.TransportHandler;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class FileTransportHandler extends TransportHandler {

    private Logger LOGGER = LogManager.getLogger();

    // protected DatagramSocket socket;

    // protected int port;

    protected FileReader inputFile;
    protected FileWriter outputFile;

    public FileTransportHandler(Connection con) {
        super(con);
    }
    /*
        public UdpTransportHandler(long firstTimeout, long timeout, ConnectionEndType type) {
            super(firstTimeout, timeout, type);
        }
    */
    @Override
    public void setTimeout(long timeout) {
        // TODO: macht noch keinen Sinn
        // try {
        this.timeout = timeout;
        // socket.setSoTimeout((int) timeout);
        // } catch (SocketException ex) {
        // LOGGER.error("Could not adjust socket timeout", ex);
        // }
    }

    @Override
    public void closeConnection() throws IOException {
        inputFile.close();
        outputFile.close();
    }

    @Override
    public boolean isClosed() throws IOException {
        return (inputFile.read() == -1);
    }

    public Integer getSrcPort() {
        /*
        if (socket == null) {
            // mimic socket.getLocalPort() behavior as if socket was closed
            return -1;
        }

        return socket.getLocalPort();
        */
        return 777;
    }

    public Integer getDstPort() {
        /*
        if (socket == null) {
            // mimic socket.getPort() behavior as if socket was not connected
            return -1;
        }

        return socket.getPort();
        */
        return 222222;
    }
}
