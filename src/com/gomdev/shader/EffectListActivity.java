package com.gomdev.shader;

import java.util.ArrayList;

import com.gomdev.shader.R;
import com.gomdev.shader.basic.BasicConfig;
import com.gomdev.shader.instancedRendering.IRConfig;
import com.gomdev.shader.instancedRendering2.IR2Config;
import com.gomdev.shader.occlusionQuery.OQConfig;
import com.gomdev.shader.perFragmentLighting.PFLConfig;
import com.gomdev.shader.perVertexLighting.PVLConfig;
import com.gomdev.shader.texture.TextureConfig;
import com.gomdev.shader.whitehole.WhiteholeConfig;
import com.gomdev.gles.GLESConfig;
import com.gomdev.gles.GLESContext;
import com.gomdev.gles.GLESConfig.Version;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
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
    private static final String CLASS = "EffectListActivity";
    private static final String TAG = GLESConfig.TAG + " " + CLASS;
    private static final boolean DEBUG = GLESConfig.DEBUG;

    static final int GET_EXTENSIONS = 100;

    private GLSurfaceView mView;
    private DummyRenderer mRenderer;
    private ArrayList<EffectInfo> mEffects = new ArrayList<EffectInfo>();

    protected Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case GET_EXTENSIONS:
                mView.setVisibility(View.INVISIBLE);
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

        setupGLRendererForExtensions();

        optionChanged();
    }

    private void setupGLRendererForExtensions() {
        mRenderer = new DummyRenderer();
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

        Version version = GLESContext.getInstance().getVersion();

        setupBasic(version);
        setupTexture(version);
        setupPVL(version);
        setupPFL(version);
        setupOQ(version);
        setupIR(version);
        setupIR2(version);
        setupWhitehole(version);

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
                    R.raw.ir_20_vs,
                    R.raw.ir_20_fs,
            };
        } else {
            info.mShaderResIDs = new int[] {
                    R.raw.ir_30_vs,
                    R.raw.ir_30_fs,
            };
        }
        info.mShaderTitle = new String[] {
                "IR VS",
                "IR FS",
        };

        mEffects.add(info);
    }

    private void setupIR2(Version version) {
        EffectInfo info = new EffectInfo();
        info.mEffectName = IR2Config.EFFECT_NAME;
        info.mIntent = new Intent(this,
                com.gomdev.shader.instancedRendering2.IR2Activity.class);

        if (version == Version.GLES_20) {
            info.mShaderResIDs = new int[] {
                    R.raw.ir2_20_vs,
                    R.raw.ir2_20_fs,
            };
        } else {
            info.mShaderResIDs = new int[] {
                    R.raw.ir2_30_vs,
                    R.raw.ir2_30_fs,
            };
        }
        info.mShaderTitle = new String[] {
                "IR2 VS",
                "IR2 FS",
        };

        mEffects.add(info);
    }

    private void setupTexture(Version version) {
        EffectInfo info = new EffectInfo();
        info.mEffectName = TextureConfig.EFFECT_NAME;
        info.mIntent = new Intent(this,
                com.gomdev.shader.texture.TextureActivity.class);

        if (version == Version.GLES_20) {
            info.mShaderResIDs = new int[] {
                    R.raw.texture_20_vs,
                    R.raw.texture_20_fs,
            };
        } else {
            info.mShaderResIDs = new int[] {
                    R.raw.texture_30_vs,
                    R.raw.texture_30_fs,
            };
        }
        info.mShaderTitle = new String[] {
                "Texture VS",
                "Texture FS",
        };

        mEffects.add(info);
    }

    private void setupBasic(Version version) {
        EffectInfo info = new EffectInfo();
        info.mEffectName = BasicConfig.EFFECT_NAME;
        info.mIntent = new Intent(this,
                com.gomdev.shader.basic.BasicActivity.class);
        if (version == Version.GLES_20) {
            info.mShaderResIDs = new int[] {
                    R.raw.basic_20_vs,
                    R.raw.basic_20_fs,
                    R.raw.basic_20_vs,
                    R.raw.basic_20_fs
            };
        } else {
            info.mShaderResIDs = new int[] {
                    R.raw.basic_30_vs,
                    R.raw.basic_30_fs
            };
        }
        info.mShaderTitle = new String[] {
                "Basic main VS",
                "Basic main FS",
                "Basic overlay VS",
                "Basic overlay FS"
        };

        mEffects.add(info);
    }

    private void setupPVL(Version version) {
        EffectInfo info = new EffectInfo();
        info.mEffectName = PVLConfig.EFFECT_NAME;
        info.mIntent = new Intent(this,
                com.gomdev.shader.perVertexLighting.PVLActivity.class);
        if (version == Version.GLES_20) {
            info.mShaderResIDs = new int[] {
                    R.raw.pvl_20_vs,
                    R.raw.pvl_20_fs
            };
        } else {
            info.mShaderResIDs = new int[] {
                    R.raw.pvl_30_vs,
                    R.raw.pvl_30_fs
            };
        }
        info.mShaderTitle = new String[] {
                "Per Vertex Lighting VS",
                "Per Vertex Lighting FS"
        };

        mEffects.add(info);
    }

    private void setupPFL(Version version) {
        EffectInfo info = new EffectInfo();
        info.mEffectName = PFLConfig.EFFECT_NAME;
        info.mIntent = new Intent(this,
                com.gomdev.shader.perFragmentLighting.PFLActivity.class);
        if (version == Version.GLES_20) {
            info.mShaderResIDs = new int[] {
                    R.raw.pfl_20_vs,
                    R.raw.pfl_20_fs
            };
        } else {
            info.mShaderResIDs = new int[] {
                    R.raw.pfl_30_vs,
                    R.raw.pfl_30_fs
            };
        }
        info.mShaderTitle = new String[] {
                "Per Fragment Lighting VS",
                "Per Fragment Lighting FS"
        };

        mEffects.add(info);
    }

    private void setupOQ(Version version) {
        EffectInfo info = new EffectInfo();
        info.mEffectName = OQConfig.EFFECT_NAME;
        info.mIntent = new Intent(this,
                com.gomdev.shader.occlusionQuery.OQActivity.class);
        if (version == Version.GLES_20) {
            info.mShaderResIDs = new int[] {
                    R.raw.oq_20_vs,
                    R.raw.oq_20_fs,
            };
        } else {
            info.mShaderResIDs = new int[] {
                    R.raw.oq_30_vs,
                    R.raw.oq_30_fs
            };
        }
        info.mShaderTitle = new String[] {
                "Occlusion Query VS",
                "Occlusion Query FS",
        };

        mEffects.add(info);
    }

    private void setupWhitehole(Version version) {
        EffectInfo info = new EffectInfo();
        info.mEffectName = WhiteholeConfig.EFFECT_NAME;
        info.mIntent = new Intent(this,
                com.gomdev.shader.whitehole.WhiteholeActivity.class);
        if (version == Version.GLES_20) {
            info.mShaderResIDs = new int[] {
                    R.raw.whitehole_20_vs,
                    R.raw.whitehole_20_fs,
                    R.raw.whitehole_20_vs,
                    R.raw.whitehole_20_fs
            };
        } else {
            info.mShaderResIDs = new int[] {
                    R.raw.whitehole_30_vs,
                    R.raw.whitehole_30_fs
            };
        }
        info.mShaderTitle = new String[] {
                "Whitehole main VS",
                "Whitehole main FS",
                "Whitehole overlay VS",
                "Whitehole overlay FS"
        };

        mEffects.add(info);
    }

    private void makeEffectList() {
        ArrayList<String> effectList = new ArrayList<String>();
        for (EffectInfo effectInfo : mEffects) {
            effectList.add(effectInfo.mEffectName);
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
                savedFileName = EffectUtils.getSavedFilePath(
                        EffectListActivity.this, title);
                context.setShaderInfo(info.mShaderTitle[i],
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
            if (effectName.compareTo(effectInfo.mEffectName) == 0) {
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
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mView.onResume();
    }

    @Override
    protected void onPause() {
        mView.onPause();
        super.onPause();

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
        EffectOptionsDialog dialog = new EffectOptionsDialog();
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
