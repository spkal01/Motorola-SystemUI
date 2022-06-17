package com.android.systemui.tracing.nano;

import com.android.p011wm.shell.nano.WmShellTraceProto;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.MessageNano;
import java.io.IOException;

public final class SystemUiTraceProto extends MessageNano {
    public EdgeBackGestureHandlerProto edgeBackGestureHandler;
    public WmShellTraceProto wmShell;

    public SystemUiTraceProto() {
        clear();
    }

    public SystemUiTraceProto clear() {
        this.edgeBackGestureHandler = null;
        this.wmShell = null;
        this.cachedSize = -1;
        return this;
    }

    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        EdgeBackGestureHandlerProto edgeBackGestureHandlerProto = this.edgeBackGestureHandler;
        if (edgeBackGestureHandlerProto != null) {
            codedOutputByteBufferNano.writeMessage(1, edgeBackGestureHandlerProto);
        }
        WmShellTraceProto wmShellTraceProto = this.wmShell;
        if (wmShellTraceProto != null) {
            codedOutputByteBufferNano.writeMessage(2, wmShellTraceProto);
        }
        super.writeTo(codedOutputByteBufferNano);
    }

    /* access modifiers changed from: protected */
    public int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        EdgeBackGestureHandlerProto edgeBackGestureHandlerProto = this.edgeBackGestureHandler;
        if (edgeBackGestureHandlerProto != null) {
            computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(1, edgeBackGestureHandlerProto);
        }
        WmShellTraceProto wmShellTraceProto = this.wmShell;
        return wmShellTraceProto != null ? computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(2, wmShellTraceProto) : computeSerializedSize;
    }
}
