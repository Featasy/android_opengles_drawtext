package ecs.com.openglesdrawtext;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.Arrays;

public class GLSquare {

    public float[] mVerticesData;

    public float[] mColourData;

    public float[] mTextureData;

    public int dCount = 0;

    public ShortBuffer mIndices;

    static int VERTEXSIZE = 3;

    static int COLOURSIZE = 4;

    static int TEXTURESIZE = 2;

    static int VERTEXSTRIDE = VERTEXSIZE * 4;

    static int COLOURSTRIDE = COLOURSIZE * 4;

    static int TEXTURESTRIDE = TEXTURESIZE * 4;

    public final short[] mIndicesData =
    {
            0, 1, 2, 0, 2, 3
    };

    GLSquare( float[] mVerticesDataParam, float[] mColourDataParam, float[] mTextureDataParam )
    {
        mVerticesData = new float[12];

        mColourData = new float[16];

        mTextureData = new float[8];

        dCount = 0;

        for( float f : mVerticesDataParam )
        {
            mVerticesData[dCount] = f;

            dCount++;
        }

        dCount = 0;

        for( float f : mColourDataParam )
        {
            mColourData[dCount] = f;

            dCount++;
        }

        dCount = 0;

        for( float f : mTextureDataParam )
        {
            mTextureData[dCount] = f;

            dCount++;
        }

        mIndices = ByteBuffer.allocateDirect ( mIndicesData.length * 2 )
                .order ( ByteOrder.nativeOrder( ) ).asShortBuffer( );

        mIndices.put ( mIndicesData ).position ( 0 );
    }

}
