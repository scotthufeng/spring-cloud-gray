package cn.springcloud.service.a.rest;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.ImmutableMap;

import cn.springcloud.service.a.feign.FeignTest;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by saleson on 2017/10/18.
 */
@RestController
@RequestMapping("/api/test")
@Slf4j
public class TestResource {

    @Value("${test.sleepTime:50}")
    private long sleepTime = 50;

    @Autowired
    Environment env;
    @Autowired
    FeignTest feign;

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, String> testGet(@RequestParam(value = "version", required = false) String version) {
        long start = System.currentTimeMillis();
        log.info("feign1--"+feign.testGet());
        log.info("feign2--"+feign.testGet());
        log.info("feign3--"+feign.testGet());
//        try {
//            Thread.sleep(sleepTime);
//        } catch (InterruptedException e) {
//        }
        return ImmutableMap.of("test", "success.",
                "version", StringUtils.defaultIfEmpty(version, ""),
                "serverPort", env.getProperty("server.port"),
                "usedTime", String.valueOf(System.currentTimeMillis() - start));
    }


}
