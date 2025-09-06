/*
package com.seasontone.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class serverCheckController {
  @Value("${server.env}")
  private String env;
  @Value("${server.port}")
  private String serverPort;
  @Value("${server.serverAddress}")
  private String serverAddress;
  @Value("${serverName}")
  private String serverName;

  @GetMapping("/sc")
  public ResponseEntity<?> serverCheck(){
    Map<String, String> responseData = new HashMap<>();
    responseData.put("serverName", serverName);
    responseData.put("serverAddress", serverAddress);
    responseData.put("serverPort", serverPort);
    responseData.put("env", env);

    return ResponseEntity.ok(responseData);
  }

  @GetMapping("/env")
  public ResponseEntity<?> getEnv(){
    return ResponseEntity.ok(env);
  }

}*/
