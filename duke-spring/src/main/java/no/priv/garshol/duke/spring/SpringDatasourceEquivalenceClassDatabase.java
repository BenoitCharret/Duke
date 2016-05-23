
package no.priv.garshol.duke.spring;

import no.priv.garshol.duke.DukeException;
import no.priv.garshol.duke.EquivalenceClassDatabase;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * An eq. class database using an RDBMS as backing.
 *
 * @since 1.0
 */
public class SpringDatasourceEquivalenceClassDatabase implements EquivalenceClassDatabase {

    private DataSource dataSource;
    private JDBCComponent jdbcComponent;

    private int nextclassid;

    public SpringDatasourceEquivalenceClassDatabase(DataSource dataSource, JDBCComponent jdbcComponent) {
        this.dataSource = dataSource;
        this.jdbcComponent = jdbcComponent;
    }

    @PostConstruct
    public void initialize() {
        this.nextclassid = getNextClassId();
    }

    public int getClassCount() {
        throw new UnsupportedOperationException();
    }

    public Iterator<Collection<String>> getClasses() {
        throw new UnsupportedOperationException();
    }

    public Collection<String> getClass(String id) {
        int clid = jdbcComponent.queryForInt("select clid from classes " +
                "where id = '" + id + "'", -1);

        List ids = new ArrayList();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select id from classes where clid = ?")) {
            preparedStatement.setInt(1, clid);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next())
                    ids.add(rs.getString(1));
            }
        } catch (SQLException e) {
            throw new DukeException(e);
        }
        return ids;
    }

    public void addLink(String id1, String id2) {
        int clid1 = getClassId(id1);
        int clid2 = getClassId(id2);

        if (clid1 == clid2 && clid1 != -1)
            return; // we already knew

        if (clid1 == -1 && clid2 == -1) {
            // don't know these from before, so make a new class for them
            addToClass(id1, nextclassid);
            addToClass(id2, nextclassid);
            nextclassid++;
        } else if ((clid1 == -1 && clid2 != -1) ||
                (clid1 != -1 && clid2 == -1)) {
            // one of these has no class, so we add it to the class of the other
            if (clid1 == -1)
                addToClass(id1, clid2);
            else
                addToClass(id2, clid1);
        } else
            // we have classes for both, but they're different
            merge(clid1, clid2);
    }

    public void commit() {
        // mysql is autocommiting, so no need
    }

    public int getClassId(String id) {
        return jdbcComponent.queryForInt("select clid from classes " +
                "where id = '" + id + "'", -1);
    }

    private void addToClass(String id, int clid) {

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("insert into classes values ( ? ,?)")) {
            preparedStatement.setString(1, id);
            preparedStatement.setInt(2, clid);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DukeException(e);
        }
    }

    private void merge(int clid1, int clid2) {

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("update classes set clid = ? where clid = ?")) {
            preparedStatement.setInt(1, clid1);
            preparedStatement.setInt(2, clid2);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DukeException(e);
        }
    }

    private int getNextClassId() {
        return jdbcComponent.queryForInt("select max(clid) from classes", 0) + 1;
    }

}