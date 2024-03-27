package org.apex.dataverse.port.example.api;

import org.apex.dataverse.port.driver.conn.pool.PortExchanger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @version : v1.0
 * @projectName : dynamic-rule-engine
 * @package : com.chinapex.fdre.api
 * @className : OdpcApi
 * @description :
 * @Author : Danny.Huo
 * @createDate : 2022/11/6 13:49
 * @updateUser :
 * @updateDate :
 * @updateRemark :
 */
@RestController
@RequestMapping("/dd")
public class PortDdApi {

    private PortExchanger connExchanger;

    @RequestMapping("")
    public ModelAndView index () {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("port-dd/dd");
        modelAndView.addObject("serverAddress", null);
        return modelAndView;
    }

    @Autowired
    public void setConnExchanger(PortExchanger connExchanger) {
        this.connExchanger = connExchanger;
    }
}
