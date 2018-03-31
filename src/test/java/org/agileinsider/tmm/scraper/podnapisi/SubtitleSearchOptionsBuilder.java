package org.agileinsider.tmm.scraper.podnapisi;

import org.tinymediamanager.scraper.SubtitleSearchOptions;

import java.util.Locale;

public class SubtitleSearchOptionsBuilder {
    private String imdbId;
    private Locale locale = Locale.ENGLISH;
    private String query;
    private int year = 0;
    private int season = -1;
    private int episode = -1;

    public static SubtitleSearchOptionsBuilder builder() {
        return new SubtitleSearchOptionsBuilder();
    }

    public SubtitleSearchOptionsBuilder withImdbId(String imdbId) {
        this.imdbId = imdbId;
        return this;
    }

    public SubtitleSearchOptionsBuilder withLanguage(Locale locale) {
        this.locale = locale;
        return this;
    }

    public SubtitleSearchOptionsBuilder withQuery(String query) {
        this.query = query;
        return this;
    }

    public SubtitleSearchOptionsBuilder withSeason(int season) {
        this.season = season;
        return this;
    }

    public SubtitleSearchOptionsBuilder withEpisode(int episode) {
        this.episode = episode;
        return this;
    }

    public SubtitleSearchOptionsBuilder withYear(int year) {
        this.year = year;
        return this;
    }

    SubtitleSearchOptions build() {
        SubtitleSearchOptions searchOptions = new SubtitleSearchOptions();
        searchOptions.setLanguage(locale);
        searchOptions.setImdbId(imdbId);
        searchOptions.setQuery(query);
        searchOptions.setYear(year);
        searchOptions.setSeason(season);
        searchOptions.setEpisode(episode);
        return searchOptions;
    }
}
