//================================================================================================================================
//
// Copyright (c) 2015-2021 VisionStar Information Technology (Shanghai) Co., Ltd. All Rights Reserved.
// EasyAR is the registered trademark or trademark of VisionStar Information Technology (Shanghai) Co., Ltd in China
// and other countries for the augmented reality technology developed by VisionStar Information Technology (Shanghai) Co., Ltd.
//
//================================================================================================================================

package com.njupt.zyhy;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import cn.easyar.Engine;

public class GLView extends GLSurfaceView
{
    private Object lock = new Object();
    private boolean finishing = false;

    private boolean initialized = false;
    private int width = 1;
    private int height = 1;

    private HelloAR helloAR;

    public GLView(final Context context)
    {
        super(context);
        //setPreserveEGLContextOnPause(true); //uncomment if EGL context need to be preserved on pause
        setEGLContextFactory(new ContextFactory());
        setEGLWindowSurfaceFactory(new WindowSurfaceFactory());
        setEGLConfigChooser(new ConfigChooser());

        this.setRenderer(new Renderer() {
            @Override
            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                if (!initialized) {
                    initialized = true;
                    helloAR = new HelloAR(context);
                    helloAR.initialize();
                } else {
                    helloAR.recreate_context();
                }
                helloAR.start();
            }

            @Override
            public void onSurfaceChanged(GL10 gl, int w, int h) {
                width = w;
                height = h;
            }

            @Override
            public void onDrawFrame(GL10 gl) {
                if (!initialized) { return; }
                helloAR.render(width, height, GetScreenRotation());
            }
        });
        this.setZOrderMediaOverlay(true);
    }

    private Activity getActivity()
    {
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity)context;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }

    private void onSurfaceDestroyed()
    {
        boolean b;
        synchronized (lock) {
            b = finishing;
        }
        if (initialized && b) {
            initialized = false;
            helloAR.stop();
            helloAR.dispose();
            helloAR = null;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Engine.onResume();
    }

    @Override
    public void onPause()
    {
        Activity a = getActivity();
        if (a != null) {
            boolean b = a.isFinishing();
            synchronized (lock) {
                finishing = b;
            }
        }
        Engine.onPause();
        super.onPause();
    }

    private int GetScreenRotation()
    {
        int rotation = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        int orientation;
        switch(rotation) {
            case Surface.ROTATION_0:
                orientation = 0;
                break;
            case Surface.ROTATION_90:
                orientation = 90;
                break;
            case Surface.ROTATION_180:
                orientation = 180;
                break;
            case Surface.ROTATION_270:
                orientation = 270;
                break;
            default:
                orientation = 0;
                break;
        }
        return orientation;
    }

    private static class ContextFactory implements EGLContextFactory
    {
        private static int EGL_CONTEXT_CLIENT_VERSION = 0x3098;

        public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig eglConfig)
        {
            EGLContext context;
            int[] attrib = { EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE };
            context = egl.eglCreateContext(display, eglConfig, EGL10.EGL_NO_CONTEXT, attrib );
            return context;
        }

        public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context)
        {
            egl.eglDestroyContext(display, context);
        }
    }

    private class WindowSurfaceFactory implements EGLWindowSurfaceFactory
    {
        @Override
        public EGLSurface createWindowSurface(EGL10 egl, EGLDisplay display, EGLConfig config, Object nativeWindow)
        {
            EGLSurface result = null;
            try {
                result = egl.eglCreateWindowSurface(display, config, nativeWindow, null);
            } catch (IllegalArgumentException e) {
                // This exception indicates that the surface flinger surface
                // is not valid. This can happen if the surface flinger surface has
                // been torn down, but the application has not yet been
                // notified via SurfaceHolder.Callback.surfaceDestroyed.
                // In theory the application should be notified first,
                // but in practice sometimes it is not. See b/4588890
                Log.e("GLSurfaceView", "eglCreateWindowSurface", e);
            }
            return result;
        }

        @Override
        public void destroySurface(EGL10 egl, EGLDisplay display, EGLSurface surface)
        {
            onSurfaceDestroyed();
            egl.eglDestroySurface(display, surface);
        }
    }

    private static class ConfigChooser implements EGLConfigChooser
    {
        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display)
        {
            final int EGL_OPENGL_ES2_BIT = 0x0004;
            final int[] attrib = { EGL10.EGL_RED_SIZE, 4, EGL10.EGL_GREEN_SIZE, 4, EGL10.EGL_BLUE_SIZE, 4,
                    EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT, EGL10.EGL_NONE };

            int[] num_config = new int[1];
            egl.eglChooseConfig(display, attrib, null, 0, num_config);

            int numConfigs = num_config[0];
            if (numConfigs <= 0)
                throw new IllegalArgumentException("fail to choose EGL configs");

            EGLConfig[] configs = new EGLConfig[numConfigs];
            egl.eglChooseConfig(display, attrib, configs, numConfigs, num_config);

            for (EGLConfig config : configs)
            {
                int[] val = new int[1];
                int r = 0, g = 0, b = 0, a = 0, d = 0;
                if (egl.eglGetConfigAttrib(display, config, EGL10.EGL_DEPTH_SIZE, val))
                    d = val[0];
                if (d < 16)
                    continue;

                if (egl.eglGetConfigAttrib(display, config, EGL10.EGL_RED_SIZE, val))
                    r = val[0];
                if (egl.eglGetConfigAttrib(display, config, EGL10.EGL_GREEN_SIZE, val))
                    g = val[0];
                if (egl.eglGetConfigAttrib(display, config, EGL10.EGL_BLUE_SIZE, val))
                    b = val[0];
                if (egl.eglGetConfigAttrib(display, config, EGL10.EGL_ALPHA_SIZE, val))
                    a = val[0];
                if (r == 8 && g == 8 && b == 8 && a == 0)
                    return config;
            }

            return configs[0];
        }
    }
}
