package org.polushin.java_enterprise.osm_xml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class StatisticsCollector {

  private final XMLStreamReader reader;

  private final Map<String, Set<String>> usersChanges = new HashMap<>();
  private final Map<String, Integer> tagsByNodes = new HashMap<>();

  private State state = State.FREE;

  private StatisticsCollector(final XMLStreamReader reader) {
    this.reader = reader;
  }

  private Statistics doCollect() throws XMLStreamException {
    while (reader.hasNext() && state != State.EXIT && state != State.ERROR)
      handleState(reader.next());
    if (state == State.ERROR)
      throw new RuntimeException("Error parsing state");
    return new Statistics(usersChanges, tagsByNodes);
  }

  private void handleState(final int event) {
    switch (state) {
      case FREE:
        handleFree(event);
        break;
      case NODE:
        handleNode(event);
        break;
      default:
        throw new RuntimeException("Unknown state " + state);
    }
  }

  private void handleFree(final int event) {
    if (event == XMLEvent.START_ELEMENT) {
      if (reader.getLocalName().equals("node")) {
        state = State.NODE;
        final var user = reader.getAttributeValue(null, "user");
        final var changeset = reader.getAttributeValue(null, "changeset");
        usersChanges.computeIfAbsent(user, key -> new HashSet<>()).add(changeset);
      }
    }
  }

  private void handleNode(final int event) {
    switch (event) {
      case XMLEvent.START_ELEMENT:
        if (reader.getLocalName().equals("tag")) {
          final var key = reader.getAttributeValue(null, "k");
          tagsByNodes.put(key, tagsByNodes.getOrDefault(key, 0) + 1);
        } else {
          state = State.ERROR;
          System.out.println(reader.getLocalName());
        }
        break;
      case XMLEvent.END_ELEMENT:
        if (reader.getLocalName().equals("node"))
          state = State.FREE;
        break;
      default:
        break;
    }
  }

  private enum State {
    FREE,
    NODE,
    EXIT,
    ERROR
  }

  public static Statistics collectStatistics(final XMLStreamReader reader) throws XMLStreamException {
    return new StatisticsCollector(reader).doCollect();
  }

  public static final class Statistics {
    public final Map<String, Integer> usersChanges;
    public final Map<String, Integer> tagsByNodes;

    private Statistics(final Map<String, Set<String>> usersChanges, final Map<String, Integer> tagsByNodes) {
      this.usersChanges = usersChanges.entrySet().stream()
              .collect(Collectors.toMap(Map.Entry::getKey, x -> x.getValue().size()));
      this.tagsByNodes = tagsByNodes;
    }
  }

}
