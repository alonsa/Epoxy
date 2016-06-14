package com.alon.test;

import com.alon.main.server.enums.AggregationType;
import com.alon.main.server.enums.ErrorType;
import com.alon.main.server.service.MessageService;

/**
 * Created by alon_ss on 6/13/16.
 */
public class test {
    public static void main(String[] args) {

        String jsonBase64 = "W3siZmlyc3QiOiAiaHR0cHM6Ly9zYWZlLWlubGV0LTgxMDUuaGVyb2t1YXBwLmNvbS9wYXltZW50cyINCn0seyJzZWNvbmQiOiAiaHR0cHM6Ly9zYWZlLWlubGV0LTgxMDUuaGVyb2t1YXBwLmNvbS9wbGFucyINCn0seyJ0aGlyZCI6ICJodHRwczovL3NhZmUtaW5sZXQtODEwNS5oZXJva3VhcHAuY29tL3BheW91dCINCn0seyJmb3VydGgiOiAiQUxPTiINCn0seyJmaWZ0aCI6ICJodHRwczovL3NhZmUtaW5sZXQtODEwNS5oZXJva3VhcHAuY29tL2ZlZWQifV0=";

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
