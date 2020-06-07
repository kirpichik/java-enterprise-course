package org.polushin.java_enterprise.osm_jdbc;

import org.polushin.java_enterprise.osm_jdbc.db.dao.NodeDaoImpl;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

public final class Main {
  public static void main(final String[] args) {
    final var file = Main.class.getClassLoader().getResourceAsStream("RU-NVS.osm");
    try (final var wrapper = new XMLReaderWrapper(file)) {
      printStatistics(StatisticsCollector.collectStatistics(wrapper.getReader(), new NodeDaoImpl()));
    } catch (final XMLStreamException | JAXBException e) {
      e.printStackTrace();
    }
  }

  private static void printStatistics(final StatisticsCollector.Statistics statistics) {
    System.out.format("PreparedStatement,Statement,Batch\n%d,%d,%d\n",
            statistics.preparedStatementTime, statistics.statementTime, statistics.batchTime);
  }

}
