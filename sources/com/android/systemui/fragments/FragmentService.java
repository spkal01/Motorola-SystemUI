package com.android.systemui.fragments;

import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Handler;
import android.util.ArrayMap;
import android.view.View;
import com.android.systemui.Dumpable;
import com.android.systemui.p006qs.QSFragment;
import com.android.systemui.statusbar.phone.CollapsedStatusBarFragment;
import com.android.systemui.statusbar.policy.ConfigurationController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Method;

public class FragmentService implements Dumpable {
    private ConfigurationController.ConfigurationListener mConfigurationListener = new ConfigurationController.ConfigurationListener() {
        public void onConfigChanged(Configuration configuration) {
            for (FragmentHostState sendConfigurationChange : FragmentService.this.mHosts.values()) {
                sendConfigurationChange.sendConfigurationChange(configuration);
            }
        }
    };
    private final FragmentCreator mFragmentCreator;
    /* access modifiers changed from: private */
    public final Handler mHandler = new Handler();
    /* access modifiers changed from: private */
    public final ArrayMap<View, FragmentHostState> mHosts = new ArrayMap<>();
    private final ArrayMap<String, Method> mInjectionMap = new ArrayMap<>();

    public interface FragmentCreator {

        public interface Factory {
            FragmentCreator build();
        }

        CollapsedStatusBarFragment createCollapsedStatusBarFragment();

        QSFragment createQSFragment();
    }

    public FragmentService(FragmentCreator.Factory factory, ConfigurationController configurationController) {
        this.mFragmentCreator = factory.build();
        initInjectionMap();
        configurationController.addCallback(this.mConfigurationListener);
    }

    /* access modifiers changed from: package-private */
    public ArrayMap<String, Method> getInjectionMap() {
        return this.mInjectionMap;
    }

    /* access modifiers changed from: package-private */
    public FragmentCreator getFragmentCreator() {
        return this.mFragmentCreator;
    }

    private void initInjectionMap() {
        for (Method method : FragmentCreator.class.getDeclaredMethods()) {
            if (Fragment.class.isAssignableFrom(method.getReturnType()) && (method.getModifiers() & 1) != 0) {
                this.mInjectionMap.put(method.getReturnType().getName(), method);
            }
        }
    }

    public FragmentHostManager getFragmentHostManager(View view) {
        View rootView = view.getRootView();
        FragmentHostState fragmentHostState = this.mHosts.get(rootView);
        if (fragmentHostState == null) {
            fragmentHostState = new FragmentHostState(rootView);
            this.mHosts.put(rootView, fragmentHostState);
        }
        return fragmentHostState.getFragmentHostManager();
    }

    public void destroyAll() {
        for (FragmentHostState access$100 : this.mHosts.values()) {
            access$100.mFragmentHostManager.destroy();
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("Dumping fragments:");
        for (FragmentHostState access$100 : this.mHosts.values()) {
            access$100.mFragmentHostManager.getFragmentManager().dump("  ", fileDescriptor, printWriter, strArr);
        }
    }

    private class FragmentHostState {
        /* access modifiers changed from: private */
        public FragmentHostManager mFragmentHostManager;
        private final View mView;

        public FragmentHostState(View view) {
            this.mView = view;
            this.mFragmentHostManager = new FragmentHostManager(FragmentService.this, view);
        }

        public void sendConfigurationChange(Configuration configuration) {
            FragmentService.this.mHandler.post(new FragmentService$FragmentHostState$$ExternalSyntheticLambda0(this, configuration));
        }

        public FragmentHostManager getFragmentHostManager() {
            return this.mFragmentHostManager;
        }

        /* access modifiers changed from: private */
        /* renamed from: handleSendConfigurationChange */
        public void lambda$sendConfigurationChange$0(Configuration configuration) {
            this.mFragmentHostManager.onConfigurationChanged(configuration);
        }
    }
}
