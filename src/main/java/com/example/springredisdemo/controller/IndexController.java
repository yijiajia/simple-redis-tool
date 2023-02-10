package com.example.springredisdemo.controller;

import com.example.springredisdemo.annotation.ControllerLimiter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    /**
     * 同一个ip 在10s 内最多调用10次
     * @return
     */
    @ControllerLimiter(limit = 10,limitTime = 10)
    @RequestMapping("/index")
    public String index() {
        return "Hello World";
    }
    
}
