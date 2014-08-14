package com.gomdev.gles;

public class GLESObject {

    public enum PrimitiveMode {
        TRIANGLES,
        TRIANGLE_STRIP,
        TRIANGLE_FAN
    }

    public enum RenderType {
        DRAW_ELEMENTS,
        DRAW_ARRAYS
    }

    protected GLESShader mShader;

    protected GLESTexture mTexture;
    protected GLESCamera mCamera;
    protected GLESTransform mTransform;

    protected PrimitiveMode mPrimitiveMode = PrimitiveMode.TRIANGLES;
    protected RenderType mRenderType = RenderType.DRAW_ELEMENTS;

    protected GLESVertexInfo mVertexInfo = null;
    protected boolean mUseVBO = false;

    protected boolean mIsVisible = false;

    public GLESObject() {
    }

    public void setVertexInfo(GLESVertexInfo vertexInfo) {
        setVertexInfo(vertexInfo, false);
    }

    public void setVertexInfo(GLESVertexInfo vertexInfo, boolean useVBO) {
        mVertexInfo = vertexInfo;
        mUseVBO = useVBO;

        if (useVBO == true) {
            GLESRenderer renderer = GLESContext.getInstance().getRenderer();
            renderer.setupVBO(vertexInfo);
        }
    }

    public boolean useVBO() {
        return mUseVBO;
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

}
