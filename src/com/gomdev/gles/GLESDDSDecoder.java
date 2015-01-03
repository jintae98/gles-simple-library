package com.gomdev.gles;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class GLESDDSDecoder {
    static final String CLASS = "GLESDDSDecoder";
    static final String TAG = GLESConfig.TAG + "_" + CLASS;
    static final boolean DEBUG = GLESConfig.DEBUG;

    public static final int DDS_HEADER_SIZE = 124 + 4;

    public static GLESCompressedTextureInfo decode(InputStream input) throws IOException {
        int width = 0;
        int height = 0;
        // int flags = 0;
        int encodedSize = 0;
        byte[] ioBuffer = new byte[4096];
        {
            if (input.read(ioBuffer, 0, DDS_HEADER_SIZE) != DDS_HEADER_SIZE) {
                throw new IOException("Unable to read PKM file header.");
            }

            ByteBuffer headerBuffer = ByteBuffer.allocateDirect(DDS_HEADER_SIZE).order(
                    ByteOrder.nativeOrder());
            headerBuffer.put(ioBuffer, 0, DDS_HEADER_SIZE).position(0);

            width = headerBuffer.getInt(16);
            height = headerBuffer.getInt(12);
            encodedSize = headerBuffer.getInt(20);
        }

        ByteBuffer dataBuffer = ByteBuffer.allocateDirect(encodedSize).order(
                ByteOrder.nativeOrder());
        for (int i = 0; i < encodedSize; ) {
            int chunkSize = Math.min(ioBuffer.length, encodedSize - i);
            if (input.read(ioBuffer, 0, chunkSize) != chunkSize) {
                throw new IOException("Unable to read compressed data.");
            }
            dataBuffer.put(ioBuffer, 0, chunkSize);
            i += chunkSize;
        }
        dataBuffer.position(0);
        return new GLESCompressedTextureInfo(width, height, dataBuffer);
    }
}