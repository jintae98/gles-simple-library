package com.gomdev.gles.gles30;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES20;
import android.opengl.GLES30;

import com.gomdev.gles.GLESConfig;
import com.gomdev.gles.GLESObject;
import com.gomdev.gles.GLESVertexInfo;
import com.gomdev.gles.gles20.GLES20Renderer;

public class GLES30Renderer extends GLES20Renderer {

    public GLES30Renderer() {

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
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, id);

            int numOfElements = vertexInfo.getNumOfVertexElements();
            GLES20.glVertexAttribPointer(GLESConfig.POSITION_LOCATION,
                    numOfElements, GLES20.GL_FLOAT, false,
                    numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                    0);
            GLES20.glEnableVertexAttribArray(GLESConfig.POSITION_LOCATION);

            if (vertexInfo.useNormal() == true) {
                id = vertexInfo.getNormalVBOID();
                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, id);

                numOfElements = vertexInfo.getNumOfNormalElements();
                GLES20.glVertexAttribPointer(GLESConfig.NORMAL_LOCATION,
                        numOfElements, GLES20.GL_FLOAT, false,
                        numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                        0);
                GLES20.glEnableVertexAttribArray(GLESConfig.NORMAL_LOCATION);
            }

            if (vertexInfo.useTexCoord() == true) {
                id = vertexInfo.getTexCoordVBOID();
                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, id);

                numOfElements = vertexInfo.getNumOfTexCoordElements();
                GLES20.glVertexAttribPointer(GLESConfig.TEXCOORD_LOCATION,
                        numOfElements, GLES20.GL_FLOAT, false,
                        numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                        0);
                GLES20.glEnableVertexAttribArray(GLESConfig.TEXCOORD_LOCATION);
            }

            if (vertexInfo.useColor() == true) {
                id = vertexInfo.getColorVBOID();
                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, id);

                numOfElements = vertexInfo.getNumOfColorElements();
                GLES20.glVertexAttribPointer(GLESConfig.COLOR_LOCATION,
                        numOfElements, GLES20.GL_FLOAT, false,
                        numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                        0);
                GLES20.glEnableVertexAttribArray(GLESConfig.COLOR_LOCATION);
            }

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        } else {
            int numOfElements = vertexInfo.getNumOfVertexElements();
            GLES20.glVertexAttribPointer(GLESConfig.POSITION_LOCATION,
                    numOfElements, GLES20.GL_FLOAT, false,
                    numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                    vertexInfo.getVertexBuffer());
            GLES20.glEnableVertexAttribArray(GLESConfig.POSITION_LOCATION);

            if (vertexInfo.useNormal() == true) {
                numOfElements = vertexInfo.getNumOfNormalElements();
                GLES20.glVertexAttribPointer(GLESConfig.NORMAL_LOCATION,
                        numOfElements, GLES20.GL_FLOAT, false,
                        numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                        vertexInfo.getNormalBuffer());
                GLES20.glEnableVertexAttribArray(GLESConfig.NORMAL_LOCATION);
            }

            if (vertexInfo.useTexCoord() == true) {
                numOfElements = vertexInfo.getNumOfTexCoordElements();
                GLES20.glVertexAttribPointer(GLESConfig.TEXCOORD_LOCATION,
                        numOfElements, GLES20.GL_FLOAT, false,
                        numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                        vertexInfo.getTexCoordBuffer());
                GLES20.glEnableVertexAttribArray(GLESConfig.TEXCOORD_LOCATION);
            }

            if (vertexInfo.useColor() == true) {
                numOfElements = vertexInfo.getNumOfColorElements();
                GLES20.glVertexAttribPointer(GLESConfig.COLOR_LOCATION,
                        numOfElements, GLES20.GL_FLOAT, false,
                        numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                        vertexInfo.getColorBuffer());
                GLES20.glEnableVertexAttribArray(GLESConfig.COLOR_LOCATION);
            }
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

        GLES20.glDisableVertexAttribArray(GLESConfig.POSITION_LOCATION);

        if (vertexInfo.useNormal() == true) {
            GLES20.glDisableVertexAttribArray(GLESConfig.NORMAL_LOCATION);
        }

        if (vertexInfo.useTexCoord() == true) {
            GLES20.glDisableVertexAttribArray(GLESConfig.TEXCOORD_LOCATION);
        }

        if (vertexInfo.useColor() == true) {
            GLES20.glDisableVertexAttribArray(GLESConfig.COLOR_LOCATION);
        }
    }
}
