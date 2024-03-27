/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apex.dataverse.port.worker.mex;

import lombok.Data;
import org.apex.dataverse.core.context.ServerContext;
import org.apex.dataverse.core.enums.CmdSet;
import org.apex.dataverse.core.enums.ConnType;
import org.apex.dataverse.core.exception.InvalidCmdException;
import org.apex.dataverse.core.exception.InvalidConnException;
import org.apex.dataverse.core.msg.Request;
import org.apex.dataverse.core.msg.Response;
import org.apex.dataverse.core.msg.packet.Packet;
import org.apex.dataverse.core.msg.packet.dd.ExeSqlReqPacket;
import org.apex.dataverse.core.msg.packet.dd.ExeSqlRspPacket;
import org.apex.dataverse.core.util.ExceptionUtil;
import org.apex.dataverse.port.core.enums.ConnTypeEnum;
import org.apex.dataverse.port.entity.StoragePort;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Danny.Huo
 * @date 2023/12/29 17:31
 * @since 0.1.0
 */
@Data
public class StorageConnection implements Serializable {

    /**
     * New odpc connection
     *
     * @param storagePort   storageId
     * @param engineContext engineContext
     * @return StorageConnection
     */
    public static StorageConnection newOdpcConn(StoragePort storagePort, EngineContext engineContext, ServerContext portServerContext) {
        StorageConnection storageConnection = new StorageConnection();
        storageConnection.setStorageId(storagePort.getStorageId());
        storageConnection.setStoragePort(storagePort);
        storageConnection.setConnType(ConnType.ODPC.getConnType());
        storageConnection.setEngineContext(engineContext);
        storageConnection.setPortServerContext(portServerContext);
        return storageConnection;
    }

    /**
     * New jdbc connection
     *
     * @param storagePort storageId
     * @param connection  jdbc connection
     * @return StorageConnection
     */
    public static StorageConnection newJdbcConn(StoragePort storagePort, Connection connection, ServerContext portServerContext) {
        StorageConnection storageConnection = new StorageConnection();
        storageConnection.setStorageId(storagePort.getStorageId());
        storageConnection.setStoragePort(storagePort);
        storageConnection.setConnType(ConnType.JDBC.getConnType());
        storageConnection.setConnection(connection);
        storageConnection.setPortServerContext(portServerContext);
        return storageConnection;
    }

    private StorageConnection() {

    }

    /**
     * Storage ID
     */
    private Long storageId;

    /**
     * Storage port
     */
    private StoragePort storagePort;

    /**
     * Connection type, JDBC/ODPC
     */
    private String connType;

    /**
     * Is busy flag
     */
    protected boolean busy;

    /**
     * Odpc connection
     */
    private EngineContext engineContext;

    /**
     * Port server context
     */
    private ServerContext portServerContext;

    /**
     * Jdbc connection
     */
    private Connection connection;

    /**
     * Execute command
     *
     * @param cmd Request<Packet>
     * @throws InterruptedException InterruptedException
     * @throws InvalidCmdException  InvalidCmdException
     * @throws InvalidConnException InvalidConnException
     * @throws SQLException         SQLException
     */
    public void exe(Request<Packet> cmd) throws InterruptedException,
            InvalidCmdException, InvalidConnException, SQLException {
        if (!Request.isValid(cmd)) {
            throw new InvalidCmdException("Invalid command : " + cmd);
        }

        // Self-developed computing engines connection
        if (ConnType.ODPC.getConnType().equalsIgnoreCase(connType)) {
            if (!engineContext.getEngineClient().isActive()) {
                throw new InvalidConnException("Invalid odpc storage connection, connection is not active");
            }

            engineContext.getEngineClient().pushRequest(cmd);
        }
        // JDBC connection
        else if (ConnType.JDBC.getConnType().equalsIgnoreCase(connType)) {
            if (this.connection.isClosed()) {
                throw new InvalidConnException("Invalid jdbc storage connection, connection is closed");
            }

            Response<Packet> response = exeJdbc(cmd);

            // Push response to port driver by port server
            portServerContext.pushResponse(response);
        }
    }

    /**
     * Execute jdbc command
     *
     * @param cmd Request<Packet> cmd
     * @return Response<Packet>
     */
    private Response<Packet> exeJdbc(Request<Packet> cmd) {
        long start = System.currentTimeMillis();
        byte command = cmd.getHeader().getCommand();
        List<Map<String, Object>> data = new ArrayList<>();
        Response<Packet> response = cmd.newSelfRsp(true);
        ExeSqlRspPacket rspPacket = (ExeSqlRspPacket) response.getPacket();
        try (Statement statement = connection.createStatement()) {
            if (command == CmdSet.EXE_SQL.getReq()) {
                ExeSqlReqPacket packet = (ExeSqlReqPacket) cmd.getPacket();
                String sql = packet.getSql();
                ResultSet resultSet = statement.executeQuery(sql);
                ResultSetMetaData meta = resultSet.getMetaData();
                int columnCount = meta.getColumnCount();
                while (resultSet.next()) {
                    Map<String, Object> row = new HashMap<>();
                    data.add(row);
                    for (int i = 1; i <= columnCount; i++) {
                        String key = meta.getColumnName(i);
                        switch (meta.getColumnType(i)) {
                            case Types.VARCHAR:
                            case Types.CHAR:
                            case Types.NCHAR:
                                String string = resultSet.getString(i);
                                row.put(key, string);
                                break;
                            case Types.TIME:
                            case Types.TIME_WITH_TIMEZONE:
                                Time time = resultSet.getTime(i);
                                row.put(key, time);
                                break;
                            case Types.DATE:
                                Date date = resultSet.getDate(i);
                                row.put(key, date);
                            case Types.TIMESTAMP:
                            case Types.TIMESTAMP_WITH_TIMEZONE:
                                Timestamp timestamp = resultSet.getTimestamp(i);
                                row.put(key, timestamp);
                                break;
                            case Types.INTEGER:
                            case Types.TINYINT:
                                int anInt = resultSet.getInt(i);
                                row.put(key, anInt);
                                break;
                            case Types.FLOAT:
                                float aFloat = resultSet.getFloat(i);
                                row.put(key, aFloat);
                                break;
                            case Types.DOUBLE:
                                double aDouble = resultSet.getDouble(i);
                                row.put(key, aDouble);
                                break;
                            case Types.DECIMAL:
                                BigDecimal bigDecimal = resultSet.getBigDecimal(i);
                                row.put(key, bigDecimal);
                                break;
                            case Types.BIGINT:
                                long aLong = resultSet.getLong(i);
                                row.put(key, aLong);
                                break;
                            case Types.ARRAY:
                                Array array = resultSet.getArray(i);
                                row.put(key, array);
                            case Types.BLOB:
                                Blob blob = resultSet.getBlob(i);
                                row.put(key, blob);
                            case Types.CLOB:
                                Clob clob = resultSet.getClob(i);
                                row.put(key, clob);
                            case Types.NCLOB:
                                NClob nClob = resultSet.getNClob(i);
                                row.put(key, nClob);
                            case Types.BIT:
                                short aShort = resultSet.getShort(i);
                                row.put(key, aShort);
                            default:
                                break;

                        }
                    }
                }
                response.setSuccess(true);
            } else {
                response.setSuccess(false);
                rspPacket.setMessage("Command [" + cmd.getHeader().getCommand() + "] is not recognized");
            }
            rspPacket.setResult(data);
            rspPacket.setDuration(System.currentTimeMillis() - start);
        } catch (Exception e) {
            response.getPacket().setMessage("Exe command[" + response.getPacket().getCommandId()
                    + "] found error : " + ExceptionUtil.getStackTrace(e));
            response.setSuccess(false);
        }

        return response;
    }

}
