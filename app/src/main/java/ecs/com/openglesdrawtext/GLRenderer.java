package ecs.com.openglesdrawtext;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

public class GLRenderer implements GLSurfaceView.Renderer
{

    /*OK. So I have in mind a plan for a total sText manager. I didn't want the simple triangle
    so flash r g b then. The frame rate is not really managed. The meat of the project is based off
    of the rosetta stones in res/assets/textures
    */
    private Context mContext;

    private int mProgramObject_20pt_black_text;

    private int mProgramObject_Triangles_notex;

    private int [] mVBOIds_text1 = new int[4];

    private int [] mVBOIds_Square_notex = new int[3];

    private int mWidth;

    private int mHeight;

    private long dNanos = 0;

    int dColourCounter = 1;

    GLTextObject mText_HelloWorld;

//    ArrayList< GLSquare > aSquares_test;


    //Matrices
    private final float[] mtrxProjection = new float[16];
    private final float[] mtrxView = new float[16];
    private final float[] mtrxProjectionAndView = new float[16];

    int mBaseMapTexId;
    int mBaseMapLoc;

    public GLRenderer ( Context mParamContext )
    {
        mContext = mParamContext;
        GLESShader.mContext = mParamContext;
    }

    public void onSurfaceCreated ( GL10 glUnused, EGLConfig config )
    {
        mProgramObject_Triangles_notex = GLESShader.loadProgramFromAsset( mContext,
                "shaders/vertexShader.vert",
                "shaders/fragmentShader.frag" );

        mVBOIds_Square_notex[0] = 0;
        mVBOIds_Square_notex[1] = 0;
        mVBOIds_Square_notex[2] = 0;

        /*I'm making a rather unorthodox adjustent to the texture co-ordinates in order to get
        the correct text out right now. What's the problem? It may be in the related:
        https://github.com/einbyte937/powershell_text_texturemap_unicodeall
        or perhaps there is a block adjustment needed in this project. In any case it is a Hello World!
        there on the screen. From here we become progressively more advanced.
         */

        mText_HelloWorld = new GLTextObject( "Hello World!",20,100,20,
                new float[]{ 0.0f,0.0f,1.0f,1.0f }, true );

//        aSquares_test = new ArrayList< GLSquare >( );


        mProgramObject_20pt_black_text = GLESShader.loadProgramFromAsset( mContext,
                "shaders/vertexShader2.vert",
                "shaders/fragmentShader2.frag");

        mVBOIds_text1[0] = 0;
        mVBOIds_text1[1] = 0;
        mVBOIds_text1[2] = 0;
        mVBOIds_text1[3] = 0;

        // Get the sampler locations
        mBaseMapLoc = GLES30.glGetUniformLocation ( mProgramObject_20pt_black_text, "s_baseMap" );

        // Load the texture images from 'assets'
        mBaseMapTexId = GLESShader.loadTextureFromAsset( "fontmaps/20pt_LatinBasic.png" );

        GLES30.glClearColor ( 255f, 255f, 255f, 1f );
    }

    public void onDrawFrame ( GL10 glUnused )
    {
        long dNanosStart = System.nanoTime( );

        GLES30.glUseProgram( mProgramObject_Triangles_notex );

        GLES30.glViewport( 0, 0, mWidth, mHeight );

        GLES30.glClear( GLES30.GL_COLOR_BUFFER_BIT );

        int mtrxhandle = GLES30.glGetUniformLocation( mProgramObject_Triangles_notex, "OrthoMatrix" );

        GLES30.glUniformMatrix4fv( mtrxhandle, 1, false, mtrxProjectionAndView, 0 );

        GLES30.glUseProgram( mProgramObject_20pt_black_text );

        int mtrxhandle2 = GLES30.glGetUniformLocation( mProgramObject_20pt_black_text, "OrthoMatrix" );

        GLES30.glUniformMatrix4fv( mtrxhandle2, 1, false, mtrxProjectionAndView, 0 );

        drawOneText_test( mText_HelloWorld );

        if( dNanos >= 10000000 )
        {
            if( dColourCounter >= 3 ) dColourCounter = 1;
            else dColourCounter++;

            switch( dColourCounter )
            {
                case 1:
                    //GLES30.glClearColor ( 1.0f, 0.0f, 0.0f, 0.0f );
                    break;
                case 2:
                    //GLES30.glClearColor ( 0.0f, 1.0f, 0.0f, 0.0f );
                    break;
                case 3:
                    //GLES30.glClearColor ( 0.0f, 0.0f, 1.0f, 0.0f );
                    break;
            }

            dNanos = 0;
        }

        dNanos += System.nanoTime( ) - dNanosStart;
    }


    private int text1Indices = 0;
    private void drawOneText_test( GLTextObject mParamTxtObj )
    {
        if ( mVBOIds_text1[0] == 0 && mVBOIds_text1[1] == 0 && mVBOIds_text1[2] == 0 && mVBOIds_text1[3] == 0 )
        {
            GLES30.glGenBuffers ( 4, mVBOIds_text1, 0 );

            ArrayList aCrunchVerts = new ArrayList< float[] >( );
            ArrayList aCrunchColours = new ArrayList< float[] >( );
            ArrayList aCrunchIndices = new ArrayList< float[] >( );
            ArrayList aCrunchTexture = new ArrayList< float[] >( );


            for( GLSquare s : mParamTxtObj.aSquares )
                aCrunchVerts.add( s.mVerticesData );

            for( GLSquare s : mParamTxtObj.aSquares )
                aCrunchColours.add( s.mColourData );

            int dSquareCount = 0;

            while( dSquareCount < mParamTxtObj.aSquares.size( ) )
            {
                short[] mIndicesData =
                        {
                                0, 1, 2, 0, 2, 3
                        };

                for( int x = 0; x < mIndicesData.length; x++ )
                    mIndicesData[x] += ( 4 * dSquareCount );

                aCrunchIndices.add( mIndicesData );

                dSquareCount++;
            }

            for( GLSquare s : mParamTxtObj.aSquares )
                aCrunchTexture.add( s.mTextureData );

            float[] mVerticesData = ArrayUtilChristopo.combFloat( aCrunchVerts );

            float[] mColourData = ArrayUtilChristopo.combFloat( aCrunchColours );

            float[] mTextureData = ArrayUtilChristopo.combFloat( aCrunchTexture );

            short[] mIndicesData = ArrayUtilChristopo.combShort( aCrunchIndices );

            text1Indices = mIndicesData.length;

            //Build Bytes and store in OpenGL Buffer Space
            FloatBuffer mVertices = ByteBuffer.allocateDirect( ( mVerticesData.length * 4 ) )
                    .order( ByteOrder.nativeOrder( ) ).asFloatBuffer( );

            FloatBuffer mColours = ByteBuffer.allocateDirect ( ( mColourData.length * 4 ) )
                    .order ( ByteOrder.nativeOrder( ) ).asFloatBuffer( );

            FloatBuffer mTexture = ByteBuffer.allocateDirect ( ( mTextureData.length * 4 ) )
                    .order ( ByteOrder.nativeOrder( ) ).asFloatBuffer( );

            ShortBuffer mIndices = ByteBuffer.allocateDirect ( ( mIndicesData.length * 2 ) )
                    .order ( ByteOrder.nativeOrder( ) ).asShortBuffer( );

            mVertices.position( 0 );
            mVertices.put( mVerticesData );

            mColours.position( 0 );
            mColours.put( mColourData );

            mTexture.position( 0 );
            mTexture.put( mTextureData );

            mIndices.position( 0 );
            mIndices.put( mIndicesData );


            GLES30.glBindBuffer( GLES30.GL_ARRAY_BUFFER, mVBOIds_text1[0] );
            mVertices.position( 0 );
			//https://www.khronos.org/opengles/sdk/1.1/docs/man/glBufferData.xml
            GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, GLSquare.VERTEXSTRIDE * ( mVerticesData.length / 3 ),
                    mVertices, GLES30.GL_STATIC_DRAW );

            GLES30.glBindBuffer( GLES30.GL_ARRAY_BUFFER, mVBOIds_text1[1] );
            mColours.position( 0 );
            GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, GLSquare.COLOURSTRIDE * ( mColourData.length / 4 ),
                    mColours, GLES30.GL_STATIC_DRAW );

            GLES30.glBindBuffer( GLES30.GL_ARRAY_BUFFER, mVBOIds_text1[3] );
            mTexture.position( 0 );
            GLES30.glBufferData( GLES30.GL_ARRAY_BUFFER, GLSquare.TEXTURESTRIDE * ( mTextureData.length / 2 ),
                    mTexture, GLES30.GL_STATIC_DRAW );

            mIndices.position ( 0 );
            GLES30.glBindBuffer ( GLES30.GL_ELEMENT_ARRAY_BUFFER, mVBOIds_text1[2] );
            GLES30.glBufferData ( GLES30.GL_ELEMENT_ARRAY_BUFFER, 2 * ( mIndicesData.length ),
                    mIndices, GLES30.GL_STATIC_DRAW );
        }

        GLES30.glBindBuffer( GLES30.GL_ARRAY_BUFFER, mVBOIds_text1[0] );
        GLES30.glEnableVertexAttribArray( 0 );
        GLES30.glVertexAttribPointer( 0, GLSquare.VERTEXSIZE,
                GLES30.GL_FLOAT, false, GLSquare.VERTEXSTRIDE, 0 );


        GLES30.glBindBuffer( GLES30.GL_ARRAY_BUFFER, mVBOIds_text1[1] );
        GLES30.glEnableVertexAttribArray( 1 );
        GLES30.glVertexAttribPointer( 1, GLSquare.COLOURSIZE,
                GLES30.GL_FLOAT, false, GLSquare.COLOURSTRIDE, 0 );

        //https://www.khronos.org/opengles/sdk/docs/man3/html/glVertexAttribPointer.xhtml
        //Load the texture coordinate
		GLES30.glBindBuffer( GLES30.GL_ARRAY_BUFFER, mVBOIds_text1[3] );
		GLES30.glEnableVertexAttribArray( 3 );
        GLES30.glVertexAttribPointer(3, GLSquare.TEXTURESIZE,
                GLES30.GL_FLOAT, false, GLSquare.TEXTURESTRIDE, 0 );

        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, mVBOIds_text1[2]);


        // Bind the base map
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mBaseMapTexId);

        // Set the base map sampler to texture unit to 0
        GLES30.glUniform1i(mBaseMapLoc, 0);

        GLES30.glDrawElements( GLES30.GL_TRIANGLES, text1Indices,
                GLES30.GL_UNSIGNED_SHORT, 0 );

    }

    public void onSurfaceChanged ( GL10 glUnused, int width, int height )
    {
        mWidth = width;
        mHeight = height;

        GLES30.glViewport( 0, 0, ( int ) mWidth, ( int ) mHeight );

        for( int i = 0; i < 16; i++ )
        {
            mtrxProjection[i] = 0.0f;
            mtrxView[i] = 0.0f;
            mtrxProjectionAndView[i] = 0.0f;
        }

        //Origin is upper left
        Matrix.orthoM( mtrxProjection, 0, 0f, mWidth, mHeight, 0.0f, 0, 50 );

        Matrix.setLookAtM( mtrxView, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1.0f, 0.0f );

        Matrix.multiplyMM( mtrxProjectionAndView, 0, mtrxProjection, 0, mtrxView, 0 );
    }
}

