package org.agileinsider.tmm.scraper.podnapisi;

import java.util.List;

public interface PodnapisiClient {
    List<PodnapisiSubtitle> byImdbId(String imdbId, int season, int episode, PodnapisiLanguage language);
    List<PodnapisiSubtitle> byQueryAndYear(String query, int year, int season, int episode, PodnapisiLanguage language);
}
