
package no.priv.garshol.duke.spring;

import no.priv.garshol.duke.DukeException;

import javax.sql.DataSource;
import java.sql.*;

/**
 * Utilities for making life with JDBC easier.
 */
public class JDBCComponent {

  DataSource dataSource;

  public JDBCComponent(DataSource dataSource) {
    this.dataSource = dataSource;
  }

    /**
   * Runs a query that returns a single int.
   */
  public int queryForInt(String sql, int nullvalue) {

    try (
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery()){
        if (!rs.next())
          return nullvalue;
        return rs.getInt(1);
    } catch (SQLException e) {
      throw new DukeException(e);
    }
  }
}