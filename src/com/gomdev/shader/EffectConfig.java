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

    public enum Options {
        SHOW_INFO("Show informations"),
        SHOW_FPS("Show FPS"),
        USE_GLES30("Use GLES 3.0");

        private final String mOptionName;

        Options(String optionName) {
            mOptionName = optionName;
        }

        public String getOption() {
            return mOptionName;
        }
    }

    public static final Options[] EFFECT_HIDE_OPTIONS = new Options[] {
            Options.SHOW_INFO
    };
    
    public static final Options[] EFFECT_SHOW_OPTIONS = new Options[] {
        Options.SHOW_INFO,
        Options.USE_GLES30,
        Options.SHOW_FPS
};
}
