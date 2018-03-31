package org.agileinsider.tmm.scraper.podnapisi;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static java.util.Collections.emptyList;

public class PodnapisiXmlParser implements PodnapisiParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(PodnapisiXmlParser.class);

    private final XmlMapper mapper;

    public PodnapisiXmlParser() {
        JacksonXmlModule module = new JacksonXmlModule();
        // to default to using "unwrapped" Lists:
        module.setDefaultUseWrapper(false);
        mapper = new XmlMapper(module);
        mapper.enable(ACCEPT_SINGLE_VALUE_AS_ARRAY);
        mapper.disable(FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Override
    public List<PodnapisiSubtitle> parse(String data) {
        try {
            PodnapisiResponse response = mapper.readValue(data, PodnapisiResponse.class);
            if (response.subtitle == null) {
                LOGGER.debug("No subtitles returned.");
                return emptyList();
            }
            return response.subtitle;
        } catch (Exception e) {
            LOGGER.warn("Failed to parse response: " + data, e);
            return emptyList();
        }
    }

    public static class PodnapisiResponse {
        public List<PodnapisiSubtitle> subtitle;
    }
}
