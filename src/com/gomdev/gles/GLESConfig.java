package com.gomdev.gles;

public class GLESConfig {
    public static final boolean DEBUG = false;
    public static final String TAG = "gomdev";

    public static final int BINARY_FORMAT_NVIDIA = 35633;
    public static final int BINARY_FORMAT_QUALCOMM = 34624;
    public static final int BINARY_FORMAT_TI = 37168;

    public static final int NUM_OF_VERTEX_ELEMENT = 3;
    public static final int NUM_OF_VERTEX_ELEMENT_WITH_W = 4;
    public static final int NUM_OF_TEXCOORD_ELEMENT = 2;
    public static final int NUM_OF_NORMAL_ELEMENT = 3;
    public static final int NUM_OF_COLOR_ELEMENT = 4;
    public static final int NUM_OF_INDEX_ELEMENT = 6;

    public static final int SHORT_SIZE_BYTES = 2;
    public static final int FLOAT_SIZE_BYTES = 4;

    public static final float SPACE_SCALE_FACTOR = 4.0F;
    public static final boolean USE_BINARY = true;
    public static final boolean USE_VBO = true;

    public enum ChipVendor {
        CHIPSET_TI, CHIPSET_QUALCOMM;
    }
}
