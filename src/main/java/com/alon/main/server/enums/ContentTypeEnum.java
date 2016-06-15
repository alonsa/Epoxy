package com.alon.main.server.enums;

import static com.alon.main.server.conf.Const.CONTENT_TYPE_APPLICATION_JSON;
import static com.alon.main.server.conf.Const.CONTENT_TYPE_TEXT_XML;

/**
 * Created by alon_ss on 6/14/16.
 */
public enum ContentTypeEnum {
    TEXT_XML(CONTENT_TYPE_TEXT_XML), APPLICATION_JSON(CONTENT_TYPE_APPLICATION_JSON), ;

    private String val;

    private ContentTypeEnum(final String s) { val = s; }

    @Override
    public String toString() {
        return val;
    }

    public static ContentTypeEnum fromString(String text) {
        if (text != null) {
            for (ContentTypeEnum b : ContentTypeEnum.values()) {
                if (text.equalsIgnoreCase(b.val)) {
                    return b;
                }
            }
        }
        return null;
    }

}
