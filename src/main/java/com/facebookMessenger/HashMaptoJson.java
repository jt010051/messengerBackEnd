package com.facebookMessenger;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class HashMaptoJson {
    public static void main(String[] args) throws Exception {
        Map<String, String> myHashMap = new HashMap<>();
        myHashMap.put("key1", "value1");
        myHashMap.put("key2", "value2");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(myHashMap);
        System.out.println(jsonString); 
    }
}