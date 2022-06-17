package com.android.launcher3.icons;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

public class GzipCompression {
    public static String decompress(byte[] bArr) {
        BufferedReader bufferedReader;
        StringBuilder sb = new StringBuilder();
        if (bArr == null || bArr.length == 0) {
            return "";
        }
        if (isCompressed(bArr)) {
            GZIPInputStream gZIPInputStream = null;
            try {
                GZIPInputStream gZIPInputStream2 = new GZIPInputStream(new ByteArrayInputStream(bArr));
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(gZIPInputStream2, "UTF-8"));
                    while (true) {
                        try {
                            String readLine = bufferedReader.readLine();
                            if (readLine == null) {
                                break;
                            }
                            sb.append(readLine);
                        } catch (IOException unused) {
                            gZIPInputStream = gZIPInputStream2;
                            closeQuietly(gZIPInputStream);
                            closeQuietly(bufferedReader);
                            return sb.toString();
                        } catch (Throwable th) {
                            th = th;
                            gZIPInputStream = gZIPInputStream2;
                            closeQuietly(gZIPInputStream);
                            closeQuietly(bufferedReader);
                            throw th;
                        }
                    }
                    closeQuietly(gZIPInputStream2);
                } catch (IOException unused2) {
                    bufferedReader = null;
                    gZIPInputStream = gZIPInputStream2;
                    closeQuietly(gZIPInputStream);
                    closeQuietly(bufferedReader);
                    return sb.toString();
                } catch (Throwable th2) {
                    th = th2;
                    bufferedReader = null;
                    gZIPInputStream = gZIPInputStream2;
                    closeQuietly(gZIPInputStream);
                    closeQuietly(bufferedReader);
                    throw th;
                }
            } catch (IOException unused3) {
                bufferedReader = null;
                closeQuietly(gZIPInputStream);
                closeQuietly(bufferedReader);
                return sb.toString();
            } catch (Throwable th3) {
                th = th3;
                bufferedReader = null;
                closeQuietly(gZIPInputStream);
                closeQuietly(bufferedReader);
                throw th;
            }
            closeQuietly(bufferedReader);
        } else {
            sb.append(bArr);
        }
        return sb.toString();
    }

    public static boolean isCompressed(byte[] bArr) {
        return bArr[0] == 31 && bArr[1] == -117;
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception unused) {
            }
        }
    }
}
