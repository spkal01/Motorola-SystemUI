package com.motorola.rro;

import java.io.PrintWriter;

public interface RROsController {
    void dump(PrintWriter printWriter);

    String getRROPkg();

    boolean isVisibleCarrier();
}
