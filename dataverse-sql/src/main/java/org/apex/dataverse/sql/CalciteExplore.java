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
package org.apex.dataverse.sql;

import org.apache.calcite.adapter.java.ReflectiveSchema;
import org.apache.calcite.config.Lex;
import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.*;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;

import java.sql.*;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 *
 * @author Danny.Huo
 * @date 2023/8/19 16:21
 * @since 0.1.0
 */
public class CalciteExplore {

    /**
     *
     * @param args
     * @throws SqlParseException
     */
    public static void main(String[] args) throws SqlParseException, SQLException, ClassNotFoundException {
        String sql = "select a.* from (select col1, col2, col3 from c) a left join (select col4, col5 from d) b on a.id = b.id and a.name = b.name where a.ke > 0 and b.col4 <> 0 and b.col4 in (select col4 from e where e.col5 is not null) order by b.col4 desc limit 10";
//        String sql = "SELECT \n" +
//                "  t1.id, 1 + 2 + t1.score AS v \n" +
//                "FROM t1, t2 \n" +
//                "WHERE \n" +
//                "  t1.id = t2.id AND \n" +
//                "  t2.id < 1000";

//        String sql = "CREATE TABLE IF NOT EXISTS ods_cdp_crowd_pack_return_data_di (\n" +
//                "    hhid bigint comment '健合hhid',\n" +
//                "    crowd_pack_id bigint comment '人群包ID',\n" +
//                "    date_created datetime comment '创建时间'\n" +
//                ") COMMENT '人群包回流数据' \n" +
//                "PARTITIONED BY (ds STRING COMMENT '时间分区yyyyMMdd') \n" +
//                "LIFECYCLE 30;";
        SqlParser sqlParser = SqlParser.create(sql);
        //SqlNode sqlNode = sqlParser.parseQuery();
        SqlNode sqlNode = sqlParser.parseStmt();
//        SqlNodeList selectList = ((SqlSelect) ((SqlOrderBy) ((SqlNodeList) sqlNode).get(0)).query).getSelectList();
        System.out.println(sqlNode);

//        sqlNode.accept();

        SqlKind kind = sqlNode.getKind();
        System.out.println("kind：" + kind);

        SqlSelect selectNode = (SqlSelect) sqlNode;
//        SqlIdentifier sqlIdentifier = (SqlIdentifier) sqlNode;
        System.out.println("------"+selectNode.getSelectList());

        System.out.println("========" + test1(sqlNode,false));
        //test();

    }

    public static Set<String> test1(SqlNode sqlNode,boolean fromOrJoin){
        SqlKind sqlKind = sqlNode.getKind();
        if(SqlKind.SELECT.equals(sqlKind)){
            SqlSelect selectNode = (SqlSelect) sqlNode;
            Set<String> selectList = new HashSet<>(test1(selectNode.getFrom(),true));
            selectNode.getSelectList().getList().stream().filter(node -> node instanceof SqlCall)
                    .forEach(node -> selectList.addAll(test1(node,false)));
            selectList.addAll(test1(selectNode.getWhere(),false));
            selectList.addAll(test1(selectNode.getSelectList(),false));
            return selectList;
        }
        if(SqlKind.JOIN.equals(sqlKind)){
            SqlJoin sqlJoin = (SqlJoin) sqlNode;
            Set<String> joinList = new HashSet<>();
            joinList.addAll(test1(sqlJoin.getRight(),true));
            joinList.addAll(test1(sqlJoin.getLeft(),true));
            return joinList;
        }
        if(SqlKind.AS.equals(sqlKind)){
            SqlCall sqlCall = (SqlCall) sqlNode;
            return test1(sqlCall.getOperandList().get(0),fromOrJoin);
        }
        if(SqlKind.IDENTIFIER.equals(sqlKind)){
            Set<String> identifierList = new HashSet<>();
            if(fromOrJoin){
                SqlIdentifier sqlIdentifier = (SqlIdentifier) sqlNode;
                identifierList.add(sqlIdentifier.toString());
            }
            return identifierList;
        }
        Set<String> defaultList = new HashSet<>();
        if(sqlNode instanceof SqlCall){
            SqlCall call = (SqlCall) sqlNode;
            call.getOperandList().
                    forEach(node -> defaultList.addAll(test1(node,false)));
        }
        return defaultList;
    }


    public static class HrSchema {
        public final Employee[] emps = new Employee[2];
        public final Department[] depts = new Department[2];
    }

    public static class Employee {
        private Integer empid;
        private String deptno;

        public Integer getEmpid() {
            return empid;
        }

        public void setEmpid(Integer empid) {
            this.empid = empid;
        }

        public String getDeptno() {
            return deptno;
        }

        public void setDeptno(String deptno) {
            this.deptno = deptno;
        }
    }

    public static class Department {
        private String deptno;
        private String deptName;

        public String getDeptno() {
            return deptno;
        }

        public void setDeptno(String deptno) {
            this.deptno = deptno;
        }

        public String getDeptName() {
            return deptName;
        }

        public void setDeptName(String deptName) {
            this.deptName = deptName;
        }
    }

    public static void test() throws ClassNotFoundException, SQLException {

        Class.forName("org.apache.calcite.jdbc.Driver");

        Properties info = new Properties();
        info.setProperty("lex", "JAVA");
        Connection connection =
                DriverManager.getConnection("jdbc:calcite:", info);
        CalciteConnection calciteConnection =
                connection.unwrap(CalciteConnection.class);
        SchemaPlus rootSchema = calciteConnection.getRootSchema();
        Schema schema = new ReflectiveSchema(new HrSchema());
        rootSchema.add("hr", schema);
        Statement statement = calciteConnection.createStatement();
        ResultSet resultSet = statement.executeQuery(
                "select d.deptno, min(e.empid)\n"
                        + "from hr.emps as e\n"
                        + "join hr.depts as d\n"
                        + "  on e.deptno = d.deptno\n"
                        + "group by d.deptno\n"
                        + "having count(*) > 1");
        System.out.println(resultSet);
        resultSet.close();
        statement.close();
        connection.close();
    }
}
