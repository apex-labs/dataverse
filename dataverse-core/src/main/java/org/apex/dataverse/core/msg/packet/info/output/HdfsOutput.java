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
import org.apex.dataverse.core.enums.OutFormat;
import org.apex.dataverse.core.enums.OutType;
import org.apex.dataverse.core.util.StringUtil;

/**
 * @author Danny.Huo
 * @date 2023/12/14 19:46
 * @since 0.1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HdfsOutput extends Output {

    /**
     * New default hdfs output, Orc format
     *
     * @param path out path
     * @return HdfsOutput
     */
    public static HdfsOutput newOutput(String path) {
        HdfsOutput hdfsOutput = new HdfsOutput();
        hdfsOutput.setOutType(OutType.HDFS);
        hdfsOutput.setFormat(OutFormat.ORC);
        hdfsOutput.setTmpTarget(path);
        hdfsOutput.setTarget(path);
        hdfsOutput.setOverWrite(Boolean.TRUE);
        return hdfsOutput;
    }

    /**
     * New default hdfs output, Orc format
     *
     * @param path out path
     * @param overWrite write file overwirte or not
     * @return HdfsOutput
     */
    public static HdfsOutput newOutput(String path, Boolean overWrite) {
        HdfsOutput hdfsOutput = new HdfsOutput();
        hdfsOutput.setOutType(OutType.HDFS);
        hdfsOutput.setFormat(OutFormat.ORC);
        hdfsOutput.setTmpTarget(path);
        hdfsOutput.setTarget(path);
        hdfsOutput.setOverWrite(overWrite);
        return hdfsOutput;
    }

    /**
     * New default hdfs output, Orc format
     *
     * @param path   out path
     * @param format OutputFormat
     * @return HdfsOutput
     */
    public static HdfsOutput newOutput(String path, OutFormat format) {
        HdfsOutput hdfsOutput = new HdfsOutput();
        hdfsOutput.setOutType(OutType.HDFS);
        hdfsOutput.setFormat(format);
        hdfsOutput.setTmpTarget(path);
        hdfsOutput.setTarget(path);
        hdfsOutput.setOverWrite(Boolean.TRUE);
        return hdfsOutput;
    }

    /**
     * New default hdfs output, Orc format
     *
     * @param path   out path
     * @param format OutputFormat
     * @param overWrite Boolean
     * @return HdfsOutput
     */
    public static HdfsOutput newOutput(String path, OutFormat format, Boolean overWrite) {
        HdfsOutput hdfsOutput = new HdfsOutput();
        hdfsOutput.setOutType(OutType.HDFS);
        hdfsOutput.setFormat(format);
        hdfsOutput.setTmpTarget(path);
        hdfsOutput.setTarget(path);
        hdfsOutput.setOverWrite(overWrite);
        return hdfsOutput;
    }

    /**
     * New hdfs output with target and temp target, Orc format
     *
     * @param target    target
     * @param tmpTarget tmp target
     * @return HdfsOutput
     */
    public static HdfsOutput newOutput(String target, String tmpTarget) {
        HdfsOutput hdfsOutput = new HdfsOutput();
        hdfsOutput.setOutType(OutType.HDFS);
        hdfsOutput.setFormat(OutFormat.ORC);
        hdfsOutput.setTmpTarget(tmpTarget);
        hdfsOutput.setTarget(target);
        hdfsOutput.setOverWrite(Boolean.TRUE);
        return hdfsOutput;
    }

    /**
     * New hdfs output with target and temp target, Orc format
     *
     * @param target    target
     * @param tmpTarget tmp target
     * @param overWrite Boolean
     * @return HdfsOutput
     */
    public static HdfsOutput newOutput(String target, String tmpTarget, Boolean overWrite) {
        HdfsOutput hdfsOutput = new HdfsOutput();
        hdfsOutput.setOutType(OutType.HDFS);
        hdfsOutput.setFormat(OutFormat.ORC);
        hdfsOutput.setTmpTarget(tmpTarget);
        hdfsOutput.setTarget(target);
        hdfsOutput.setOverWrite(overWrite);
        return hdfsOutput;
    }

    /**
     * New hdfs output with target and temp target, Orc format
     *
     * @param target    target
     * @param tmpTarget tmp target
     * @param format    OutputFormat
     * @return HdfsOutput
     */
    public static HdfsOutput newOutput(String target, String tmpTarget, OutFormat format) {
        HdfsOutput hdfsOutput = new HdfsOutput();
        hdfsOutput.setOutType(OutType.HDFS);
        hdfsOutput.setFormat(format);
        hdfsOutput.setTmpTarget(tmpTarget);
        hdfsOutput.setTarget(target);
        hdfsOutput.setOverWrite(Boolean.TRUE);
        return hdfsOutput;
    }

    /**
     * New hdfs output with target and temp target, Orc format
     *
     * @param target    target
     * @param tmpTarget tmp target
     * @param format    OutputFormat
     * @param overWrite    Boolean
     * @return HdfsOutput
     */
    public static HdfsOutput newOutput(String target, String tmpTarget, OutFormat format, Boolean overWrite) {
        HdfsOutput hdfsOutput = new HdfsOutput();
        hdfsOutput.setOutType(OutType.HDFS);
        hdfsOutput.setFormat(format);
        hdfsOutput.setTmpTarget(tmpTarget);
        hdfsOutput.setTarget(target);
        hdfsOutput.setOverWrite(overWrite);
        return hdfsOutput;
    }

    /**
     * Constructor without parameter
     */
    private HdfsOutput() {

    }

    /**
     * ORC, PARQUET, AVRO, ...
     */
    private OutFormat format;

    /**
     * Output target path
     */
    private String target;

    /**
     * Temp output path
     */
    private String tmpTarget;

    /**
     * Overwrite or not
     */
    private Boolean overWrite = false;

    /**
     * Get write path, tmpTarget first, if tmpTarget null return target
     * @return String
     */
    public String writePath() {
        return StringUtil.isBlank(tmpTarget) ? target : tmpTarget;
    }

}
