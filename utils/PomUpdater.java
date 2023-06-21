package utils;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public final class PomUpdater {

    private PomUpdater() {
        // It is forbidden to create instances of util classes.
    }

    public static void updateValuesInPomXMLs(final File rootDirectory, final Map<String, String> properties)
            throws IOException, ParserConfigurationException, SAXException, TransformerException {
        final List<Path> pomXMLs = findPomXmls(rootDirectory);
        for (Path pomXML : pomXMLs) {
            updateValuesInPomXML(pomXML, properties);
        }
    }

    public static void updateValuesInPomXML(final Path pomXmlPath, final Map<String, String> tagValuePairs)
            throws IOException, ParserConfigurationException, SAXException, TransformerException {
        final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        builderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        final Document pomXmlDocument = builderFactory.newDocumentBuilder().parse(pomXmlPath.toFile());

        for (Map.Entry<String, String> tagValueEntry : tagValuePairs.entrySet()) {
            final NodeList foundElements = pomXmlDocument.getElementsByTagName(tagValueEntry.getKey());
            for (int i = 0; i < foundElements.getLength(); i++) {
                final Element element = (Element) foundElements.item(i);
                element.setNodeValue(tagValueEntry.getValue());
            }
        }

        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        final Transformer xformer = transformerFactory.newTransformer();
        xformer.setOutputProperty(OutputKeys.INDENT, "yes");
        Writer output = new StringWriter();
        xformer.transform(new DOMSource(pomXmlDocument), new StreamResult(output));
        Files.write(pomXmlPath, output.toString().getBytes(Charset.defaultCharset()));
    }

    private static List<Path> findPomXmls(final File rootDirectory) throws IOException {
        final BiPredicate<Path, BasicFileAttributes> matcher = (filePath,
                fileAttributes) -> fileAttributes.isRegularFile()
                        && filePath.getFileName().toString().equals("pom.xml");
        try (final Stream<Path> foundPathsStream = Files.find(rootDirectory.toPath(), Integer.MAX_VALUE, matcher)) {
            return foundPathsStream.toList();
        }
    }

}
