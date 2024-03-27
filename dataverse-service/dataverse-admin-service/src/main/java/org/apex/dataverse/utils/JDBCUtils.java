package org.apex.dataverse.utils;

import cn.hutool.core.collection.CollectionUtil;
import org.apex.dataverse.dto.DatasourceColumnDTO;
import org.apex.dataverse.dto.DatasourceTableDTO;
import org.apex.dataverse.entity.Datasource;
import org.apex.dataverse.entity.JdbcSource;
import org.apex.dataverse.enums.DataTypeEnum;
import org.apex.dataverse.enums.IsPkEnum;
import org.apex.dataverse.enums.StorageTypeEnum;
import org.apex.dataverse.param.TableLoadParam;
import org.apex.dataverse.param.TableTransformParam;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName JDBCUtils
 * @Description TODO
 * @Author wwd
 * @Date 2023/2/28 14:12
 **/
public class JDBCUtils {

    public static List<DatasourceTableDTO> getDatasourceTable(JdbcSource jdbcSource, Integer datasourceTypeId) throws SQLException {
        List<DatasourceTableDTO> datasourceTableDTOList = new ArrayList<>();
        if (Objects.nonNull(jdbcSource)) {
            try (Connection connection = ConnectionUtils.connection(jdbcSource.getJdbcUrl(), jdbcSource.getUserName(), jdbcSource.getPassword(), datasourceTypeId)) {
                DatabaseMetaData metaData = connection.getMetaData();
                try (ResultSet resultSet = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
                    while (Objects.nonNull(resultSet) && resultSet.next()) {
                        String tableName = resultSet.getString("TABLE_NAME");
                        String tableRemarks = resultSet.getString("REMARKS");
                        DatasourceTableDTO datasourceTableDTO = new DatasourceTableDTO();
                        datasourceTableDTO.setTableName(tableName);
                        datasourceTableDTO.setTableRemarks(tableRemarks);
                        datasourceTableDTOList.add(datasourceTableDTO);
                    }
                    return datasourceTableDTOList;
                }
            }
        }
        return null;
    }

    public static List<DatasourceColumnDTO> getDatasourceTableAndColumn(JdbcSource jdbcSource, String tableName, Integer datasourceTypeId) throws SQLException {
        List<DatasourceColumnDTO> datasourceColumnDTOList = new ArrayList<>();
        List<String> pkColumnList = new ArrayList<>();
        if (Objects.nonNull(jdbcSource)) {
            try (Connection connection = ConnectionUtils.connection(jdbcSource.getJdbcUrl(), jdbcSource.getUserName(), jdbcSource.getPassword(), datasourceTypeId)) {
                DatabaseMetaData metaData = connection.getMetaData();
                try (ResultSet resultSet = metaData.getColumns(null, "%", tableName, "%");
                     ResultSet pKResultSet = metaData.getPrimaryKeys(null, "%", tableName)) {
                    while (Objects.nonNull(pKResultSet) && pKResultSet.next()) {
                        pkColumnList.add(pKResultSet.getString("COLUMN_NAME"));
                    }
                    while (Objects.nonNull(resultSet) && resultSet.next()) {
                        DatasourceColumnDTO datasourceColumnDTO = new DatasourceColumnDTO();
                        datasourceColumnDTO.setTableName(tableName);
                        datasourceColumnDTO.setColumnName(resultSet.getString("COLUMN_NAME"));
                        datasourceColumnDTO.setSourceColumnType(resultSet.getString("TYPE_NAME"));
                        datasourceColumnDTO.setTypeId(resultSet.getInt("DATA_TYPE"));
                        if (CollectionUtil.isNotEmpty(pkColumnList) && pkColumnList.contains(datasourceColumnDTO.getColumnName())) {
                            datasourceColumnDTO.setIsPk(IsPkEnum.PK_YES.getValue());
                        } else {
                            datasourceColumnDTO.setIsPk(IsPkEnum.PK_NO.getValue());
                        }
                        datasourceColumnDTO.setDateTypeId(getDateTypeId(datasourceColumnDTO.getTypeId()));
                        datasourceColumnDTO.setDataTypeName(getDateTypeName(datasourceColumnDTO.getTypeId()));
                        datasourceColumnDTO.setShortDataTypeId(getDateTypeId(datasourceColumnDTO.getTypeId()));
                        datasourceColumnDTO.setShortDataTypeName(getDateTypeName(datasourceColumnDTO.getTypeId()));
                        datasourceColumnDTOList.add(datasourceColumnDTO);
                    }
                    return datasourceColumnDTOList;
                }
            }
        }
        return null;
    }

    private static String getDateTypeName(Integer typeId) {
        if (Objects.isNull(typeId)) {
            return null;
        }
        if (typeId.equals(Types.BIT) ||
                typeId.equals(Types.BIGINT) ||
                typeId.equals(Types.SMALLINT) ||
                typeId.equals(Types.BOOLEAN) ||
                typeId.equals(Types.TINYINT)) {
            return DataTypeEnum.INTEGER.getDesc();
        }
        if (typeId.equals(Types.FLOAT) ||
                typeId.equals(Types.DOUBLE) ||
                typeId.equals(Types.NUMERIC) ||
                typeId.equals(Types.DECIMAL)) {
            return DataTypeEnum.FLOAT.getDesc();
        }
        if (typeId.equals(Types.TIMESTAMP)) {
            return DataTypeEnum.DATETIME.getDesc();
        }
        if (typeId.equals(Types.DATE)) {
            return DataTypeEnum.DATE.getDesc();
        }
        if (typeId.equals(Types.CHAR) ||
                typeId.equals(Types.CLOB) ||
                typeId.equals(Types.NCLOB) ||
                typeId.equals(Types.LONGNVARCHAR) ||
                typeId.equals(Types.NVARCHAR) ||
                typeId.equals(Types.NCHAR) ||
                typeId.equals(Types.BLOB) ||
                typeId.equals(Types.LONGNVARCHAR) ||
                typeId.equals(Types.VARCHAR)) {
            return DataTypeEnum.STRING.getDesc();
        }
        return null;
    }

    private static Integer getDateTypeId(Integer typeId) {
        if (Objects.isNull(typeId)) {
            return null;
        }
        if (typeId.equals(Types.BIT) ||
                typeId.equals(Types.BIGINT) ||
                typeId.equals(Types.SMALLINT) ||
                typeId.equals(Types.BOOLEAN) ||
                typeId.equals(Types.TINYINT)) {
            return DataTypeEnum.INTEGER.getValue();
        }
        if (typeId.equals(Types.FLOAT) ||
                typeId.equals(Types.DOUBLE) ||
                typeId.equals(Types.NUMERIC) ||
                typeId.equals(Types.DECIMAL)) {
            return DataTypeEnum.FLOAT.getValue();
        }
        if (typeId.equals(Types.TIMESTAMP)) {
            return DataTypeEnum.DATETIME.getValue();
        }
        if (typeId.equals(Types.DATE)) {
            return DataTypeEnum.DATE.getValue();
        }
        if (typeId.equals(Types.CHAR) ||
                typeId.equals(Types.CLOB) ||
                typeId.equals(Types.NCLOB) ||
                typeId.equals(Types.LONGNVARCHAR) ||
                typeId.equals(Types.NVARCHAR) ||
                typeId.equals(Types.NCHAR) ||
                typeId.equals(Types.BLOB) ||
                typeId.equals(Types.LONGNVARCHAR) ||
                typeId.equals(Types.VARCHAR)) {
            return DataTypeEnum.STRING.getValue();
        }
        return null;
    }

    public static String createTableDdl(String storageType, String tableName, List<TableTransformParam> tableTransformParamList, TableLoadParam tableLoadParam) {
        // todo 本次需求暂不考虑分区表的创建
        StringBuilder sql = new StringBuilder();
        sql.append("create table ").append(SqlUtils.encloseWithEscapeStr(SqlUtils.BACK_QUOTE, tableName));
        sql.append(" (");
        // 暂时只考虑创建mysql
        if (storageType.toUpperCase().equals(StorageTypeEnum.MYSQL.getDesc())) {
            sql.append(MysqlUtils.createTableDdl(tableTransformParamList, tableLoadParam));
        }
        sql.append(" )");
        return sql.toString();
    }

    public static String getOutColumnDataType(Integer inDataTypeId, String inDataTypeName, String outDataTypeName, String storageType, Datasource datasource, String transform) {
        if (storageType.toUpperCase().equals(StorageTypeEnum.MYSQL.getDesc())) {
            if (datasource.getDatasourceTypeName().toUpperCase().equals(StorageTypeEnum.MYSQL.getDesc())) {
                return MysqlUtils.getOutColumnDataType(inDataTypeId, inDataTypeName, outDataTypeName, transform);
            }
            // todo 处理oracle sqlserver postgresql 暂时
        }
        return null;
    }
}
