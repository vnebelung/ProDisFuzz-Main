/*
 * This file is part of ProDisFuzz, modified on 31.03.14 19:36.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.utilities;

@SuppressWarnings("HardCodedStringLiteral")
public enum Constants {
    ;

    public static final int RELEASE_NUMBER = 4;
    public static final String FILE_PREFIX = "prodisfuzz_";

    public static final String XML_PROTOCOL_ROOT = "prodisfuzz";
    public static final String XML_PROTOCOL_BLOCKS = "protocolblocks";
    public static final String XML_PROTOCOL_BLOCK_VAR = "blockvar";
    public static final String XML_PROTOCOL_BLOCK_FIX = "blockfix";
    public static final String XML_PROTOCOL_CONTENT = "content";
    public static final String XML_PROTOCOL_MAXLENGTH = "maxlength";
    public static final String XML_PROTOCOL_MINLENGTH = "minlength";
    public static final String XML_NAMESPACE_PRODISFUZZ = "http://prodisfuzz.net";
    public static final String XML_NAMESPACE_SIGNATURE = "http://www.w3.org/2000/09/xmldsig#";

}
