package org.polushin.java_enterprise.osm_jdbc.db.dao;

import org.polushin.java_enterprise.osm_jdbc.db.ConnectionManager;
import org.polushin.java_enterprise.osm_jdbc.osm.Node;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public final class NodeDaoImpl implements NodeDao {

    private static final DatatypeFactory datatypeFactory;

    private static final int BATCH_SIZE = 256;

    static {
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (final DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean insertUsingPrepareStatement(final Node node) {
        return ConnectionManager.executeBoolean(connection -> {
            final PreparedStatement prepared = connection.prepareStatement("INSERT INTO nodes VALUES (?, ?, ?, ?, ?, ?, ?, ?)" +
                    "id = ?, " +
                    "node_id version = ?, " +
                    "timestamp = ?, " +
                    "uid = ?, " +
                    "user = ?, " +
                    "changeset = ?, " +
                    "lat = ?, " +
                    "lon = ? ", Statement.RETURN_GENERATED_KEYS);
            storeNodeIntoPreparedStatement(node, prepared);

            return prepared.executeUpdate() == 1;
        });
    }

    @Override
    public boolean insertUsingStatement(final Node node) {
        return ConnectionManager.executeBoolean(connection ->
                connection.createStatement().executeUpdate(
                        String.format("INSERT INTO nodes VALUES (%d, %d, '%tF', %d, '%s', %d, %f, %f)",
                                node.getId(),
                                node.getVersion(),
                                new Timestamp(node.getTimestamp().toGregorianCalendar().getTimeInMillis()),
                                node.getUid(),
                                node.getUser(),
                                node.getChangeset(),
                                node.getLat(),
                                node.getLon()
                        )
                ) == 1);
    }

    @Override
    public boolean insertBatch(final Collection<Node> nodes) {
        return ConnectionManager.executeBoolean(connection -> {
            final PreparedStatement prepared = connection.prepareStatement(
                    "INSERT INTO nodes VALUES (default, ?, ?, ?, ?, ?, ?, ?, ?)");

            int count = 0;
            for (final Node node : nodes) {
                storeNodeIntoPreparedStatement(node, prepared);
                prepared.addBatch();
                ++count;

                if (count % BATCH_SIZE == 0 || count == nodes.size())
                    prepared.executeBatch();
            }

            return true;
        });
    }

    private void storeNodeIntoPreparedStatement(final Node node, final PreparedStatement prepared) throws SQLException {
        prepared.setInt(1, node.getId().intValue());
        prepared.setInt(2, node.getVersion().intValue());
        prepared.setTimestamp(3, new Timestamp(node.getTimestamp().toGregorianCalendar().getTimeInMillis()));
        prepared.setInt(4, node.getUid().intValue());
        prepared.setString(5, node.getUser());
        prepared.setInt(6, node.getChangeset().intValue());
        prepared.setDouble(7, node.getLat());
        prepared.setDouble(8, node.getLon());
    }

    @Override
    public Optional<Node> getNodeById(final int id) {
        return Optional.ofNullable(
                ConnectionManager.execute(connection ->
                        nodeFromResultSet(connection.createStatement()
                                .executeQuery("SELECT * FROM nodes WHERE id = " + id)))
        );
    }

    private Node nodeFromResultSet(final ResultSet result) throws SQLException {
        final Node node = new Node();
        node.setId(BigInteger.valueOf(result.getInt("id")));
        node.setVersion(BigInteger.valueOf(result.getInt("version")));
        node.setTimestamp(dbTimeToXmlTime(result.getTimestamp("timestamp")));
        node.setUid(BigInteger.valueOf(result.getInt("uid")));
        node.setUser(result.getString("user"));
        node.setChangeset(BigInteger.valueOf(result.getInt("changeset")));
        node.setLat(result.getDouble("lat"));
        node.setLon(result.getDouble("lon"));
        return node;
    }

    private XMLGregorianCalendar dbTimeToXmlTime(final Timestamp timestamp) {
        final LocalDateTime dbTime = timestamp.toLocalDateTime();
        final XMLGregorianCalendar xmlCalendar = datatypeFactory.newXMLGregorianCalendar();
        xmlCalendar.setYear(dbTime.getYear());
        xmlCalendar.setMonth(dbTime.getMonthValue());
        xmlCalendar.setDay(dbTime.getDayOfMonth());
        xmlCalendar.setHour(dbTime.getHour());
        xmlCalendar.setMinute(dbTime.getMinute());
        xmlCalendar.setSecond(dbTime.getSecond());
        xmlCalendar.setFractionalSecond(new BigDecimal("0." + dbTime.getNano()));
        return xmlCalendar;
    }

    @Override
    public boolean updateNode(final Node node) {
        return ConnectionManager.executeBoolean(connection -> {
            final PreparedStatement prepared = connection.prepareStatement("UPDATE nodes SET" +
                    "id = ?, " +
                    "node_id version = ?, " +
                    "timestamp = ?, " +
                    "uid = ?, " +
                    "user = ?, " +
                    "changeset = ?, " +
                    "lat = ?, " +
                    "lon = ? " +
                    "WHERE id = ?");
            storeNodeIntoPreparedStatement(node, prepared);
            prepared.setInt(9, node.getId().intValue());

            return prepared.executeUpdate() == 1;
        });
    }

    @Override
    public boolean deleteNodeById(final int id) {
        return ConnectionManager.executeBoolean(connection ->
                connection.createStatement().executeUpdate("DELETE FROM nodes WHERE id = " + id) == 1);
    }
}
