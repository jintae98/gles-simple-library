package com.gomdev.gles;

public class GLESObject extends GLESSpatial {
    static final String CLASS = "GLESObject";
    static final String TAG = GLESConfig.TAG + "_" + CLASS;
    static final boolean DEBUG = GLESConfig.DEBUG;

    protected GLESShader mShader;

    protected GLESTexture mTexture;
    protected GLESCamera mCamera;
    protected GLESGLState mGLState;
    protected GLESObjectListener mListener = null;

    protected GLESVertexInfo mVertexInfo = null;
    protected boolean mUseVBO = true;

    protected boolean mIsVisible = true;

    public GLESObject() {
        super();
    }

    public GLESObject(String name) {
        super(name);
    }

    public void setVertexInfo(GLESVertexInfo vertexInfo) {
        setVertexInfo(vertexInfo, true, false);
    }

    public void setVertexInfo(GLESVertexInfo vertexInfo, boolean useVBO,
            boolean useVAO) {
        mVertexInfo = vertexInfo;
        mUseVBO = useVBO;

        mShader.useProgram();

        GLESRenderer renderer = GLESContext.getInstance().getRenderer();
        if (useVBO == true) {
            renderer.setupVBO(vertexInfo);
        }
    }

    public boolean useVBO() {
        return mUseVBO;
    }

    public boolean useVAO() {
        return false;
    }

    public GLESVertexInfo getVertexInfo() {
        return mVertexInfo;
    }

    public void setShader(GLESShader shader) {
        mShader = shader;
        shader.useProgram();

        getUniformLocations();
    }

    public GLESShader getShader() {
        return mShader;
    }

    public void setCamera(GLESCamera camera) {
        mCamera = camera;
    }

    public GLESCamera getCamera() {
        return mCamera;
    }

    public void setTexture(GLESTexture texture) {
        if (texture == null) {
            return;
        }

        mTexture = texture;
    }

    public GLESTexture getTexture() {
        return mTexture;
    }

    public void changeTexture(GLESTexture texture) {
        if (texture == null) {
            return;
        }

        mTexture = texture;
    }

    public void setGLState(GLESGLState state) {
        mGLState = state;
    }

    public GLESGLState getGLState() {
        return mGLState;
    }

    public void setListener(GLESObjectListener listener) {
        mListener = listener;
    }

    public void show() {
        mIsVisible = true;
    }

    public void hide() {
        mIsVisible = false;
    }

    protected void update() {
    }

    protected void getUniformLocations() {
    }

    @Override
    public void update(double applicationTime, boolean parentHasChanged) {
        if (parentHasChanged == true) {
            needToUpdate();
        }

        if (mListener != null) {
            mListener.update(this);
        }

        updateWorldData(applicationTime);
    }

    @Override
    public void draw(GLESRenderer renderer) {
        mShader.useProgram();

        if (mListener != null) {
            mListener.apply(this);
        }

        renderer.draw(this);
    }
}
