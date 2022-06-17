package kotlin.collections;

import java.util.Map;
import kotlin.jvm.internal.markers.KMappedMarker;
import org.jetbrains.annotations.NotNull;

/* compiled from: MapWithDefault.kt */
interface MapWithDefault<K, V> extends Map<K, V>, KMappedMarker {
    @NotNull
    Map<K, V> getMap();

    V getOrImplicitDefault(K k);
}
