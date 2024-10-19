package me.veryyoung.game2048;

/* loaded from: classes.dex */
public class Cell {
    private int x;
    private int y;

    public Cell(int i, int i2) {
        /*  JADX ERROR: Method code generation error
            java.lang.NullPointerException: Cannot invoke "jadx.core.dex.nodes.IContainer.get(jadx.api.plugins.input.data.attributes.IJadxAttrType)" because "cont" is null
            	at jadx.core.codegen.RegionGen.declareVars(RegionGen.java:70)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:65)
            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:297)
            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:276)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:406)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:335)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$3(ClassGen.java:301)
            	at java.base/java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:184)
            	at java.base/java.util.ArrayList.forEach(ArrayList.java:1597)
            	at java.base/java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.base/java.util.stream.Sink$ChainedReference.end(Sink.java:261)
            	at java.base/java.util.stream.ReferencePipeline$7$1FlatMap.end(ReferencePipeline.java:285)
            	at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:571)
            	at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:560)
            	at java.base/java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:151)
            	at java.base/java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:174)
            	at java.base/java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:265)
            	at java.base/java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:636)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:297)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:286)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:270)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:161)
            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:103)
            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:45)
            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:34)
            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:22)
            	at jadx.core.ProcessClass.process(ProcessClass.java:80)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:118)
            	at jadx.core.dex.nodes.ClassNode.generateClassCode(ClassNode.java:400)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:388)
            	at jadx.core.dex.nodes.ClassNode.getCode(ClassNode.java:338)
            */
        /*
            this = this;
            r0.<init>()
            r0.x = r1
            r0.y = r2
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: me.veryyoung.game2048.Cell.<init>(int, int):void");
    }

    public int getX() {
        /*  JADX ERROR: Method code generation error
            java.lang.NullPointerException: Cannot invoke "jadx.core.dex.nodes.IContainer.get(jadx.api.plugins.input.data.attributes.IJadxAttrType)" because "cont" is null
            	at jadx.core.codegen.RegionGen.declareVars(RegionGen.java:70)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:65)
            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:297)
            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:276)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:406)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:335)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$3(ClassGen.java:301)
            	at java.base/java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:184)
            	at java.base/java.util.ArrayList.forEach(ArrayList.java:1597)
            	at java.base/java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.base/java.util.stream.Sink$ChainedReference.end(Sink.java:261)
            	at java.base/java.util.stream.ReferencePipeline$7$1FlatMap.end(ReferencePipeline.java:285)
            	at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:571)
            	at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:560)
            	at java.base/java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:151)
            	at java.base/java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:174)
            	at java.base/java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:265)
            	at java.base/java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:636)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:297)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:286)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:270)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:161)
            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:103)
            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:45)
            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:34)
            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:22)
            	at jadx.core.ProcessClass.process(ProcessClass.java:80)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:118)
            	at jadx.core.dex.nodes.ClassNode.generateClassCode(ClassNode.java:400)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:388)
            	at jadx.core.dex.nodes.ClassNode.getCode(ClassNode.java:338)
            */
        /*
            this = this;
            int r0 = r1.x
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: me.veryyoung.game2048.Cell.getX():int");
    }

    public int getY() {
        /*  JADX ERROR: Method code generation error
            java.lang.NullPointerException: Cannot invoke "jadx.core.dex.nodes.IContainer.get(jadx.api.plugins.input.data.attributes.IJadxAttrType)" because "cont" is null
            	at jadx.core.codegen.RegionGen.declareVars(RegionGen.java:70)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:65)
            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:297)
            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:276)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:406)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:335)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$3(ClassGen.java:301)
            	at java.base/java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:184)
            	at java.base/java.util.ArrayList.forEach(ArrayList.java:1597)
            	at java.base/java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.base/java.util.stream.Sink$ChainedReference.end(Sink.java:261)
            	at java.base/java.util.stream.ReferencePipeline$7$1FlatMap.end(ReferencePipeline.java:285)
            	at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:571)
            	at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:560)
            	at java.base/java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:151)
            	at java.base/java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:174)
            	at java.base/java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:265)
            	at java.base/java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:636)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:297)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:286)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:270)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:161)
            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:103)
            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:45)
            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:34)
            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:22)
            	at jadx.core.ProcessClass.process(ProcessClass.java:80)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:118)
            	at jadx.core.dex.nodes.ClassNode.generateClassCode(ClassNode.java:400)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:388)
            	at jadx.core.dex.nodes.ClassNode.getCode(ClassNode.java:338)
            */
        /*
            this = this;
            int r0 = r1.y
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: me.veryyoung.game2048.Cell.getY():int");
    }

    public void setX(int i) {
        /*  JADX ERROR: Method code generation error
            java.lang.NullPointerException: Cannot invoke "jadx.core.dex.nodes.IContainer.get(jadx.api.plugins.input.data.attributes.IJadxAttrType)" because "cont" is null
            	at jadx.core.codegen.RegionGen.declareVars(RegionGen.java:70)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:65)
            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:297)
            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:276)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:406)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:335)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$3(ClassGen.java:301)
            	at java.base/java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:184)
            	at java.base/java.util.ArrayList.forEach(ArrayList.java:1597)
            	at java.base/java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.base/java.util.stream.Sink$ChainedReference.end(Sink.java:261)
            	at java.base/java.util.stream.ReferencePipeline$7$1FlatMap.end(ReferencePipeline.java:285)
            	at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:571)
            	at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:560)
            	at java.base/java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:151)
            	at java.base/java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:174)
            	at java.base/java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:265)
            	at java.base/java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:636)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:297)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:286)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:270)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:161)
            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:103)
            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:45)
            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:34)
            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:22)
            	at jadx.core.ProcessClass.process(ProcessClass.java:80)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:118)
            	at jadx.core.dex.nodes.ClassNode.generateClassCode(ClassNode.java:400)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:388)
            	at jadx.core.dex.nodes.ClassNode.getCode(ClassNode.java:338)
            */
        /*
            this = this;
            r0.x = r1
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: me.veryyoung.game2048.Cell.setX(int):void");
    }

    public void setY(int i) {
        /*  JADX ERROR: Method code generation error
            java.lang.NullPointerException: Cannot invoke "jadx.core.dex.nodes.IContainer.get(jadx.api.plugins.input.data.attributes.IJadxAttrType)" because "cont" is null
            	at jadx.core.codegen.RegionGen.declareVars(RegionGen.java:70)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:65)
            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:297)
            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:276)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:406)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:335)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$3(ClassGen.java:301)
            	at java.base/java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:184)
            	at java.base/java.util.ArrayList.forEach(ArrayList.java:1597)
            	at java.base/java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.base/java.util.stream.Sink$ChainedReference.end(Sink.java:261)
            	at java.base/java.util.stream.ReferencePipeline$7$1FlatMap.end(ReferencePipeline.java:285)
            	at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:571)
            	at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:560)
            	at java.base/java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:151)
            	at java.base/java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:174)
            	at java.base/java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:265)
            	at java.base/java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:636)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:297)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:286)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:270)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:161)
            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:103)
            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:45)
            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:34)
            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:22)
            	at jadx.core.ProcessClass.process(ProcessClass.java:80)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:118)
            	at jadx.core.dex.nodes.ClassNode.generateClassCode(ClassNode.java:400)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:388)
            	at jadx.core.dex.nodes.ClassNode.getCode(ClassNode.java:338)
            */
        /*
            this = this;
            r0.y = r1
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: me.veryyoung.game2048.Cell.setY(int):void");
    }
}
