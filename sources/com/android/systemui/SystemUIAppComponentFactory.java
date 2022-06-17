package com.android.systemui;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.core.app.AppComponentFactory;
import com.android.systemui.dagger.ContextComponentHelper;
import com.android.systemui.dagger.SysUIComponent;
import java.lang.reflect.InvocationTargetException;

public class SystemUIAppComponentFactory extends AppComponentFactory {
    public ContextComponentHelper mComponentHelper;

    public interface ContextAvailableCallback {
        void onContextAvailable(Context context);
    }

    public interface ContextInitializer {
        void setContextAvailableCallback(ContextAvailableCallback contextAvailableCallback);
    }

    public Application instantiateApplicationCompat(ClassLoader classLoader, String str) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        Application instantiateApplicationCompat = super.instantiateApplicationCompat(classLoader, str);
        if (instantiateApplicationCompat instanceof ContextInitializer) {
            ((ContextInitializer) instantiateApplicationCompat).setContextAvailableCallback(new SystemUIAppComponentFactory$$ExternalSyntheticLambda1(this));
        }
        return instantiateApplicationCompat;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$instantiateApplicationCompat$0(Context context) {
        SystemUIFactory.createFromConfig(context);
        SystemUIFactory.getInstance().getSysUIComponent().inject(this);
    }

    public ContentProvider instantiateProviderCompat(ClassLoader classLoader, String str) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        ContentProvider instantiateProviderCompat = super.instantiateProviderCompat(classLoader, str);
        if (instantiateProviderCompat instanceof ContextInitializer) {
            ((ContextInitializer) instantiateProviderCompat).setContextAvailableCallback(new SystemUIAppComponentFactory$$ExternalSyntheticLambda0(instantiateProviderCompat));
        }
        return instantiateProviderCompat;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$instantiateProviderCompat$1(ContentProvider contentProvider, Context context) {
        SystemUIFactory.createFromConfig(context);
        SysUIComponent sysUIComponent = SystemUIFactory.getInstance().getSysUIComponent();
        try {
            sysUIComponent.getClass().getMethod("inject", new Class[]{contentProvider.getClass()}).invoke(sysUIComponent, new Object[]{contentProvider});
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            Log.w("AppComponentFactory", "No injector for class: " + contentProvider.getClass(), e);
        }
    }

    public Activity instantiateActivityCompat(ClassLoader classLoader, String str, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        if (this.mComponentHelper == null) {
            SystemUIFactory.getInstance().getSysUIComponent().inject(this);
        }
        Activity resolveActivity = this.mComponentHelper.resolveActivity(str);
        if (resolveActivity != null) {
            return resolveActivity;
        }
        return super.instantiateActivityCompat(classLoader, str, intent);
    }

    public Service instantiateServiceCompat(ClassLoader classLoader, String str, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        if (SystemUIFactory.getInstance() != null) {
            if (this.mComponentHelper == null) {
                SystemUIFactory.getInstance().getSysUIComponent().inject(this);
            }
            Service resolveService = this.mComponentHelper.resolveService(str);
            if (resolveService != null) {
                return resolveService;
            }
        }
        return super.instantiateServiceCompat(classLoader, str, intent);
    }

    public BroadcastReceiver instantiateReceiverCompat(ClassLoader classLoader, String str, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        if (this.mComponentHelper == null) {
            SystemUIFactory.getInstance().getSysUIComponent().inject(this);
        }
        BroadcastReceiver resolveBroadcastReceiver = this.mComponentHelper.resolveBroadcastReceiver(str);
        if (resolveBroadcastReceiver != null) {
            return resolveBroadcastReceiver;
        }
        return super.instantiateReceiverCompat(classLoader, str, intent);
    }
}
