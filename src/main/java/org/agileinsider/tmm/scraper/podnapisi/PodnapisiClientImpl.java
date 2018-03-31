package org.agileinsider.tmm.scraper.podnapisi;

import okhttp3.Call;
import okhttp3.Request.Builder;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinymediamanager.scraper.http.TmmHttpClient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import static java.util.Collections.emptyList;

public class PodnapisiClientImpl implements PodnapisiClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(PodnapisiClient.class);

    private final Call.Factory client;
    private final PodnapisiParser parser;

    public PodnapisiClientImpl() {
        this(TmmHttpClient.getHttpClient(), new PodnapisiXmlParser());
    }

    PodnapisiClientImpl(Call.Factory client, PodnapisiParser parser) {
        this.client = client;
        this.parser = parser;
    }

    @Override
    public List<PodnapisiSubtitle> byImdbId(String imdbId, int season, int episode, PodnapisiLanguage language) {
        String uri = builderFor(language, season, episode)
                .withImdbId(imdbId)
                .build();

        return doGet(uri);
    }

    @Override
    public List<PodnapisiSubtitle> byQueryAndYear(String query, int year, int season, int episode, PodnapisiLanguage language) {
        String uri = builderFor(language, season, episode)
                .withQuery(query)
                .withYear(year)
                .build();

        return doGet(uri);
    }

    private UriBuilder builderFor(PodnapisiLanguage language, int season, int episode) {
        return UriBuilder.builder().withLanguage(language).withSeason(season).withEpisode(episode);
    }

    private List<PodnapisiSubtitle> doGet(String url) {
        try {
            LOGGER.debug("Searching for subtitles using " + url);
            Response response = client.newCall(new Builder().url(url).build()).execute();
            int responseCode = response.code();

            if (responseCode == 200) {
                String data = response.body().string();
//                LOGGER.debug("Response: " + data);
                return parser.parse(data);
            } else {
                LOGGER.warn("Unexpected response code: " + responseCode);
                return emptyList();
            }
        } catch (Exception e) {
            LOGGER.warn("Unexpected exception.", e);
            return emptyList();
        }
    }

    private static class UriBuilder {
        private static String SEARCH_BASE = "https://www.podnapisi.net/ppodnapisi/search?";

        private PodnapisiLanguage language = PodnapisiLanguage.Unknown;
        private String imdbId;
        private String query;
        private int year = 0;
        private int season = -1;
        private int episode = -1;

        public static UriBuilder builder() {
            return new UriBuilder();
        }

        public UriBuilder withImdbId(String imdbId) {
            this.imdbId = imdbId;
            return this;
        }

        public UriBuilder withLanguage(PodnapisiLanguage language) {
            this.language = language;
            return this;
        }

        public UriBuilder withQuery(String query) {
            this.query = query;
            return this;
        }

        public UriBuilder withYear(int year) {
            this.year = year;
            return this;
        }

        public UriBuilder withSeason(int season) {
            this.season = season;
            return this;
        }

        public UriBuilder withEpisode(int episode) {
            this.episode = episode;
            return this;
        }

        private static String encode(String in) {
            try {
                return URLEncoder.encode(in, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                return in;
            }
        }

        public String build() {
            StringBuilder builder = new StringBuilder();
            builder.append(SEARCH_BASE);
            if (StringUtils.isNotBlank(imdbId)) {
                builder.append("sI=" + imdbId + "&");
            }
            if (StringUtils.isNotBlank(query)) {
                builder.append("sK=" + encode(query) + "&");
            }
            if (year > 0) {
                builder.append("sY=" + year + "&");
            }
            if (season > -1) {
                builder.append("sS=" + season + "&");
            }
            if (episode > -1) {
                builder.append("sE=" + episode + "&");
            }
            builder.append("sJ=" + language.id() + "&");
            builder.append("sXML=1");
            return builder.toString();
        }
    }
}
