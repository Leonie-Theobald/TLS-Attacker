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

    protected FileReader inputFile;
    protected FileWriter outputFile;

    public FileTransportHandler(Connection con) {
        super(con);
    }

    @Override
    public void setTimeout(long timeout) {
        // waiting for a file to read doesn't have a time out available
        // nothing todo here really
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
}
