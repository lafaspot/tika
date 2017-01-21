package org.apache.tika.parser.ocr;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.InputStream;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mail.RFC822Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.XHTMLContentHandler;
import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;

public class RFC822ParserMultipartTest {
	
	private static InputStream getStream(String name) {
        InputStream stream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(name);
        assertNotNull("Test file not found " + name, stream);
        return stream;
    }
	
    @Test
    public void testMultipart() {
        Parser parser = new RFC822Parser();
        Metadata metadata = new Metadata();
        InputStream stream = getStream("test-documents/testRFC822-multipart");
        ContentHandler handler = mock(XHTMLContentHandler.class);
        ParseContext context = new ParseContext();
        context.set(Parser.class, new AutoDetectParser());
        try {
            parser.parse(stream, handler, metadata, context);
            verify(handler).startDocument();
            int bodyExpectedTimes = 4, multipackExpectedTimes = 5;
            // TIKA-1422. TesseractOCRParser interferes with the number of times the handler is invoked.
            // But, different versions of Tesseract lead to a different number of invocations. So, we
            // only verify the handler if Tesseract cannot run.
            if (!TesseractOCRParserTest.canRun()) {
                verify(handler, times(bodyExpectedTimes)).startElement(eq(XHTMLContentHandler.XHTML), eq("div"), eq("div"), any(Attributes.class));
                verify(handler, times(bodyExpectedTimes)).endElement(XHTMLContentHandler.XHTML, "div", "div");
            }
            verify(handler, times(multipackExpectedTimes)).startElement(eq(XHTMLContentHandler.XHTML), eq("p"), eq("p"), any(Attributes.class));
            verify(handler, times(multipackExpectedTimes)).endElement(XHTMLContentHandler.XHTML, "p", "p");
            verify(handler).endDocument();

        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }

        //repeat, this time looking at content
        parser = new RFC822Parser();
        metadata = new Metadata();
        stream = getStream("test-documents/testRFC822-multipart");
        handler = new BodyContentHandler();
        try {
            parser.parse(stream, handler, metadata, context);
            //tests correct decoding of quoted printable text, including UTF-8 bytes into Unicode
            String bodyText = handler.toString();
            assertTrue(bodyText.contains("body 1"));
            assertTrue(bodyText.contains("body 2"));
            assertFalse(bodyText.contains("R0lGODlhNgE8AMQAA")); //part of encoded gif
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }
}
