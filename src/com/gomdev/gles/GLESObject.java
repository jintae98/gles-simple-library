package com.gomdev.gles;

public class GLESObject {

    public enum PrimitiveMode {
        TRIANGLES,
        TRIANGLE_STRIP,
        TRIANGLE_FAN,
        LINES,
        LINE_STRIP,
        LINE_LOOP
    }

    public enum RenderType {
        DRAW_ELEMENTS,
        DRAW_ARRAYS,
        DRAW_ELEMENTS_INSTANCED,
        DRAW_ARRAYS_INSTANCED,
    }

    protected GLESShader mShader;

    protected GLESTexture mTexture;
    protected GLESCamera mCamera;
    protected GLESTransform mTransform;
    protected GLESGLState mGLState;

    protected PrimitiveMode mPrimitiveMode = PrimitiveMode.TRIANGLES;
    protected RenderType mRenderType = RenderType.DRAW_ELEMENTS;

    protected GLESVertexInfo mVertexInfo = null;
    protected boolean mUseVBO = true;

    protected boolean mIsVisible = false;

    public GLESObject() {
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

    public void setPrimitiveMode(PrimitiveMode mode) {
        mPrimitiveMode = mode;
    }

    public PrimitiveMode getPrimitiveMode() {
        return mPrimitiveMode;
    }

    public void setRenderType(RenderType renderType) {
        mRenderType = renderType;
    }

    public RenderType getRenderType() {
        return mRenderType;
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

    public void setTransform(GLESTransform transform) {
        mTransform = transform;
    }

    public GLESTransform getTransform() {
        return mTransform;
    }

    public void setGLState(GLESGLState state) {
        mGLState = state;
    }

    public GLESGLState getGLState() {
        return mGLState;
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

    public void setNumOfInstance(int num) {
    }

    public int getNumOfInstance() {
        return 0;
    }
}
