package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.view.ViewGroup;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.systemui.Dependency;
import com.android.systemui.Dumpable;
import com.android.systemui.R$dimen;
import com.android.systemui.demomode.DemoMode;
import com.android.systemui.demomode.DemoModeController;
import com.android.systemui.moto.DualSimIconController;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.StatusIconDisplayable;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.statusbar.phone.StatusBarIconList;
import com.android.systemui.statusbar.phone.StatusBarSignalPolicy;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.tuner.TunerService;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class StatusBarIconControllerImpl extends StatusBarIconList implements TunerService.Tunable, ConfigurationController.ConfigurationListener, Dumpable, CommandQueue.Callbacks, StatusBarIconController, DemoMode {
    private Context mContext;
    private final CopyOnWriteArrayList<StatusBarIconController.IconManager> mIconGroups = new CopyOnWriteArrayList<>();
    private final ArraySet<String> mIconHideList = new ArraySet<>();

    private void loadDimens() {
    }

    public StatusBarIconControllerImpl(Context context, CommandQueue commandQueue, DemoModeController demoModeController) {
        super(context.getResources().getStringArray(17236151));
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).addCallback(this);
        this.mContext = context;
        loadDimens();
        commandQueue.addCallback((CommandQueue.Callbacks) this);
        ((TunerService) Dependency.get(TunerService.class)).addTunable(this, "icon_blacklist");
        demoModeController.addCallback((DemoMode) this);
    }

    public void addIconGroup(StatusBarIconController.IconManager iconManager) {
        this.mIconGroups.add(iconManager);
        ArrayList<StatusBarIconList.Slot> slots = getSlots();
        for (int i = 0; i < slots.size(); i++) {
            StatusBarIconList.Slot slot = slots.get(i);
            List<StatusBarIconHolder> holderListInViewOrder = slot.getHolderListInViewOrder();
            boolean contains = this.mIconHideList.contains(slot.getName());
            for (StatusBarIconHolder next : holderListInViewOrder) {
                next.getTag();
                iconManager.onIconAdded(getViewIndex(getSlotIndex(slot.getName()), next.getTag()), slot.getName(), contains, next);
            }
        }
    }

    public void removeIconGroup(StatusBarIconController.IconManager iconManager) {
        iconManager.destroy();
        this.mIconGroups.remove(iconManager);
    }

    public void onTuningChanged(String str, String str2) {
        if ("icon_blacklist".equals(str)) {
            this.mIconHideList.clear();
            this.mIconHideList.addAll(StatusBarIconController.getIconHideList(this.mContext, str2));
            ArrayList<StatusBarIconList.Slot> slots = getSlots();
            ArrayMap arrayMap = new ArrayMap();
            for (int size = slots.size() - 1; size >= 0; size--) {
                StatusBarIconList.Slot slot = slots.get(size);
                arrayMap.put(slot, slot.getHolderList());
                removeAllIconsForSlot(slot.getName());
            }
            for (int i = 0; i < slots.size(); i++) {
                StatusBarIconList.Slot slot2 = slots.get(i);
                List<StatusBarIconHolder> list = (List) arrayMap.get(slot2);
                if (list != null) {
                    for (StatusBarIconHolder icon : list) {
                        setIcon(getSlotIndex(slot2.getName()), icon);
                    }
                }
            }
        }
    }

    private void addSystemIcon(int i, StatusBarIconHolder statusBarIconHolder) {
        String slotName = getSlotName(i);
        this.mIconGroups.forEach(new StatusBarIconControllerImpl$$ExternalSyntheticLambda5(getViewIndex(i, statusBarIconHolder.getTag()), slotName, this.mIconHideList.contains(slotName), statusBarIconHolder));
    }

    public void setIcon(String str, int i, CharSequence charSequence) {
        int slotIndex = getSlotIndex(str);
        StatusBarIconHolder icon = getIcon(slotIndex, 0);
        if (icon == null) {
            setIcon(slotIndex, StatusBarIconHolder.fromIcon(new StatusBarIcon(UserHandle.SYSTEM, this.mContext.getPackageName(), Icon.createWithResource(this.mContext, i), 0, 0, charSequence)));
            return;
        }
        icon.getIcon().icon = Icon.createWithResource(this.mContext, i);
        icon.getIcon().contentDescription = charSequence;
        handleSet(slotIndex, icon);
    }

    public void setSignalIcon(String str, StatusBarSignalPolicy.WifiIconState wifiIconState) {
        int slotIndex = getSlotIndex(str);
        if (wifiIconState == null) {
            removeIcon(slotIndex, 0);
            return;
        }
        StatusBarIconHolder icon = getIcon(slotIndex, 0);
        if (icon == null) {
            setIcon(slotIndex, StatusBarIconHolder.fromWifiIconState(wifiIconState));
            return;
        }
        icon.setWifiState(wifiIconState);
        handleSet(slotIndex, icon);
    }

    public void setMobileIcons(String str, List<StatusBarSignalPolicy.MobileIconState> list) {
        StatusBarIconList.Slot slot = getSlot(str);
        int slotIndex = getSlotIndex(str);
        if (!((DualSimIconController) Dependency.get(DualSimIconController.class)).getShowDualSimIcon() || list.size() < 2) {
            Collections.reverse(list);
            for (StatusBarSignalPolicy.MobileIconState next : list) {
                StatusBarIconHolder holderForTag = slot.getHolderForTag(next.subId);
                if (holderForTag == null) {
                    setIcon(slotIndex, StatusBarIconHolder.fromMobileIconState(next));
                } else {
                    holderForTag.setMobileState(next);
                    handleSet(slotIndex, holderForTag);
                }
            }
            return;
        }
        StatusBarSignalPolicy.MobileIconState mobileIconState = null;
        for (StatusBarSignalPolicy.MobileIconState next2 : list) {
            if (mobileIconState == null) {
                next2.mIsDual = true;
                mobileIconState = next2;
            } else {
                mobileIconState.mNext = next2;
            }
        }
        int slotIndex2 = getSlotIndex(str);
        StatusBarIconHolder icon = getIcon(slotIndex2, 0);
        if (icon == null) {
            setIcon(slotIndex2, StatusBarIconHolder.fromMobileIconState(mobileIconState));
            return;
        }
        icon.setMobileState(mobileIconState);
        handleSet(slotIndex2, icon);
    }

    public void setCallStrengthIcons(String str, List<StatusBarSignalPolicy.CallIndicatorIconState> list) {
        StatusBarIconList.Slot slot = getSlot(str);
        int slotIndex = getSlotIndex(str);
        Collections.reverse(list);
        for (StatusBarSignalPolicy.CallIndicatorIconState next : list) {
            if (!next.isNoCalling) {
                StatusBarIconHolder holderForTag = slot.getHolderForTag(next.subId);
                if (holderForTag == null) {
                    setIcon(slotIndex, StatusBarIconHolder.fromCallIndicatorState(this.mContext, next));
                } else {
                    holderForTag.setIcon(new StatusBarIcon(UserHandle.SYSTEM, this.mContext.getPackageName(), Icon.createWithResource(this.mContext, next.callStrengthResId), 0, 0, next.callStrengthDescription));
                    setIcon(slotIndex, holderForTag);
                }
            }
            setIconVisibility(str, !next.isNoCalling, next.subId);
        }
    }

    public void setNoCallingIcons(String str, List<StatusBarSignalPolicy.CallIndicatorIconState> list) {
        StatusBarIconList.Slot slot = getSlot(str);
        int slotIndex = getSlotIndex(str);
        Collections.reverse(list);
        for (StatusBarSignalPolicy.CallIndicatorIconState next : list) {
            if (next.isNoCalling) {
                StatusBarIconHolder holderForTag = slot.getHolderForTag(next.subId);
                if (holderForTag == null) {
                    setIcon(slotIndex, StatusBarIconHolder.fromCallIndicatorState(this.mContext, next));
                } else {
                    holderForTag.setIcon(new StatusBarIcon(UserHandle.SYSTEM, this.mContext.getPackageName(), Icon.createWithResource(this.mContext, next.noCallingResId), 0, 0, next.noCallingDescription));
                    setIcon(slotIndex, holderForTag);
                }
            }
            setIconVisibility(str, next.isNoCalling, next.subId);
        }
    }

    public void setExternalIcon(String str) {
        this.mIconGroups.forEach(new StatusBarIconControllerImpl$$ExternalSyntheticLambda2(getViewIndex(getSlotIndex(str), 0), this.mContext.getResources().getDimensionPixelSize(R$dimen.status_bar_icon_drawing_size)));
    }

    public void setIcon(String str, StatusBarIcon statusBarIcon) {
        setIcon(getSlotIndex(str), statusBarIcon);
    }

    private void setIcon(int i, StatusBarIcon statusBarIcon) {
        String slotName = getSlotName(i);
        if (statusBarIcon == null) {
            removeAllIconsForSlot(slotName);
        } else {
            setIcon(i, StatusBarIconHolder.fromIcon(statusBarIcon));
        }
    }

    public void setIcon(int i, StatusBarIconHolder statusBarIconHolder) {
        boolean z = getIcon(i, statusBarIconHolder.getTag()) == null;
        super.setIcon(i, statusBarIconHolder);
        if (z) {
            addSystemIcon(i, statusBarIconHolder);
        } else {
            handleSet(i, statusBarIconHolder);
        }
    }

    public void setIconVisibility(String str, boolean z) {
        setIconVisibility(str, z, 0);
    }

    public void setIconVisibility(String str, boolean z, int i) {
        int slotIndex = getSlotIndex(str);
        StatusBarIconHolder icon = getIcon(slotIndex, i);
        if (icon != null && icon.isVisible() != z) {
            icon.setVisible(z);
            handleSet(slotIndex, icon);
        }
    }

    public void setIconAccessibilityLiveRegion(String str, int i) {
        StatusBarIconList.Slot slot = getSlot(str);
        if (slot.hasIconsInSlot()) {
            int slotIndex = getSlotIndex(str);
            for (StatusBarIconHolder tag : slot.getHolderListInViewOrder()) {
                this.mIconGroups.forEach(new StatusBarIconControllerImpl$$ExternalSyntheticLambda3(getViewIndex(slotIndex, tag.getTag()), i));
            }
        }
    }

    public void removeIcon(String str) {
        removeAllIconsForSlot(str);
    }

    public void removeIcon(String str, int i) {
        removeIcon(getSlotIndex(str), i);
    }

    public void removeAllIconsForSlot(String str) {
        StatusBarIconList.Slot slot = getSlot(str);
        if (slot.hasIconsInSlot()) {
            int slotIndex = getSlotIndex(str);
            for (StatusBarIconHolder next : slot.getHolderListInViewOrder()) {
                int viewIndex = getViewIndex(slotIndex, next.getTag());
                slot.removeForTag(next.getTag());
                this.mIconGroups.forEach(new StatusBarIconControllerImpl$$ExternalSyntheticLambda0(viewIndex));
            }
        }
    }

    public void removeIcon(int i, int i2) {
        if (getIcon(i, i2) != null) {
            super.removeIcon(i, i2);
            this.mIconGroups.forEach(new StatusBarIconControllerImpl$$ExternalSyntheticLambda1(getViewIndex(i, 0)));
        }
    }

    private void handleSet(int i, StatusBarIconHolder statusBarIconHolder) {
        this.mIconGroups.forEach(new StatusBarIconControllerImpl$$ExternalSyntheticLambda4(getViewIndex(i, statusBarIconHolder.getTag()), statusBarIconHolder));
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("StatusBarIconController state:");
        Iterator<StatusBarIconController.IconManager> it = this.mIconGroups.iterator();
        while (it.hasNext()) {
            StatusBarIconController.IconManager next = it.next();
            if (next.shouldLog()) {
                ViewGroup viewGroup = next.mGroup;
                int childCount = viewGroup.getChildCount();
                printWriter.println("  icon views: " + childCount);
                for (int i = 0; i < childCount; i++) {
                    printWriter.println("    [" + i + "] icon=" + ((StatusIconDisplayable) viewGroup.getChildAt(i)));
                }
            }
        }
        super.dump(printWriter);
    }

    public void onDemoModeStarted() {
        Iterator<StatusBarIconController.IconManager> it = this.mIconGroups.iterator();
        while (it.hasNext()) {
            StatusBarIconController.IconManager next = it.next();
            if (next.isDemoable()) {
                next.onDemoModeStarted();
            }
        }
    }

    public void onDemoModeFinished() {
        Iterator<StatusBarIconController.IconManager> it = this.mIconGroups.iterator();
        while (it.hasNext()) {
            StatusBarIconController.IconManager next = it.next();
            if (next.isDemoable()) {
                next.onDemoModeFinished();
            }
        }
    }

    public void dispatchDemoCommand(String str, Bundle bundle) {
        Iterator<StatusBarIconController.IconManager> it = this.mIconGroups.iterator();
        while (it.hasNext()) {
            StatusBarIconController.IconManager next = it.next();
            if (next.isDemoable()) {
                next.dispatchDemoCommand(str, bundle);
            }
        }
    }

    public List<String> demoCommands() {
        ArrayList arrayList = new ArrayList();
        arrayList.add("status");
        return arrayList;
    }

    public void onDensityOrFontScaleChanged() {
        loadDimens();
    }
}
