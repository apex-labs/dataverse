package org.apex.dataverse.constrant;

import org.apex.dataverse.enums.DataSourceTypeEnum;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName DriverClassConstants
 * @Description TODO
 * @Author wwd
 * @Date 2023/2/28 16:53
 **/
public class DriverClassConstants {
    private static final Map<Integer, String> driverClassMap = new ConcurrentHashMap<>();

     static {
         for (DataSourceTypeEnum dataSourceTypeEnum : DataSourceTypeEnum.values()) {
             driverClassMap.put(dataSourceTypeEnum.getValue(), dataSourceTypeEnum.getDriverName());
         }
    }

    public static String getDriverName(Integer driverId) {
         return driverClassMap.get(driverId);
    }
}
