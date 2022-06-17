package com.android.p011wm.shell.nano;

import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.MessageNano;
import java.io.IOException;

/* renamed from: com.android.wm.shell.nano.WmShellTraceProto */
public final class WmShellTraceProto extends MessageNano {
    public boolean testValue;

    public WmShellTraceProto() {
        clear();
    }

    public WmShellTraceProto clear() {
        this.testValue = false;
        this.cachedSize = -1;
        return this;
    }

    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        boolean z = this.testValue;
        if (z) {
            codedOutputByteBufferNano.writeBool(1, z);
        }
        super.writeTo(codedOutputByteBufferNano);
    }

    /* access modifiers changed from: protected */
    public int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        boolean z = this.testValue;
        return z ? computeSerializedSize + CodedOutputByteBufferNano.computeBoolSize(1, z) : computeSerializedSize;
    }
}
