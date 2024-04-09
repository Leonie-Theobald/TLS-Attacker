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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientFileTransportHandler extends FileTransportHandler {

    private static final Logger LOGGER = LogManager.getLogger();

    protected String hostname;
    protected long connectionTimeout;
    private boolean retryFailedSocketInitialization = false;

    public ClientFileTransportHandler(Connection connection) {
        super(connection);
        this.connectionTimeout = connection.getConnectionTimeout();
    }

    @Override
    public void closeConnection() throws IOException {
        // nothing to do here as there is no active connection
    }

    @Override
    public void preInitialize() throws IOException {
        // nothing to do here
    }

    @Override
    public void initialize() throws IOException {
        PushbackInputStream inputStream =
                new PushbackInputStream(
                        new FileInputStream(
                                "/Users/lth/Library/Mobile Documents/com~apple~CloudDocs/Zweitstudium/Module/00_Masterarbeit/Netzwerk/Repo/TLS-Attacker/StreamFiles/FromServer.txt"));

        OutputStream outputStream =
                new FileOutputStream(
                        "/Users/lth/Library/Mobile Documents/com~apple~CloudDocs/Zweitstudium/Module/00_Masterarbeit/Netzwerk/Repo/TLS-Attacker/StreamFiles/FromClient.txt");

        setStreams(inputStream, outputStream);
    }

    @Override
    public boolean isClosed() throws IOException {
        // TODO: make real check
        return true;
    }

    @Override
    public void closeClientConnection() throws IOException {
        closeConnection();
    }
}
