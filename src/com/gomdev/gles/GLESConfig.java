package com.gomdev.gles;

public class GLESConfig {
    public static final boolean DEBUG = false;
    public static final String TAG = "gomdev";
    
    public enum Version {
        GLES_20,
        GLES_30
    }

    public static final Version GLES_VERSION = Version.GLES_30;
    
    public static final int POSITION_LOCATION = 0;
    public static final int TEXCOORD_LOCATION = 1;
    public static final int NORMAL_LOCATION = 2;
    public static final int COLOR_LOCATION = 3;

    public static final int NUM_OF_VERTEX_ELEMENT = 3;
    public static final int NUM_OF_VERTEX_ELEMENT_WITH_W = 4;
    public static final int NUM_OF_TEXCOORD_ELEMENT = 2;
    public static final int NUM_OF_NORMAL_ELEMENT = 3;
    public static final int NUM_OF_COLOR_ELEMENT = 4;
    public static final int NUM_OF_INDEX_ELEMENT = 6;

    public static final int SHORT_SIZE_BYTES = 2;
    public static final int FLOAT_SIZE_BYTES = 4;
}
