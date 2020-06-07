package org.polushin.java_enterprise.osm_jdbc;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;

public final class XMLReaderWrapper implements AutoCloseable {
  private static final XMLInputFactory factory = XMLInputFactory.newInstance();

  private final XMLStreamReader reader;

  XMLReaderWrapper(final InputStream stream) throws XMLStreamException {
    this.reader = factory.createXMLStreamReader(stream);
  }

  public XMLStreamReader getReader() {
    return reader;
  }

  @Override
  public void close() throws XMLStreamException {
    reader.close();
  }
}
