package me.veryyoung.game2048;

import android.view.MotionEvent;
import android.view.View;

/* loaded from: classes.dex */
public class InputListener implements View.OnTouchListener {
    private static final int MOVE_THRESHOLD = 250;
    private static final int RESET_STARTING = 10;
    private static final int SWIPE_MIN_DISTANCE = 0;
    private static final int SWIPE_THRESHOLD_VELOCITY = 25;
    private float lastdx;
    private float lastdy;
    MainView mView;
    private float previousX;
    private float previousY;
    private float startingX;
    private float startingY;
    private float x;
    private float y;
    private int previousDirection = 1;
    private int veryLastDirection = 1;
    private boolean hasMoved = false;

    private boolean inRange(float f, float f2, float f3) {
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
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 > 0) goto La
            int r1 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r1 > 0) goto La
            r1 = 1
            goto Lb
        La:
            r1 = 0
        Lb:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: me.veryyoung.game2048.InputListener.inRange(float, float, float):boolean");
    }

    public InputListener(MainView mainView) {
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
            r1.<init>()
            r0 = 1
            r1.previousDirection = r0
            r1.veryLastDirection = r0
            r0 = 0
            r1.hasMoved = r0
            r1.mView = r2
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: me.veryyoung.game2048.InputListener.<init>(me.veryyoung.game2048.MainView):void");
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
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
            Method dump skipped, instructions count: 560
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: me.veryyoung.game2048.InputListener.onTouch(android.view.View, android.view.MotionEvent):boolean");
    }

    private float pathMoved() {
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
            float r0 = r4.x
            float r1 = r4.startingX
            float r0 = r0 - r1
            float r1 = r4.x
            float r2 = r4.startingX
            float r1 = r1 - r2
            float r0 = r0 * r1
            float r1 = r4.y
            float r2 = r4.startingY
            float r1 = r1 - r2
            float r2 = r4.y
            float r3 = r4.startingY
            float r2 = r2 - r3
            float r1 = r1 * r2
            float r0 = r0 + r1
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: me.veryyoung.game2048.InputListener.pathMoved():float");
    }

    private boolean iconPressed(int i, int i2) {
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
            r0 = 1
            boolean r1 = r4.isTap(r0)
            if (r1 == 0) goto L26
            float r1 = (float) r5
            float r2 = r4.x
            me.veryyoung.game2048.MainView r3 = r4.mView
            int r3 = r3.iconSize
            int r5 = r5 + r3
            float r5 = (float) r5
            boolean r5 = r4.inRange(r1, r2, r5)
            if (r5 == 0) goto L26
            float r5 = (float) r6
            float r1 = r4.y
            me.veryyoung.game2048.MainView r2 = r4.mView
            int r2 = r2.iconSize
            int r6 = r6 + r2
            float r6 = (float) r6
            boolean r5 = r4.inRange(r5, r1, r6)
            if (r5 == 0) goto L26
            goto L27
        L26:
            r0 = 0
        L27:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: me.veryyoung.game2048.InputListener.iconPressed(int, int):boolean");
    }

    private boolean isTap(int i) {
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
            float r0 = r2.pathMoved()
            me.veryyoung.game2048.MainView r1 = r2.mView
            int r1 = r1.iconSize
            int r1 = r1 * r3
            float r3 = (float) r1
            int r3 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r3 > 0) goto L11
            r3 = 1
            goto L12
        L11:
            r3 = 0
        L12:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: me.veryyoung.game2048.InputListener.isTap(int):boolean");
    }
}
