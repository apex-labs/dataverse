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
package org.apex.dataverse.core.msg.packet.info.output;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apex.dataverse.core.enums.OutType;

/**
 * @author Danny.Huo
 * @date 2023/12/14 19:46
 * @since 0.1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HiveOutput extends Output {

    /**
     * New hive output
     * @param db database name
     * @param table table name
     * @return HiveOutput
     */
    public static HiveOutput newOutput(String db, String table) {
        HiveOutput hiveOutput = new HiveOutput();
        hiveOutput.setOutType(OutType.HIVE);
        hiveOutput.setTable(table);
        hiveOutput.setDatabase(db);
        return hiveOutput;
    }

    /**
     * New hive output
     * @param db database name
     * @param table table name
     * @param repartition repartition before write
     * @return HiveOutput
     */
    public static HiveOutput newOutput(String db, String table, Integer repartition) {
        HiveOutput hiveOutput = new HiveOutput();
        hiveOutput.setOutType(OutType.HIVE);
        hiveOutput.setTable(table);
        hiveOutput.setDatabase(db);
        hiveOutput.setPartitions(repartition);
        return hiveOutput;
    }

    /**
     * New hive output
     * @param db database name
     * @param table table name
     * @param tmpTarget tmp target
     * @return HiveOutput
     */
    public static HiveOutput newOutput(String db, String table, String tmpTarget) {
        HiveOutput hiveOutput = new HiveOutput();
        hiveOutput.setOutType(OutType.HIVE);
        hiveOutput.setTable(table);
        hiveOutput.setDatabase(db);
        hiveOutput.setTmpTarget(tmpTarget);
        return hiveOutput;
    }

    /**
     * Constructor without parameter
     */
    private HiveOutput() {

    }

    /**
     * Hive database name
     */
    private String database;

    /**
     * Hive table name
     */
    private String table;

    /**
     * Temp output path
     */
    private String tmpTarget;
}
