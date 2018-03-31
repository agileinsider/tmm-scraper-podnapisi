package org.agileinsider.tmm.scraper.podnapisi;

import org.apache.commons.lang3.StringUtils;

import java.beans.Transient;
import java.util.List;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class PodnapisiSubtitle {
    public String id;
    public String pid;
    public String title;
    public String year;
//    public String movieId;
//    public String url;
//    public int uploaderId;
    public String uploaderName;
    public String release;
    public Releases releases;
    public int languageId;
//    public String languageName;
//    public String language;
//    public String time;
//    public String tvSeason;
//    public String tvEpisode;
//    public String tvSpecial;
//    public String cds;
//    public String format;
//    public String fps;
    public String rating;
//    public String flags;
    public Flags new_flags;
    public int downloads;

    public PodnapisiSubtitle() {};

    private PodnapisiSubtitle(String id, String pid, String title, String year, String uploaderName, String release,
                              int languageId, String rating, int downloads) {
        this.id = id;
        this.pid = pid;
        this.title = title;
        this.year = year;
        this.uploaderName = uploaderName;
        this.release = release;
        this.languageId = languageId;
        this.rating = rating;
        this.downloads = downloads;
    }

    @Transient
    public String downloadUrl() {
        return "http://www.podnapisi.net/subtitles/" + pid + "/download";
    }

    @Transient
    public String releaseName() {
        if (isNotBlank(release)) {
            return withUploader(release);
        }
        if (releases == null) {
            return "Unknown";
        }
        return releases.releases()
                .map(this::withUploader)
                .findFirst()
                .orElse("Unknown");
    }

    @Transient
    public String withUploader(String original) {
        if (StringUtils.isNotBlank(uploaderName)) {
            return original + " (" + uploaderName + ")";
        }
        return original;
    }

    @Transient
    public float rating() {
        try {
            return Float.parseFloat(rating);
        } catch (Exception e) {
            return 0.0f;
        }
    }

    @Transient
    public PodnapisiLanguage language() {
        return PodnapisiLanguage.forId(languageId);
    }

    @Transient
    public String id() {
        return id;
    }

    @Transient
    public String title() {
        return withYear(title);
    }

    @Transient
    public String withYear(String original) {
        if (StringUtils.isNotBlank(year)) {
            return original + " (" + year + ")";
        }
        return original;
    }

    static class Releases {
        public List<String> release;

        @Transient
        public Stream<String> releases() {
            if (release == null) {
                return Stream.empty();
            }
            return release.stream().filter(StringUtils::isNotBlank);
        }
    }

    static class Flags {
        public List<String> flag;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id = "12345";
        private String pid = "pid";
        private String title = "A Movie";
        private String year = "2018";
        private String uploaderName = "Anonymous";
        private String release = "A_Movie.2018.DVD.RG";
        private int languageId = PodnapisiLanguage.English.id();
        private String rating = "9.9";
        private int downloads = 50;

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withPid(String pid) {
            this.pid = pid;
            return this;
        }

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder withYear(String year) {
            this.year = year;
            return this;
        }

        public Builder withUploaderName(String uploaderName) {
            this.uploaderName = uploaderName;
            return this;
        }

        public Builder withRelease(String release) {
            this.release = release;
            return this;
        }

        public Builder withLanguageId(int languageId) {
            this.languageId = languageId;
            return this;
        }

        public Builder withLanguage(PodnapisiLanguage language) {
            this.languageId = language.id();
            return this;
        }

        public Builder withRating(String rating) {
            this.rating = rating;
            return this;
        }

        public Builder withDownloads(int downloads) {
            this.downloads = downloads;
            return this;
        }

        public PodnapisiSubtitle build() {
            return new PodnapisiSubtitle(id, pid, title, year, uploaderName, release, languageId, rating, downloads);
        }
    }
}
