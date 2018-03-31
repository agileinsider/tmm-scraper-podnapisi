package org.agileinsider.tmm.scraper.podnapisi;

import okhttp3.*;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.jmock.Expectations;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PodnapisiClientImplTest extends JMockTestCase {
    private Call.Factory okHttpFactory = mock(Call.Factory.class);
    private Call okHttpCall = mock(Call.class);

    private PodnapisiParser parser = mock(PodnapisiParser.class);

    private PodnapisiClientImpl client = new PodnapisiClientImpl(okHttpFactory, parser);

    @Test
    public void canGetSubtitlesByImdbId() throws IOException {
        String imdbId = "tt12345";
        int season = 3;
        int episode = 5;
        PodnapisiLanguage language = PodnapisiLanguage.French;

        String content = "<subtitles />";

        Request request = new Request.Builder()
                .url("https://foo.org/")
                .build();

        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .message("OK")
                .code(200)
                .body(ResponseBody.create(MediaType.parse("application/xml"), content))
                .build();

        List<PodnapisiSubtitle> expected = singletonList(PodnapisiSubtitle.builder().build());

        checking(new Expectations() {{
            oneOf(okHttpFactory).newCall(with(
                    allOf(
                            requestWithQuery("sI=" + imdbId),
                            requestWithQuery("sS=" + season),
                            requestWithQuery("sE=" + episode),
                            requestWithQuery("sJ=" + language.id())
                    )
            )); will(returnValue(okHttpCall));
            oneOf(okHttpCall).execute(); will(returnValue(response));
            oneOf(parser).parse(content); will(returnValue(expected));
        }});

        List<PodnapisiSubtitle> results = client.byImdbId(imdbId, season, episode, language);

        assertThat(results, is(expected));
    }

    @Test
    public void canGetSubtitlesByQueryAnyYear() throws IOException {
        String query = "A Movie To Find";
        int year = 2021;
        int season = 3;
        int episode = 5;
        PodnapisiLanguage language = PodnapisiLanguage.French;

        String content = "<subtitles />";

        Request request = new Request.Builder()
                .url("https://foo.org/")
                .build();

        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .message("OK")
                .code(200)
                .body(ResponseBody.create(MediaType.parse("application/xml"), content))
                .build();

        List<PodnapisiSubtitle> expected = singletonList(PodnapisiSubtitle.builder().build());

        checking(new Expectations() {{
            oneOf(okHttpFactory).newCall(with(
                    allOf(
                            requestWithQuery("sK=" + query),
                            requestWithQuery("sY=" + year),
                            requestWithQuery("sS=" + season),
                            requestWithQuery("sE=" + episode),
                            requestWithQuery("sJ=" + language.id())
                    )
            )); will(returnValue(okHttpCall));
            oneOf(okHttpCall).execute(); will(returnValue(response));
            oneOf(parser).parse(content); will(returnValue(expected));
        }});

        List<PodnapisiSubtitle> results = client.byQueryAndYear(query, year, season, episode, language);

        assertThat(results, is(expected));
    }

    private Matcher<Request> requestWithQuery(String queryString) {
        return new TypeSafeMatcher<Request>() {
            @Override
            protected boolean matchesSafely(Request item) {
                String query = item.url().query();
                return query.contains(queryString);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Expected query to contain " + queryString);
            }
        };
    }
}