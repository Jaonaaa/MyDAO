package com.geta.dao.entity;

import java.sql.Connection;
import java.sql.SQLException;

import com.geta.dao.connection.Connect;

public class ConnectionHandler {

    Boolean isNull;

    public Connection checkConnection(Connection connection) throws SQLException {
        if (connection == null) {
            connection = Connect.getConnectionPostgresql();
            this.isNull = true;
        } else
            this.isNull = false;
        return connection;
    }

    public void rollback(Connection connection) throws SQLException {
        connection.rollback();
    }

    public void commit(Connection connection) throws SQLException {
        if (this.isNull) {
            connection.commit();
        }
    }

    public void close(Connection connection) throws SQLException {
        if (this.isNull) {
            connection.close();
        }
    }

    @Override
    public String toString() {
        return "ConnectionHandler [isNull=" + isNull + "]";
    }
}
