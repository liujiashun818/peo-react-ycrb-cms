package cn.people.one.appapi.constant;

/**
 * Created by wilson on 2018-11-05.
 */
public class CacheConstant {

    public static class Value {
        public static final String NULL = "(&@#%:NULL:%#@&)";
    }

    public static class Time {
        public static final Long ONE_MONTH = 30 * 24 * 60 * 60L;
        public static final Long HALF_OF_MONTH = 15 * 24 * 60 * 60L;
        public static final Long A_WEEK = 7 * 24 * 60 * 60L;
        public static final Long ONE_DAY = 24 * 60 * 60L;
        public static final Long AN_HOUR = 60 * 60L;
        public static final Long HALF_OF_HOUR = 30 * 60L;
        public static final Long QUARTER_OF_HOUR = 15 * 60L;
        public static final Long FIVE_MINUTE = 5 * 60L;
        public static final Long ONE_MINUTE = 60L;
        public static final Long TEN_SECOND=10L;
    }

    public static class Key {

        private static String PREFIX = "CDRB:APP:API2:";

        public static class Aiui {
            public static final String SUGGESTIONS = PREFIX + "AIUI:SUGGESTIONS";
        }

        public static class LifeService{
            public static final String LIST = PREFIX + "LIFESERVICE:LIST";
        }

        public static class Keywords{
            public static final String LIST = PREFIX + "KEYWORDS:LIST";
        }

        public static class Article {
            public static final String LIST = PREFIX + "ARTICLE:LIST:CATEGORY:%d:PAGE:%s:SIZE:%d";
            public static final String DETAIL = PREFIX + "ARTICLE:DETAIL:%d";
        }

        public static class Subject {
            public static final String LIST = PREFIX + "SUBJECT:LIST:ARTICLEID:%d:PAGE:%s:SIZE:%d";
        }

        public static class Live {
            public static final String ROOM = PREFIX + "LIVE:ROOM:%d";
            public static final String USER_COUNT = PREFIX + "LIVE:ROOM:USER:COUNT:%d";
        }

        public static class Paper {
            public static final String LIST = PREFIX + "PAPER:LIST:NAME:%s:CODE:%s";
            public static final String DETAIL = PREFIX + "PAPER:DETAIL:%s:NAME:%s:CODE:%s";
            public static final String LAST_PATH = PREFIX + "PAPER:CODE:%s:NSDATE:%s";
        }
        public static class Ask {
            public static final String DETAIL = PREFIX + "ASK:DETAIL:%d";

        }

        public static class Advert {
            public static final String LIST = PREFIX + "ADS:LIST:TYPE_%d:CAT_%d";
            public static final String DETAIL = PREFIX + "ADS:DETAIL:TYPE_%d";
        }

    }

}
