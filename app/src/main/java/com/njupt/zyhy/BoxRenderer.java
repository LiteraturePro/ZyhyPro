//================================================================================================================================
//
// Copyright (c) 2015-2021 VisionStar Information Technology (Shanghai) Co., Ltd. All Rights Reserved.
// EasyAR is the registered trademark or trademark of VisionStar Information Technology (Shanghai) Co., Ltd in China
// and other countries for the augmented reality technology developed by VisionStar Information Technology (Shanghai) Co., Ltd.
//
//================================================================================================================================

package com.njupt.zyhy;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLContext;

import cn.easyar.Vec2F;
import cn.easyar.Matrix44F;

// all methods of this class can only be called on one thread with the same OpenGLES context
public class BoxRenderer
{
    private EGLContext current_context = null;
    private int program_box;
    private int pos_coord_box;
    private int pos_color_box;
    private int pos_trans_box;
    private int pos_proj_box;
    private int vbo_coord_box;
    private int vbo_color_box;
    private int vbo_color_box_2;
    private int vbo_faces_box;

    private String box_vert="uniform mat4 trans;\n"
            + "uniform mat4 proj;\n"
            + "attribute vec4 coord;\n"
            + "attribute vec4 color;\n"
            + "varying vec4 vcolor;\n"
            + "\n"
            + "void main(void)\n"
            + "{\n"
            + "    vcolor = color;\n"
            + "    gl_Position = proj*trans*coord;\n"
            + "}\n"
            + "\n"
    ;

    private String box_frag="#ifdef GL_ES\n"
            + "precision highp float;\n"
            + "#endif\n"
            + "varying vec4 vcolor;\n"
            + "\n"
            + "void main(void)\n"
            + "{\n"
            + "    gl_FragColor = vcolor;\n"
            + "}\n"
            + "\n"
    ;

    private float[] flatten(float[][] a)
    {
        int size = 0;
        for (int k = 0; k < a.length; k += 1) {
            size += a[k].length;
        }
        float[] l = new float[size];
        int offset = 0;
        for (int k = 0; k < a.length; k += 1) {
            System.arraycopy(a[k], 0, l, offset, a[k].length);
            offset += a[k].length;
        }
        return l;
    }
    private int[] flatten(int[][] a)
    {
        int size = 0;
        for (int k = 0; k < a.length; k += 1) {
            size += a[k].length;
        }
        int[] l = new int[size];
        int offset = 0;
        for (int k = 0; k < a.length; k += 1) {
            System.arraycopy(a[k], 0, l, offset, a[k].length);
            offset += a[k].length;
        }
        return l;
    }
    private short[] flatten(short[][] a)
    {
        int size = 0;
        for (int k = 0; k < a.length; k += 1) {
            size += a[k].length;
        }
        short[] l = new short[size];
        int offset = 0;
        for (int k = 0; k < a.length; k += 1) {
            System.arraycopy(a[k], 0, l, offset, a[k].length);
            offset += a[k].length;
        }
        return l;
    }
    private byte[] flatten(byte[][] a)
    {
        int size = 0;
        for (int k = 0; k < a.length; k += 1) {
            size += a[k].length;
        }
        byte[] l = new byte[size];
        int offset = 0;
        for (int k = 0; k < a.length; k += 1) {
            System.arraycopy(a[k], 0, l, offset, a[k].length);
            offset += a[k].length;
        }
        return l;
    }
    private byte[] byteArrayFromIntArray(int[] a)
    {
        byte[] l = new byte[a.length];
        for (int k = 0; k < a.length; k += 1) {
            l[k] = (byte)(a[k] & 0xFF);
        }
        return l;
    }

    private int generateOneBuffer()
    {
        int[] buffer = {0};
        GLES20.glGenBuffers(1, buffer, 0);
        return buffer[0];
    }
    private void deleteOneBuffer(int id)
    {
        int[] buffer = {id};
        GLES20.glDeleteBuffers(1, buffer, 0);
    }

    private static float[] getGLMatrix(Matrix44F m)
    {
        float[] d = m.data;
        return new float[]{d[0], d[4], d[8], d[12], d[1], d[5], d[9], d[13], d[2], d[6], d[10], d[14], d[3], d[7], d[11], d[15]};
    }

    public BoxRenderer()
    {
        current_context = ((EGL10)EGLContext.getEGL()).eglGetCurrentContext();
        program_box = GLES20.glCreateProgram();
        int vertShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vertShader, box_vert);
        GLES20.glCompileShader(vertShader);
        int fragShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fragShader, box_frag);
        GLES20.glCompileShader(fragShader);
        GLES20.glAttachShader(program_box, vertShader);
        GLES20.glAttachShader(program_box, fragShader);
        GLES20.glLinkProgram(program_box);
        GLES20.glUseProgram(program_box);
        GLES20.glDeleteShader(vertShader);
        GLES20.glDeleteShader(fragShader);
        pos_coord_box = GLES20.glGetAttribLocation(program_box, "coord");
        pos_color_box = GLES20.glGetAttribLocation(program_box, "color");
        pos_trans_box = GLES20.glGetUniformLocation(program_box, "trans");
        pos_proj_box = GLES20.glGetUniformLocation(program_box, "proj");

        vbo_coord_box = generateOneBuffer();
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_coord_box);
        float cube_vertices[][] = {
            /* +z */{1.0f / 2, 1.0f / 2, 0.01f / 2}, {1.0f / 2, -1.0f / 2, 0.01f / 2}, {-1.0f / 2, -1.0f / 2, 0.01f / 2}, {-1.0f / 2, 1.0f / 2, 0.01f / 2},
            /* -z */{1.0f / 2, 1.0f / 2, -0.01f / 2}, {1.0f / 2, -1.0f / 2, -0.01f / 2}, {-1.0f / 2, -1.0f / 2, -0.01f / 2}, {-1.0f / 2, 1.0f / 2, -0.01f / 2}
        };
        FloatBuffer cube_vertices_buffer = FloatBuffer.wrap(flatten(cube_vertices));
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, cube_vertices_buffer.limit() * 4, cube_vertices_buffer, GLES20.GL_DYNAMIC_DRAW);

        vbo_color_box = generateOneBuffer();
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_color_box);
        int cube_vertex_colors[][] = {
            {255, 0, 0, 128}, {0, 255, 0, 128}, {0, 0, 255, 128}, {0, 0, 0, 128},
            {0, 255, 255, 128}, {255, 0, 255, 128}, {255, 255, 0, 128}, {255, 255, 255, 128}};
        ByteBuffer cube_vertex_colors_buffer = ByteBuffer.wrap(byteArrayFromIntArray(flatten(cube_vertex_colors)));
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, cube_vertex_colors_buffer.limit(), cube_vertex_colors_buffer, GLES20.GL_STATIC_DRAW);

        vbo_color_box_2 = generateOneBuffer();
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_color_box_2);
        int cube_vertex_colors_2[][] = {
            {255, 0, 0, 255}, {255, 255, 0, 255}, {0, 255, 0, 255}, {255, 0, 255, 255},
            {255, 0, 255, 255}, {255, 255, 255, 255}, {0, 255, 255, 255}, {255, 0, 255, 255}};
        ByteBuffer cube_vertex_colors_2_buffer = ByteBuffer.wrap(byteArrayFromIntArray(flatten(cube_vertex_colors_2)));
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, cube_vertex_colors_2_buffer.limit(), cube_vertex_colors_2_buffer, GLES20.GL_STATIC_DRAW);

        vbo_faces_box = generateOneBuffer();
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, vbo_faces_box);
        short cube_faces[][] = {
            /* +x */ {0, 1, 5, 4},
            /* -x */ {3, 7, 6, 2},
            /* +y */ {0, 4, 7, 3},
            /* -y */ {1, 2, 6, 5},
            /* +z */ {0, 3, 2, 1},
            /* -z */ {4, 5, 6, 7}
        };
        ShortBuffer cube_faces_buffer = ShortBuffer.wrap(flatten(cube_faces));
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, cube_faces_buffer.limit() * 2, cube_faces_buffer, GLES20.GL_STATIC_DRAW);
    }
    public void dispose()
    {
        if (((EGL10)EGLContext.getEGL()).eglGetCurrentContext().equals(current_context)) { //destroy resources unless the context has lost
            GLES20.glDeleteProgram(program_box);

            deleteOneBuffer(vbo_coord_box);
            deleteOneBuffer(vbo_color_box);
            deleteOneBuffer(vbo_color_box_2);
            deleteOneBuffer(vbo_faces_box);
        }
    }

    public void render(Matrix44F projectionMatrix, Matrix44F cameraview, Vec2F size)
    {
        float size0 = size.data[0];
        float size1 = size.data[1];

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_coord_box);
        float height = size0 / 1000;
        float cube_vertices[][] = {
            /* +z */{size0 / 2, size1 / 2, height / 2}, {size0 / 2, -size1 / 2, height / 2}, {-size0 / 2, -size1 / 2, height / 2}, {-size0 / 2, size1 / 2, height / 2},
            /* -z */{size0 / 2, size1 / 2, 0}, {size0 / 2, -size1 / 2, 0}, {-size0 / 2, -size1 / 2, 0}, {-size0 / 2, size1 / 2, 0}};
        FloatBuffer cube_vertices_buffer = FloatBuffer.wrap(flatten(cube_vertices));
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, cube_vertices_buffer.limit() * 4, cube_vertices_buffer, GLES20.GL_DYNAMIC_DRAW);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glUseProgram(program_box);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_coord_box);
        GLES20.glEnableVertexAttribArray(pos_coord_box);
        GLES20.glVertexAttribPointer(pos_coord_box, 3, GLES20.GL_FLOAT, false, 0, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_color_box);
        GLES20.glEnableVertexAttribArray(pos_color_box);
        GLES20.glVertexAttribPointer(pos_color_box, 4, GLES20.GL_UNSIGNED_BYTE, true, 0, 0);
        GLES20.glUniformMatrix4fv(pos_trans_box, 1, false, getGLMatrix(cameraview), 0);
        GLES20.glUniformMatrix4fv(pos_proj_box, 1, false, getGLMatrix(projectionMatrix), 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, vbo_faces_box);
        for(int i = 0; i < 6; i++) {
            GLES20.glDrawElements(GLES20.GL_TRIANGLE_FAN, 4, GLES20.GL_UNSIGNED_SHORT, i * 4 * 2);
        }

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_coord_box);
        float cube_vertices_2[][] = {
            /* +z */{size0 / 4, size1 / 4, size0 / 4},{size0 / 4, -size1 / 4, size0 / 4},{-size0 / 4, -size1 / 4, size0 / 4},{-size0 / 4, size1 / 4, size0 / 4},
            /* -z */{size0 / 4, size1 / 4, 0},{size0 / 4, -size1 / 4, 0},{-size0 / 4, -size1 / 4, 0},{-size0 / 4, size1 / 4, 0}};
        FloatBuffer cube_vertices_2_buffer = FloatBuffer.wrap(flatten(cube_vertices_2));
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, cube_vertices_2_buffer.limit() * 4, cube_vertices_2_buffer, GLES20.GL_DYNAMIC_DRAW);
        GLES20.glEnableVertexAttribArray(pos_coord_box);
        GLES20.glVertexAttribPointer(pos_coord_box, 3, GLES20.GL_FLOAT, false, 0, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_color_box_2);
        GLES20.glEnableVertexAttribArray(pos_color_box);
        GLES20.glVertexAttribPointer(pos_color_box, 4, GLES20.GL_UNSIGNED_BYTE, true, 0, 0);
        for(int i = 0; i < 6; i++) {
            GLES20.glDrawElements(GLES20.GL_TRIANGLE_FAN, 4, GLES20.GL_UNSIGNED_SHORT, i * 4 * 2);
        }
    }
}
