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

public class ServerFileTransportHandler extends FileTransportHandler {

    private static final Logger LOGGER = LogManager.getLogger();

    public ServerFileTransportHandler(Connection con) {
        super(con);
    }

    @Override
    public void closeConnection() throws IOException {
        // nothing to do here as there is no active connection
    }

    @Override
    public void initialize() throws IOException {
        new FileOutputStream(
                        "/Users/lth/Library/Mobile Documents/com~apple~CloudDocs/Zweitstudium/Module/00_Masterarbeit/Netzwerk/Repo/TLS-Attacker/StreamFiles/FromServer.txt",
                        false)
                .close();
        new FileOutputStream(
                        "/Users/lth/Library/Mobile Documents/com~apple~CloudDocs/Zweitstudium/Module/00_Masterarbeit/Netzwerk/Repo/TLS-Attacker/StreamFiles/FromClient.txt",
                        false)
                .close();

        PushbackInputStream inputStream =
                new PushbackInputStream(
                        new FileInputStream(
                                "/Users/lth/Library/Mobile Documents/com~apple~CloudDocs/Zweitstudium/Module/00_Masterarbeit/Netzwerk/Repo/TLS-Attacker/StreamFiles/FromClient.txt"));

        OutputStream outputStream =
                new FileOutputStream(
                        "/Users/lth/Library/Mobile Documents/com~apple~CloudDocs/Zweitstudium/Module/00_Masterarbeit/Netzwerk/Repo/TLS-Attacker/StreamFiles/FromServer.txt");

        setStreams(inputStream, outputStream);
    }

    @Override
    public void preInitialize() throws IOException {
        // nothing to do here
    }

    @Override
    public boolean isClosed() throws IOException {
        // TODO: make real check
        return true;
    }

    @Override
    public void closeClientConnection() throws IOException {
        // nothing to do here as there is no active connection
    }

    public void closeServerSocket() throws IOException {
        closeConnection();
    }
}
