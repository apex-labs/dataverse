package org.apex.dataverse.utils.sql;

import org.apache.commons.lang3.StringUtils;
import org.apex.dataverse.entity.Datasource;
import org.apex.dataverse.entity.StorageBox;
import org.apex.dataverse.enums.StorageFormatEnum;
import org.apex.dataverse.enums.StorageTypeEnum;
import org.apex.dataverse.param.TableLoadParam;
import org.apex.dataverse.param.TableTransformParam;
import org.apex.dataverse.utils.MysqlUtils;
import org.apex.dataverse.utils.SqlUtils;

import java.util.List;
import java.util.Objects;

/**
 * @ClassName: HiveUtils
 * @Author: wwd
 * @TODO:
 * @Date: 2024/1/27 13:55
 */
public class HiveUtils {


    public static String createTableDdl(String tableName, List<TableTransformParam> tableTransformParamList, TableLoadParam tableLoadParam, StorageBox storageBox) {
        StringBuilder sql = new StringBuilder();
        sql.append("create external table if not exists ")
                .append(SqlUtils.encloseWithEscapeStr(SqlUtils.BACK_QUOTE, tableName.toLowerCase()))
                .append("(");
        int length = tableTransformParamList.size();
        for (int i = 0; i < length; i++) {
            TableTransformParam tableTransformParam = tableTransformParamList.get(i);
            sql.append(SqlUtils.encloseWithEscapeStr(SqlUtils.BACK_QUOTE, tableTransformParam.getOutColumn()))
                    .append(" ")
                    .append(tableTransformParam.getOutDataTypeName());
            if (i != length - 1) {
                sql.append(SqlUtils.COMMA_SEPARATOR);
            }
        }
        sql.append(") ");
        // 分区字段
        if (StringUtils.isNotBlank(tableLoadParam.getPartitionField())) {
            sql.append(" partitioned by ( ");
            sql.append(SqlUtils.encloseWithEscapeStr(SqlUtils.BACK_QUOTE, tableLoadParam.getPartitionField()));
            // 分区字段默认字符串
            sql.append(" string)");
        }
        sql.append(" stored as ");
        // 文件格式
        if (Objects.nonNull(storageBox.getStorageFormat())) {
            sql.append(StorageFormatEnum.getDescByValue(storageBox.getStorageFormat()));
        } else {
            sql.append(" orc ");
        }
        // 文件路径
        sql.append(" location '").append(storageBox.getStorageName()).append("/").append(tableName.toLowerCase())
                .append("'");
        return sql.toString();
    }

    /**
     * 将JDBC数据字段转为Hive数据字段, 其中transform不为空时 用inDataTypeName转换
     *
     * @param inDataTypeId
     * @param inDataTypeName
     * @param outDataTypeName
     * @param datasource
     * @param transform
     * @return
     */
    public static String getOutColumnDataType(Integer inDataTypeId, String inDataTypeName, String outDataTypeName, Datasource datasource, String transform) {
        // 默认处理mysql
        if (datasource.getDatasourceTypeName().toUpperCase().equals(StorageTypeEnum.MYSQL.getDesc())) {
            return MysqlUtils.getHiveOutColumnDataType(inDataTypeId, outDataTypeName, transform);
        }
        return null;
    }
}
