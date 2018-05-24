package com.example.ruslan.curs2project.utils;

/**
 * Created by Ruslan on 18.02.2018.
 */

//обычный класс констант и прочего общего кода
public class Const {

    public final static int FILTER_YEAR = 1950;

    public static final String TAG_LOG = "TAG";

    public static final boolean isRoom = true;

    // Http request
    public final static String API_KEY_PARAM = "apikey";
    public final static String TIMESTAMP_PARAM = "ts";
    public final static String HASH_PARAM = "hash";

    public final static Integer PAGE_SIZE = 20;
    public final static Integer ZERO_OFFSET = 0;

    // Http response sorting
    public static final String DEFAULT_COMICS_SORT = "author";
    public static final String DEFAULT_CHARACTER_SORT = "-name";
    public static final String DEFAULT_CREATOR_SORT = "-firstName";

    // Intent's constants
    public final static String ID_KEY = "id";
    public final static String NAME_KEY = "name";
    public final static String PHOTO_KEY = "photo";
    public final static String AUTHOR_KEY = "author";

    public final static String BOOK_KEY = "book";
    public final static String CROSSING_KEY = "crossing";
    public final static String USER_KEY = "user";
    public final static String POINT_KEY = "point";


    public final static String WATCHER_TYPE = "watcher";
    public final static String OWNER_TYPE = "owner";
    public final static String RESTRICT_OWNER_TYPE = "restrict_owner";
    public final static String FOLLOWER_TYPE = "follower";

    //Friend type
    public final static String ADD_FRIEND = "addF";
    public final static String REMOVE_FRIEND = "removeF";
    public final static String ADD_REQUEST = "addR";
    public final static String REMOVE_REQUEST = "removeR";


    public final static String READER_LIST_TYPE = "type";

    public final static String READER_LIST = "readers";
    public final static String FRIEND_LIST = "friends";
    public final static String REQUEST_LIST = "requests";

    public static final String DEFAULT_ORDER_BOOK = "relevance";
    public static final String DEFAULT_PROJECTION = "full";
    public static final Integer DEFAULT_MAX_RESULTS = 20;
    public static final String DEFAULT_QUERY = "inauthor: Пушкин";

    public static final String SEP = "/";
    public static final String QUERY_END = "\uf8ff";

    public static final String QUERY_TYPE = "query";
    public static final String DEFAULT_TYPE = "default";

    public static final int MAX_UPLOAD_RETRY_MILLIS = 60000; //1 minute

    public static class Profile {
        public static final int MAX_AVATAR_SIZE = 1280; //px, side of square
        public static final int MIN_AVATAR_SIZE = 100; //px, side of square
        public static final int MAX_NAME_LENGTH = 120;
    }


}
