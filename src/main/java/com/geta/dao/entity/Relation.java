package com.geta.dao.entity;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class Relation extends ConfigEntity {

    public Relation() {
    }

    public List<String> getTableColumns(Connection connection) throws Exception {
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet resCol = metaData.getColumns(null, null, this.getTableName(), null);
        List<String> columns = new Vector<String>();
        while (resCol.next()) {
            String columnName = resCol.getString("COLUMN_NAME");
            columns.add(columnName);
        }
        return columns;
    }

    public <T> List<T> selectAll(Connection connection) throws Exception {
        ConnectionHandler connHandler = new ConnectionHandler();
        connection = connHandler.checkConnection(connection);
        List<T> result = new Vector<T>();
        try {
            List<String> columns = getTableColumns(connection);
            String request = formatRequest(request("select_all")).replace("*", toColumnsList(columns));
            Statement statement = connection.createStatement();
            System.out.println(request);
            ResultSet resultSet = statement.executeQuery(request);

            while (resultSet.next()) {
                T row = this.makeMe(resultSet, columns, connection);
                result.add(row);
            }
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            connHandler.close(connection);
        }
        return result;
    }

    public <T> T selectById(Connection connection, Object id) throws Exception {
        ConnectionHandler connHandler = new ConnectionHandler();
        connection = connHandler.checkConnection(connection);
        T result = null;
        try {
            this.addValueToPk(id);
            List<String> columns = getTableColumns(connection);
            String request = formatRequest(request("select_by_id")).replace("*", toColumnsList(columns));
            Statement statement = connection.createStatement();
            System.out.println(request);
            ResultSet resultSet = statement.executeQuery(request);
            while (resultSet.next()) {
                result = this.makeMe(resultSet, columns, connection);
            }

        } catch (Exception e) {
            System.out.println(e);
        } finally {
            connHandler.close(connection);
        }
        return (T) result;
    }

    public <T> T selectBy(Connection connection) throws Exception {
        ConnectionHandler connHandler = new ConnectionHandler();
        connection = connHandler.checkConnection(connection);
        T result = null;
        try {
            List<String> columns = getTableColumns(connection);
            String request = formatRequest(request("select_by_condition")).replace("*", toColumnsList(columns));
            Statement statement = connection.createStatement();
            System.out.println(request);
            ResultSet resultSet = statement.executeQuery(request);
            while (resultSet.next()) {
                result = this.makeMe(resultSet, columns, connection);
            }

        } catch (Exception e) {
            System.out.println(e);
        } finally {
            connHandler.close(connection);
        }
        return (T) result;
    }

    public <T> T persit(Connection connection) throws Exception {
        ConnectionHandler connHandler = new ConnectionHandler();
        connection = connHandler.checkConnection(connection);
        T newEntity = null;
        try {
            String request = "";
            Object sequence_value = null;
            if (this.getPkField() != null)
                if (this.getPkField().getValue() != null)
                    request = formatRequest(request("update_by_id"));
                else {
                    sequence_value = getSequenceValue(connection);
                    try {
                        this.getPkField().getField().setAccessible(true);
                        this.getPkField().getField().set(this, getType(Class.forName(this.getPkField().getNameType()),
                                sequence_value));

                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    request = formatRequest(request("insert"));
                }
            Statement statement = connection.createStatement();

            System.out.println(request);
            statement.execute(request);

            newEntity = selectById(connection, getType(Class.forName(this.getPkField().getNameType()),
                    sequence_value));
            connHandler.commit(connection);

        } catch (Exception e) {
            connHandler.rollback(connection);
            System.out.println(e);
        } finally {
            connHandler.close(connection);
        }
        return newEntity;
    }

    public void updateBy(Connection connection) throws Exception {
        ConnectionHandler connHandler = new ConnectionHandler();
        connection = connHandler.checkConnection(connection);
        try {
            String request = formatRequest(request("update_by_condition"));
            Statement statement = connection.createStatement();
            System.out.println(request);
            statement.execute(request);
            connHandler.commit(connection);

        } catch (Exception e) {
            connHandler.rollback(connection);
            System.out.println(e);
        } finally {
            connHandler.close(connection);
        }
    }

    public void updateAll(Connection connection) throws Exception {
        ConnectionHandler connHandler = new ConnectionHandler();
        connection = connHandler.checkConnection(connection);
        try {
            String request = formatRequest(request("update_all"));
            Statement statement = connection.createStatement();
            System.out.println(request);
            statement.execute(request);
            connHandler.commit(connection);

        } catch (Exception e) {
            connHandler.rollback(connection);
            System.out.println(e);
        } finally {
            connHandler.close(connection);
        }
    }

    public void deleteAll(Connection connection) throws Exception {
        ConnectionHandler connHandler = new ConnectionHandler();
        connection = connHandler.checkConnection(connection);
        try {
            String request = formatRequest(request("delete_all"));
            Statement statement = connection.createStatement();
            System.out.println(request);
            statement.execute(request);
            connHandler.commit(connection);

        } catch (Exception e) {
            connHandler.rollback(connection);
            System.out.println(e);
        } finally {
            connHandler.close(connection);
        }
    }

    public void deleteById(Connection connection) throws Exception {
        ConnectionHandler connHandler = new ConnectionHandler();
        connection = connHandler.checkConnection(connection);
        try {
            String request = formatRequest(request("delete_by_id"));
            Statement statement = connection.createStatement();
            System.out.println(request);
            statement.execute(request);
            connHandler.commit(connection);

        } catch (Exception e) {
            connHandler.rollback(connection);
            System.out.println(e);
        } finally {
            connHandler.close(connection);
        }
    }

    public void deleteBy(Connection connection) throws Exception {
        ConnectionHandler connHandler = new ConnectionHandler();
        connection = connHandler.checkConnection(connection);
        try {
            String request = formatRequest(request("delete_by_condition"));
            Statement statement = connection.createStatement();
            System.out.println(request);
            statement.execute(request);
            connHandler.commit(connection);

        } catch (Exception e) {
            connHandler.rollback(connection);
            System.out.println(e);
        } finally {
            connHandler.close(connection);
        }
    }

    public String getSequenceValue(Connection connection) throws Exception {
        ConnectionHandler connHandler = new ConnectionHandler();
        connection = connHandler.checkConnection(connection);
        String response = "";
        try {
            String request = formatRequest(request("sequence_val"));
            Statement statement = connection.createStatement();
            System.out.println(request);
            ResultSet resultSet = statement.executeQuery(request);
            connHandler.commit(connection);
            while (resultSet.next()) {
                response = resultSet.getString(1);
            }
        } catch (Exception e) {
            connHandler.rollback(connection);
            System.out.println(e);
        } finally {
            connHandler.close(connection);
        }
        return response;

    }

    public void createSequence(Connection connection) throws Exception {
        ConnectionHandler connHandler = new ConnectionHandler();
        connection = connHandler.checkConnection(connection);
        try {
            String request = formatRequest(request("sequence_create"));
            Statement statement = connection.createStatement();
            System.out.println(request);
            statement.execute(request);
            connHandler.commit(connection);
        } catch (Exception e) {
            connHandler.rollback(connection);
            System.out.println(e);
        } finally {
            connHandler.close(connection);
        }
    }

    public String formatRequest(String request) throws ClassNotFoundException {

        DaoField pkField = getPkField();
        request = request.replace("[table_name]", getTableName());
        request = request.replace("[id]", pkField.getName());
        request = request.replace("[id_value]", pkField.getValue() == null ? "null" : pkField.getValue().toString());
        request = request.replace("[connectionditions]", getConditions());
        request = request.replace("[columns]", getColumnsParams("columns"));
        request = request.replace("[columns_value]", getColumnsParams("values"));
        request = request.replace("[columns_and_value]", getColumnsAndValue());

        return request;
    }

    public String request(String typeRequest) throws Exception {
        HashMap<String, String> request = new HashMap<String, String>();
        request.put("select_all", "select * from [table_name]");
        request.put("select_by_id", "select * from [table_name] WHERE [id] = [id_value]");
        request.put("select_by_condition", "select * from [table_name] WHERE [conditions]");
        request.put("insert", "insert into [table_name] ([columns]) VALUES ([columns_value])");
        request.put("update_all", "update [table_name] set [columns_and_value] ");
        request.put("update_by_id", "update [table_name] set [columns_and_value] WHERE [id] = [id_value]");
        request.put("update_by_condition", "update [table_name] set [columns_and_value] WHERE [conditions]");
        request.put("delete_all", "delete from [table_name] ");
        request.put("delete_by_id", "delete from [table_name] WHERE [id] = [id_value]");
        request.put("delete_by_condition", "delete from [table_name] WHERE [conditions]");
        request.put("sequence_val", "select nextval('seq_[table_name]')");
        request.put("sequence_create",
                "create sequence seq_[table_name] minvalue 1 maxvalue 99999999 start with 1 increment by 1");

        String value = request.get(typeRequest);
        if (value == null)
            throw new RuntimeException("This request type " + typeRequest + " doesn't exist");
        return value;
    }

}
