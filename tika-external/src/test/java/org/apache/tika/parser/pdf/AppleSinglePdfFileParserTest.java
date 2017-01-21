package org.apache.tika.parser.pdf;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.apache.tika.TikaTest;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.RecursiveParserWrapper;
import org.apache.tika.parser.apple.AppleSingleFileParser;
import org.apache.tika.parser.pdf.PDFParser;
import org.junit.Test;

public class AppleSinglePdfFileParserTest extends TikaTest {
	@Test
    public void testBasic() throws Exception {
        List<Metadata> list = getRecursiveMetadata("testAppleSingleFile.pdf");
        assertEquals(2, list.size());
        assertContains(AppleSingleFileParser.class.getName(),
                Arrays.asList(list.get(0).getValues("X-Parsed-By")));
        assertContains(PDFParser.class.getName(),
                Arrays.asList(list.get(1).getValues("X-Parsed-By")));
        assertContains("Hello World", list.get(1).get(RecursiveParserWrapper.TIKA_CONTENT));
        assertEquals("fltsyllabussortie2rev1_2.pdf", list.get(1).get(TikaCoreProperties.ORIGINAL_RESOURCE_NAME));
    }
}
