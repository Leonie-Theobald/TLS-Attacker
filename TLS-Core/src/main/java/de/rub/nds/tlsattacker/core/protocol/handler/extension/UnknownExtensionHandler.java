/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2021 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tlsattacker.core.protocol.handler.extension;

import de.rub.nds.tlsattacker.core.protocol.message.extension.UnknownExtensionMessage;
import de.rub.nds.tlsattacker.core.protocol.parser.extension.UnknownExtensionParser;
import de.rub.nds.tlsattacker.core.protocol.preparator.extension.UnknownExtensionPreparator;
import de.rub.nds.tlsattacker.core.protocol.serializer.extension.UnknownExtensionSerializer;
import de.rub.nds.tlsattacker.core.state.TlsContext;
import java.io.InputStream;

public class UnknownExtensionHandler extends ExtensionHandler<UnknownExtensionMessage> {

    public UnknownExtensionHandler(TlsContext context) {
        super(context);
    }

    @Override
    public void adjustTLSExtensionContext(UnknownExtensionMessage message) {
    }

    @Override
    public UnknownExtensionParser getParser(InputStream stream) {
        return new UnknownExtensionParser(stream, context.getConfig());
    }

    @Override
    public UnknownExtensionPreparator getPreparator(UnknownExtensionMessage message) {
        return new UnknownExtensionPreparator(context.getChooser(), message, getSerializer(message));
    }

    @Override
    public UnknownExtensionSerializer getSerializer(UnknownExtensionMessage message) {
        return new UnknownExtensionSerializer(message);
    }

}
