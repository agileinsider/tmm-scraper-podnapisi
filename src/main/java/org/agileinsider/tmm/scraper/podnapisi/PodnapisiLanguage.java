package org.agileinsider.tmm.scraper.podnapisi;

import java.util.Arrays;
import java.util.Locale;

public enum PodnapisiLanguage {
    Unknown(0, Locale.ROOT),
    Slovenian(1, new Locale("sl")),
    English(2, Locale.ENGLISH),
    Norwegian(3, new Locale("no")),
    Korean(4, Locale.KOREAN),
    German(5, Locale.GERMAN),
    Icelandic(6, new Locale("is")),
    Czech(7, new Locale("cs")),
    French(8, Locale.FRENCH),
    Italian(9, Locale.ITALIAN),
    Bosnian(10, new Locale("bs")),
    Japanese(11, Locale.JAPANESE),
    Arabic(12, new Locale("ar")),
    Romanian(13, new Locale("ro")),

    Hungarian(15, new Locale("hu")),
    Greek(16, new Locale("el")),
    Chinese(17, Locale.CHINESE),

    Estonian(20, new Locale("et")),
    Latvian(21, new Locale("lv")),
    Hebrew(22, new Locale("he")),
    Dutch(23, new Locale("nl")),
    Danish(24, new Locale("da")),
    Swedish(25, new Locale("sv")),
    Polish(26, new Locale("pl")),
    Russian(27, new Locale("ru")),
    Spanish(28, new Locale("es")),
    Albanian(29, new Locale("sq")),
    Turkish(30, new Locale("tr")),
    Finnish(31, new Locale("fi")),
    Portuguese(32, new Locale("pt")),
    Bulgarian(33, new Locale("bg")),

    Macedonian(35, new Locale("mk")),

    Slovak(37, new Locale("sk")),
    Croatian(38, new Locale("hr")),

    Hindi(42, new Locale("hi")),

    Ukrainian(46, new Locale("uk")),
    Serbian(47, new Locale("sr")),
    Brazilian(48, new Locale("pb")),

    Vietnamese(51, new Locale("vi")),
    Persian(52, new Locale("fa")),
    Catalan(53, new Locale("ca"));

    private final int id;
    private final Locale locale;

    PodnapisiLanguage(int id, Locale locale) {
        this.id = id;
        this.locale = locale;
    }

    public static PodnapisiLanguage forId(int id) {
        return Arrays.stream(PodnapisiLanguage.values())
                .filter(l -> l.id == id)
                .findFirst()
                .orElse(PodnapisiLanguage.Unknown);
    }

    public static PodnapisiLanguage forLocale(Locale language) {
        try {
            return Arrays.stream(PodnapisiLanguage.values())
                    .filter(l -> l.locale.getISO3Language().equals(language.getISO3Language()))
                    .findFirst()
                    .orElse(PodnapisiLanguage.Unknown);
        } catch (Exception e) {
            return PodnapisiLanguage.Unknown;
        }
    }

    public int id() {
        return id;
    }
}
