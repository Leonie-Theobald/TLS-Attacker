/*
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tlsattacker.core.layer.context;

import de.rub.nds.tlsattacker.core.state.Context;
import java.util.ArrayList;

/** Holds all runtime variables of the TCPLayer including measurement results. */
public class TimeableTcpContext extends TcpContext {

    private ArrayList<Long> allMeasurements;

    public TimeableTcpContext(Context context) {
        super(context);
        context.setTcpContext(this);
    }

    public void addMeasurement(Long measurement) {
        if (allMeasurements == null) {
            allMeasurements = new ArrayList<>();
            allMeasurements.add(measurement);
        } else {
            allMeasurements.add(measurement);
        }
    }

    public ArrayList<Long> getAllMeasurements() {
        if (allMeasurements == null) {
            return new ArrayList<>();
        } else {
            return allMeasurements;
        }
    }
}
