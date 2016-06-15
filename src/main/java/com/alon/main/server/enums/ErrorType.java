package com.alon.main.server.enums;

import static com.alon.main.server.conf.Const.ERROR_FAIL_ANY;
import static com.alon.main.server.conf.Const.ERROR_REPLACE;

/**
 * Created by alon_ss on 6/14/16.
 */
public enum ErrorType {
    FAIL_ANY(ERROR_FAIL_ANY), REPLACE(ERROR_REPLACE);

    private String val;

    ErrorType(String errorStr) {
        this.val = errorStr;
    }
}
