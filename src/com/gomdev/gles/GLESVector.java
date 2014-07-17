package com.gomdev.gles;

public class GLESVector {
    private static final String CLASS = "GLESVector";
    private static final String TAG = GLESConfig.TAG + " " + CLASS;
    private static final boolean DEBUG = GLESConfig.DEBUG;

    public float mX = 0.0F;
    public float mY = 0.0F;
    public float mZ = 0.0F;

    public GLESVector(float x, float y, float z) {
        mX = x;
        mY = y;
        mZ = z;
    }

    public GLESVector(GLESVector vector) {
        mX = vector.mX;
        mY = vector.mY;
        mZ = vector.mZ;
    }

    public GLESVector(float[] paramArrayOfFloat) {
        mX = paramArrayOfFloat[0];
        mY = paramArrayOfFloat[1];
        mZ = paramArrayOfFloat[2];
    }

    public static GLESVector add(GLESVector vector1, GLESVector vector2) {
        return new GLESVector(vector1.mX + vector2.mX, vector1.mY + vector2.mY,
                vector1.mZ + vector2.mZ);
    }

    public static GLESVector cross(GLESVector vector1, GLESVector vector2) {
        return new GLESVector(
                vector1.mY * vector2.mZ - vector1.mZ * vector2.mY, vector1.mZ
                        * vector2.mX - vector1.mX * vector2.mZ, vector1.mX
                        * vector2.mY - vector1.mY * vector2.mX);
    }

    public static float dot(GLESVector vector1, GLESVector vector2) {
        return vector1.mX * vector2.mX + vector1.mY * vector2.mY + vector1.mZ
                * vector2.mZ;
    }

    public static GLESVector getNomalVector(GLESVector vector1,
            GLESVector vector2) {
        GLESVector normalVector = cross(vector1, vector2);
        normalVector.normalize();
        return normalVector;
    }

    public static GLESVector getNomalVector(float[] point1, float[] point2,
            float[] point3) {
        GLESVector normalVector = cross(new GLESVector(point2[0] - point1[0],
                point2[1] - point1[1], point2[2] - point1[2]), new GLESVector(
                point3[0] - point1[0], point3[1] - point1[1], point3[2]
                        - point1[2]));
        normalVector.normalize();
        return normalVector;
    }

    public static GLESVector subtract(GLESVector vector1, GLESVector vector2) {
        return new GLESVector(vector1.mX - vector2.mX, vector1.mY - vector2.mY,
                vector1.mZ - vector2.mZ);
    }

    public GLESVector add(GLESVector vector) {
        mX += vector.mX;
        mY += vector.mY;
        mZ += vector.mZ;
        return this;
    }

    public float[] get() {
        float[] arrayOfFloat = new float[3];
        arrayOfFloat[0] = mX;
        arrayOfFloat[1] = mY;
        arrayOfFloat[2] = mZ;
        return arrayOfFloat;
    }

    public float length() {
        return (float) Math.sqrt(mX * mX + mY * mY + mZ * mZ);
    }

    public GLESVector multiply(float paramFloat) {
        mX = (paramFloat * mX);
        mY = (paramFloat * mY);
        mZ = (paramFloat * mZ);
        return this;
    }

    public GLESVector normalize() {
        float f = 1.0F / length();
        mX = (f * mX);
        mY = (f * mY);
        mZ = (f * mZ);
        return this;
    }

    public void set(float paramFloat1, float paramFloat2, float paramFloat3) {
        mX = paramFloat1;
        mY = paramFloat2;
        mZ = paramFloat3;
    }

    public GLESVector subtract(GLESVector vector) {
        mX -= vector.mX;
        mY -= vector.mY;
        mZ -= vector.mZ;
        return this;
    }

    public String toString() {
        return "quilt GLESVector mX=" + mX + " mY=" + mY + " mZ=" + mZ;
    }
}

/*
 * Location: D:\김진태\rooting\ Qualified Name: com.lge.gles.GLESVector JD-Core
 * Version: 0.6.0
 */