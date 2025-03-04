package poo.infra;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public abstract class BaseDao<T> {
  private final Connection connection;

  public BaseDao(Connection connection) {
    this.connection = connection;
  }

  public Connection getConnection() {
    return connection;
  }

  public abstract T findById(Integer id) throws SQLException;
  public abstract ArrayList<T> findAll() throws SQLException;
  public abstract void create(T entity) throws SQLException;
  public abstract void update(T entity) throws SQLException;
  public abstract void delete(Integer id) throws SQLException;
}
