package android.support.v4.app;

import android.graphics.Rect;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
class FragmentTransitionCompat21 {

    /* loaded from: classes.dex */
    public static class EpicenterView {
        public View epicenter;
    }

    /* loaded from: classes.dex */
    public interface ViewRetriever {
        View getView();
    }

    FragmentTransitionCompat21() {
    }

    public static String getTransitionName(View view) {
        return view.getTransitionName();
    }

    public static Object cloneTransition(Object obj) {
        return obj != null ? ((Transition) obj).clone() : obj;
    }

    public static Object captureExitingViews(Object obj, View view, ArrayList<View> arrayList, Map<String, View> map, View view2) {
        if (obj == null) {
            return obj;
        }
        captureTransitioningViews(arrayList, view);
        if (map != null) {
            arrayList.removeAll(map.values());
        }
        if (arrayList.isEmpty()) {
            return null;
        }
        arrayList.add(view2);
        addTargets((Transition) obj, arrayList);
        return obj;
    }

    public static void excludeTarget(Object obj, View view, boolean z) {
        ((Transition) obj).excludeTarget(view, z);
    }

    public static void beginDelayedTransition(ViewGroup viewGroup, Object obj) {
        TransitionManager.beginDelayedTransition(viewGroup, (Transition) obj);
    }

    public static void setEpicenter(Object obj, View view) {
        final Rect boundsOnScreen = getBoundsOnScreen(view);
        ((Transition) obj).setEpicenterCallback(new Transition.EpicenterCallback() { // from class: android.support.v4.app.FragmentTransitionCompat21.1
            @Override // android.transition.Transition.EpicenterCallback
            public Rect onGetEpicenter(Transition transition) {
                return boundsOnScreen;
            }
        });
    }

    public static Object wrapSharedElementTransition(Object obj) {
        Transition transition;
        if (obj == null || (transition = (Transition) obj) == null) {
            return null;
        }
        TransitionSet transitionSet = new TransitionSet();
        transitionSet.addTransition(transition);
        return transitionSet;
    }

    public static void addTransitionTargets(Object obj, Object obj2, final View view, final ViewRetriever viewRetriever, final View view2, EpicenterView epicenterView, final Map<String, String> map, final ArrayList<View> arrayList, Map<String, View> map2, final Map<String, View> map3, ArrayList<View> arrayList2) {
        if (obj == null && obj2 == null) {
            return;
        }
        final Transition transition = (Transition) obj;
        if (transition != null) {
            transition.addTarget(view2);
        }
        if (obj2 != null) {
            setSharedElementTargets(obj2, view2, map2, arrayList2);
        }
        if (viewRetriever != null) {
            view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: android.support.v4.app.FragmentTransitionCompat21.2
                @Override // android.view.ViewTreeObserver.OnPreDrawListener
                public boolean onPreDraw() {
                    view.getViewTreeObserver().removeOnPreDrawListener(this);
                    if (transition != null) {
                        transition.removeTarget(view2);
                    }
                    View view3 = viewRetriever.getView();
                    if (view3 == null) {
                        return true;
                    }
                    if (!map.isEmpty()) {
                        FragmentTransitionCompat21.findNamedViews(map3, view3);
                        map3.keySet().retainAll(map.values());
                        for (Map.Entry entry : map.entrySet()) {
                            View view4 = (View) map3.get((String) entry.getValue());
                            if (view4 != null) {
                                view4.setTransitionName((String) entry.getKey());
                            }
                        }
                    }
                    if (transition == null) {
                        return true;
                    }
                    FragmentTransitionCompat21.captureTransitioningViews(arrayList, view3);
                    arrayList.removeAll(map3.values());
                    arrayList.add(view2);
                    FragmentTransitionCompat21.addTargets(transition, arrayList);
                    return true;
                }
            });
        }
        setSharedElementEpicenter(transition, epicenterView);
    }

    /* JADX WARN: Code restructure failed: missing block: B:29:0x0040, code lost:
    
        if (r1 != null) goto L19;
     */
    /* JADX WARN: Removed duplicated region for block: B:21:0x0045  */
    /* JADX WARN: Removed duplicated region for block: B:26:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static java.lang.Object mergeTransitions(java.lang.Object r1, java.lang.Object r2, java.lang.Object r3, boolean r4) {
        /*
            android.transition.Transition r1 = (android.transition.Transition) r1
            android.transition.Transition r2 = (android.transition.Transition) r2
            android.transition.Transition r3 = (android.transition.Transition) r3
            r0 = 1
            if (r1 == 0) goto Lc
            if (r2 == 0) goto Lc
            goto Ld
        Lc:
            r4 = 1
        Ld:
            if (r4 == 0) goto L24
            android.transition.TransitionSet r4 = new android.transition.TransitionSet
            r4.<init>()
            if (r1 == 0) goto L19
            r4.addTransition(r1)
        L19:
            if (r2 == 0) goto L1e
            r4.addTransition(r2)
        L1e:
            if (r3 == 0) goto L53
            r4.addTransition(r3)
            goto L53
        L24:
            r4 = 0
            if (r2 == 0) goto L3c
            if (r1 == 0) goto L3c
            android.transition.TransitionSet r4 = new android.transition.TransitionSet
            r4.<init>()
            android.transition.TransitionSet r2 = r4.addTransition(r2)
            android.transition.TransitionSet r1 = r2.addTransition(r1)
            android.transition.TransitionSet r1 = r1.setOrdering(r0)
        L3a:
            r4 = r1
            goto L43
        L3c:
            if (r2 == 0) goto L40
            r4 = r2
            goto L43
        L40:
            if (r1 == 0) goto L43
            goto L3a
        L43:
            if (r3 == 0) goto L53
            android.transition.TransitionSet r1 = new android.transition.TransitionSet
            r1.<init>()
            if (r4 == 0) goto L4f
            r1.addTransition(r4)
        L4f:
            r1.addTransition(r3)
            r4 = r1
        L53:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.app.FragmentTransitionCompat21.mergeTransitions(java.lang.Object, java.lang.Object, java.lang.Object, boolean):java.lang.Object");
    }

    public static void setSharedElementTargets(Object obj, View view, Map<String, View> map, ArrayList<View> arrayList) {
        TransitionSet transitionSet = (TransitionSet) obj;
        arrayList.clear();
        arrayList.addAll(map.values());
        List<View> targets = transitionSet.getTargets();
        targets.clear();
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            bfsAddViewChildren(targets, arrayList.get(i));
        }
        arrayList.add(view);
        addTargets(transitionSet, arrayList);
    }

    private static void bfsAddViewChildren(List<View> list, View view) {
        int size = list.size();
        if (containedBeforeIndex(list, view, size)) {
            return;
        }
        list.add(view);
        for (int i = size; i < list.size(); i++) {
            View view2 = list.get(i);
            if (view2 instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view2;
                int childCount = viewGroup.getChildCount();
                for (int i2 = 0; i2 < childCount; i2++) {
                    View childAt = viewGroup.getChildAt(i2);
                    if (!containedBeforeIndex(list, childAt, size)) {
                        list.add(childAt);
                    }
                }
            }
        }
    }

    private static boolean containedBeforeIndex(List<View> list, View view, int i) {
        for (int i2 = 0; i2 < i; i2++) {
            if (list.get(i2) == view) {
                return true;
            }
        }
        return false;
    }

    private static void setSharedElementEpicenter(Transition transition, final EpicenterView epicenterView) {
        if (transition != null) {
            transition.setEpicenterCallback(new Transition.EpicenterCallback() { // from class: android.support.v4.app.FragmentTransitionCompat21.3
                private Rect mEpicenter;

                @Override // android.transition.Transition.EpicenterCallback
                public Rect onGetEpicenter(Transition transition2) {
                    if (this.mEpicenter == null && EpicenterView.this.epicenter != null) {
                        this.mEpicenter = FragmentTransitionCompat21.getBoundsOnScreen(EpicenterView.this.epicenter);
                    }
                    return this.mEpicenter;
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Rect getBoundsOnScreen(View view) {
        Rect rect = new Rect();
        int[] iArr = new int[2];
        view.getLocationOnScreen(iArr);
        rect.set(iArr[0], iArr[1], iArr[0] + view.getWidth(), iArr[1] + view.getHeight());
        return rect;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void captureTransitioningViews(ArrayList<View> arrayList, View view) {
        if (view.getVisibility() == 0) {
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                if (viewGroup.isTransitionGroup()) {
                    arrayList.add(viewGroup);
                    return;
                }
                int childCount = viewGroup.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    captureTransitioningViews(arrayList, viewGroup.getChildAt(i));
                }
                return;
            }
            arrayList.add(view);
        }
    }

    public static void findNamedViews(Map<String, View> map, View view) {
        if (view.getVisibility() == 0) {
            String transitionName = view.getTransitionName();
            if (transitionName != null) {
                map.put(transitionName, view);
            }
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                int childCount = viewGroup.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    findNamedViews(map, viewGroup.getChildAt(i));
                }
            }
        }
    }

    public static void cleanupTransitions(final View view, final View view2, Object obj, final ArrayList<View> arrayList, Object obj2, final ArrayList<View> arrayList2, Object obj3, final ArrayList<View> arrayList3, Object obj4, final ArrayList<View> arrayList4, final Map<String, View> map) {
        final Transition transition = (Transition) obj;
        final Transition transition2 = (Transition) obj2;
        final Transition transition3 = (Transition) obj3;
        final Transition transition4 = (Transition) obj4;
        if (transition4 != null) {
            view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: android.support.v4.app.FragmentTransitionCompat21.4
                @Override // android.view.ViewTreeObserver.OnPreDrawListener
                public boolean onPreDraw() {
                    view.getViewTreeObserver().removeOnPreDrawListener(this);
                    if (transition != null) {
                        FragmentTransitionCompat21.removeTargets(transition, arrayList);
                    }
                    if (transition2 != null) {
                        FragmentTransitionCompat21.removeTargets(transition2, arrayList2);
                    }
                    if (transition3 != null) {
                        FragmentTransitionCompat21.removeTargets(transition3, arrayList3);
                    }
                    for (Map.Entry entry : map.entrySet()) {
                        ((View) entry.getValue()).setTransitionName((String) entry.getKey());
                    }
                    int size = arrayList4.size();
                    for (int i = 0; i < size; i++) {
                        transition4.excludeTarget((View) arrayList4.get(i), false);
                    }
                    transition4.excludeTarget(view2, false);
                    return true;
                }
            });
        }
    }

    public static void removeTargets(Object obj, ArrayList<View> arrayList) {
        List<View> targets;
        Transition transition = (Transition) obj;
        if (transition instanceof TransitionSet) {
            TransitionSet transitionSet = (TransitionSet) transition;
            int transitionCount = transitionSet.getTransitionCount();
            for (int i = 0; i < transitionCount; i++) {
                removeTargets(transitionSet.getTransitionAt(i), arrayList);
            }
            return;
        }
        if (hasSimpleTarget(transition) || (targets = transition.getTargets()) == null || targets.size() != arrayList.size() || !targets.containsAll(arrayList)) {
            return;
        }
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            transition.removeTarget(arrayList.get(size));
        }
    }

    public static void addTargets(Object obj, ArrayList<View> arrayList) {
        Transition transition = (Transition) obj;
        int i = 0;
        if (transition instanceof TransitionSet) {
            TransitionSet transitionSet = (TransitionSet) transition;
            int transitionCount = transitionSet.getTransitionCount();
            while (i < transitionCount) {
                addTargets(transitionSet.getTransitionAt(i), arrayList);
                i++;
            }
            return;
        }
        if (hasSimpleTarget(transition) || !isNullOrEmpty(transition.getTargets())) {
            return;
        }
        int size = arrayList.size();
        while (i < size) {
            transition.addTarget(arrayList.get(i));
            i++;
        }
    }

    private static boolean hasSimpleTarget(Transition transition) {
        return (isNullOrEmpty(transition.getTargetIds()) && isNullOrEmpty(transition.getTargetNames()) && isNullOrEmpty(transition.getTargetTypes())) ? false : true;
    }

    private static boolean isNullOrEmpty(List list) {
        return list == null || list.isEmpty();
    }
}
