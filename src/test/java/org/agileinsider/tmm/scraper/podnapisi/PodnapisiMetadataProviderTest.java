package org.agileinsider.tmm.scraper.podnapisi;

import org.jmock.Expectations;
import org.junit.Test;
import org.tinymediamanager.scraper.SubtitleSearchOptions;
import org.tinymediamanager.scraper.SubtitleSearchResult;

import java.util.List;
import java.util.Locale;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PodnapisiMetadataProviderTest extends JMockTestCase {
    private PodnapisiClient client = mock(PodnapisiClient.class);
    private PodnapisiMetadataProvider provider = new PodnapisiMetadataProvider(client);

    @Test
    public void returnsEmptyListIfNoOptionsAvailable() throws Exception {
        SubtitleSearchOptions searchOptions = SubtitleSearchOptionsBuilder.builder()
                .withImdbId(null)
                .withQuery(null)
                .withLanguage(Locale.CHINESE)
                .build();

        checking(new Expectations() {{
            never(client);
        }});

        List<SubtitleSearchResult> searchResults = provider.search(searchOptions);

        assertThat(searchResults.size(), is(0));
    }

    @Test
    public void usesImdbIdAndLanguageIfAvailable() throws Exception {
        String imdbId = "tt12345";

        SubtitleSearchOptions searchOptions = SubtitleSearchOptionsBuilder.builder()
                .withImdbId(imdbId)
                .withLanguage(Locale.CHINESE)
                .withSeason(4)
                .withEpisode(2)
                .build();
        PodnapisiSubtitle subtitle = PodnapisiSubtitle.builder()
                .withLanguage(PodnapisiLanguage.Chinese)
                .build();

        checking(new Expectations() {{
            oneOf(client).byImdbId(imdbId, 4, 2, PodnapisiLanguage.Chinese);
            will(returnValue(singletonList(subtitle)));
        }});

        List<SubtitleSearchResult> searchResults = provider.search(searchOptions);

        assertThat(searchResults.size(), is(1));
        SubtitleSearchResult searchResult = searchResults.get(0);

        assertSearchResultMatchesSubtitle(searchResult, subtitle);
    }

    @Test
    public void returnsEmptyListIfUnknownLanguage() throws Exception {
        SubtitleSearchOptions searchOptions = SubtitleSearchOptionsBuilder.builder()
                .withImdbId("12345")
                .withQuery("A Movie Query")
                .withLanguage(new Locale("klingon"))
                .build();

        checking(new Expectations() {{
            never(client);
        }});

        List<SubtitleSearchResult> searchResults = provider.search(searchOptions);

        assertThat(searchResults.size(), is(0));
    }


    @Test
    public void usesQueryAndYearIfImdbNotPresent() throws Exception {
        String query = "A Movie Query";
        int year = 2018;

        SubtitleSearchOptions searchOptions = SubtitleSearchOptionsBuilder.builder()
                .withImdbId("")
                .withQuery(query)
                .withLanguage(Locale.GERMAN)
                .withSeason(2)
                .withEpisode(7)
                .withYear(year)
                .build();

        PodnapisiSubtitle subtitle = PodnapisiSubtitle.builder()
                .build();

        checking(new Expectations() {{
            oneOf(client).byQueryAndYear(query, year, 2, 7, PodnapisiLanguage.German);
            will(returnValue(singletonList(subtitle)));
        }});

        List<SubtitleSearchResult> searchResults = provider.search(searchOptions);

        assertThat(searchResults.size(), is(1));
        SubtitleSearchResult searchResult = searchResults.get(0);

        assertSearchResultMatchesSubtitle(searchResult, subtitle);
    }

    @Test
    public void usesQueryAndYearIfImdbResultsEmpty() throws Exception {
        String imdbId = "tt12345";
        String query = "A Movie Query";
        int year = 2018;

        SubtitleSearchOptions searchOptions = SubtitleSearchOptionsBuilder.builder()
                .withImdbId(imdbId)
                .withQuery(query)
                .withLanguage(Locale.GERMAN)
                .withYear(year)
                .withSeason(1)
                .withEpisode(5)
                .build();

        PodnapisiSubtitle subtitle = PodnapisiSubtitle.builder()
                .build();

        checking(new Expectations() {{
            oneOf(client).byImdbId(imdbId, 1, 5, PodnapisiLanguage.German);
            will(returnValue(emptyList()));

            oneOf(client).byQueryAndYear(query, year, 1, 5, PodnapisiLanguage.German);
            will(returnValue(singletonList(subtitle)));
        }});

        List<SubtitleSearchResult> searchResults = provider.search(searchOptions);

        assertThat(searchResults.size(), is(1));
        SubtitleSearchResult searchResult = searchResults.get(0);

        assertSearchResultMatchesSubtitle(searchResult, subtitle);
    }

    private void assertSearchResultMatchesSubtitle(SubtitleSearchResult searchResult, PodnapisiSubtitle subtitle) {
        assertThat(searchResult.getId(), is(subtitle.id()));
        assertThat(searchResult.getRating(), is(subtitle.rating()));
        assertThat(searchResult.getTitle(), is(subtitle.title()));
        assertThat(searchResult.getUrl(), is(subtitle.downloadUrl()));
        assertThat(searchResult.getReleaseName(), is(subtitle.releaseName()));
    }
}