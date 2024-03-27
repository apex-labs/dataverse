package org.apex.dataverse.utils;

import com.mysql.cj.MysqlType;
import org.apache.commons.lang3.StringUtils;
import org.apex.dataverse.constrant.HiveDataTypeName;
import org.apex.dataverse.dto.DatasourceColumnDTO;
import org.apex.dataverse.dto.DatasourceTableDTO;
import org.apex.dataverse.entity.JdbcSource;
import org.apex.dataverse.enums.DataSourceTypeEnum;
import org.apex.dataverse.enums.DataTypeEnum;
import org.apex.dataverse.enums.IsPkEnum;
import org.apex.dataverse.enums.TestDatasourceConnResultEnum;
import org.apex.dataverse.param.TableLoadParam;
import org.apex.dataverse.param.TableTransformParam;
import org.apex.dataverse.vo.TestDataSourceLinkVO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName MysqlUtils
 * @Description TODO
 * @Author wwd
 * @Date 2023/2/23 10:16
 **/
public class MysqlUtils {


    /**
     * @return java.lang.Boolean
     * @Author wwd
     * @Description TODO
     * @Date 2023/2/23 10:38
     * @Param [url, username, password]
     **/
    public static Boolean testMysqlJdbcConnection(String url, String username, String password, TestDataSourceLinkVO testDataSourceLinkVO) throws SQLException {
        try (Connection connection = ConnectionUtils.connection(url, username, password, DataSourceTypeEnum.MYSQL.getValue())) {
            return Boolean.TRUE;
        } catch (SQLException e) {
            testDataSourceLinkVO.setErrMsg(e.getMessage());
            testDataSourceLinkVO.setResult(TestDatasourceConnResultEnum.FAILURE.getValue());
            return Boolean.FALSE;
        } catch (Throwable throwable) {
            testDataSourceLinkVO.setErrMsg(throwable.getMessage());
            testDataSourceLinkVO.setResult(TestDatasourceConnResultEnum.FAILURE.getValue());
            return Boolean.FALSE;
        }
    }

    public static List<DatasourceTableDTO> getDatasourceTable(JdbcSource jdbcSource) {
        List<DatasourceTableDTO> datasourceTableDTOList = new ArrayList<>();
        try (Connection connection = ConnectionUtils.connection(jdbcSource.getJdbcUrl(), jdbcSource.getUserName(), jdbcSource.getPassword(), DataSourceTypeEnum.MYSQL.getValue())) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet rs = statement.executeQuery("show tables")) {
                    while (Objects.nonNull(rs) && rs.next()) {
                        String tableName = rs.getString(1);
                        DatasourceTableDTO datasourceTableDTO = new DatasourceTableDTO();
                        datasourceTableDTO.setTableName(tableName);
                        datasourceTableDTOList.add(datasourceTableDTO);
                    }
                    return datasourceTableDTOList;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    public static List<DatasourceColumnDTO> getDatasourceTableAndColumn(JdbcSource jdbcSource, String tableName) {
        List<DatasourceColumnDTO> datasourceColumnDTOList = new ArrayList<>();
        try (Connection connection = ConnectionUtils.connection(jdbcSource.getJdbcUrl(), jdbcSource.getUserName(), jdbcSource.getPassword(), DataSourceTypeEnum.MYSQL.getValue())) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet rs = statement.executeQuery("desc " + tableName)) {
                    while (Objects.nonNull(rs) && rs.next()) {
                        DatasourceColumnDTO datasourceColumnDTO = new DatasourceColumnDTO();
                        datasourceColumnDTO.setTableName(tableName);
                        datasourceColumnDTO.setColumnName(rs.getString("field"));
                        datasourceColumnDTO.setSourceColumnType(rs.getString("type"));
                        String key = rs.getString("key");
                        if (StringUtils.isNotEmpty(key)) {
                            if (key.toLowerCase().equals("pri")) {
                                datasourceColumnDTO.setIsPk(IsPkEnum.PK_YES.getValue());
                                datasourceColumnDTO.setIsIncrement(Boolean.TRUE);
                            } else {
                                datasourceColumnDTO.setIsPk(IsPkEnum.PK_NO.getValue());
                            }
                        } else {
                            datasourceColumnDTO.setIsPk(IsPkEnum.PK_NO.getValue());
                        }
                        datasourceColumnDTO.setTypeId(getTypeId(datasourceColumnDTO.getSourceColumnType()));
                        datasourceColumnDTO.setDateTypeId(getDateTypeId(datasourceColumnDTO.getSourceColumnType()));
                        datasourceColumnDTO.setDataTypeName(getDateTypeName(datasourceColumnDTO.getSourceColumnType()));
                        datasourceColumnDTO.setShortDataTypeId(getDateTypeId(datasourceColumnDTO.getSourceColumnType()));
                        datasourceColumnDTO.setShortDataTypeName(getDateTypeName(datasourceColumnDTO.getSourceColumnType()));
                        if (datasourceColumnDTO.getDataTypeName().equals(DataTypeEnum.DATE.getDesc()) ||
                                datasourceColumnDTO.getDataTypeName().equals(DataTypeEnum.DATETIME.getDesc())) {
                            datasourceColumnDTO.setIsIncrement(Boolean.TRUE);
                        }
                        datasourceColumnDTOList.add(datasourceColumnDTO);
                    }
                    return datasourceColumnDTOList;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    private static Integer getTypeId(String sourceColumnType) {
        if (StringUtils.isBlank(sourceColumnType)) {
            return null;
        }
        if (sourceColumnType.toUpperCase().startsWith(MysqlType.BIT.getName())){
            return MysqlType.BIT.getJdbcType();
        }
        if (sourceColumnType.toUpperCase().startsWith(MysqlType.MEDIUMINT.getName())) {
            return MysqlType.MEDIUMINT.getJdbcType();
        } if (sourceColumnType.toUpperCase().startsWith(MysqlType.BIGINT.getName())) {
            return MysqlType.BIGINT.getJdbcType();
        } if (sourceColumnType.toUpperCase().startsWith(MysqlType.INT.getName())) {
            return MysqlType.INT.getJdbcType();
        } if (sourceColumnType.toUpperCase().startsWith(MysqlType.SMALLINT.getName())) {
            return MysqlType.SMALLINT.getJdbcType();
        } if (sourceColumnType.toUpperCase().startsWith(MysqlType.BOOLEAN.getName())) {
            return MysqlType.BOOLEAN.getJdbcType();
        } if (sourceColumnType.toUpperCase().startsWith(MysqlType.TINYINT.getName())) {
            return MysqlType.TINYINT.getJdbcType();
        }

        if (sourceColumnType.toUpperCase().startsWith(MysqlType.FLOAT.name())) {
            return MysqlType.FLOAT.getJdbcType();
        } if (sourceColumnType.toUpperCase().startsWith(MysqlType.DOUBLE.getName())) {
            return MysqlType.DOUBLE.getJdbcType();
        } if (sourceColumnType.toUpperCase().startsWith(MysqlType.DECIMAL.getName())) {
            return MysqlType.DECIMAL.getJdbcType();
        }

        if (sourceColumnType.toUpperCase().startsWith(MysqlType.DATETIME.getName())) {
            return MysqlType.DATETIME.getJdbcType();
        } if (sourceColumnType.toUpperCase().startsWith(MysqlType.TIMESTAMP.getName())) {
            return MysqlType.TIMESTAMP.getJdbcType();
        }

        if (sourceColumnType.toUpperCase().startsWith(MysqlType.DATE.getName())) {
            return MysqlType.DATE.getJdbcType();
        }

        if (sourceColumnType.toUpperCase().startsWith(MysqlType.CHAR.getName())) {
            return MysqlType.CHAR.getJdbcType();
        } if (sourceColumnType.toUpperCase().startsWith(MysqlType.TEXT.getName())) {
            return MysqlType.TEXT.getJdbcType();
        } if (sourceColumnType.toUpperCase().startsWith(MysqlType.LONGTEXT.getName())) {
            return MysqlType.LONGTEXT.getJdbcType();
        } if (sourceColumnType.toUpperCase().startsWith(MysqlType.MEDIUMTEXT.getName())) {
            return MysqlType.MEDIUMTEXT.getJdbcType();
        } if (sourceColumnType.toUpperCase().startsWith(MysqlType.TINYTEXT.getName())) {
            return MysqlType.TINYTEXT.getJdbcType();
        } if (sourceColumnType.toUpperCase().startsWith(MysqlType.SET.getName())) {
            return MysqlType.SET.getJdbcType();
        } if (sourceColumnType.toUpperCase().startsWith(MysqlType.ENUM.getName())) {
            return MysqlType.ENUM.getJdbcType();
        } if (sourceColumnType.toUpperCase().startsWith(MysqlType.JSON.getName())) {
            return MysqlType.JSON.getJdbcType();
        } if (sourceColumnType.toUpperCase().startsWith(MysqlType.VARCHAR.getName())) {
            return MysqlType.VARCHAR.getJdbcType();
        }
        return null;
    }

    private static String getDateTypeName(String sourceColumnType) {
        if (StringUtils.isBlank(sourceColumnType)) {
            return null;
        }
        if (sourceColumnType.toUpperCase().startsWith(MysqlType.BIT.getName()) ||
                sourceColumnType.toUpperCase().startsWith(MysqlType.MEDIUMINT.getName()) ||
                sourceColumnType.toUpperCase().startsWith(MysqlType.BIGINT.getName()) ||
                sourceColumnType.toUpperCase().startsWith(MysqlType.INT.getName()) ||
                sourceColumnType.toUpperCase().startsWith(MysqlType.SMALLINT.getName()) ||
                sourceColumnType.toUpperCase().startsWith(MysqlType.BOOLEAN.getName()) ||
                sourceColumnType.toUpperCase().startsWith(MysqlType.TINYINT.getName())) {
            return DataTypeEnum.INTEGER.getDesc();
        }
        if (sourceColumnType.toUpperCase().startsWith(MysqlType.FLOAT.name()) ||
                sourceColumnType.toUpperCase().startsWith(MysqlType.DOUBLE.getName()) ||
                sourceColumnType.toUpperCase().startsWith(MysqlType.DECIMAL.getName())) {
            return DataTypeEnum.FLOAT.getDesc();
        }
        if (sourceColumnType.toUpperCase().startsWith(MysqlType.DATETIME.getName()) ||
                sourceColumnType.toUpperCase().startsWith(MysqlType.TIMESTAMP.getName())) {
            return DataTypeEnum.DATETIME.getDesc();
        }
        if (sourceColumnType.toUpperCase().startsWith(MysqlType.DATE.getName())) {
            return DataTypeEnum.DATE.getDesc();
        }
        if (sourceColumnType.toUpperCase().startsWith(MysqlType.CHAR.getName()) ||
                sourceColumnType.toUpperCase().startsWith(MysqlType.TEXT.getName()) ||
                sourceColumnType.toUpperCase().startsWith(MysqlType.LONGTEXT.getName()) ||
                sourceColumnType.toUpperCase().startsWith(MysqlType.MEDIUMTEXT.getName()) ||
                sourceColumnType.toUpperCase().startsWith(MysqlType.TINYTEXT.getName()) ||
                sourceColumnType.toUpperCase().startsWith(MysqlType.SET.getName()) ||
                sourceColumnType.toUpperCase().startsWith(MysqlType.ENUM.getName()) ||
                sourceColumnType.toUpperCase().startsWith(MysqlType.JSON.getName()) ||
                sourceColumnType.toUpperCase().startsWith(MysqlType.VARCHAR.getName())) {
            return DataTypeEnum.STRING.getDesc();
        }
        return null;
    }

    private static Integer getDateTypeId(String sourceColumnType) {
        if (StringUtils.isBlank(sourceColumnType)) {
            return null;
        }
        if (sourceColumnType.toUpperCase().startsWith(MysqlType.BIT.getName()) ||
                sourceColumnType.toUpperCase().startsWith(MysqlType.MEDIUMINT.getName()) ||
                sourceColumnType.toUpperCase().startsWith(MysqlType.BIGINT.getName()) ||
                sourceColumnType.toUpperCase().startsWith(MysqlType.INT.getName()) ||
                sourceColumnType.toUpperCase().startsWith(MysqlType.SMALLINT.getName()) ||
                sourceColumnType.toUpperCase().startsWith(MysqlType.BOOLEAN.getName()) ||
                sourceColumnType.toUpperCase().startsWith(MysqlType.TINYINT.getName())) {
                return DataTypeEnum.INTEGER.getValue();
        }
        if (sourceColumnType.toUpperCase().startsWith(MysqlType.FLOAT.name()) ||
                sourceColumnType.toUpperCase().startsWith(MysqlType.DOUBLE.getName()) ||
                sourceColumnType.toUpperCase().startsWith(MysqlType.DECIMAL.getName())) {
            return DataTypeEnum.FLOAT.getValue();
        }
        if (sourceColumnType.toUpperCase().startsWith(MysqlType.DATETIME.getName()) ||
                sourceColumnType.toUpperCase().startsWith(MysqlType.TIMESTAMP.getName())) {
            return DataTypeEnum.DATETIME.getValue();
        }
        if (sourceColumnType.toUpperCase().startsWith(MysqlType.DATE.getName())) {
            return DataTypeEnum.DATE.getValue();
        }
        if (sourceColumnType.toUpperCase().startsWith(MysqlType.CHAR.getName()) ||
                sourceColumnType.toUpperCase().startsWith(MysqlType.TEXT.getName()) ||
                sourceColumnType.toUpperCase().startsWith(MysqlType.LONGTEXT.getName()) ||
                sourceColumnType.toUpperCase().startsWith(MysqlType.MEDIUMTEXT.getName()) ||
                sourceColumnType.toUpperCase().startsWith(MysqlType.TINYTEXT.getName()) ||
                sourceColumnType.toUpperCase().startsWith(MysqlType.SET.getName()) ||
                sourceColumnType.toUpperCase().startsWith(MysqlType.ENUM.getName()) ||
                sourceColumnType.toUpperCase().startsWith(MysqlType.JSON.getName()) ||
                sourceColumnType.toUpperCase().startsWith(MysqlType.VARCHAR.getName())) {
            return DataTypeEnum.STRING.getValue();
        }
        return null;
    }

    public static String createTableDdl(List<TableTransformParam> tableTransformParamList, TableLoadParam tableLoadParam) {
        StringBuilder sql = new StringBuilder();
        int length = tableTransformParamList.size();
        for (int i = 0; i < length; i++) {
            TableTransformParam tableTransformParam = tableTransformParamList.get(i);
            sql.append(SqlUtils.encloseWithEscapeStr(SqlUtils.BACK_QUOTE, tableTransformParam.getOutColumn())).append(" ");
            sql.append(tableTransformParam.getOutDataTypeName());
            if (i != length - 1) {
                sql.append(SqlUtils.COMMA_SEPARATOR);
            }
        }
        // 主键
        if (StringUtils.isNotBlank(tableLoadParam.getPkField())) {
            sql.append(" primary key (");
            sql.append(SqlUtils.encloseWithEscapeStr(SqlUtils.BACK_QUOTE, tableLoadParam.getPkField()));
            sql.append(")");
        }
        return sql.toString();
    }

    public static String getHiveOutColumnDataType(Integer inDataTypeId, String outDataTypeName, String transform) {
        String hiveDataType;
        switch (inDataTypeId) {
            case Types.TINYINT:
                hiveDataType = HiveDataTypeName.TINYINT;
                break;
            case Types.SMALLINT:
                hiveDataType = HiveDataTypeName.SMALLINT;
                break;
            case Types.INTEGER:
                hiveDataType = HiveDataTypeName.INT;
                break;
            case Types.BIGINT:
                hiveDataType = HiveDataTypeName.BIGINT;
                break;
            case Types.ROWID:
                hiveDataType = HiveDataTypeName.BIGINT;
                break;
            case Types.DECIMAL:
                hiveDataType = HiveDataTypeName.withPrecision(HiveDataTypeName.DECIMAL, 0, 2);
                break;
            case Types.DOUBLE:
            case Types.REAL:
            case Types.FLOAT:
                hiveDataType = HiveDataTypeName.DOUBLE;
                break;
            case Types.BIT:
                hiveDataType = HiveDataTypeName.BOOLEAN;
                break;
            case Types.TIMESTAMP:
                hiveDataType = HiveDataTypeName.TIMESTAMP;
                break;
            case Types.DATE:
                hiveDataType = HiveDataTypeName.DATE;
                break;
            case Types.VARCHAR:
                hiveDataType = HiveDataTypeName.STRING;
                break;
            default:
                hiveDataType = HiveDataTypeName.STRING;
                break;

        }
        return hiveDataType;
    }

    public static String getOutColumnDataType(Integer inDataTypeId, String inDataTypeName, String outDataTypeName, String transform) {
        if (StringUtils.isBlank(transform)) {
            return inDataTypeName;
        } else {
            if (transform.startsWith("count")) {
                return MysqlType.BIGINT.getName();
            } else if (transform.startsWith("avg")) {
                return MysqlType.DECIMAL.getName();
            } else if (transform.startsWith("date") || transform.startsWith("time")) {
                return MysqlType.DATETIME.getName();
            } else {
                return MysqlType.VARCHAR.getName();
            }
        }
    }
}
