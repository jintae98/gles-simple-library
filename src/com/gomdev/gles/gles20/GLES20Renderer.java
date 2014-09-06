package com.gomdev.gles.gles20;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES20;
import android.util.Log;

import com.gomdev.gles.GLESConfig;
import com.gomdev.gles.GLESGLState;
import com.gomdev.gles.GLESObject;
import com.gomdev.gles.GLESRenderer;
import com.gomdev.gles.GLESShader;
import com.gomdev.gles.GLESShaderConstant;
import com.gomdev.gles.GLESTexture;
import com.gomdev.gles.GLESTransform;
import com.gomdev.gles.GLESVertexInfo;
import com.gomdev.gles.GLESObject.PrimitiveMode;

public class GLES20Renderer extends GLESRenderer {
    private static final String CLASS = "GLES20Renderer";
    private static final String TAG = GLESConfig.TAG + " " + CLASS;

    public GLES20Renderer() {

    }

    @Override
    public void setupVBO(GLESVertexInfo vertexInfo) {
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

        if (vertexInfo.useTexCoord() == true) {
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

        if (vertexInfo.useNormal() == true) {
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

        if (vertexInfo.useColor() == true) {
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

        if (vertexInfo.useIndex() == true) {
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

        if (mListener != null) {
            mListener.setupVBO(vertexInfo);
        }
    }

    @Override
    protected void updateTransform(GLESShader shader, GLESTransform transform) {
        float[] matrix = transform.getMatrix();
        String uniformName = GLESShaderConstant.UNIFORM_MODEL_MATRIX;
        GLES20.glUniformMatrix4fv(shader.getUniformLocation(uniformName),
                1, false, matrix, 0);

    }

    @Override
    protected void setGLState(GLESObject object) {
        GLESGLState glState = object.getGLState();

        if (mCurrentGLState == null ||
                glState.getBlendState() != mCurrentGLState.getBlendState()) {
            if (glState.getBlendState() == true) {
                GLES20.glEnable(GLES20.GL_BLEND);

                GLESGLState.BlendFunc blendFunc = glState.getBlendFunc();
                GLES20.glBlendFuncSeparate(blendFunc.mSrcColor,
                        blendFunc.mDstColor,
                        blendFunc.mSrcAlpha,
                        blendFunc.mDstAlpha);
            } else {
                GLES20.glDisable(GLES20.GL_BLEND);
            }
        }

        if (mCurrentGLState == null ||
                glState.getDepthState() != mCurrentGLState.getDepthState()) {
            if (glState.getDepthState() == true) {
                GLES20.glEnable(GLES20.GL_DEPTH_TEST);

                GLES20.glDepthFunc(glState.getDepthFunc());
            } else {
                GLES20.glDisable(GLES20.GL_DEPTH_TEST);
            }
        }

        if (mCurrentGLState == null
                || glState.getCullFaceState() != mCurrentGLState
                        .getCullFaceState()) {
            if (glState.getCullFaceState() == true) {
                GLES20.glEnable(GLES20.GL_CULL_FACE);

                GLES20.glCullFace(glState.getCullFace());
            } else {
                GLES20.glDisable(GLES20.GL_CULL_FACE);
            }
        }

        mCurrentGLState = glState;
    }

    @Override
    protected void bindTexture(GLESObject object) {
        GLESTexture texture = object.getTexture();

        if (texture != null) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.getTextureID());
        }
    }

    @Override
    protected void enableVertexAttribute(GLESObject object) {
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

            if (vertexInfo.useNormal() == true) {
                id = vertexInfo.getNormalVBOID();
                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, id);

                numOfElements = vertexInfo.getNumOfNormalElements();
                GLES20.glVertexAttribPointer(shader.getNormalAttribIndex(),
                        numOfElements, GLES20.GL_FLOAT, false,
                        numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                        0);
                GLES20.glEnableVertexAttribArray(shader.getNormalAttribIndex());
            }

            if (vertexInfo.useTexCoord() == true) {
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

            if (vertexInfo.useColor() == true) {
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

            if (vertexInfo.useNormal() == true) {
                numOfElements = vertexInfo.getNumOfNormalElements();
                GLES20.glVertexAttribPointer(shader.getNormalAttribIndex(),
                        numOfElements, GLES20.GL_FLOAT, false,
                        numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                        vertexInfo.getNormalBuffer());
                GLES20.glEnableVertexAttribArray(shader.getNormalAttribIndex());
            }

            if (vertexInfo.useTexCoord() == true) {
                numOfElements = vertexInfo.getNumOfTexCoordElements();
                GLES20.glVertexAttribPointer(shader.getTexCoordAttribIndex(),
                        numOfElements, GLES20.GL_FLOAT, false,
                        numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                        vertexInfo.getTexCoordBuffer());
                GLES20.glEnableVertexAttribArray(shader
                        .getTexCoordAttribIndex());
            }

            if (vertexInfo.useColor() == true) {
                numOfElements = vertexInfo.getNumOfColorElements();
                GLES20.glVertexAttribPointer(shader.getColorAttribIndex(),
                        numOfElements, GLES20.GL_FLOAT, false,
                        numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                        vertexInfo.getColorBuffer());
                GLES20.glEnableVertexAttribArray(shader.getColorAttribIndex());
            }
        }

        if (mListener != null) {
            mListener.enableVertexAttribute(object);
        }
    }

    @Override
    protected void drawArrays(GLESObject object) {
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
        case LINES:
            GLES20.glDrawArrays(GLES20.GL_LINES, 0, numOfVertex);
            break;
        case LINE_STRIP:
            GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, numOfVertex);
            break;
        case LINE_LOOP:
            GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, numOfVertex);
            break;
        default:
            Log.d(TAG, "drawArrays() mode is invalid. mode=" + mode);
            break;
        }
    }

    @Override
    protected void drawElements(GLESObject object) {
        GLESVertexInfo vertexInfo = object.getVertexInfo();
        PrimitiveMode mode = object.getPrimitiveMode();

        if (object.useVBO() == true) {
            int id = vertexInfo.getIndexVBOID();
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, id);

            ShortBuffer indexBuffer = vertexInfo.getIndexBuffer();
            switch (mode) {
            case TRIANGLES:
                GLES20.glDrawElements(GLES20.GL_TRIANGLES,
                        indexBuffer.capacity(),
                        GLES20.GL_UNSIGNED_SHORT, 0);
                break;
            case TRIANGLE_FAN:
                GLES20.glDrawElements(GLES20.GL_TRIANGLE_FAN,
                        indexBuffer.capacity(),
                        GLES20.GL_UNSIGNED_SHORT, 0);
                break;
            case TRIANGLE_STRIP:
                GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP,
                        indexBuffer.capacity(),
                        GLES20.GL_UNSIGNED_SHORT, 0);
                break;
            case LINES:
                GLES20.glDrawElements(GLES20.GL_LINES,
                        indexBuffer.capacity(),
                        GLES20.GL_UNSIGNED_SHORT, 0);
                break;
            case LINE_STRIP:
                GLES20.glDrawElements(GLES20.GL_LINE_STRIP,
                        indexBuffer.capacity(),
                        GLES20.GL_UNSIGNED_SHORT, 0);
                break;
            case LINE_LOOP:
                GLES20.glDrawElements(GLES20.GL_LINE_LOOP,
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
            case LINES:
                GLES20.glDrawElements(GLES20.GL_LINES,
                        indexBuffer.capacity(),
                        GLES20.GL_UNSIGNED_SHORT, indexBuffer);
                break;
            case LINE_STRIP:
                GLES20.glDrawElements(GLES20.GL_LINE_STRIP,
                        indexBuffer.capacity(),
                        GLES20.GL_UNSIGNED_SHORT, indexBuffer);
                break;
            case LINE_LOOP:
                GLES20.glDrawElements(GLES20.GL_LINE_LOOP,
                        indexBuffer.capacity(),
                        GLES20.GL_UNSIGNED_SHORT, indexBuffer);
                break;
            default:
                Log.d(TAG, "drawElements() mode is invalid. mode=" + mode);
                break;
            }
        }
    }

    @Override
    protected void drawArraysInstanced(GLESObject object) {
        throw new IllegalStateException(
                "this feature is available in OpenGL ES 3.0");
    }

    @Override
    protected void drawElementsInstanced(GLESObject object) {
        throw new IllegalStateException(
                "this feature is available in OpenGL ES 3.0");
    }

    @Override
    protected void disableVertexAttribute(GLESObject object) {
        GLESVertexInfo vertexInfo = object.getVertexInfo();
        GLESShader shader = object.getShader();

        GLES20.glDisableVertexAttribArray(shader.getVertexAttribIndex());

        if (vertexInfo.useNormal() == true) {
            GLES20.glDisableVertexAttribArray(shader.getNormalAttribIndex());
        }

        if (vertexInfo.useTexCoord() == true) {
            GLES20.glDisableVertexAttribArray(shader.getTexCoordAttribIndex());
        }

        if (vertexInfo.useColor() == true) {
            GLES20.glDisableVertexAttribArray(shader.getColorAttribIndex());
        }
        
        if (mListener != null) {
            mListener.disableVertexAttribute(object);
        }
    }
}
