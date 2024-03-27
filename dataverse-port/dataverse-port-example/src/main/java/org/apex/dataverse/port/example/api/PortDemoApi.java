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
package org.apex.dataverse.port.example.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apex.dataverse.core.enums.CmdSet;
import org.apex.dataverse.core.exception.InvalidCmdException;
import org.apex.dataverse.core.exception.InvalidConnException;
import org.apex.dataverse.core.msg.Header;
import org.apex.dataverse.core.msg.Request;
import org.apex.dataverse.core.msg.Response;
import org.apex.dataverse.core.msg.packet.Packet;
import org.apex.dataverse.core.msg.packet.di.DiReqPacket;
import org.apex.dataverse.core.msg.packet.dd.ExeSqlReqPacket;
import org.apex.dataverse.core.msg.packet.info.output.HdfsOutput;
import org.apex.dataverse.core.msg.serialize.ISerialize;
import org.apex.dataverse.core.netty.codec.MessageCodec;
import org.apex.dataverse.core.util.UCodeUtil;
import org.apex.dataverse.port.core.exception.NoPortNodeException;
import org.apex.dataverse.port.driver.conn.PortConnection;
import org.apex.dataverse.port.driver.conn.pool.PortExchanger;
import org.apex.dataverse.port.driver.exception.PortConnectionException;
import org.apex.dataverse.port.example.param.DiParam;
import org.apex.dataverse.port.example.param.SqlParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Danny.Huo
 * @date 2023/12/7 14:30
 * @since 0.1.0
 */
@RestController
@RequestMapping("/api")
public class PortDemoApi {

    private String defaultUrl = "jdbc:mysql://ip:3306/ecommerce?tinyInt1isBit=false&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=CONVERT_TO_NULL";
    private String defaultUser = "username";
    private String defaultPassword = "password";
    private String defaultDriver = "com.mysql.jdbc.Driver";

    private PortExchanger connExchanger;

    @PostMapping("/query")
    public Object query(@RequestBody SqlParam sqlParam) throws InvalidCmdException,
            InterruptedException, InvalidConnException, PortConnectionException, NoPortNodeException, JsonProcessingException {
        Long storageId = 10002L;
        ExeSqlReqPacket exeSqlReqPacket = new ExeSqlReqPacket();
        exeSqlReqPacket.setExeEnv((byte) 1);
        exeSqlReqPacket.setStoreInfos(sqlParam.getStoreInfos());
        exeSqlReqPacket.setSql(sqlParam.getSql());
        exeSqlReqPacket.setCommandId(UCodeUtil.produce());
        exeSqlReqPacket.setSourceStorageId(storageId);

        Request<Packet> request = new Request<>();
        request.setPacket(exeSqlReqPacket);

        Header header = Header.newHeader(CmdSet.EXE_SQL.getReq());

        request.setHeader(header);

        PortConnection conn = connExchanger.getConn(storageId.toString());
        Response<Packet> response = null;
        try {
            response = conn.exe(request);
        } finally {
            connExchanger.close(conn);
        }
        return response.getPacket();
    }

    @PostMapping("/di")
    public Object di(@RequestBody DiParam di) throws InvalidCmdException,
            InterruptedException, InvalidConnException, PortConnectionException,
            NoPortNodeException, JsonProcessingException {
        DiReqPacket diPacket = new DiReqPacket();
        diPacket.setCommandId(UCodeUtil.produce());
        diPacket.setIncr(false);
        diPacket.setDriver(di.getDriver() == null ? defaultDriver : di.getDriver());
        diPacket.setUser(di.getUser() == null ? defaultUser : di.getUser());
        diPacket.setPassword(di.getPassword() == null ? defaultPassword : di.getPassword());
        diPacket.setQuery(di.getQuery());
        diPacket.setFetchSize(500);
        diPacket.setNumPartitions((short)2);
        diPacket.setPartitionColumn(null);
        diPacket.setLowerBound(null);
        diPacket.setUpperBound(null);
        diPacket.setQueryTimeout(50000);
        diPacket.setUrl(di.getUrl() == null ? defaultUrl : di.getUrl());
        diPacket.setOutput(HdfsOutput.newOutput(di.getStoreName()));
        Long storageId = 10002L;
        diPacket.setSourceStorageId(storageId);

        Request<Packet> request = new Request<>();
        request.setPacket(diPacket);
        Header header = Header.newHeader(CmdSet.DATA_INTEGRATION.getReq(), ISerialize.JDK_SERIALIZE);
        request.setHeader(header);

        PortConnection conn = connExchanger.getConn(storageId.toString());
        Response<Packet> response;
        try {
            response = conn.exe(request);
        } finally {
            connExchanger.close(conn);
        }

        return response.getPacket();
    }

    @Autowired
    public void setConnExchanger(PortExchanger connExchanger) {
        this.connExchanger = connExchanger;
    }
}
