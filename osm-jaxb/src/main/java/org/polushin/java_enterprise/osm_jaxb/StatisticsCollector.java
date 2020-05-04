package org.polushin.java_enterprise.osm_jaxb;

import org.polushin.java_enterprise.osm_jaxb.osm.Node;
import org.polushin.java_enterprise.osm_jaxb.osm.Tag;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class StatisticsCollector {

  private final XMLStreamReader reader;
  private final Unmarshaller unmarshaller;

  private final Map<String, Set<BigInteger>> usersChanges = new HashMap<>();
  private final Map<String, Integer> tagsByNodes = new HashMap<>();

  private StatisticsCollector(final XMLStreamReader reader) throws JAXBException {
    this.reader = reader;
    final var jaxbContext = JAXBContext.newInstance(Node.class);
    unmarshaller = jaxbContext.createUnmarshaller();
  }

  private Statistics doCollect() throws XMLStreamException {
    while (reader.hasNext()) {
      final int event = reader.next();
      if (event == XMLEvent.START_ELEMENT && reader.getLocalName().equals("node")) {
        final Node node;
        try {
          node = (Node) unmarshaller.unmarshal(reader);
        } catch (final JAXBException e) {
          throw new RuntimeException(e);
        }

        usersChanges.computeIfAbsent(node.getUser(), key -> new HashSet<>()).add(node.getChangeset());
        node.getTag().stream()
                .map(Tag::getK)
                .forEach(key -> tagsByNodes.put(key, tagsByNodes.getOrDefault(key, 0) + 1));
      }
    }

    return new Statistics(usersChanges, tagsByNodes);
  }

  public static Statistics collectStatistics(final XMLStreamReader reader) throws XMLStreamException, JAXBException {
    return new StatisticsCollector(reader).doCollect();
  }

  public static final class Statistics {
    public final Map<String, Integer> usersChanges;
    public final Map<String, Integer> tagsByNodes;

    private Statistics(final Map<String, Set<BigInteger>> usersChanges, final Map<String, Integer> tagsByNodes) {
      this.usersChanges = usersChanges.entrySet().stream()
              .collect(Collectors.toMap(Map.Entry::getKey, x -> x.getValue().size()));
      this.tagsByNodes = tagsByNodes;
    }
  }

}
