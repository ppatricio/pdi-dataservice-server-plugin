package org.pentaho.di.repository.pur;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.junit.Test;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.RepositoryMeta;

public class PurRepositoryMetaTest {

  private static final String URL_WITHOUT_TRAILING = "http://host:0000/pentaho-di";
  private static final String EXAMPLE_RESOURCES =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?><repositories><repository><id>PentahoEnterpriseRepository</id><name>pentaho-di</name><description>pentaho-di</description><repository_location_url>http://host:0000/pentaho-di</repository_location_url><version_comment_mandatory>N</version_comment_mandatory></repository><repository><id>PentahoEnterpriseRepository</id><name>pentaho-di</name><description>pentaho-di</description><repository_location_url>http://host:0000/pentaho-di/</repository_location_url><version_comment_mandatory>N</version_comment_mandatory></repository></repositories>";

  /**
   * check URL trailing slash load throw exception when internal test resource unavailable
   * 
   * @throws ParserConfigurationException
   * @throws IOException
   * @throws SAXException
   * @throws KettleException
   * @see <a href="http://jira.pentaho.com/browse/PDI-10401">http://jira.pentaho.com/browse/PDI-10401</a>
   */
  @Test
  public void testURLTralingSlashTolerante() throws ParserConfigurationException, SAXException, KettleException,
    IOException {

    InputStream stream = new ByteArrayInputStream( EXAMPLE_RESOURCES.getBytes( StandardCharsets.UTF_8 ) );
    DocumentBuilder db;
    Document doc;
    db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    doc = db.parse( stream );
    Node repsnode = XMLHandler.getSubNode( doc, "repositories" );
    Node repnode = XMLHandler.getSubNodeByNr( repsnode, RepositoryMeta.XML_TAG, 0 );

    @SuppressWarnings( "unchecked" )
    List<DatabaseMeta> databases = mock( List.class );

    // check with trailing
    PurRepositoryMeta repositoryMeta = new PurRepositoryMeta();
    repositoryMeta.loadXML( repnode, databases );
    assertEquals( repositoryMeta.getRepositoryLocation().getUrl(), URL_WITHOUT_TRAILING );

    // check without trailing
    Node repnode2 = XMLHandler.getSubNodeByNr( repsnode, RepositoryMeta.XML_TAG, 1 );
    repositoryMeta = new PurRepositoryMeta();
    repositoryMeta.loadXML( repnode2, databases );

    assertEquals( repositoryMeta.getRepositoryLocation().getUrl(), URL_WITHOUT_TRAILING );
  }
}
