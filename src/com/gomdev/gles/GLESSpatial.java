package com.gomdev.gles;

import android.util.Log;

public abstract class GLESSpatial {
    protected static final String CLASS = "GLESSpatial";
    protected static final String TAG = GLESConfig.TAG + " " + CLASS;

    private GLESSpatial mParent = null;

    private GLESTransform mWorldTransform = null;
    private GLESTransform mLocalTransform = null;

    private boolean mNeedToUpdate = true;

    private String mName = null;

    public GLESSpatial() {
        init();
    }

    public GLESSpatial(String name) {
        mName = name;

        init();
    }

    private void init() {
        mWorldTransform = new GLESTransform(this);
        mLocalTransform = new GLESTransform(this);
    }

    abstract public void update(double applicationTime, boolean parentHasChanged);

    abstract public void draw(GLESRenderer renderer);

    final public void updateWorldData(double applicationTime) {
        if (mNeedToUpdate == false) {
            return;
        }

        if (mParent != null) {
            GLESTransform parentWT = mParent.getWorldTransform();

            // translate
            if (parentWT.isSetTranslate() == true) {
                mWorldTransform.setTranslate(parentWT.getTranslate());

                if (mLocalTransform.isSetTranslate() == true) {
                    mWorldTransform.translate(mLocalTransform.getTranslate());
                }
            } else {
                if (mLocalTransform.isSetTranslate() == true) {
                    mWorldTransform
                            .setTranslate(mLocalTransform.getTranslate());
                }
            }

            // preTranslate
            if (parentWT.isSetPreTranslate() == true) {
                mWorldTransform.setPreTranslate(parentWT.getPreTranslate());

                if (mLocalTransform.isSetPreTranslate() == true) {
                    mWorldTransform.preTranslate(mLocalTransform
                            .getPreTranslate());
                }
            } else {
                if (mLocalTransform.isSetPreTranslate() == true) {
                    mWorldTransform.setPreTranslate(mLocalTransform
                            .getPreTranslate());
                }
            }

            // scale
            if (parentWT.isSetScale() == true) {
                mWorldTransform.setScale(parentWT.getScale());
                if (mLocalTransform.isSetScale() == true) {
                    mWorldTransform.scale(mLocalTransform.getScale());
                }
            } else {
                if (mLocalTransform.isSetScale() == true) {
                    mWorldTransform.setScale(mLocalTransform.getScale());
                }
            }

            // rotate
            if (mLocalTransform.isSetRotate() == true) {
                mWorldTransform.setRotate(parentWT.getRotate());
                if (mLocalTransform.isSetRotate() == true) {
                    mWorldTransform.rotate(mLocalTransform.getRotate());
                }
            } else {
                if (mLocalTransform.isSetRotate() == true) {
                    mWorldTransform.setRotate(mLocalTransform.getRotate());
                }
            }

        } else {

            if (mLocalTransform.isSetTranslate() == true) {
                mWorldTransform.setTranslate(mLocalTransform.getTranslate());
            }

            if (mLocalTransform.isSetPreTranslate() == true) {
                mWorldTransform.setPreTranslate(mLocalTransform
                        .getPreTranslate());
            }

            if (mLocalTransform.isSetScale() == true) {
                mWorldTransform.setScale(mLocalTransform.getScale());
            }

            if (mLocalTransform.isSetRotate() == true) {
                mWorldTransform.setRotate(mLocalTransform.getRotate());
            }
        }

        mNeedToUpdate = false;
    }

    public GLESTransform getTransform() {
        return mLocalTransform;
    }

    public GLESTransform getWorldTransform() {
        return mWorldTransform;
    }

    public void setParent(GLESSpatial parent) {
        mParent = parent;
    }

    public GLESSpatial getParent() {
        return mParent;
    }

    public void needToUpdate() {
        mNeedToUpdate = true;
    }

    protected boolean getNeedToUpdate() {
        return mNeedToUpdate;
    }

    public void dump() {
        StringBuilder str = new StringBuilder();

        str.append(CLASS);
        str.append("(");
        str.append(mName);
        str.append(")");

        Log.d(TAG, str.toString());
    }
}