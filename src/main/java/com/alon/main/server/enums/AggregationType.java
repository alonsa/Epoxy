package com.alon.main.server.enums;

import static com.alon.main.server.conf.Conf.*;

/**
 * Created by alon_ss on 6/14/16.
 */
public enum AggregationType {
    COMBINED(AGGREGATION_COMBINED), APPENDED(AGGREGATION_APPENDED);

    private String val;

    AggregationType(String errorStr) {
        this.val = errorStr;
    }
}
