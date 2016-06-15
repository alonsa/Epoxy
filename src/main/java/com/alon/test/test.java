package com.alon.test;

import com.alon.main.server.enums.AggregationType;
import com.alon.main.server.enums.ErrorType;
import com.alon.main.server.MessageService;

/**
 * Created by alon_ss on 6/13/16.
 */
public class test {
    public static void main(String[] args) {

        String jsonBase64 = "WyJodHRwczovL3NhZmUtaW5sZXQtODEwNS5oZXJva3VhcHAuY29tL3BheW1lbnRzIiwgImh0dHBzOi8vc2FmZS1pbmxldC04MTA1Lmhlcm9rdWFwcC5jb20vcGxhbnMiLCAiaHR0cHM6Ly9zYWZlLWlubGV0LTgxMDUuaGVyb2t1YXBwLmNvbS9mZWVkIiwgImh0dHBzOi8vc2FmZS1pbmxldC04MTA1Lmhlcm9rdWFwcC5jb20vcGF5b3V0Il0=";

        testMessageService(jsonBase64, 0, ErrorType.REPLACE, AggregationType.APPENDED);
        testMessageService(jsonBase64, 0, ErrorType.FAIL_ANY, AggregationType.APPENDED);
        testMessageService(jsonBase64, 0, ErrorType.REPLACE, AggregationType.COMBINED);
        testMessageService(jsonBase64, 0, ErrorType.FAIL_ANY, AggregationType.COMBINED);

        testMessageService(jsonBase64, null, ErrorType.REPLACE, AggregationType.APPENDED);
        testMessageService(jsonBase64, null, ErrorType.FAIL_ANY, AggregationType.APPENDED);
        testMessageService(jsonBase64, null, ErrorType.REPLACE, AggregationType.COMBINED);
        testMessageService(jsonBase64, null, ErrorType.FAIL_ANY, AggregationType.COMBINED);

        testMessageService(jsonBase64, 300, ErrorType.REPLACE, AggregationType.APPENDED);
        testMessageService(jsonBase64, 300, ErrorType.FAIL_ANY, AggregationType.APPENDED);
        testMessageService(jsonBase64, 300, ErrorType.REPLACE, AggregationType.COMBINED);
        testMessageService(jsonBase64, 300, ErrorType.FAIL_ANY, AggregationType.COMBINED);

        testMessageService("jsonBase64", 300, ErrorType.FAIL_ANY, AggregationType.COMBINED);
    }

    private static void testMessageService(String jsonBase64, Integer timeout, ErrorType errorType, AggregationType aggregationType) {
        MessageService messageService = new MessageService();
        String str;
        try {
            str = messageService.handleMessage(jsonBase64, timeout, errorType, aggregationType);
        }catch (Exception e){
            str = e.getMessage();
        }

        System.out.println("handleMessage For timeout: " + timeout + " errorType: " + errorType + " AggregationType: " + aggregationType);
        System.out.println(str);
        System.out.println("###############################");


    }


}
