package net.mehvahdjukaar.goated.mixins.forge;

import net.mehvahdjukaar.moonlight.api.misc.OptionalMixin;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

import java.util.List;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String s) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    //TODO: change
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        ClassNode node;
        try {
            node = MixinService.getService().getBytecodeProvider().getClassNode(mixinClassName);
        } catch (Exception e) {
            return false;
        }
        if (node != null && node.invisibleAnnotations != null) {
            for (AnnotationNode annotationNode : node.invisibleAnnotations) {
                if (annotationNode.desc.equals("L" + OptionalMixin.class.getName().replace('.', '/') + ";")) {
                    // Access the annotation's values and attributes
                    List<Object> values = annotationNode.values;
                    boolean needsClass = values.size() < 4 || (Boolean) values.get(3);
                    try {
                        String name = values.get(1).toString();
                        MixinService.getService().getBytecodeProvider().getClassNode(name);
                        if (!needsClass) return false;
                    } catch (Exception e) {
                        // not present
                        if (needsClass) return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> set, Set<String> set1) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

    }

    @Override
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

    }
}
