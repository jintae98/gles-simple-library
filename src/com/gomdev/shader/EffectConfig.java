package com.gomdev.shader;

public class EffectConfig {
    static final boolean DEBUG = false;
    static final String TAG = "gomdev";

    static final String EXTRA_SHADER = "shader";

    public static final String PREF_NAME = "effect_pref";
    public static final String PREF_EFFECT_NAME = "effect_name";
    public static final String PREF_SHADER_COUNT = "shader_count";
    public static final String PREF_SHADER_TITLE = "shader_title";
    public static final String PREF_SHADER_RES_ID = "res_id";
    public static final String PREF_SAVED_FILE_NAME = "saved_file_name";
    public static final String PREF_SAVED_RES_ID = "saved_res_id";

    public static final String APP_DIRECTORY_NAME = "gomdev";
    
    public static final boolean ENABLE_AD = true; 

    public enum Options {
        SHOW_INFO(0, "Show informations"),
        SHOW_FPS(1, "Show FPS"),
        USE_GLES30(2, "Use GLES 3.0");

        private final int mIndex;
        private final String mOptionName;

        Options(int index, String optionName) {
            mIndex = index;
            mOptionName = optionName;
        }

        public int getIndex() {
            return mIndex;
        }

        public String getOption() {
            return mOptionName;
        }
    }

    public static final Options[] EFFECT_OPTIONS = new Options[] {
            Options.SHOW_INFO,
            Options.USE_GLES30,
            Options.SHOW_FPS
    };
}
