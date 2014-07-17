package com.gomdev.gles;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.util.Log;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import com.gomdev.gles.GLESConfig.DepthLevel;
import com.gomdev.gles.GLESConfig.MeshType;
import com.gomdev.gles.GLESConfig.ObjectType;
import com.gomdev.gles.GLESConfig.ProjectionType;

public abstract class GLESObject {
    private static final String CLASS = "GLESObject";
    private static final String TAG = GLESConfig.TAG + " " + CLASS;
    private static final boolean DEBUG = GLESConfig.DEBUG;

    protected Context mContext;

    protected Resources mRes;
    protected GLESShader mShader;

    protected GLESTexture mTexture;
    protected GLESProjection mProjection;
    protected GLESTransform mTransform;

    protected float mBitmapHeight;
    protected float mBitmapWidth;

    protected float mHeight;
    protected float mWidth;

    protected FloatBuffer mVertexBuffer = null;
    protected FloatBuffer mTexCoordBuffer = null;
    protected ShortBuffer mIndexBuffer = null;

    protected int mVertexOffset = 0;
    protected int mTexCoordOffset = 0;
    protected int mNormalOffset = 0;

    protected boolean mUseTexture = false;
    protected boolean mUseNormal = false;

    protected boolean mIsVisible = false;

    protected int mNumOfVertexElement = 0;
    protected int mNumOfVertics;

    protected GLESConfig.ObjectType mObjectType;
    protected GLESConfig.DepthLevel mDepth = GLESConfig.DepthLevel.DEFAULT_LEVEL_DEPTH;
    protected GLESConfig.MeshType mMeshType;

    protected int mSpaceInfoHandle = -1;

    protected int[] mVBOIDs = null;

    public GLESObject(Context context, boolean useTexture, boolean useNormal) {
        this(context, useTexture, useNormal, ObjectType.SOLID);
    }

    public GLESObject(Context context, boolean useTexture, boolean useNormal,
            ObjectType objectType) {
        mContext = context;
        mRes = context.getResources();

        mUseTexture = useTexture;
        mUseNormal = useNormal;

        mNumOfVertexElement = 3;
        mVertexOffset = 0;
        if (mUseNormal == true) {
            mNormalOffset = mNumOfVertexElement;
            mNumOfVertexElement = (3 + mNumOfVertexElement);
        }

        if (mUseTexture == true) {
            mTexCoordOffset = mNumOfVertexElement;
            mNumOfVertexElement = (2 + mNumOfVertexElement);
        }

        mObjectType = objectType;

        mTexture = new GLESTexture();
    }

    public void setShader(GLESShader shader) {
        mShader = shader;
        shader.useProgram();

        shader.setVertexAttribIndex("aPosition");

        if (mUseTexture == true) {
            shader.setTexCoordAttribIndex("aTexCoord");
        }

        if (mUseNormal == true) {
            shader.setNormalAttribIndex("aNormal");
        }

        getUniformLocations();

        mTransform = new GLESTransform(shader);
    }

    public void setupSpace(GLESProjection projection, int width, int height) {
        mProjection = projection;

        mWidth = width;
        mHeight = height;
    }

    public void setupSpace(ProjectionType projectionType, int width, int height) {
        mWidth = width;
        mHeight = height;

        mProjection = new GLESProjection(mShader, projectionType, width, height);
    }

    public void setupSpace(ProjectionType projectionType, int width,
            int height, float projScale) {
        mWidth = width;
        mHeight = height;

        mProjection = new GLESProjection(mShader, projectionType, width,
                height, projScale);
    }

    public void setTexture(Bitmap bitmap, boolean needToRecycle,
            boolean updateVertexInfo) {
        if (bitmap == null) {
            return;
        }
        mTexture.changeTexture(bitmap, needToRecycle);

        if (updateVertexInfo == true) {
            updateVertexBuffer(bitmap.getWidth(), bitmap.getHeight());
        }
    }

    public void setTexture(GLESTexture texture, boolean updateVertexInfo) {
        if (texture == null) {
            return;
        }

        mTexture = texture;

        if (updateVertexInfo != true) {
            updateVertexBuffer(texture.getWidth(), texture.getHeight());
        }
    }

    public GLESTexture getTexture() {
        return mTexture;
    }

    public void changeTexture(Bitmap bitmap, boolean needToRecycle,
            boolean updateVertexInfo) {
        if (bitmap == null || mTexture == null) {
            return;
        }

        mTexture.changeTexture(bitmap, needToRecycle);

        if (updateVertexInfo == true) {
            updateVertexBuffer(bitmap.getWidth(), bitmap.getHeight());
        }
    }

    public void changeTexture(GLESTexture texture, boolean updateVertexInfo) {
        if (texture == null) {
            return;
        }

        mTexture = texture;
        if (updateVertexInfo != true) {
            updateVertexBuffer(texture.getWidth(), texture.getHeight());
        }
    }

    public void show() {
        mIsVisible = true;
    }

    public void hide() {
        mIsVisible = false;
    }

    public void setDepth(DepthLevel depth) {
        mDepth = depth;
    }

    public void drawObject() {
        if (mIsVisible == true) {
            mShader.useProgram();

            this.update();
            this.draw();
        }
    }

    public void syncAll() {
        mProjection.sync();
        mTransform.sync();
    }

    public void createMesh(MeshType meshType, float leftX, float topY,
            float width, float height, int resolution) {
        mMeshType = meshType;
        switch (meshType) {
        case PLANE:
            createPlane(leftX, topY, width, height);
            return;
        case PLANE_MESH:
            createPlaneMesh(leftX, topY, width, height, resolution);
            return;
        case CUSTOM_MESH:
            Log.e("quilt GLESObject", "You should override this function");
        default:
            return;
        }

    }

    protected void createPlane(float leftX, float topY, float width,
            float height) {
        width = GLESUtils.convertScreenToSpace(width);
        height = GLESUtils.convertScreenToSpace(height);
        float right = width * 0.5F;
        float left = -right;
        float top = height * 0.5F;
        float bottom = -top;

        float[] vertex = { left, bottom, 0.0F, right, bottom, 0.0F, left, top,
                0.0F, right, top, 0.0F };

        mVertexBuffer = ByteBuffer.allocateDirect(4 * vertex.length)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertexBuffer.put(vertex).position(0);

        if (mUseTexture == true) {
            float[] texCoord = { 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F };

            mTexCoordBuffer = ByteBuffer.allocateDirect(4 * texCoord.length)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            mTexCoordBuffer.put(texCoord).position(0);
        }
    }

    public void createPlaneMesh(float leftX, float topY, float width,
            float height, int resolution) {
        width = GLESUtils.convertScreenToSpace(width);
        height = GLESUtils.convertScreenToSpace(height);

        int wResolution = 0;
        int hResolution = resolution + 2;
        if (height > width) {
            wResolution = (int) (resolution * width / (float) height) + 2;
        } else {
            wResolution = (int) (resolution * height / (float) width) + 2;
        }
        mNumOfVertics = (wResolution + 1) * (hResolution + 1);

        if (DEBUG)
            Log.d(TAG, "createMesh() hResolution=" + hResolution
                    + " wResolution=" + wResolution);

        float[] vertices = new float[mNumOfVertics * mNumOfVertexElement];

        float halfWidth = width * 0.5f;
        float halfHeight = height * 0.5f;
        for (int y = 0, index = 0, texIdx = 0; y <= hResolution; y++) {
            final float normalizedY = (float) y / hResolution;
            final float yOffset = (1.0f - normalizedY) * height;

            for (int x = 0; x <= wResolution; x++) {
                float normalizedX = (float) x / wResolution;
                float xOffset = normalizedX * width;

                vertices[index++] = xOffset - halfWidth;
                vertices[index++] = yOffset - halfHeight;
                vertices[index++] = 0f;

                if (mUseNormal == true) {
                    vertices[index++] = 0f;
                    vertices[index++] = 0f;
                    vertices[index++] = 1f;
                }

                if (mUseTexture == true) {
                    vertices[index++] = normalizedX;
                    vertices[index++] = normalizedY;
                }
            }
        }

        mVertexBuffer = ByteBuffer
                .allocateDirect(vertices.length * GLESConfig.FLOAT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertexBuffer.put(vertices).position(0);

        short[] indices = new short[hResolution * wResolution
                * GLESConfig.NUM_OF_INDEX_ELEMENT];

        for (int y = 0, idx = 0; y < hResolution; y++) {
            final int curY = y * (wResolution + 1);
            final int belowY = (y + 1) * (wResolution + 1);

            for (int x = 0; x < wResolution; x++) {
                int curV = curY + x;
                int belowV = belowY + x;

                indices[idx++] = (short) curV;
                indices[idx++] = (short) (belowV);
                indices[idx++] = (short) (curV + 1);

                indices[idx++] = (short) (belowV);
                indices[idx++] = (short) (belowV + 1);
                indices[idx++] = (short) (curV + 1);

                // if(DEBUG) Log.d(TAG, "index (" + curV + ", " + belowV + ", "
                // + (curV + 1) + ") \t (" + belowV + ", " + (belowV + 1) + ", "
                // + (curV + 1) + ")");
            }
        }

        mIndexBuffer = ByteBuffer
                .allocateDirect(indices.length * GLESConfig.SHORT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        mIndexBuffer.put(indices).position(0);

        // vbo
        if (GLESConfig.USE_VBO == true) {
            int vboIndex = 0;
            mVBOIDs = new int[2];
            GLES20.glGenBuffers(2, mVBOIDs, 0);

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVBOIDs[vboIndex++]);
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,
                    mVertexBuffer.capacity() * GLESConfig.FLOAT_SIZE_BYTES,
                    mVertexBuffer, GLES20.GL_STATIC_DRAW);

            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER,
                    mVBOIDs[vboIndex++]);
            GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER,
                    mIndexBuffer.capacity() * GLESConfig.SHORT_SIZE_BYTES,
                    mIndexBuffer, GLES20.GL_STATIC_DRAW);
        }
    }

    protected void updateVertexBuffer(float width, float height) {
        if (mMeshType != MeshType.PLANE) {
            return;
        }

        if ((Float.compare(mBitmapWidth, width) == 0)
                && (Float.compare(mBitmapHeight, height) == 0)) {
            return;
        }

        mBitmapWidth = width;
        mBitmapHeight = height;

        float halfWidth = GLESUtils.convertScreenToSpace(width * 0.5F);
        float halfHeight = GLESUtils.convertScreenToSpace(height * 0.5F);

        mVertexBuffer.put(0, -halfWidth);
        mVertexBuffer.put(1, -halfHeight);
        mVertexBuffer.put(3, halfWidth);
        mVertexBuffer.put(4, -halfHeight);
        mVertexBuffer.put(6, -halfWidth);
        mVertexBuffer.put(7, halfHeight);
        mVertexBuffer.put(9, halfWidth);
        mVertexBuffer.put(10, halfHeight);
    }

    protected int getNumOfVertexElement() {
        return mNumOfVertexElement;
    }

    protected int getVertexOffset() {
        return mVertexOffset;
    }

    protected int getTexCoordOffset() {
        return mTexCoordOffset;
    }

    protected int getNormalOffset() {
        return mNormalOffset;
    }

    protected abstract void update();

    protected abstract void draw();

    protected abstract void getUniformLocations();

}