/*
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tlsattacker.core.workflow.action;

import de.rub.nds.tlsattacker.core.exceptions.ActionExecutionException;
import de.rub.nds.tlsattacker.core.layer.context.TcpContext;
import de.rub.nds.tlsattacker.core.layer.context.TimeableTcpContext;
import de.rub.nds.tlsattacker.core.state.State;
import de.rub.nds.tlsattacker.transport.TimeableTransportHandler;
import de.rub.nds.tlsattacker.transport.TransportHandler;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Allows the user to enable / disable measuring when using the TimingClientTcpTransportHandler.
 * Disabling the measurements prevents TLS-Attacker from receiving in the middle of a flight if
 * multiple SendActions are used. To enable measurements, place this action immediately before the
 * last SendAction.
 */
@XmlRootElement
public class LogLastMeasurementAction extends ConnectionBoundAction {
    private static final Logger LOGGER = LogManager.getLogger();
    boolean asPlanned = false;

    public LogLastMeasurementAction() {}

    @Override
    public void execute(State state) throws ActionExecutionException {
        Long lastMeasurement;

        TransportHandler transportHandler =
                state.getTlsContext(getConnectionAlias()).getTransportHandler();
        if (transportHandler instanceof TimeableTransportHandler) {
            TimeableTransportHandler timeableTransportHandler =
                    (TimeableTransportHandler) transportHandler;

            lastMeasurement = timeableTransportHandler.getLastMeasurement();
            if (lastMeasurement == null) {
                LOGGER.warn("Nothing has been logged so far and therefore nothing can be stored");
                return;
            }
        } else {
            LOGGER.warn(
                    "Can't log measurement as transport handler is not suited to collect measurements");
            asPlanned = false;
            return;
        }

        TcpContext tcpContext = state.getTcpContext();
        if (tcpContext instanceof TimeableTcpContext) {
            TimeableTcpContext timeableTcpContext = (TimeableTcpContext) tcpContext;

            timeableTcpContext.addMeasurement(lastMeasurement);
            LOGGER.debug("Logged last measurement: {}", lastMeasurement);
        } else {
            LOGGER.warn(
                    "Can't log measurement as transport context is not suited to collect measurements");
            asPlanned = false;
            return;
        }

        asPlanned = true;
    }

    @Override
    public void reset() {
        asPlanned = false;
    }

    @Override
    public boolean executedAsPlanned() {
        return asPlanned;
    }
}
