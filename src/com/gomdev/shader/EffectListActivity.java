package com.gomdev.shader;

import java.util.ArrayList;
import java.util.Map;

import com.gomdev.shader.R;
import com.gomdev.shader.coloredRectangle.ColoredRectangleConfig;
import com.gomdev.shader.coloredTriangle.ColoredTriangleConfig;
import com.gomdev.shader.icon.IconConfig;
import com.gomdev.shader.instancedRendering.IRConfig;
import com.gomdev.shader.instancedRendering2.IR2Config;
import com.gomdev.shader.mipmap.MipmapConfig;
import com.gomdev.shader.multiLighting.MultiLightingConfig;
//import com.gomdev.shader.occlusionQuery.OQConfig;
import com.gomdev.shader.perFragmentLighting.PFLConfig;
import com.gomdev.shader.perVertexLighting.PVLConfig;
import com.gomdev.shader.texturedCube.TexturedCubeConfig;
import com.gomdev.shader.texturedRectangle.TexturedRectangleConfig;
//import com.gomdev.shader.whitehole.WhiteholeConfig;
import com.gomdev.gles.GLESConfig;
import com.gomdev.gles.GLESContext;
import com.gomdev.gles.GLESConfig.Version;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class EffectListActivity extends Activity implements
        DialogListener, Ad {
    static final String CLASS = "EffectListActivity";
    static final String TAG = ShaderConfig.TAG + " " + CLASS;
    static final boolean DEBUG = ShaderConfig.DEBUG;

    static final int REMOVE_DUMMY_GL_SURFACE = 100;

    private GLSurfaceView mView;
    private DummyRenderer mRenderer;
    private ArrayList<EffectInfo> mEffects = new ArrayList<EffectInfo>();

    protected Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case REMOVE_DUMMY_GL_SURFACE:
                mView.setVisibility(View.GONE);
                break;
            default:
            }
        }
    };

    class EffectInfo {
        String mEffectName;
        Intent mIntent = null;
        int[] mShaderResIDs;
        String[] mShaderTitle;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.effect_list);

        ShaderContext.newInstance();

        SharedPreferences pref = getSharedPreferences(ShaderConfig.PREF_NAME, 0);
        String extensions = pref
                .getString(ShaderConfig.PREF_GLES_EXTENSION, "");

        String renderer = pref
                .getString(ShaderConfig.PREF_GLES_RENDERER, "");

        String vendor = pref
                .getString(ShaderConfig.PREF_GLES_VENDOR, "");

        String version = pref
                .getString(ShaderConfig.PREF_GLES_VERSION, "");

        String hardware = pref
                .getString(ShaderConfig.PREF_CPU_HARDWARE, "");

        String architecture = pref
                .getString(ShaderConfig.PREF_CPU_ARCHITECTURE, "");

        String feature = pref
                .getString(ShaderConfig.PREF_CPU_FEATURE, "");

        if (extensions.compareTo("") != 0 &&
                renderer.compareTo("") != 0 &&
                vendor.compareTo("") != 0 &&
                version.compareTo("") != 0 &&
                hardware.compareTo("") != 0 &&
                architecture.compareTo("") != 0 &&
                feature.compareTo("") != 0) {
            ShaderContext.getInstance().setExtensions(extensions);
            ShaderContext.getInstance().setRenderer(renderer);
            ShaderContext.getInstance().setVendor(vendor);
            ShaderContext.getInstance().setVersion(version);

            ShaderContext.getInstance().setHardware(hardware);
            ShaderContext.getInstance().setArchitecture(architecture);
            ShaderContext.getInstance().setFeature(feature);

            mView = (GLSurfaceView) findViewById(R.id.glsurfaceview);
            mView.setVisibility(View.GONE);
        } else {
            setupGLRendererForGPUInfo();
        }

        getCPUInfo();

        optionChanged();
    }

    private void getCPUInfo() {
        String[] infos = new String[] {
                ShaderConfig.PREF_CPU_HARDWARE,
                ShaderConfig.PREF_CPU_ARCHITECTURE,
                ShaderConfig.PREF_CPU_FEATURE
        };
        Map<String, String> cpuInfos = ShaderUtils.getCPUInfo(infos);

        SharedPreferences pref = this.getSharedPreferences(
                ShaderConfig.PREF_NAME, 0);
        SharedPreferences.Editor editor = pref.edit();

        // cpu hardware
        String hardware = cpuInfos.get(ShaderConfig.PREF_CPU_HARDWARE);
        ShaderContext.getInstance().setHardware(hardware);

        editor.putString(ShaderConfig.PREF_CPU_HARDWARE, hardware);

        // cpu architecture
        String architecture = cpuInfos.get(ShaderConfig.PREF_CPU_ARCHITECTURE);
        ShaderContext.getInstance().setArchitecture(architecture);

        editor.putString(ShaderConfig.PREF_CPU_FEATURE, architecture);

        // cpu feature
        String feature = cpuInfos.get(ShaderConfig.PREF_CPU_FEATURE);
        ShaderContext.getInstance().setFeature(feature);

        editor.putString(ShaderConfig.PREF_CPU_FEATURE, feature);

        editor.commit();
    }

    private void setupGLRendererForGPUInfo() {
        mRenderer = new DummyRenderer(this);
        mRenderer.setHandler(mHandler);
        mView = (GLSurfaceView) findViewById(R.id.glsurfaceview);
        mView.setEGLContextClientVersion(2);
        mView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mView.setRenderer(mRenderer);
        mView.setZOrderOnTop(true);
        mView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    private void optionChanged() {
        setupEffectInfos();
        makeEffectList();
    }

    private void setupEffectInfos() {
        mEffects.clear();

        SharedPreferences pref = getSharedPreferences(ShaderConfig.PREF_NAME, 0);
        boolean useGLES30 = pref.getBoolean(ShaderConfig.PREF_USE_GLES_30,
                GLESConfig.GLES_VERSION == Version.GLES_30);

        Version version = Version.GLES_20;
        if (useGLES30 == true) {
            GLESContext.getInstance().setVersion(Version.GLES_30);
            version = Version.GLES_30;
        } else {
            GLESContext.getInstance().setVersion(Version.GLES_20);
            version = Version.GLES_20;
        }

        setupColoredTriangle(version);
        setupColoredPlane(version);
        setupTexturePlane(version);
        setupTextureCube(version);
        setupIcon(version);
        setupMipmap(version);
        setupPVL(version);
        setupPFL(version);
        setupMultiLighting(version);
        // setupOQ(version);
        setupIR(version);
        setupIR2(version);
        // setupWhitehole(version);

        if (DEBUG) {
            Log.d(TAG, "onCreate() Effects");
            for (EffectInfo effectInfo : mEffects) {
                Log.d(TAG, "\t " + effectInfo.mEffectName);
            }
        }
    }

    private void setupIR(Version version) {
        EffectInfo info = new EffectInfo();
        info.mEffectName = IRConfig.EFFECT_NAME;
        info.mIntent = new Intent(this,
                com.gomdev.shader.instancedRendering.IRActivity.class);

        if (version == Version.GLES_20) {
            info.mShaderResIDs = new int[] {
                    R.raw.pfl_color_20_vs,
                    R.raw.pfl_color_20_fs,
            };

            info.mShaderTitle = new String[] {
                    "IR 20 VS",
                    "IR 20 FS",
            };
        } else {
            info.mShaderResIDs = new int[] {
                    R.raw.ir_30_vs,
                    R.raw.pfl_color_30_fs
            };

            info.mShaderTitle = new String[] {
                    "IR 30 VS",
                    "IR 30 FS",
            };
        }

        mEffects.add(info);
    }

    private void setupIR2(Version version) {
        EffectInfo info = new EffectInfo();
        info.mEffectName = IR2Config.EFFECT_NAME;
        info.mIntent = new Intent(this,
                com.gomdev.shader.instancedRendering2.IR2Activity.class);

        if (version == Version.GLES_20) {
            info.mShaderResIDs = new int[] {
                    R.raw.pfl_color_20_vs,
                    R.raw.pfl_color_20_fs,
            };

            info.mShaderTitle = new String[] {
                    "IR2 20 VS",
                    "IR2 20 FS",
            };
        } else {
            info.mShaderResIDs = new int[] {
                    R.raw.ir2_30_vs,
                    R.raw.pfl_color_30_fs
            };

            info.mShaderTitle = new String[] {
                    "IR2 30 VS",
                    "IR2 30 FS",
            };
        }

        mEffects.add(info);
    }

    private void setupTexturePlane(Version version) {
        EffectInfo info = new EffectInfo();
        info.mEffectName = TexturedRectangleConfig.EFFECT_NAME;
        info.mIntent = new Intent(
                this,
                com.gomdev.shader.texturedRectangle.TexturedRectangleActivity.class);

        if (version == Version.GLES_20) {
            info.mShaderResIDs = new int[] {
                    R.raw.texture_20_vs,
                    R.raw.texture_20_fs,
            };

            info.mShaderTitle = new String[] {
                    "Texture Rectangle 20 VS",
                    "Texture Rectangle 20 FS",
            };
        } else {
            info.mShaderResIDs = new int[] {
                    R.raw.texture_30_vs,
                    R.raw.texture_30_fs,
            };

            info.mShaderTitle = new String[] {
                    "Texture Rectangle 30 VS",
                    "Texture Rectangle 30 FS",
            };
        }

        mEffects.add(info);
    }

    private void setupTextureCube(Version version) {
        EffectInfo info = new EffectInfo();
        info.mEffectName = TexturedCubeConfig.EFFECT_NAME;
        info.mIntent = new Intent(
                this,
                com.gomdev.shader.texturedCube.TexturedCubeActivity.class);

        if (version == Version.GLES_20) {
            info.mShaderResIDs = new int[] {
                    R.raw.texture_20_vs,
                    R.raw.texture_20_fs,
            };

            info.mShaderTitle = new String[] {
                    "Texture Cube 20 VS",
                    "Texture Cube 20 FS",
            };
        } else {
            info.mShaderResIDs = new int[] {
                    R.raw.texture_30_vs,
                    R.raw.texture_30_fs,
            };

            info.mShaderTitle = new String[] {
                    "Texture Cube 30 VS",
                    "Texture Cube 30 FS",
            };
        }

        mEffects.add(info);
    }

    private void setupMipmap(Version version) {
        EffectInfo info = new EffectInfo();
        info.mEffectName = MipmapConfig.EFFECT_NAME;
        info.mIntent = new Intent(this,
                com.gomdev.shader.mipmap.MipmapActivity.class);

        if (version == Version.GLES_20) {
            info.mShaderResIDs = new int[] {
                    R.raw.texture_20_vs,
                    R.raw.texture_20_fs,
            };

            info.mShaderTitle = new String[] {
                    "Mipmapping 20 VS",
                    "Mipmapping 20 FS",
            };
        } else {
            info.mShaderResIDs = new int[] {
                    R.raw.texture_30_vs,
                    R.raw.texture_30_fs,
            };

            info.mShaderTitle = new String[] {
                    "Mipmaping 30 VS",
                    "Mipampping 30 FS",
            };
        }

        mEffects.add(info);
    }

    private void setupColoredTriangle(Version version) {
        EffectInfo info = new EffectInfo();
        info.mEffectName = ColoredTriangleConfig.EFFECT_NAME;
        info.mIntent = new Intent(this,
                com.gomdev.shader.coloredTriangle.ColoredTriangleActivity.class);
        if (version == Version.GLES_20) {
            info.mShaderResIDs = new int[] {
                    R.raw.color_20_vs,
                    R.raw.color_20_fs,
            };

            info.mShaderTitle = new String[] {
                    "Colored Triangle 20 VS",
                    "Colored Triangle 20 FS",
            };
        } else {
            info.mShaderResIDs = new int[] {
                    R.raw.color_30_vs,
                    R.raw.color_30_fs
            };

            info.mShaderTitle = new String[] {
                    "Colored Triangle 30 VS",
                    "Colored Triangle 30 FS",
            };
        }

        mEffects.add(info);
    }

    private void setupColoredPlane(Version version) {
        EffectInfo info = new EffectInfo();
        info.mEffectName = ColoredRectangleConfig.EFFECT_NAME;
        info.mIntent = new Intent(
                this,
                com.gomdev.shader.coloredRectangle.ColoredRectangleActivity.class);
        if (version == Version.GLES_20) {
            info.mShaderResIDs = new int[] {
                    R.raw.color_20_vs,
                    R.raw.color_20_fs,
            };

            info.mShaderTitle = new String[] {
                    "Colored Rectangle 20 VS",
                    "Colored Rectangle 20 FS",
            };
        } else {
            info.mShaderResIDs = new int[] {
                    R.raw.color_30_vs,
                    R.raw.color_30_fs
            };

            info.mShaderTitle = new String[] {
                    "Colored Rectangle 30 VS",
                    "Colored Rectangle 30 FS",
            };
        }

        mEffects.add(info);
    }

    private void setupIcon(Version version) {
        EffectInfo info = new EffectInfo();
        info.mEffectName = IconConfig.EFFECT_NAME;
        info.mIntent = new Intent(this,
                com.gomdev.shader.icon.IconActivity.class);
        if (version == Version.GLES_20) {
            info.mShaderResIDs = new int[] {
                    R.raw.color_20_vs,
                    R.raw.color_20_fs,
                    R.raw.texture_20_vs,
                    R.raw.texture_20_fs
            };

            info.mShaderTitle = new String[] {
                    "Object 20 VS",
                    "Object 20 FS",
                    "BG 20 VS",
                    "BG 20 FS",
            };
        } else {
            info.mShaderResIDs = new int[] {
                    R.raw.color_30_vs,
                    R.raw.color_30_fs,
                    R.raw.texture_30_vs,
                    R.raw.texture_30_fs
            };

            info.mShaderTitle = new String[] {
                    "Object 30 VS",
                    "Object 30 FS",
                    "BG 30 VS",
                    "BG 30 FS",
            };
        }

        mEffects.add(info);
    }

    private void setupPVL(Version version) {
        EffectInfo info = new EffectInfo();
        info.mEffectName = PVLConfig.EFFECT_NAME;
        info.mIntent = new Intent(this,
                com.gomdev.shader.perVertexLighting.PVLActivity.class);
        if (version == Version.GLES_20) {
            info.mShaderResIDs = new int[] {
                    R.raw.pvl_color_20_vs,
                    R.raw.pvl_color_20_fs
            };

            info.mShaderTitle = new String[] {
                    "Per Vertex Lighting 20 VS",
                    "Per Vertex Lighting 20 FS"
            };
        } else {
            info.mShaderResIDs = new int[] {
                    R.raw.pvl_color_30_vs,
                    R.raw.pvl_color_30_fs
            };

            info.mShaderTitle = new String[] {
                    "Per Vertex Lighting 30 VS",
                    "Per Vertex Lighting 30 FS"
            };
        }

        mEffects.add(info);
    }

    private void setupPFL(Version version) {
        EffectInfo info = new EffectInfo();
        info.mEffectName = PFLConfig.EFFECT_NAME;
        info.mIntent = new Intent(this,
                com.gomdev.shader.perFragmentLighting.PFLActivity.class);
        if (version == Version.GLES_20) {
            info.mShaderResIDs = new int[] {
                    R.raw.pfl_color_20_vs,
                    R.raw.pfl_color_20_fs
            };

            info.mShaderTitle = new String[] {
                    "Per Fragment Lighting 20 VS",
                    "Per Fragment Lighting 20 FS"
            };
        } else {
            info.mShaderResIDs = new int[] {
                    R.raw.pfl_color_30_vs,
                    R.raw.pfl_color_30_fs
            };

            info.mShaderTitle = new String[] {
                    "Per Fragment Lighting 30 VS",
                    "Per Fragment Lighting 30 FS"
            };
        }

        mEffects.add(info);
    }

    // private void setupOQ(Version version) {
    // EffectInfo info = new EffectInfo();
    // info.mEffectName = OQConfig.EFFECT_NAME;
    // info.mIntent = new Intent(this,
    // com.gomdev.shader.occlusionQuery.OQActivity.class);
    // if (version == Version.GLES_20) {
    // info.mShaderResIDs = new int[] {
    // R.raw.oq_20_vs,
    // R.raw.oq_20_fs,
    // };
    //
    // info.mShaderTitle = new String[] {
    // "Occlusion Query 20 VS",
    // "Occlusion Query 20 FS",
    // };
    // } else {
    // info.mShaderResIDs = new int[] {
    // R.raw.oq_30_vs,
    // R.raw.oq_30_fs
    // };
    //
    // info.mShaderTitle = new String[] {
    // "Occlusion Query 30 VS",
    // "Occlusion Query 30 FS",
    // };
    // }
    //
    // mEffects.add(info);
    // }

    private void setupMultiLighting(Version version) {
        EffectInfo info = new EffectInfo();
        info.mEffectName = MultiLightingConfig.EFFECT_NAME;
        info.mIntent = new Intent(this,
                com.gomdev.shader.multiLighting.MultiLightingActivity.class);
        if (version == Version.GLES_20) {
            info.mShaderResIDs = new int[] {
                    R.raw.pfl_color_20_vs,
                    R.raw.pfl_color_20_fs,
            };

            info.mShaderTitle = new String[] {
                    "MultiLighting 20 VS",
                    "MultiLighting 20 FS",
            };
        } else {
            info.mShaderResIDs = new int[] {
                    R.raw.pfl_color_30_vs,
                    R.raw.pfl_color_30_fs
            };

            info.mShaderTitle = new String[] {
                    "MultiLighting 30 VS",
                    "MultiLighting 30 FS",
            };
        }

        mEffects.add(info);
    }

    // private void setupWhitehole(Version version) {
    // EffectInfo info = new EffectInfo();
    // info.mEffectName = WhiteholeConfig.EFFECT_NAME;
    // info.mIntent = new Intent(this,
    // com.gomdev.shader.whitehole.WhiteholeActivity.class);
    // if (version == Version.GLES_20) {
    // info.mShaderResIDs = new int[] {
    // R.raw.whitehole_20_vs,
    // R.raw.whitehole_20_fs,
    // };
    //
    // info.mShaderTitle = new String[] {
    // "Whitehole 20 VS",
    // "Whitehole 20 FS",
    // };
    // } else {
    // info.mShaderResIDs = new int[] {
    // R.raw.whitehole_30_vs,
    // R.raw.whitehole_30_fs
    // };
    //
    // info.mShaderTitle = new String[] {
    // "Whitehole 30 VS",
    // "Whitehole 30 FS",
    // };
    // }
    //
    // mEffects.add(info);
    // }

    private void makeEffectList() {
        ArrayList<String> effectList = new ArrayList<String>();
        // for (EffectInfo effectInfo : mEffects) {
        // effectList.add(effectInfo.mEffectName);
        // }
        for (int i = 0; i < mEffects.size(); i++) {
            EffectInfo info = mEffects.get(i);
            String effectTitle = (i + 1) + ". " + info.mEffectName;
            effectList.add(effectTitle);
        }

        if (DEBUG) {
            Log.d(TAG, "onCreate() string list");

            for (String str : effectList) {
                Log.d(TAG, "\t Item=" + str);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, effectList);

        ListView list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(mItemClickListener);
    }

    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            String effectName = parent.getItemAtPosition(position).toString();

            EffectInfo info = getEffectInfo(effectName);
            int numOfShader = info.mShaderResIDs.length;

            ShaderContext context = ShaderContext.getInstance();
            context.setEffetName(effectName);
            context.setNumOfShaders(numOfShader);

            context.clearShaderInfos();

            String title = null;
            String savedFileName = null;
            for (int i = 0; i < numOfShader; i++) {
                title = info.mShaderTitle[i];
                savedFileName = ShaderUtils.getSavedFilePath(
                        EffectListActivity.this, info.mEffectName, title);
                context.setShaderInfo(info.mEffectName, info.mShaderTitle[i],
                        info.mShaderResIDs[i], savedFileName);
            }

            if (DEBUG) {
                Log.d(TAG, "onItemClick() item=" + effectName);
            }
            startActivity(info.mIntent);
        }
    };

    EffectInfo getEffectInfo(String effectName) {
        for (EffectInfo effectInfo : mEffects) {
            int index = effectName.indexOf(' ');
            String name = effectName.substring(index + 1);
            if (name.compareTo(effectInfo.mEffectName) == 0) {
                return effectInfo;
            }
        }

        return null;
    }

    @Override
    public int getLayoutID() {
        return R.layout.fragment_list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.effect_list_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.options:
            showOptionsDialog();
            return true;
        case R.id.deviceInfo:
            showDeviceInfoDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showOptionsDialog() {
        ShaderOptionsDialog dialog = new ShaderOptionsDialog();
        dialog.show(getFragmentManager(), "effect_options");
    }

    private void showDeviceInfoDialog() {
        DeviceInfoDialog dialog = new DeviceInfoDialog();
        dialog.show(getFragmentManager(), "effect_device_info");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        optionChanged();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    }
}
