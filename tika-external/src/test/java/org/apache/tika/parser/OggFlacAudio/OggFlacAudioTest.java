package org.apache.tika.parser.OggFlacAudio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.metadata.XMPDM;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.AutoDetectParserTest;
import org.apache.tika.parser.CompositeParser;
import org.apache.tika.parser.ParseContext;
import org.gagravarr.tika.FlacParser;
import org.gagravarr.tika.OpusParser;
import org.gagravarr.tika.VorbisParser;
import org.apache.tika.sax.BodyContentHandler;
import org.junit.Test;
import org.xml.sax.ContentHandler;

public class OggFlacAudioTest {
	
	private TikaConfig tika = TikaConfig.getDefaultConfig();

    private static final String OGG_VORBIS = "audio/vorbis";
    private static final String OGG_OPUS   = "audio/opus";
    private static final String OGG_FLAC   = "audio/x-oggflac"; 
    private static final String FLAC_NATIVE= "audio/x-flac";
	
    /**
     * Test to ensure that the Ogg Audio parsers (Vorbis, Opus, Flac etc)
     *  have been correctly included, and are available
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testOggFlacAudio() throws Exception {
       // The three test files should all have similar test data
       String[] testFiles = new String[] {
             "testVORBIS.ogg", "testFLAC.flac", "testFLAC.oga",
             "testOPUS.opus"
       };
       MediaType[] mediaTypes = new MediaType[] {
               MediaType.parse(OGG_VORBIS), MediaType.parse(FLAC_NATIVE),
               MediaType.parse(OGG_FLAC), MediaType.parse(OGG_OPUS)
       };
       
       // Check we can load the parsers, and they claim to do the right things
       VorbisParser vParser = new VorbisParser();
       assertNotNull("Parser not found for " + mediaTypes[0], 
                     vParser.getSupportedTypes(new ParseContext()));
       
       FlacParser fParser = new FlacParser();
       assertNotNull("Parser not found for " + mediaTypes[1], 
                     fParser.getSupportedTypes(new ParseContext()));
       assertNotNull("Parser not found for " + mediaTypes[2], 
                     fParser.getSupportedTypes(new ParseContext()));
       
       OpusParser oParser = new OpusParser();
       assertNotNull("Parser not found for " + mediaTypes[3], 
                     oParser.getSupportedTypes(new ParseContext()));
       
       // Check we found the parser
       CompositeParser parser = (CompositeParser)tika.getParser();
       for (MediaType mt : mediaTypes) {
          assertNotNull("Parser not found for " + mt, parser.getParsers().get(mt) );
       }
       
       // Have each file parsed, and check
       for (int i=0; i<testFiles.length; i++) {
           String file = testFiles[i];
           try (InputStream input = AutoDetectParserTest.class.getResourceAsStream(
                   "/test-documents/" + file)) {
               if (input == null) {
                   fail("Could not find test file " + file);
               }
               Metadata metadata = new Metadata();
               ContentHandler handler = new BodyContentHandler();
               new AutoDetectParser(tika).parse(input, handler, metadata);

               assertEquals("Incorrect content type for " + file,
                       mediaTypes[i].toString(), metadata.get(Metadata.CONTENT_TYPE));

               // Check some of the common metadata
               // Old style metadata
               assertEquals("Test Artist", metadata.get(Metadata.AUTHOR));
               assertEquals("Test Title", metadata.get(Metadata.TITLE));
               // New style metadata
               assertEquals("Test Artist", metadata.get(TikaCoreProperties.CREATOR));
               assertEquals("Test Title", metadata.get(TikaCoreProperties.TITLE));

               // Check some of the XMPDM metadata
               if (!file.endsWith(".opus")) {
                   assertEquals("Test Album", metadata.get(XMPDM.ALBUM));
               }
               assertEquals("Test Artist", metadata.get(XMPDM.ARTIST));
               assertEquals("Stereo", metadata.get(XMPDM.AUDIO_CHANNEL_TYPE));
               assertEquals("44100", metadata.get(XMPDM.AUDIO_SAMPLE_RATE));

               // Check some of the text
               String content = handler.toString();
               assertTrue(content.contains("Test Title"));
               assertTrue(content.contains("Test Artist"));
           }
       }
    }
}
