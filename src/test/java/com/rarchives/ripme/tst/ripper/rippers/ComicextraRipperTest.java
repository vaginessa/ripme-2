package com.rarchives.ripme.tst.ripper.rippers;

import java.io.IOException;
import java.net.URL;
import com.rarchives.ripme.ripper.rippers.ComicextraRipper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class ComicextraRipperTest extends RippersTest {
    @Test
    @Tag("flaky")
    public void testComicUrl() throws IOException {
        URL url = new URL("https://www.comicextra.com/comic/karma-police");
        ComicextraRipper ripper = new ComicextraRipper(url);
        testRipper(ripper);
    }
    @Test
    @Disabled("no images found error, broken ripper?")
    public void testChapterUrl() throws IOException {
        URL url = new URL("https://www.comicextra.com/v-for-vendetta/chapter-1");
        ComicextraRipper ripper = new ComicextraRipper(url);
        testRipper(ripper);
    }

}
