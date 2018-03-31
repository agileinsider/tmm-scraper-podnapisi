package org.agileinsider.tmm.scraper.podnapisi;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinymediamanager.scraper.MediaProviderInfo;
import org.tinymediamanager.scraper.SubtitleSearchOptions;
import org.tinymediamanager.scraper.SubtitleSearchResult;
import org.tinymediamanager.scraper.UnsupportedMediaTypeException;
import org.tinymediamanager.scraper.entities.MediaType;
import org.tinymediamanager.scraper.mediaprovider.IMediaSubtitleProvider;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@PluginImplementation
public class PodnapisiMetadataProvider implements IMediaSubtitleProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(PodnapisiMetadataProvider.class);

    private static MediaProviderInfo providerInfo = createMediaProviderInfo();
    private final PodnapisiClient client;

    public PodnapisiMetadataProvider() {
        this(new PodnapisiClientImpl());
    }

    PodnapisiMetadataProvider(PodnapisiClient client) {
        this.client = client;
    }

    @Override
    public MediaProviderInfo getProviderInfo() {
        return providerInfo;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SubtitleSearchResult> search(SubtitleSearchOptions options) throws Exception {
        if (options.getMediaType() != MediaType.SUBTITLE) {
            throw new UnsupportedMediaTypeException(options.getMediaType());
        }

        try {
            PodnapisiLanguage language = PodnapisiLanguage.forLocale(options.getLanguage());
            if (language != PodnapisiLanguage.Unknown) {
                String imdbId = options.getImdbId();
                String query = options.getQuery();
                int year = options.getYear();
                int season = options.getSeason();
                int episode = options.getEpisode();

                if (isNotBlank(imdbId)) {
                    LOGGER.debug("searching subtitle for imdb id: " + imdbId);

                    List<PodnapisiSubtitle> subtitles = client.byImdbId(imdbId, season, episode, language);
                    LOGGER.debug("found " + subtitles.size() + " subtitles for imdb id: " + imdbId);
                    if (!subtitles.isEmpty()) {
                        return asResults(subtitles);
                    }
                }

                if (isNotBlank(query)) {
                    LOGGER.debug("searching subtitle for query: " + query + " and year: " + year);

                    List<PodnapisiSubtitle> subtitles = client.byQueryAndYear(query, year, season, episode, language);
                    LOGGER.debug("found " + subtitles.size() + " subtitles for query: " + query + " and year: " + year);
                    if (!subtitles.isEmpty()) {
                        return asResults(subtitles);
                    }
                }
            } else {
                LOGGER.info("can't use podnapis to find subtitles for unsupported language: " + options.getLanguage());
            }
        } catch (Exception e) {
            LOGGER.warn("unexpected exception", e);
        }
        return emptyList();
    }

    private List<SubtitleSearchResult> asResults(List<PodnapisiSubtitle> subtitles) {
        List<SubtitleSearchResult> results = new LinkedList<>();

        for (PodnapisiSubtitle subtitle: subtitles) {
            SubtitleSearchResult result = new SubtitleSearchResult(providerInfo.getId(), 1.0F);
            result.setId(subtitle.id());
            result.setTitle(subtitle.title());
            result.setReleaseName(subtitle.releaseName());
            result.setUrl(subtitle.downloadUrl());
            result.setRating(subtitle.rating());
            results.add(result);
        }
        Collections.sort(results);
        Collections.reverse(results);
        return results;
    }

    private static MediaProviderInfo createMediaProviderInfo() {
        MediaProviderInfo providerInfo = new MediaProviderInfo(
                "podnapisi",
                "podnapisi.net",
                "<html><h3>podnapisi.net</h3><br />A subtitle scraper for podnapisi.net</html>",
                PodnapisiMetadataProvider.class.getResource("/podnapisi_net.png"));
        providerInfo.setVersion(PodnapisiMetadataProvider.class);
        return providerInfo;
    }
}
