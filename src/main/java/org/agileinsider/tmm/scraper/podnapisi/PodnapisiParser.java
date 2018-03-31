package org.agileinsider.tmm.scraper.podnapisi;

import java.util.List;

public interface PodnapisiParser {
    List<PodnapisiSubtitle> parse(String data);
}
