package com.gomdev.gles;

import java.util.ArrayList;

public class GLESNode extends GLESSpatial {
    static final String CLASS = "GLESNode";
    static final String TAG = GLESConfig.TAG + "_" + CLASS;
    static final boolean DEBUG = GLESConfig.DEBUG;

    protected GLESNodeListener mListener = null;

    private ArrayList<GLESSpatial> mChildList = new ArrayList<GLESSpatial>();

    GLESNode() {
        super();

        init();
    }

    GLESNode(String name) {
        super(name);

        init();
    }

    private void init() {
        mChildList.clear();
    }

    public void addChild(GLESSpatial spatial) {
        mChildList.add(spatial);
        spatial.setParent(this);
    }

    public int getNumOfChild() {
        return mChildList.size();
    }

    public void setListener(GLESNodeListener listener) {
        mListener = listener;
    }

    @Override
    public void draw(GLESRenderer renderer) {
        for (GLESSpatial spatial : mChildList) {
            spatial.draw(renderer);
        }
    }

    @Override
    public void update(double applicationTime, boolean parentHasChanged) {
        if (parentHasChanged == true) {
            needToUpdate();
        }

        if (mListener != null) {
            mListener.update(this);
        }

        boolean hasChanged = getNeedToUpdate();
        updateWorldData(applicationTime);

        for (GLESSpatial spatial : mChildList) {
            spatial.update(applicationTime, hasChanged);
        }
    }

    public ArrayList<GLESSpatial> getChildList() {
        return mChildList;
    }

    @Override
    public void dump() {
        super.dump();

        for (GLESSpatial spatial : mChildList) {
            spatial.dump();
        }
    }
}
