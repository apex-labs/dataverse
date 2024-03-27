package org.apex.dataverse.port.example.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @version : v1.0
 * @projectName : dynamic-rule-engine
 * @package : com.chinapex.fdre.api
 * @className : KafkaViewApi
 * @description :
 * @Author : Danny.Huo
 * @createDate : 2022/10/22 11:25
 * @updateUser :
 * @updateDate :
 * @updateRemark :
 */
@RestController
@RequestMapping("/di")
public class PortDiApi {

    @RequestMapping("")
    public ModelAndView index () {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("port-di/di");
        modelAndView.addObject("serverAddress", "");
        return modelAndView;
    }
}
