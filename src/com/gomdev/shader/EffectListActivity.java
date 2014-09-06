package com.gomdev.shader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.gomdev.shader.R;
import com.gomdev.shader.basic.BasicConfig;
import com.gomdev.shader.instancedRendering.IRConfig;
import com.gomdev.shader.instancedRendering2.IR2Config;
import com.gomdev.shader.occlusionQuery.OQConfig;
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
import android.widget.FrameLayout;
import android.widget.ListView;

public class EffectListActivity extends Activity implements DialogListener {
    private static final String CLASS = "EffectListActivity";
    private static final String TAG = GLESConfig.TAG + " " + CLASS;
    private static final boolean DEBUG = GLESConfig.DEBUG;

    static final int GET_EXTENSIONS = 100;

    private GLSurfaceView mView;
    private DummyRenderer mRenderer;
    private Map<String, EffectInfo> mEffectMap = new HashMap<String, EffectInfo>();

    protected Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case GET_EXTENSIONS:
                FrameLayout layout = (FrameLayout) findViewById(R.id.layout);
                layout.removeView(mView);
                break;
            default:
            }
        }

    };

    class EffectInfo {
        Intent mIntent = null;
        int[] mShaderResIDs;
        String[] mShaderTitle;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list);

        EffectContext.newInstance();

        setupGLRendererForExtensions();

        optionChanged();
    }

    private void setupGLRendererForExtensions() {
        mRenderer = new DummyRenderer();
        mRenderer.setHandler(mHandler);
        mView = new GLSurfaceView(this);
        mView.setEGLContextClientVersion(2);
        mView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mView.setRenderer(mRenderer);
        mView.setZOrderOnTop(true);
        mView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        FrameLayout layout = (FrameLayout) findViewById(R.id.layout);
        layout.addView(mView);
    }

    private void optionChanged() {
        setupEffectInfos();
        makeEffectList();
    }

    private void setupEffectInfos() {
        mEffectMap.clear();

        Version version = GLESContext.getInstance().getVersion();

        setupBasic(version);
        setupPVL(version);
        setupOQ(version);
        setupTexture(version);
        setupIR(version);
        setupIR2(version);
        setupWhitehole(version);

        if (DEBUG) {
            Log.d(TAG, "onCreate() map<String, EffectInfo>");
            Set<Entry<String, EffectInfo>> entrySet = mEffectMap.entrySet();

            for (Entry<String, EffectInfo> entry : entrySet) {
                Log.d(TAG, "\t Item=" + entry.getKey());
            }
        }
    }

    private void setupIR(Version version) {
        EffectInfo info = new EffectInfo();
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

        mEffectMap.put(IRConfig.EFFECT_NAME, info);
    }

    private void setupIR2(Version version) {
        EffectInfo info = new EffectInfo();
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

        mEffectMap.put(IR2Config.EFFECT_NAME, info);
    }

    private void setupTexture(Version version) {
        EffectInfo info = new EffectInfo();
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

        mEffectMap.put(TextureConfig.EFFECT_NAME, info);
    }

    private void setupBasic(Version version) {
        EffectInfo info = new EffectInfo();
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

        mEffectMap.put(BasicConfig.EFFECT_NAME, info);
    }
    
    private void setupPVL(Version version) {
        EffectInfo info = new EffectInfo();
        info.mIntent = new Intent(this,
                com.gomdev.shader.perVertexLighting.PVLActivity.class);
        if (version == Version.GLES_20) {
            info.mShaderResIDs = new int[] {
                    R.raw.pvl_20_vs,
                    R.raw.pvl_20_fs,
                    R.raw.pvl_light_20_vs,
                    R.raw.pvl_light_20_fs
            };
        } else {
            info.mShaderResIDs = new int[] {
                    R.raw.pvl_30_vs,
                    R.raw.pvl_30_fs,
                    R.raw.pvl_light_30_vs,
                    R.raw.pvl_light_30_fs
            };
        }
        info.mShaderTitle = new String[] {
                "Per Vertex Lighting VS",
                "Per Vertex Lighting FS",
                "Per Vertex Lighting Light VS",
                "Per Vertex Lighting Light FS"
        };

        mEffectMap.put(PVLConfig.EFFECT_NAME, info);
    }

    private void setupOQ(Version version) {
        EffectInfo info = new EffectInfo();
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

        mEffectMap.put(OQConfig.EFFECT_NAME, info);
    }

    private void setupWhitehole(Version version) {
        EffectInfo info = new EffectInfo();
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

        mEffectMap.put(WhiteholeConfig.EFFECT_NAME, info);
    }

    private void makeEffectList() {
        ArrayList<String> effectList = new ArrayList<String>();
        Set<String> effectSet = mEffectMap.keySet();

        for (String str : effectSet) {
            effectList.add(str);
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

            EffectInfo info = mEffectMap.get(effectName);
            int numOfShader = info.mShaderResIDs.length;

            EffectContext context = EffectContext.getInstance();
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

    @Override
    protected void onResume() {
        super.onResume();
        mView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mView.onPause();
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
