package com.gomdev.gles.gles30;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES30;
import android.util.Log;

import com.gomdev.gles.GLESConfig;
import com.gomdev.gles.GLESObject;
import com.gomdev.gles.GLESVertexInfo;
import com.gomdev.gles.GLESObject.PrimitiveMode;
import com.gomdev.gles.gles20.GLES20Renderer;

public class GLES30Renderer extends GLES20Renderer {
    private static final String CLASS = "GLES30Renderer";
    private static final String TAG = GLESConfig.TAG + " " + CLASS;

    public GLES30Renderer() {

    }

    @Override
    public void setupVBO(GLESVertexInfo vertexInfo) {
        int[] ids = new int[1];
        GLES30.glGenBuffers(1, ids, 0);
        vertexInfo.setVertexVBOID(ids[0]);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, ids[0]);
        FloatBuffer floatBuffer = vertexInfo.getVertexBuffer();
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,
                floatBuffer.capacity() * GLESConfig.FLOAT_SIZE_BYTES,
                floatBuffer,
                GLES30.GL_STATIC_DRAW);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);

        if (vertexInfo.useTexCoord() == true) {
            GLES30.glGenBuffers(1, ids, 0);
            vertexInfo.setTexCoordVBOID(ids[0]);
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, ids[0]);
            floatBuffer = vertexInfo.getTexCoordBuffer();
            GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,
                    floatBuffer.capacity() * GLESConfig.FLOAT_SIZE_BYTES,
                    floatBuffer,
                    GLES30.GL_STATIC_DRAW);
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        }

        if (vertexInfo.useNormal() == true) {
            GLES30.glGenBuffers(1, ids, 0);
            vertexInfo.setNormalVBOID(ids[0]);
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, ids[0]);
            floatBuffer = vertexInfo.getNormalBuffer();
            GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,
                    floatBuffer.capacity() * GLESConfig.FLOAT_SIZE_BYTES,
                    floatBuffer,
                    GLES30.GL_STATIC_DRAW);
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        }

        if (vertexInfo.useColor() == true) {
            GLES30.glGenBuffers(1, ids, 0);
            vertexInfo.setColorVBOID(ids[0]);
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, ids[0]);
            floatBuffer = vertexInfo.getColorBuffer();
            GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,
                    floatBuffer.capacity() * GLESConfig.FLOAT_SIZE_BYTES,
                    floatBuffer,
                    GLES30.GL_STATIC_DRAW);
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        }

        if (mListener != null) {
            mListener.setupVBO(vertexInfo);
        }

        if (vertexInfo.useIndex() == true) {
            GLES30.glGenBuffers(1, ids, 0);
            vertexInfo.setIndexVBOID(ids[0]);
            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, ids[0]);
            ShortBuffer shortBuffer = vertexInfo.getIndexBuffer();
            GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER,
                    shortBuffer.capacity() * GLESConfig.SHORT_SIZE_BYTES,
                    shortBuffer,
                    GLES30.GL_STATIC_DRAW);
            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0);
        }
    }

    public void setupVAO(GLESObject object) {
        GLESVertexInfo vertexInfo = object.getVertexInfo();
        boolean useVBO = object.useVBO();

        int[] vaoIDs = new int[1];
        GLES30.glGenVertexArrays(1, vaoIDs, 0);
        vertexInfo.setVAOID(vaoIDs[0]);
        GLES30.glBindVertexArray(vaoIDs[0]);

        if (useVBO == true) {
            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER,
                    vertexInfo.getIndexVBOID());
        }

        int numOfElements = vertexInfo.getNumOfVertexElements();
        GLES30.glEnableVertexAttribArray(GLESConfig.POSITION_LOCATION);

        if (useVBO == true) {
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,
                    vertexInfo.getVertexVBOID());
            GLES30.glVertexAttribPointer(GLESConfig.POSITION_LOCATION,
                    numOfElements, GLES30.GL_FLOAT, false,
                    numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                    0);
        } else {
            GLES30.glVertexAttribPointer(GLESConfig.POSITION_LOCATION,
                    numOfElements, GLES30.GL_FLOAT, false,
                    numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                    vertexInfo.getVertexBuffer());
        }

        if (vertexInfo.useTexCoord() == true) {
            numOfElements = vertexInfo.getNumOfTexCoordElements();
            GLES30.glEnableVertexAttribArray(GLESConfig.TEXCOORD_LOCATION);

            if (useVBO == true) {
                GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,
                        vertexInfo.getTexCoordVBOID());
                GLES30.glVertexAttribPointer(GLESConfig.TEXCOORD_LOCATION,
                        numOfElements, GLES30.GL_FLOAT, false,
                        numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                        0);
            } else {
                GLES30.glVertexAttribPointer(GLESConfig.TEXCOORD_LOCATION,
                        numOfElements, GLES30.GL_FLOAT, false,
                        numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                        vertexInfo.getTexCoordBuffer());
            }
        }

        if (vertexInfo.useNormal() == true) {
            numOfElements = vertexInfo.getNumOfNormalElements();
            GLES30.glEnableVertexAttribArray(GLESConfig.NORMAL_LOCATION);

            if (useVBO == true) {
                GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,
                        vertexInfo.getNormalVBOID());
                GLES30.glVertexAttribPointer(GLESConfig.NORMAL_LOCATION,
                        numOfElements, GLES30.GL_FLOAT, false,
                        numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                        0);
            } else {
                GLES30.glVertexAttribPointer(GLESConfig.NORMAL_LOCATION,
                        numOfElements, GLES30.GL_FLOAT, false,
                        numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                        vertexInfo.getNormalBuffer());
            }
        }

        if (vertexInfo.useColor() == true) {
            numOfElements = vertexInfo.getNumOfColorElements();
            GLES30.glEnableVertexAttribArray(GLESConfig.COLOR_LOCATION);

            if (useVBO == true) {
                GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,
                        vertexInfo.getColorVBOID());
                GLES30.glVertexAttribPointer(GLESConfig.COLOR_LOCATION,
                        numOfElements, GLES30.GL_FLOAT, false,
                        numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                        0);
            } else {
                GLES30.glVertexAttribPointer(GLESConfig.COLOR_LOCATION,
                        numOfElements, GLES30.GL_FLOAT, false,
                        numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                        vertexInfo.getColorBuffer());
            }
        }

        if (mListener != null) {
            mListener.setupVAO(object);
        }

        GLES30.glBindVertexArray(vaoIDs[0]);
    }

    @Override
    protected void enableVertexAttribute(GLESObject object) {
        GLESVertexInfo vertexInfo = object.getVertexInfo();

        boolean useVBO = object.useVBO();
        boolean useVAO = object.useVAO();
        if (useVAO == true) {
            GLES30.glBindVertexArray(vertexInfo.getVAOID());
            return;
        }

        if (useVBO == true) {
            int id = vertexInfo.getVertexVBOID();
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, id);

            int numOfElements = vertexInfo.getNumOfVertexElements();
            GLES30.glVertexAttribPointer(GLESConfig.POSITION_LOCATION,
                    numOfElements, GLES30.GL_FLOAT, false,
                    numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                    0);
            GLES30.glEnableVertexAttribArray(GLESConfig.POSITION_LOCATION);

            if (vertexInfo.useNormal() == true) {
                id = vertexInfo.getNormalVBOID();
                GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, id);

                numOfElements = vertexInfo.getNumOfNormalElements();
                GLES30.glVertexAttribPointer(GLESConfig.NORMAL_LOCATION,
                        numOfElements, GLES30.GL_FLOAT, false,
                        numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                        0);
                GLES30.glEnableVertexAttribArray(GLESConfig.NORMAL_LOCATION);
            }

            if (vertexInfo.useTexCoord() == true) {
                id = vertexInfo.getTexCoordVBOID();
                GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, id);

                numOfElements = vertexInfo.getNumOfTexCoordElements();
                GLES30.glVertexAttribPointer(GLESConfig.TEXCOORD_LOCATION,
                        numOfElements, GLES30.GL_FLOAT, false,
                        numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                        0);
                GLES30.glEnableVertexAttribArray(GLESConfig.TEXCOORD_LOCATION);
            }

            if (vertexInfo.useColor() == true) {
                id = vertexInfo.getColorVBOID();
                GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, id);

                numOfElements = vertexInfo.getNumOfColorElements();
                GLES30.glVertexAttribPointer(GLESConfig.COLOR_LOCATION,
                        numOfElements, GLES30.GL_FLOAT, false,
                        numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                        0);
                GLES30.glEnableVertexAttribArray(GLESConfig.COLOR_LOCATION);
            }

            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        } else {
            int numOfElements = vertexInfo.getNumOfVertexElements();
            GLES30.glVertexAttribPointer(GLESConfig.POSITION_LOCATION,
                    numOfElements, GLES30.GL_FLOAT, false,
                    numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                    vertexInfo.getVertexBuffer());
            GLES30.glEnableVertexAttribArray(GLESConfig.POSITION_LOCATION);

            if (vertexInfo.useNormal() == true) {
                numOfElements = vertexInfo.getNumOfNormalElements();
                GLES30.glVertexAttribPointer(GLESConfig.NORMAL_LOCATION,
                        numOfElements, GLES30.GL_FLOAT, false,
                        numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                        vertexInfo.getNormalBuffer());
                GLES30.glEnableVertexAttribArray(GLESConfig.NORMAL_LOCATION);
            }

            if (vertexInfo.useTexCoord() == true) {
                numOfElements = vertexInfo.getNumOfTexCoordElements();
                GLES30.glVertexAttribPointer(GLESConfig.TEXCOORD_LOCATION,
                        numOfElements, GLES30.GL_FLOAT, false,
                        numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                        vertexInfo.getTexCoordBuffer());
                GLES30.glEnableVertexAttribArray(GLESConfig.TEXCOORD_LOCATION);
            }

            if (vertexInfo.useColor() == true) {
                numOfElements = vertexInfo.getNumOfColorElements();
                GLES30.glVertexAttribPointer(GLESConfig.COLOR_LOCATION,
                        numOfElements, GLES30.GL_FLOAT, false,
                        numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                        vertexInfo.getColorBuffer());
                GLES30.glEnableVertexAttribArray(GLESConfig.COLOR_LOCATION);
            }
        }

        if (mListener != null) {
            mListener.enableVertexAttribute(object);
        }
    }

    @Override
    protected void disableVertexAttribute(GLESObject object) {
        GLESVertexInfo vertexInfo = object.getVertexInfo();

        boolean useVAO = object.useVAO();
        if (useVAO == true) {
            GLES30.glBindVertexArray(0);
            return;
        }

        GLES30.glDisableVertexAttribArray(GLESConfig.POSITION_LOCATION);

        if (vertexInfo.useNormal() == true) {
            GLES30.glDisableVertexAttribArray(GLESConfig.NORMAL_LOCATION);
        }

        if (vertexInfo.useTexCoord() == true) {
            GLES30.glDisableVertexAttribArray(GLESConfig.TEXCOORD_LOCATION);
        }

        if (vertexInfo.useColor() == true) {
            GLES30.glDisableVertexAttribArray(GLESConfig.COLOR_LOCATION);
        }

        if (mListener != null) {
            mListener.disableVertexAttribute(object);
        }
    }

    @Override
    protected void drawArraysInstanced(GLESObject object) {
        GLESVertexInfo vertexInfo = object.getVertexInfo();
        PrimitiveMode mode = object.getPrimitiveMode();
        int instanceCount = object.getNumOfInstance();

        int numOfVertex = vertexInfo.getVertexBuffer().capacity()
                / vertexInfo.getNumOfVertexElements();

        switch (mode) {
        case TRIANGLES:
            GLES30.glDrawArraysInstanced(GLES30.GL_TRIANGLES, 0, numOfVertex,
                    instanceCount);
            break;
        case TRIANGLE_FAN:
            GLES30.glDrawArraysInstanced(GLES30.GL_TRIANGLE_FAN, 0,
                    numOfVertex, instanceCount);
            break;
        case TRIANGLE_STRIP:
            GLES30.glDrawArraysInstanced(GLES30.GL_TRIANGLE_STRIP, 0,
                    numOfVertex, instanceCount);
            break;
        case LINES:
            GLES30.glDrawArraysInstanced(GLES30.GL_LINES, 0,
                    numOfVertex, instanceCount);
            break;
        case LINE_STRIP:
            GLES30.glDrawArraysInstanced(GLES30.GL_LINE_STRIP, 0,
                    numOfVertex, instanceCount);
            break;
        case LINE_LOOP:
            GLES30.glDrawArraysInstanced(GLES30.GL_LINE_LOOP, 0,
                    numOfVertex, instanceCount);
            break;
        default:
            Log.d(TAG, "drawArrays() mode is invalid. mode=" + mode);
            break;
        }
    }

    @Override
    protected void drawElementsInstanced(GLESObject object) {
        GLESVertexInfo vertexInfo = object.getVertexInfo();
        PrimitiveMode mode = object.getPrimitiveMode();
        int instanceCount = object.getNumOfInstance();

        if (object.useVBO() == true) {
            int id = vertexInfo.getIndexVBOID();
            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, id);

            ShortBuffer indexBuffer = vertexInfo.getIndexBuffer();
            switch (mode) {
            case TRIANGLES:
                GLES30.glDrawElementsInstanced(GLES30.GL_TRIANGLES,
                        indexBuffer.capacity(),
                        GLES30.GL_UNSIGNED_SHORT, 0, instanceCount);
                break;
            case TRIANGLE_FAN:
                GLES30.glDrawElementsInstanced(GLES30.GL_TRIANGLE_FAN,
                        indexBuffer.capacity(),
                        GLES30.GL_UNSIGNED_SHORT, 0, instanceCount);
                break;
            case TRIANGLE_STRIP:
                GLES30.glDrawElementsInstanced(GLES30.GL_TRIANGLE_STRIP,
                        indexBuffer.capacity(),
                        GLES30.GL_UNSIGNED_SHORT, 0, instanceCount);
                break;
            case LINES:
                GLES30.glDrawElementsInstanced(GLES30.GL_LINES,
                        indexBuffer.capacity(),
                        GLES30.GL_UNSIGNED_SHORT, 0, instanceCount);
                break;
            case LINE_STRIP:
                GLES30.glDrawElementsInstanced(GLES30.GL_LINE_STRIP,
                        indexBuffer.capacity(),
                        GLES30.GL_UNSIGNED_SHORT, 0, instanceCount);
                break;
            case LINE_LOOP:
                GLES30.glDrawElementsInstanced(GLES30.GL_LINE_LOOP,
                        indexBuffer.capacity(),
                        GLES30.GL_UNSIGNED_SHORT, 0, instanceCount);
                break;
            default:
                Log.d(TAG, "drawElements() mode is invalid. mode=" + mode);
                break;
            }

            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0);
        } else {
            ShortBuffer indexBuffer = vertexInfo.getIndexBuffer();
            switch (mode) {
            case TRIANGLES:
                GLES30.glDrawElementsInstanced(GLES30.GL_TRIANGLES,
                        indexBuffer.capacity(),
                        GLES30.GL_UNSIGNED_SHORT, indexBuffer, instanceCount);
                break;
            case TRIANGLE_FAN:
                GLES30.glDrawElementsInstanced(GLES30.GL_TRIANGLE_FAN,
                        indexBuffer.capacity(),
                        GLES30.GL_UNSIGNED_SHORT, indexBuffer, instanceCount);
                break;
            case TRIANGLE_STRIP:
                GLES30.glDrawElementsInstanced(GLES30.GL_TRIANGLE_FAN,
                        indexBuffer.capacity(),
                        GLES30.GL_UNSIGNED_SHORT, indexBuffer, instanceCount);
                break;
            case LINES:
                GLES30.glDrawElementsInstanced(GLES30.GL_LINES,
                        indexBuffer.capacity(),
                        GLES30.GL_UNSIGNED_SHORT, indexBuffer, instanceCount);
                break;
            case LINE_STRIP:
                GLES30.glDrawElementsInstanced(GLES30.GL_LINE_STRIP,
                        indexBuffer.capacity(),
                        GLES30.GL_UNSIGNED_SHORT, indexBuffer, instanceCount);
                break;
            case LINE_LOOP:
                GLES30.glDrawElementsInstanced(GLES30.GL_LINE_LOOP,
                        indexBuffer.capacity(),
                        GLES30.GL_UNSIGNED_SHORT, indexBuffer, instanceCount);
                break;
            default:
                Log.d(TAG, "drawElements() mode is invalid. mode=" + mode);
                break;
            }
        }
    }
}
