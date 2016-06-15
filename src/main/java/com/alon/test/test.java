package com.alon.test;

import com.alon.main.server.enums.AggregationType;
import com.alon.main.server.enums.ErrorType;
import com.alon.main.server.MessageService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Created by alon_ss on 6/13/16.
 */
public class test {

    private String jsonBase64 = "WyJodHRwczovL3NhZmUtaW5sZXQtODEwNS5oZXJva3VhcHAuY29tL3BheW1lbnRzIiwgImh0dHBzOi8vc2FmZS1pbmxldC04MTA1Lmhlcm9rdWFwcC5jb20vcGxhbnMiLCAiaHR0cHM6Ly9zYWZlLWlubGV0LTgxMDUuaGVyb2t1YXBwLmNvbS9mZWVkIiwgImh0dHBzOi8vc2FmZS1pbmxldC04MTA1Lmhlcm9rdWFwcC5jb20vcGF5b3V0Il0=";

    @Test
    public void testAll() {

        List<Integer> timeouts = Arrays.asList(0,300, null);
        List<ErrorType> errorTypes = Arrays.asList(ErrorType.values());
        List<AggregationType> aggregationTypes = Arrays.asList(AggregationType.values());

        for (Integer timeout: timeouts){
            for (ErrorType errorType: errorTypes){
                for (AggregationType aggregationType: aggregationTypes){
                    try{
                        String jsonString = testMessageService(jsonBase64, timeout, errorType, aggregationType);
                        testResult(jsonString, aggregationType, errorType);
                    }catch (RuntimeException e){
                        assert ErrorType.FAIL_ANY.equals(errorType);
                    }
                }
            }
        }
    }

    private void testResult(String jsonString, AggregationType aggregationType, ErrorType errorType) {
        switch (aggregationType){
            case APPENDED:
                JSONArray jsonArr = new JSONArray(jsonString);
                testJson(jsonArr, errorType);
                break;
            case COMBINED:
                JSONObject jsonObj = new JSONObject(jsonString);
                testJson(jsonObj, errorType);
                break;
        }
    }

    private void testJson(JSONObject jsonObject, ErrorType errorType) {
        for (String key: jsonObject.keySet()){
            if ("fail".equals(jsonObject.get(key))){
                assert ErrorType.REPLACE.equals(errorType);
            }

        }
    }

    private void testJson(JSONArray jsonArray, ErrorType errorType) {
        for (Object object: jsonArray){
            JSONObject json = (JSONObject) object;
            testJson(json, errorType);
        }
    }

    private String testMessageService(String jsonBase64, Integer timeout, ErrorType errorType, AggregationType aggregationType) {
        MessageService messageService = new MessageService(errorType);
        String str = messageService.handleMessage(jsonBase64, timeout, aggregationType);

        System.out.println("handleMessage For timeout: " + timeout + " errorType: " + errorType + " AggregationType: " + aggregationType);
        System.out.println(str);
        System.out.println("###############################");

        return str;

    }


}
