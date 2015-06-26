/*
 * This file is part of ProDisFuzz, modified on 6/26/15 9:26 PM.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.connector;

import model.modificator.FuzzedData;

public class TCPConnector extends AbstractConnector {

    @Override
    protected boolean connect() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected boolean call(FuzzedData data) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void setTarget(String... args) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
