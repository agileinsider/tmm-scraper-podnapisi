package org.agileinsider.tmm.scraper.podnapisi;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PodnapisiXmlParserTest {
    PodnapisiXmlParser parser = new PodnapisiXmlParser();

    @Test
    public void handlesEmptyResults() {
        String example = "<results></results>";
        List<PodnapisiSubtitle> subtitles = parser.parse(example);
        assertThat(subtitles.size(), is(0));
    }

    @Test
    public void handlesInvalidXml() {
        String example = "just some bad data";
        List<PodnapisiSubtitle> subtitles = parser.parse(example);
        assertThat(subtitles.size(), is(0));
    }

    @Test
    public void canParseRating() {
        String example = "<results><subtitle>" +
                "<rating>7.4</rating>" +
                "</subtitle></results>";
        List<PodnapisiSubtitle> subtitles = parser.parse(example);
        assertThat(subtitles.size(), is(1));
        assertThat(subtitles.get(0).rating(), is(7.4f));
    }

    @Test
    public void canParseTitle() {
        String title = "A Movie";
        String example = "<results><subtitle>" +
                "<title>" + title + "</title>" +
                "</subtitle></results>";
        List<PodnapisiSubtitle> subtitles = parser.parse(example);
        assertThat(subtitles.size(), is(1));
        assertThat(subtitles.get(0).title(), is(title));
    }

    @Test
    public void usesYearInTitleIfAvailable() {
        String title = "A Movie";
        String year = "2016";
        String example = "<results><subtitle>" +
                "<title>" + title + "</title>" +
                "<year>" + year + "</year>" +
                "</subtitle></results>";
        List<PodnapisiSubtitle> subtitles = parser.parse(example);
        assertThat(subtitles.size(), is(1));
        assertThat(subtitles.get(0).title(), is(title + " (" + year + ")"));
    }

    @Test
    public void canHandleMissingRating() {
        String example = "<results><subtitle>" +
                "</subtitle></results>";
        List<PodnapisiSubtitle> subtitles = parser.parse(example);
        assertThat(subtitles.size(), is(1));
        assertThat(subtitles.get(0).rating(), is(0.0f));
    }

    @Test
    public void usesReleaseIfAvailable() {
        String example = "<results><subtitle>" +
                "<release>Raftaci.2006.DVDRip.Subs.En.XviD</release>" +
                "<releases>" +
                "<release>Nested Raftaci.2006.DVDRip.Subs.En.XviD</release>" +
                "</releases>" +
                "</subtitle></results>";
        List<PodnapisiSubtitle> subtitles = parser.parse(example);
        assertThat(subtitles.size(), is(1));
        assertThat(subtitles.get(0).releaseName(), is("Raftaci.2006.DVDRip.Subs.En.XviD"));
    }

    @Test
    public void usesUploaderIfAvailable() {
        String example = "<results><subtitle>" +
                "<uploaderName>Anonymous</uploaderName>" +
                "<release>Raftaci.2006.DVDRip.Subs.En.XviD</release>" +
                "</subtitle></results>";
        List<PodnapisiSubtitle> subtitles = parser.parse(example);
        assertThat(subtitles.size(), is(1));
        assertThat(subtitles.get(0).releaseName(), is("Raftaci.2006.DVDRip.Subs.En.XviD (Anonymous)"));
    }

    @Test
    public void usesFirstValidReleaseFromReleasesIfNoReleaseAvailable() {
        String example = "<results><subtitle>" +
                "<releases>" +
                "<release>    </release>" +
                "<release>Raftaci.2006.DVDRip.Subs.En.XviD</release>" +
                "<release>Another Raftaci.2006.DVDRip.Subs.En.XviD</release>" +
                "<release />" +
                "</releases>" +
                "</subtitle></results>";
        List<PodnapisiSubtitle> subtitles = parser.parse(example);
        assertThat(subtitles.size(), is(1));
        assertThat(subtitles.get(0).releaseName(), is("Raftaci.2006.DVDRip.Subs.En.XviD"));
    }

    @Test
    public void returnsUnknownForReleaseIfNotAvailable() {
        String example = "<results><subtitle>" +
                "</subtitle></results>";
        List<PodnapisiSubtitle> subtitles = parser.parse(example);
        assertThat(subtitles.size(), is(1));
        assertThat(subtitles.get(0).releaseName(), is("Unknown"));
    }

    @Test
    public void canParseLanguage() {
        String example = "<results><subtitle>" +
                "<languageId>" + PodnapisiLanguage.Czech.id() + "</languageId>" +
                "<languageName>Czech</languageName>" +
                "<language>cs</language>" +
                "</subtitle></results>";
        List<PodnapisiSubtitle> subtitles = parser.parse(example);
        assertThat(subtitles.size(), is(1));
        assertThat(subtitles.get(0).language(), is(PodnapisiLanguage.Czech));
    }

    @Test
    public void canHandleUnknownLanguage() {
        String example = "<results><subtitle>" +
                "<languageId>9999</languageId>" +
                "<languageName>Klingon</languageName>" +
                "<language>kling</language>" +
                "</subtitle></results>";
        List<PodnapisiSubtitle> subtitles = parser.parse(example);
        assertThat(subtitles.size(), is(1));
        assertThat(subtitles.get(0).language(), is(PodnapisiLanguage.Unknown));
    }

    @Test
    public void canGenerateDownloadLink() {
        String example = "<results><subtitle><pid>9oEi</pid></subtitle></results>";
        List<PodnapisiSubtitle> subtitles = parser.parse(example);
        assertThat(subtitles.size(), is(1));
        assertThat(subtitles.get(0).language(), is(PodnapisiLanguage.Unknown));
    }

    @Test
    public void canParseValidResponse() {
        String example = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?>" +
                "<results>" +
                    "<pagination>" +
                        "<current>1</current>" +
                        "<count>1</count>" +
                        "<results>3</results>" +
                    "</pagination>" +
                    "<subtitle>" +
                        "<id>2261494</id>" +
                        "<pid>9oEi</pid>" +
                        "<title>Raft&#225;ci</title>" +
                        "<year>2006</year>" +
                        "<movieId>26cG</movieId>" +
                        "<movieEntryPid>_qY</movieEntryPid>" +
                        "<externalMovieIdentifiers>" +
                            "<omdb>26cG</omdb>" +
                        "</externalMovieIdentifiers>" +
                        "<url>http://www.podnapisi.net/en/subtitles/cs-raftaci-2006/9oEi</url>" +
                        "<uploaderId>1</uploaderId>" +
                        "<uploaderName>Anonymous</uploaderName>" +
                        "<release>Raftaci.2006.DVDRip.Subs.En.XviD</release>" +
                        "<releases>" +
                            "<release>Raftaci.2006.DVDRip.Subs.En.XviD</release>" +
                        "</releases>" +
                        "<languageId>7</languageId>" +
                        "<languageName>Czech</languageName>" +
                        "<language>cs</language>" +
                        "<time>1361190716</time>" +
                        "<tvSeason>0</tvSeason>" +
                        "<tvEpisode>0</tvEpisode>" +
                        "<tvSpecial>0</tvSpecial>" +
                        "<cds />" +
                        "<format>N/A</format>" +
                        "<fps>N/A</fps>" +
                        "<rating>0.0</rating>" +
                        "<flags />" +
                        "<new_flags />" +
                        "<downloads>15</downloads>" +
                        "<exactHashes />" +
                    "</subtitle>" +
                    "<subtitle>" +
                        "<id>1957628</id>" +
                        "<pid>_N4d</pid>" +
                        "<title>Raft&#225;ci</title>" +
                        "<year>2006</year>" +
                        "<movieId>26cG</movieId>" +
                        "<movieEntryPid>_qY</movieEntryPid>" +
                        "<externalMovieIdentifiers>" +
                         "<omdb>26cG</omdb>" +
                        "</externalMovieIdentifiers>" +
                        "<url>http://www.podnapisi.net/en/subtitles/en-raftaci-2006/_N4d</url>" +
                        "<uploaderId>1</uploaderId>" +
                        "<uploaderName>Anonymous</uploaderName>" +
                        "<release>Raftaci.2006.DVDRip.Subs.En.XviD</release>" +
                        "<releases>" +
                         "<release>Raftaci.2006.DVDRip.Subs.En.XviD</release>" +
                        "</releases>" +
                        "<languageId>2</languageId>" +
                        "<languageName>English</languageName>" +
                        "<language>en</language>" +
                        "<time>1348693783</time>" +
                        "<tvSeason>0</tvSeason>" +
                        "<tvEpisode>0</tvEpisode>" +
                        "<tvSpecial>0</tvSpecial>" +
                        "<cds />" +
                        "<format>N/A</format>" +
                        "<fps>N/A</fps>" +
                        "<rating>0.0</rating>" +
                        "<flags />" +
                        "<new_flags />" +
                        "<downloads>48</downloads>" +
                        "<exactHashes />" +
                    "</subtitle>" +
                    "<subtitle>" +
                        "<id>1452649</id>" +
                        "<pid>aSoW</pid>" +
                        "<title>Raft&#225;ci</title>" +
                        "<year>2006</year>" +
                        "<movieId>26cG</movieId>" +
                        "<movieEntryPid>_qY</movieEntryPid>" +
                        "<externalMovieIdentifiers>" +
                            "<omdb>26cG</omdb>" +
                        "</externalMovieIdentifiers>" +
                        "<url>http://www.podnapisi.net/en/subtitles/en-raftaci-2006/aSoW</url>" +
                        "<uploaderId>1</uploaderId>" +
                        "<uploaderName>Anonymous</uploaderName>" +
                        "<release>Raftaci.2006.XviD.DVDRip</release>" +
                        "<releases>" +
                            "<release>Raftaci.2006.XviD.DVDRip</release>" +
                        "</releases>" +
                        "<languageId>2</languageId>" +
                        "<languageName>English</languageName>" +
                        "<language>en</language>" +
                        "<time>1327728551</time>" +
                        "<tvSeason>0</tvSeason>" +
                        "<tvEpisode>0</tvEpisode>" +
                        "<tvSpecial>0</tvSpecial>" +
                        "<cds />" +
                        "<format>N/A</format>" +
                        "<fps>N/A</fps>" +
                        "<rating>0.0</rating>" +
                        "<flags />" +
                        "<new_flags />" +
                        "<downloads>106</downloads>" +
                        "<exactHashes />" +
                    "</subtitle>" +
                "</results>";
        List<PodnapisiSubtitle> subtitles = parser.parse(example);
        assertThat(subtitles.size(), is(3));
    }
}