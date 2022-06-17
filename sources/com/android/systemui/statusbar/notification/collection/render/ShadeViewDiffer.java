package com.android.systemui.statusbar.notification.collection.render;

import android.view.View;
import java.util.LinkedHashMap;
import java.util.Map;
import kotlin.TuplesKt;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: ShadeViewDiffer.kt */
public final class ShadeViewDiffer {
    @NotNull
    private final ShadeViewDifferLogger logger;
    @NotNull
    private final Map<NodeController, ShadeNode> nodes;
    @NotNull
    private final ShadeNode rootNode;
    @NotNull
    private final Map<View, ShadeNode> views = new LinkedHashMap();

    public ShadeViewDiffer(@NotNull NodeController nodeController, @NotNull ShadeViewDifferLogger shadeViewDifferLogger) {
        Intrinsics.checkNotNullParameter(nodeController, "rootController");
        Intrinsics.checkNotNullParameter(shadeViewDifferLogger, "logger");
        this.logger = shadeViewDifferLogger;
        ShadeNode shadeNode = new ShadeNode(nodeController);
        this.rootNode = shadeNode;
        this.nodes = MapsKt__MapsKt.mutableMapOf(TuplesKt.m104to(nodeController, shadeNode));
    }

    public final void applySpec(@NotNull NodeSpec nodeSpec) {
        Intrinsics.checkNotNullParameter(nodeSpec, "spec");
        Map<NodeController, NodeSpec> treeToMap = treeToMap(nodeSpec);
        if (Intrinsics.areEqual((Object) nodeSpec.getController(), (Object) this.rootNode.getController())) {
            detachChildren(this.rootNode, treeToMap);
            attachChildren(this.rootNode, treeToMap);
            return;
        }
        throw new IllegalArgumentException("Tree root " + nodeSpec.getController().getNodeLabel() + " does not match own root at " + this.rootNode.getLabel());
    }

    private final void detachChildren(ShadeNode shadeNode, Map<NodeController, ? extends NodeSpec> map) {
        NodeSpec nodeSpec = (NodeSpec) map.get(shadeNode.getController());
        int childCount = shadeNode.getChildCount() - 1;
        if (childCount >= 0) {
            while (true) {
                int i = childCount - 1;
                ShadeNode shadeNode2 = this.views.get(shadeNode.getChildAt(childCount));
                if (shadeNode2 != null) {
                    maybeDetachChild(shadeNode, nodeSpec, shadeNode2, (NodeSpec) map.get(shadeNode2.getController()));
                    if (shadeNode2.getController().getChildCount() > 0) {
                        detachChildren(shadeNode2, map);
                    }
                }
                if (i >= 0) {
                    childCount = i;
                } else {
                    return;
                }
            }
        }
    }

    private final void maybeDetachChild(ShadeNode shadeNode, NodeSpec nodeSpec, ShadeNode shadeNode2, NodeSpec nodeSpec2) {
        String str;
        NodeSpec parent = nodeSpec2 == null ? null : nodeSpec2.getParent();
        ShadeNode node = parent == null ? null : getNode(parent);
        if (!Intrinsics.areEqual((Object) node, (Object) shadeNode)) {
            boolean z = node == null;
            if (z) {
                this.nodes.remove(shadeNode2.getController());
                this.views.remove(shadeNode2.getController().getView());
            }
            if (!z || nodeSpec != null) {
                ShadeViewDifferLogger shadeViewDifferLogger = this.logger;
                String label = shadeNode2.getLabel();
                boolean z2 = !z;
                String label2 = shadeNode.getLabel();
                if (node == null) {
                    str = null;
                } else {
                    str = node.getLabel();
                }
                shadeViewDifferLogger.logDetachingChild(label, z2, label2, str);
                shadeNode.removeChild(shadeNode2, !z);
                shadeNode2.setParent((ShadeNode) null);
                return;
            }
            this.logger.logSkippingDetach(shadeNode2.getLabel(), shadeNode.getLabel());
        }
    }

    private final void attachChildren(ShadeNode shadeNode, Map<NodeController, ? extends NodeSpec> map) {
        NodeSpec nodeSpec = (NodeSpec) map.get(shadeNode.getController());
        if (nodeSpec != null) {
            int i = 0;
            for (NodeSpec next : nodeSpec.getChildren()) {
                int i2 = i + 1;
                View childAt = shadeNode.getChildAt(i);
                ShadeNode node = getNode(next);
                if (!Intrinsics.areEqual((Object) node.getView(), (Object) childAt)) {
                    ShadeNode parent = node.getParent();
                    if (parent == null) {
                        this.logger.logAttachingChild(node.getLabel(), shadeNode.getLabel());
                        shadeNode.addChildAt(node, i);
                        node.setParent(shadeNode);
                    } else if (Intrinsics.areEqual((Object) parent, (Object) shadeNode)) {
                        this.logger.logMovingChild(node.getLabel(), shadeNode.getLabel(), i);
                        shadeNode.moveChildTo(node, i);
                    } else {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Child ");
                        sb.append(node.getLabel());
                        sb.append(" should have parent ");
                        sb.append(shadeNode.getLabel());
                        sb.append(" but is actually ");
                        ShadeNode parent2 = node.getParent();
                        sb.append(parent2 == null ? null : parent2.getLabel());
                        throw new IllegalStateException(sb.toString());
                    }
                }
                if (!next.getChildren().isEmpty()) {
                    attachChildren(node, map);
                }
                i = i2;
            }
            return;
        }
        throw new IllegalStateException("Required value was null.".toString());
    }

    private final ShadeNode getNode(NodeSpec nodeSpec) {
        ShadeNode shadeNode = this.nodes.get(nodeSpec.getController());
        if (shadeNode != null) {
            return shadeNode;
        }
        ShadeNode shadeNode2 = new ShadeNode(nodeSpec.getController());
        this.nodes.put(shadeNode2.getController(), shadeNode2);
        this.views.put(shadeNode2.getView(), shadeNode2);
        return shadeNode2;
    }

    private final Map<NodeController, NodeSpec> treeToMap(NodeSpec nodeSpec) {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        try {
            registerNodes(nodeSpec, linkedHashMap);
            return linkedHashMap;
        } catch (DuplicateNodeException e) {
            this.logger.logDuplicateNodeInTree(nodeSpec, e);
            throw e;
        }
    }

    private final void registerNodes(NodeSpec nodeSpec, Map<NodeController, NodeSpec> map) {
        if (!map.containsKey(nodeSpec.getController())) {
            map.put(nodeSpec.getController(), nodeSpec);
            if (!nodeSpec.getChildren().isEmpty()) {
                for (NodeSpec registerNodes : nodeSpec.getChildren()) {
                    registerNodes(registerNodes, map);
                }
                return;
            }
            return;
        }
        throw new DuplicateNodeException("Node " + nodeSpec.getController().getNodeLabel() + " appears more than once");
    }
}
