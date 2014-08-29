package com.gomdev.gles;

import com.gomdev.gles.GLESConfig.Version;
import com.gomdev.gles.gles30.GLES30Object;

public class GLESSceneManager {
    private GLESSceneManager() {

    }

    public static GLESSceneManager createSceneManager() {
        return new GLESSceneManager();
    }

    public static GLESObject createObject() {
        Version version = GLESContext.getInstance().getVersion();

        GLESObject object = null;
        switch (version) {
        case GLES_20:
            object = new GLESObject();
            break;
        case GLES_30:
            object = new GLES30Object();
            break;
        default:
            object = new GLESObject();
        }

        return object;
    }
}
