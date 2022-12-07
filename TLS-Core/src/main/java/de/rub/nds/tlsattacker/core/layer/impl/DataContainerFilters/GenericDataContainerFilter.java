/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.layer.impl.DataContainerFilters;

import de.rub.nds.tlsattacker.core.layer.data.DataContainer;
import de.rub.nds.tlsattacker.core.layer.DataContainerFilter;

public class GenericDataContainerFilter extends DataContainerFilter {

    private final Class filteredClass;

    public GenericDataContainerFilter(Class<? extends DataContainer> filteredClass) {
        this.filteredClass = filteredClass;
    }

    @Override
    public boolean filterApplies(DataContainer container) {
        return filteredClass.equals(container.getClass());
    }

}