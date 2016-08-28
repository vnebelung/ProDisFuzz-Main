/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.util;

/**
 * This class contains constants that are not assigned to a single class.
 */
@SuppressWarnings("HardCodedStringLiteral")
public enum Constants {
    ;

    public static final int RELEASE_NUMBER = 4;
    public static final String FILE_PREFIX = "prodisfuzz_";

    public static final String XML_TAG_NAME_ROOT = "prodisfuzz";
    public static final String XML_TAG_NAME_BLOCKS = "protocolblocks";
    public static final String XML_TAG_NAME_BLOCK_VAR = "blockvar";
    public static final String XML_TAG_NAME_BLOCK_FIX = "blockfix";
    public static final String XML_TAG_NAME_CONTENT = "content";
    public static final String XML_TAG_NAME_MAX_LENGTH = "maxlength";
    public static final String XML_TAG_NAME_MIN_LENGTH = "minlength";
    public static final String XML_TAG_NAME_SIGNATURE = "signature";
    public static final String RECORDINGS_DIRECTORY_POSTFIX = "_recordings";

    public static final int LOG_ENTRY_SIZE = 500;

    public static final String UPDATE_URL = "http://prodisfuzz.net/updater/releases.xml";
}
