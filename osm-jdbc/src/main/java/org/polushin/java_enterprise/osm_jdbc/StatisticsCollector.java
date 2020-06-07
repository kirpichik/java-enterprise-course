package org.polushin.java_enterprise.osm_jdbc;

import org.polushin.java_enterprise.osm_jdbc.db.dao.NodeDao;
import org.polushin.java_enterprise.osm_jdbc.osm.Node;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.util.ArrayList;
import java.util.List;

public final class StatisticsCollector {

  private final XMLStreamReader reader;
  private final Unmarshaller unmarshaller;
  private final NodeDao nodeDao;

  private StatisticsCollector(final XMLStreamReader reader, final NodeDao nodeDao) throws JAXBException {
    this.reader = reader;
    final var jaxbContext = JAXBContext.newInstance(Node.class);
    unmarshaller = jaxbContext.createUnmarshaller();
    this.nodeDao = nodeDao;
  }

  private void insertUsingPreparedStatement() throws XMLStreamException {
    while (reader.hasNext()) {
      final int event = reader.next();
      if (event == XMLEvent.START_ELEMENT && reader.getLocalName().equals("node")) {
        final Node node;
        try {
          node = (Node) unmarshaller.unmarshal(reader);
        } catch (final JAXBException e) {
          throw new RuntimeException(e);
        }

        nodeDao.insertUsingPrepareStatement(node);
      }
    }
  }

  private void insertUsingStatement() throws XMLStreamException {
    while (reader.hasNext()) {
      final int event = reader.next();
      if (event == XMLEvent.START_ELEMENT && reader.getLocalName().equals("node")) {
        final Node node;
        try {
          node = (Node) unmarshaller.unmarshal(reader);
        } catch (final JAXBException e) {
          throw new RuntimeException(e);
        }

        nodeDao.insertUsingStatement(node);
      }
    }
  }

  private void insertUsingBatch() throws XMLStreamException {
    final List<Node> nodes = new ArrayList<>();
    while (reader.hasNext()) {
      final int event = reader.next();
      if (event == XMLEvent.START_ELEMENT && reader.getLocalName().equals("node")) {
        final Node node;
        try {
          node = (Node) unmarshaller.unmarshal(reader);
        } catch (final JAXBException e) {
          throw new RuntimeException(e);
        }

        nodes.add(node);
      }
    }

    nodeDao.insertBatch(nodes);
  }

  private static long measureTime(final ThrowableRunnable runnable) {
    final long start = System.currentTimeMillis();
    try {
      runnable.run();
    } catch (final Throwable e) {
      throw new RuntimeException(e);
    }
    return System.currentTimeMillis() - start;
  }

  @FunctionalInterface
  private interface ThrowableRunnable {
    void run() throws Throwable;
  }

  public static Statistics collectStatistics(final XMLStreamReader reader, final NodeDao nodeDao) throws JAXBException {
    final StatisticsCollector collector = new StatisticsCollector(reader, nodeDao);
    return new Statistics(
            measureTime(collector::insertUsingPreparedStatement),
            measureTime(collector::insertUsingStatement),
            measureTime(collector::insertUsingBatch)
    );
  }

  public static final class Statistics {
    public final long preparedStatementTime;
    public final long statementTime;
    public final long batchTime;

    public Statistics(final long preparedStatementTime, final long statementTime, final long batchTime) {
      this.preparedStatementTime = preparedStatementTime;
      this.statementTime = statementTime;
      this.batchTime = batchTime;
    }
  }

}
