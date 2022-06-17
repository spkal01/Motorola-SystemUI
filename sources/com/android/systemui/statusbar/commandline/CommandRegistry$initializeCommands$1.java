package com.android.systemui.statusbar.commandline;

import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* compiled from: CommandRegistry.kt */
final class CommandRegistry$initializeCommands$1 extends Lambda implements Function0<Command> {
    final /* synthetic */ CommandRegistry this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CommandRegistry$initializeCommands$1(CommandRegistry commandRegistry) {
        super(0);
        this.this$0 = commandRegistry;
    }

    @NotNull
    public final Command invoke() {
        return new PrefsCommand(this.this$0.getContext());
    }
}
