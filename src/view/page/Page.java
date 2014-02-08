/*
 * This file is part of ProDisFuzz, modified on 03.02.14 23:23.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.page;

public interface Page {

    /**
     * Initializes the process of the model connected with this page.
     */
    public void initProcess();
}
