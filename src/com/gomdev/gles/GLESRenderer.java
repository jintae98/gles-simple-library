package com.gomdev.gles;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import com.gomdev.gles.GLESObject.PrimitiveMode;
import com.gomdev.gles.GLESObject.RenderType;

import android.opengl.GLES20;
import android.util.Log;

public class GLESRenderer {
    private static final String CLASS = "GLESRenderer";
    private static final String TAG = GLESConfig.TAG + " " + CLASS;

    private ArrayList<GLESObject> mObjects = new ArrayList<GLESObject>();

    public GLESRenderer() {
        GLESContext.getInstance().setRenderer(this);
    }

    public void addObject(GLESObject object) {
        mObjects.add(object);
    }

    public void updateObjects() {
        for (GLESObject object : mObjects) {
            object.update();
            updateTransform(object.getShader(), object.getTransform());
        }
    }

    private void updateTransform(GLESShader shader, GLESTransform transform) {
        float[] matrix = transform.getMatrix();
        String uniformName = GLESShaderConstant.UNIFORM_MODEL_MATRIX;
        GLES20.glUniformMatrix4fv(shader.getUniformLocation(uniformName),
                1, false, matrix, 0);

    }

    public void drawObjects() {
        for (GLESObject object : mObjects) {
            bindTexture(object);
            drawPrimitive(object);
        }
    }

    private void bindTexture(GLESObject object) {
        GLESTexture texture = object.getTexture();

        if (texture != null) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.getTextureID());
        }
    }

    private void drawPrimitive(GLESObject object) {
        GLESVertexInfo vertexInfo = object.getVertexInfo();
        GLESShader shader = object.getShader();

        shader.useProgram();

        enableVertexAttribute(object);

        RenderType renderType = object.getRenderType();
        if (renderType == RenderType.DRAW_ARRAYS) {
            drawArrays(object);
        } else if (renderType == RenderType.DRAW_ELEMENTS) {
            drawElements(object);
        }

        disableVertexAttribute(vertexInfo, shader);
    }

    private void drawArrays(GLESObject object) {
        GLESVertexInfo vertexInfo = object.getVertexInfo();
        PrimitiveMode mode = object.getPrimitiveMode();

        int numOfVertex = vertexInfo.getVertexBuffer().capacity()
                / vertexInfo.getNumOfVertexElements();

        switch (mode) {
        case TRIANGLES:
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, numOfVertex);
            break;
        case TRIANGLE_FAN:
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, numOfVertex);
            break;
        case TRIANGLE_STRIP:
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, numOfVertex);
            break;
        default:
            Log.d(TAG, "drawArrays() mode is invalid. mode=" + mode);
            break;
        }
    }

    private void drawElements(GLESObject object) {
        GLESVertexInfo vertexInfo = object.getVertexInfo();
        PrimitiveMode mode = object.getPrimitiveMode();

        if (object.useVBO() == true) {
            int id = vertexInfo.getIndexVBOID();
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, id);
            
            ShortBuffer indexBuffer = vertexInfo.getIndexBuffer();
            switch (mode) {
            case TRIANGLES:
                GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexBuffer.capacity(),
                        GLES20.GL_UNSIGNED_SHORT, 0);
                break;
            case TRIANGLE_FAN:
                GLES20.glDrawElements(GLES20.GL_TRIANGLE_FAN,
                        indexBuffer.capacity(),
                        GLES20.GL_UNSIGNED_SHORT, 0);
                break;
            case TRIANGLE_STRIP:
                GLES20.glDrawElements(GLES20.GL_TRIANGLE_FAN,
                        indexBuffer.capacity(),
                        GLES20.GL_UNSIGNED_SHORT, 0);
                break;
            default:
                Log.d(TAG, "drawElements() mode is invalid. mode=" + mode);
                break;
            }
            
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        } else {
            ShortBuffer indexBuffer = vertexInfo.getIndexBuffer();
            switch (mode) {
            case TRIANGLES:
                GLES20.glDrawElements(GLES20.GL_TRIANGLES,
                        indexBuffer.capacity(),
                        GLES20.GL_UNSIGNED_SHORT, indexBuffer);
                break;
            case TRIANGLE_FAN:
                GLES20.glDrawElements(GLES20.GL_TRIANGLE_FAN,
                        indexBuffer.capacity(),
                        GLES20.GL_UNSIGNED_SHORT, indexBuffer);
                break;
            case TRIANGLE_STRIP:
                GLES20.glDrawElements(GLES20.GL_TRIANGLE_FAN,
                        indexBuffer.capacity(),
                        GLES20.GL_UNSIGNED_SHORT, indexBuffer);
                break;
            default:
                Log.d(TAG, "drawElements() mode is invalid. mode=" + mode);
                break;
            }
        }
    }

    private void enableVertexAttribute(GLESObject object) {
        GLESVertexInfo vertexInfo = object.getVertexInfo();
        GLESShader shader = object.getShader();
        boolean useVBO = object.useVBO();

        if (useVBO == true) {
            int id = vertexInfo.getVertexVBOID();
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, id);

            int numOfElements = vertexInfo.getNumOfVertexElements();
            GLES20.glVertexAttribPointer(shader.getVertexAttribIndex(),
                    numOfElements, GLES20.GL_FLOAT, false,
                    numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                    0);
            GLES20.glEnableVertexAttribArray(shader.getVertexAttribIndex());

            if (vertexInfo.isUseNormal() == true) {
                id = vertexInfo.getNormalVBOID();
                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, id);

                numOfElements = vertexInfo.getNumOfNormalElements();
                GLES20.glVertexAttribPointer(shader.getNormalAttribIndex(),
                        numOfElements, GLES20.GL_FLOAT, false,
                        numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                        0);
                GLES20.glEnableVertexAttribArray(shader.getNormalAttribIndex());
            }

            if (vertexInfo.isUseTexCoord() == true) {
                id = vertexInfo.getTexCoordVBOID();
                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, id);

                numOfElements = vertexInfo.getNumOfTexCoordElements();
                GLES20.glVertexAttribPointer(shader.getTexCoordAttribIndex(),
                        numOfElements, GLES20.GL_FLOAT, false,
                        numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                        0);
                GLES20.glEnableVertexAttribArray(shader
                        .getTexCoordAttribIndex());
            }

            if (vertexInfo.isUseColor() == true) {
                id = vertexInfo.getColorVBOID();
                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, id);

                numOfElements = vertexInfo.getNumOfColorElements();
                GLES20.glVertexAttribPointer(shader.getColorAttribIndex(),
                        numOfElements, GLES20.GL_FLOAT, false,
                        numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                        0);
                GLES20.glEnableVertexAttribArray(shader.getColorAttribIndex());
            }

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        } else {
            int numOfElements = vertexInfo.getNumOfVertexElements();
            GLES20.glVertexAttribPointer(shader.getVertexAttribIndex(),
                    numOfElements, GLES20.GL_FLOAT, false,
                    numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                    vertexInfo.getVertexBuffer());
            GLES20.glEnableVertexAttribArray(shader.getVertexAttribIndex());

            if (vertexInfo.isUseNormal() == true) {
                numOfElements = vertexInfo.getNumOfNormalElements();
                GLES20.glVertexAttribPointer(shader.getNormalAttribIndex(),
                        numOfElements, GLES20.GL_FLOAT, false,
                        numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                        vertexInfo.getNormalBuffer());
                GLES20.glEnableVertexAttribArray(shader.getNormalAttribIndex());
            }

            if (vertexInfo.isUseTexCoord() == true) {
                numOfElements = vertexInfo.getNumOfTexCoordElements();
                GLES20.glVertexAttribPointer(shader.getTexCoordAttribIndex(),
                        numOfElements, GLES20.GL_FLOAT, false,
                        numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                        vertexInfo.getTexCoordBuffer());
                GLES20.glEnableVertexAttribArray(shader
                        .getTexCoordAttribIndex());
            }

            if (vertexInfo.isUseColor() == true) {
                numOfElements = vertexInfo.getNumOfColorElements();
                GLES20.glVertexAttribPointer(shader.getColorAttribIndex(),
                        numOfElements, GLES20.GL_FLOAT, false,
                        numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                        vertexInfo.getColorBuffer());
                GLES20.glEnableVertexAttribArray(shader.getColorAttribIndex());
            }
        }
    }

    private void disableVertexAttribute(GLESVertexInfo vertexInfo,
            GLESShader shader) {
        GLES20.glDisableVertexAttribArray(shader.getVertexAttribIndex());

        if (vertexInfo.isUseNormal() == true) {
            GLES20.glDisableVertexAttribArray(shader.getNormalAttribIndex());
        }

        if (vertexInfo.isUseTexCoord() == true) {
            GLES20.glDisableVertexAttribArray(shader.getTexCoordAttribIndex());
        }

        if (vertexInfo.isUseColor() == true) {
            GLES20.glDisableVertexAttribArray(shader.getColorAttribIndex());
        }
    }

    void setupVBO(GLESVertexInfo vertexInfo) {
        int[] ids = new int[1];
        GLES20.glGenBuffers(1, ids, 0);
        vertexInfo.setVertexVBOID(ids[0]);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, ids[0]);
        FloatBuffer floatBuffer = vertexInfo.getVertexBuffer();
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,
                floatBuffer.capacity() * GLESConfig.FLOAT_SIZE_BYTES,
                floatBuffer,
                GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        if (vertexInfo.isUseTexCoord() == true) {
            GLES20.glGenBuffers(1, ids, 0);
            vertexInfo.setTexCoordVBOID(ids[0]);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, ids[0]);
            floatBuffer = vertexInfo.getTexCoordBuffer();
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,
                    floatBuffer.capacity() * GLESConfig.FLOAT_SIZE_BYTES,
                    floatBuffer,
                    GLES20.GL_STATIC_DRAW);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        }

        if (vertexInfo.isUseNormal() == true) {
            GLES20.glGenBuffers(1, ids, 0);
            vertexInfo.setNormalVBOID(ids[0]);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, ids[0]);
            floatBuffer = vertexInfo.getNormalBuffer();
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,
                    floatBuffer.capacity() * GLESConfig.FLOAT_SIZE_BYTES,
                    floatBuffer,
                    GLES20.GL_STATIC_DRAW);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        }

        if (vertexInfo.isUseColor() == true) {
            GLES20.glGenBuffers(1, ids, 0);
            vertexInfo.setColorVBOID(ids[0]);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, ids[0]);
            floatBuffer = vertexInfo.getColorBuffer();
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,
                    floatBuffer.capacity() * GLESConfig.FLOAT_SIZE_BYTES,
                    floatBuffer,
                    GLES20.GL_STATIC_DRAW);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        }

        if (vertexInfo.isUseIndex() == true) {
            GLES20.glGenBuffers(1, ids, 0);
            vertexInfo.setIndexVBOID(ids[0]);
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ids[0]);
            ShortBuffer shortBuffer = vertexInfo.getIndexBuffer();
            GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER,
                    shortBuffer.capacity() * GLESConfig.SHORT_SIZE_BYTES,
                    shortBuffer,
                    GLES20.GL_STATIC_DRAW);
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        }
    }
}
