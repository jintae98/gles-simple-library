package com.gomdev.gles;

import android.util.Log;

public class GLESMeshUtils {
    private static final String CLASS = "GLESMeshUtils";
    private static final String TAG = GLESConfig.TAG + " " + CLASS;
    private static final boolean DEBUG = GLESConfig.DEBUG;

    private GLESMeshUtils() {

    }

    public static GLESVertexInfo createPlaneMesh(float width, float height,
            int resolution, boolean useTexCoord, boolean useNormal) {

        int wResolution = 0;
        int hResolution = resolution + 2;
        if (height > width) {
            wResolution = (int) (resolution * width / height) + 2;
        } else {
            wResolution = (int) (resolution * height / width) + 2;
        }

        if (DEBUG) {
            Log.d(TAG, "createMesh() hResolution=" + hResolution
                    + " wResolution=" + wResolution);
        }

        int numOfVertics = (wResolution + 1) * (hResolution + 1);

        float[] vertices = new float[numOfVertics
                * GLESConfig.NUM_OF_VERTEX_ELEMENT];
        float[] texCoord = null;
        float[] normal = null;
        short[] index = null;

        if (useNormal == true) {
            normal = new float[numOfVertics * GLESConfig.NUM_OF_NORMAL_ELEMENT];
        }

        if (useTexCoord == true) {
            texCoord = new float[numOfVertics
                    * GLESConfig.NUM_OF_TEXCOORD_ELEMENT];
        }

        float halfWidth = width * 0.5f;
        float halfHeight = height * 0.5f;

        int vertexIndex = 0;
        int texCoordIndex = 0;
        int normalIndex = 0;
        for (int y = 0; y <= hResolution; y++) {
            final float normalizedY = (float) y / hResolution;
            final float yOffset = (1.0f - normalizedY) * height;
            for (int x = 0; x <= wResolution; x++) {
                float normalizedX = (float) x / wResolution;
                float xOffset = normalizedX * width;

                vertices[vertexIndex++] = xOffset - halfWidth;
                vertices[vertexIndex++] = yOffset - halfHeight;
                vertices[vertexIndex++] = 0f;

                if (useNormal == true) {
                    normal[normalIndex++] = 0f;
                    normal[normalIndex++] = 0f;
                    normal[normalIndex++] = 1f;
                }

                if (useTexCoord == true) {
                    texCoord[texCoordIndex++] = normalizedX;
                    texCoord[texCoordIndex++] = normalizedY;
                }
            }
        }

        GLESVertexInfo vertexInfo = new GLESVertexInfo();

        vertexInfo.setVertexBuffer(vertices, 3);
        if (useTexCoord == true) {
            vertexInfo.setTexCoordBuffer(texCoord, 2);
        }

        if (useNormal == true) {
            vertexInfo.setNormalBuffer(normal, 3);
        }

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

        vertexInfo.setIndexBuffer(indices);

        return vertexInfo;
    }

    public static GLESVertexInfo createPlane(float width, float height,
            boolean useTexCoord, boolean useNormal, boolean useIndex) {

        float right = width * 0.5f;
        float left = -right;
        float top = height * 0.5f;
        float bottom = -top;
        float z = 0.0f;

        float[] vertex = { left, bottom, z, right, bottom, z, left, top, z,
                right, top, z };

        GLESVertexInfo vertexInfo = new GLESVertexInfo();

        vertexInfo.setVertexBuffer(vertex, 3);

        if (useTexCoord == true) {
            float[] texCoord = { 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f };

            vertexInfo.setTexCoordBuffer(texCoord, 2);
        }

        if (useNormal == true) {
            float[] normal = { 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f };

            vertexInfo.setNormalBuffer(normal, 3);
        }

        if (useIndex == true) {
            short[] index = { 0, 1, 2, 2, 1, 3 };

            vertexInfo.setIndexBuffer(index);
        }

        return vertexInfo;
    }

    public static GLESVertexInfo createCube(float width, float height,
            boolean useTexCoord, boolean useNormal) {

        float right = width * 0.5f;
        float left = -right;
        float top = height * 0.5f;
        float bottom = -top;
        float z = width * 0.5f;

        float[] vertex = {
             // right
                right, bottom, z,
                right, bottom, -z,
                right, top, z,
                right, top, -z,
                
                // front
                left, bottom, z, 
                right, bottom, z, 
                left, top, z,
                right, top, z,
                
                
                
                // back
                right, bottom, -z,
                left, bottom, -z,
                right, top, -z,
                left, top, -z,
                
                // left
                left, bottom, -z,
                left, bottom, z,
                left, top, -z, 
                left, top, z,
                
                // top
                left, top, z,
                right, top, z,
                left, top, -z,
                right, top, -z,
                
                // bottom
                left, bottom, -z,
                right, bottom, -z,
                left, bottom, z,
                right, bottom, z
        };

        GLESVertexInfo vertexInfo = new GLESVertexInfo();

        vertexInfo.setVertexBuffer(vertex, 3);

        if (useTexCoord == true) {
            float[] texCoord = {
                    // front
                    0f, 1f, 
                    1f, 1f, 
                    0f, 0f, 
                    1f, 0f,
                    
                    // right
                    0f, 1f, 
                    1f, 1f, 
                    0f, 0f, 
                    1f, 0f,
                    
                    // back
                    0f, 1f, 
                    1f, 1f, 
                    0f, 0f, 
                    1f, 0f,
                    
                    // left
                    0f, 1f, 
                    1f, 1f, 
                    0f, 0f, 
                    1f, 0f,
                    
                    // top
                    0f, 1f, 
                    1f, 1f, 
                    0f, 0f, 
                    1f, 0f,
                    
                    // bottom
                    0f, 1f, 
                    1f, 1f, 
                    0f, 0f, 
                    1f, 0f,
                    
            };

            vertexInfo.setTexCoordBuffer(texCoord, 2);
        }

        if (useNormal == true) {
            float[] normal = { 
                    // front
                    0f, 0f, 1f, 
                    0f, 0f, 1f, 
                    0f, 0f, 1f, 
                    0f, 0f, 1f,
                    
                    // right
                    1f, 0f, 0f, 
                    1f, 0f, 0f, 
                    1f, 0f, 0f, 
                    1f, 0f, 0f,
                    
                    // back
                    0f, 0f, -1f, 
                    0f, 0f, -1f, 
                    0f, 0f, -1f, 
                    0f, 0f, -1f,
                    
                    // left
                    -1f, 0f, 0f, 
                    -1f, 0f, 0f, 
                    -1f, 0f, 0f, 
                    -1f, 0f, 0f,
                    
                    // top
                    0f, 1f, 0f, 
                    0f, 1f, 0f, 
                    0f, 1f, 0f, 
                    0f, 1f, 0f,
                    
                    // bottom
                    0f, -1f, 0f, 
                    0f, -1f, 0f, 
                    0f, -1f, 0f, 
                    0f, -1f, 0f,
            };
            

            vertexInfo.setNormalBuffer(normal, 3);
        }

        return vertexInfo;
    }
}
