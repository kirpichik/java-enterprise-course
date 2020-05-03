package org.polushin.java_enterprise.osm_xml;

import javax.xml.stream.XMLStreamException;

public final class Main {
  public static void main(final String[] args) {
    final var file = Main.class.getClassLoader().getResourceAsStream("RU-NVS.osm");
    try (final var wrapper = new XMLReaderWrapper(file)) {
      printStatistics(StatisticsCollector.collectStatistics(wrapper.getReader()));
    } catch (final XMLStreamException e) {
      e.printStackTrace();
    }
  }

  private static void printStatistics(final StatisticsCollector.Statistics statistics) {
    System.out.println("Users changes:");
    statistics.usersChanges.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue() - e1.getValue())
            .forEach(e -> System.out.println(e.getKey() + ": " + e.getValue()));

    System.out.println("Tags by nodes:");
    statistics.tagsByNodes.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue() - e1.getValue())
            .forEach(e -> System.out.println(e.getKey() + ": " + e.getValue()));
  }

}
