/*
 * This file is part of ProDisFuzz, modified on 29.03.14 10:44.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.helper;

public abstract class Constants {

    public final static int RELEASE_NUMBER = 1;
    public final static int RELEASE_NAME = 1;
    public final static String FILE_PREFIX = "prodisfuzz_";

    public final static String XML_PROTOCOL_ROOT = "prodisfuzz";
    public final static String XML_PROTOCOL_PARTS = "protocolparts";
    public final static String XML_PROTOCOL_PART_VAR = "partvar";
    public final static String XML_PROTOCOL_PART_FIX = "partfix";
    public final static String XML_PROTOCOL_CONTENT = "content";
    public final static String XML_PROTOCOL_MAXLENGTH = "maxlength";
    public final static String XML_PROTOCOL_MINLENGTH = "minlength";
    public final static String XML_NAMESPACE_PRODISFUZZ = "http://prodisfuzz.net";
}
