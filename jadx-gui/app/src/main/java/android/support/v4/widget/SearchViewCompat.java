package android.support.v4.widget;

import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.support.v4.widget.SearchViewCompatHoneycomb;
import android.view.View;

/* loaded from: classes.dex */
public class SearchViewCompat {
    private static final SearchViewCompatImpl IMPL;

    /* loaded from: classes.dex */
    public static abstract class OnCloseListenerCompat {
        final Object mListener = SearchViewCompat.IMPL.newOnCloseListener(this);

        public boolean onClose() {
            return false;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class OnQueryTextListenerCompat {
        final Object mListener = SearchViewCompat.IMPL.newOnQueryTextListener(this);

        public boolean onQueryTextChange(String str) {
            return false;
        }

        public boolean onQueryTextSubmit(String str) {
            return false;
        }
    }

    /* loaded from: classes.dex */
    interface SearchViewCompatImpl {
        CharSequence getQuery(View view);

        boolean isIconified(View view);

        boolean isQueryRefinementEnabled(View view);

        boolean isSubmitButtonEnabled(View view);

        Object newOnCloseListener(OnCloseListenerCompat onCloseListenerCompat);

        Object newOnQueryTextListener(OnQueryTextListenerCompat onQueryTextListenerCompat);

        View newSearchView(Context context);

        void setIconified(View view, boolean z);

        void setImeOptions(View view, int i);

        void setInputType(View view, int i);

        void setMaxWidth(View view, int i);

        void setOnCloseListener(Object obj, Object obj2);

        void setOnQueryTextListener(Object obj, Object obj2);

        void setQuery(View view, CharSequence charSequence, boolean z);

        void setQueryHint(View view, CharSequence charSequence);

        void setQueryRefinementEnabled(View view, boolean z);

        void setSearchableInfo(View view, ComponentName componentName);

        void setSubmitButtonEnabled(View view, boolean z);
    }

    /* loaded from: classes.dex */
    static class SearchViewCompatStubImpl implements SearchViewCompatImpl {
        @Override // android.support.v4.widget.SearchViewCompat.SearchViewCompatImpl
        public CharSequence getQuery(View view) {
            return null;
        }

        @Override // android.support.v4.widget.SearchViewCompat.SearchViewCompatImpl
        public boolean isIconified(View view) {
            return true;
        }

        @Override // android.support.v4.widget.SearchViewCompat.SearchViewCompatImpl
        public boolean isQueryRefinementEnabled(View view) {
            return false;
        }

        @Override // android.support.v4.widget.SearchViewCompat.SearchViewCompatImpl
        public boolean isSubmitButtonEnabled(View view) {
            return false;
        }

        @Override // android.support.v4.widget.SearchViewCompat.SearchViewCompatImpl
        public Object newOnCloseListener(OnCloseListenerCompat onCloseListenerCompat) {
            return null;
        }

        @Override // android.support.v4.widget.SearchViewCompat.SearchViewCompatImpl
        public Object newOnQueryTextListener(OnQueryTextListenerCompat onQueryTextListenerCompat) {
            return null;
        }

        @Override // android.support.v4.widget.SearchViewCompat.SearchViewCompatImpl
        public View newSearchView(Context context) {
            return null;
        }

        @Override // android.support.v4.widget.SearchViewCompat.SearchViewCompatImpl
        public void setIconified(View view, boolean z) {
        }

        @Override // android.support.v4.widget.SearchViewCompat.SearchViewCompatImpl
        public void setImeOptions(View view, int i) {
        }

        @Override // android.support.v4.widget.SearchViewCompat.SearchViewCompatImpl
        public void setInputType(View view, int i) {
        }

        @Override // android.support.v4.widget.SearchViewCompat.SearchViewCompatImpl
        public void setMaxWidth(View view, int i) {
        }

        @Override // android.support.v4.widget.SearchViewCompat.SearchViewCompatImpl
        public void setOnCloseListener(Object obj, Object obj2) {
        }

        @Override // android.support.v4.widget.SearchViewCompat.SearchViewCompatImpl
        public void setOnQueryTextListener(Object obj, Object obj2) {
        }

        @Override // android.support.v4.widget.SearchViewCompat.SearchViewCompatImpl
        public void setQuery(View view, CharSequence charSequence, boolean z) {
        }

        @Override // android.support.v4.widget.SearchViewCompat.SearchViewCompatImpl
        public void setQueryHint(View view, CharSequence charSequence) {
        }

        @Override // android.support.v4.widget.SearchViewCompat.SearchViewCompatImpl
        public void setQueryRefinementEnabled(View view, boolean z) {
        }

        @Override // android.support.v4.widget.SearchViewCompat.SearchViewCompatImpl
        public void setSearchableInfo(View view, ComponentName componentName) {
        }

        @Override // android.support.v4.widget.SearchViewCompat.SearchViewCompatImpl
        public void setSubmitButtonEnabled(View view, boolean z) {
        }

        SearchViewCompatStubImpl() {
        }
    }

    /* loaded from: classes.dex */
    static class SearchViewCompatHoneycombImpl extends SearchViewCompatStubImpl {
        SearchViewCompatHoneycombImpl() {
        }

        @Override // android.support.v4.widget.SearchViewCompat.SearchViewCompatStubImpl, android.support.v4.widget.SearchViewCompat.SearchViewCompatImpl
        public View newSearchView(Context context) {
            return SearchViewCompatHoneycomb.newSearchView(context);
        }

        @Override // android.support.v4.widget.SearchViewCompat.SearchViewCompatStubImpl, android.support.v4.widget.SearchViewCompat.SearchViewCompatImpl
        public void setSearchableInfo(View view, ComponentName componentName) {
            SearchViewCompatHoneycomb.setSearchableInfo(view, componentName);
        }

        @Override // android.support.v4.widget.SearchViewCompat.SearchViewCompatStubImpl, android.support.v4.widget.SearchViewCompat.SearchViewCompatImpl
        public Object newOnQueryTextListener(final OnQueryTextListenerCompat onQueryTextListenerCompat) {
            return SearchViewCompatHoneycomb.newOnQueryTextListener(new SearchViewCompatHoneycomb.OnQueryTextListenerCompatBridge() { // from class: android.support.v4.widget.SearchViewCompat.SearchViewCompatHoneycombImpl.1
                @Override // android.support.v4.widget.SearchViewCompatHoneycomb.OnQueryTextListenerCompatBridge
                public boolean onQueryTextSubmit(String str) {
                    return onQueryTextListenerCompat.onQueryTextSubmit(str);
                }

                @Override // android.support.v4.widget.SearchViewCompatHoneycomb.OnQueryTextListenerCompatBridge
                public boolean onQueryTextChange(String str) {
                    return onQueryTextListenerCompat.onQueryTextChange(str);
                }
            });
        }

        @Override // android.support.v4.widget.SearchViewCompat.SearchViewCompatStubImpl, android.support.v4.widget.SearchViewCompat.SearchViewCompatImpl
        public void setOnQueryTextListener(Object obj, Object obj2) {
            SearchViewCompatHoneycomb.setOnQueryTextListener(obj, obj2);
        }

        @Override // android.support.v4.widget.SearchViewCompat.SearchViewCompatStubImpl, android.support.v4.widget.SearchViewCompat.SearchViewCompatImpl
        public Object newOnCloseListener(final OnCloseListenerCompat onCloseListenerCompat) {
            return SearchViewCompatHoneycomb.newOnCloseListener(new SearchViewCompatHoneycomb.OnCloseListenerCompatBridge() { // from class: android.support.v4.widget.SearchViewCompat.SearchViewCompatHoneycombImpl.2
                @Override // android.support.v4.widget.SearchViewCompatHoneycomb.OnCloseListenerCompatBridge
                public boolean onClose() {
                    return onCloseListenerCompat.onClose();
                }
            });
        }

        @Override // android.support.v4.widget.SearchViewCompat.SearchViewCompatStubImpl, android.support.v4.widget.SearchViewCompat.SearchViewCompatImpl
        public void setOnCloseListener(Object obj, Object obj2) {
            SearchViewCompatHoneycomb.setOnCloseListener(obj, obj2);
        }

        @Override // android.support.v4.widget.SearchViewCompat.SearchViewCompatStubImpl, android.support.v4.widget.SearchViewCompat.SearchViewCompatImpl
        public CharSequence getQuery(View view) {
            return SearchViewCompatHoneycomb.getQuery(view);
        }

        @Override // android.support.v4.widget.SearchViewCompat.SearchViewCompatStubImpl, android.support.v4.widget.SearchViewCompat.SearchViewCompatImpl
        public void setQuery(View view, CharSequence charSequence, boolean z) {
            SearchViewCompatHoneycomb.setQuery(view, charSequence, z);
        }

        @Override // android.support.v4.widget.SearchViewCompat.SearchViewCompatStubImpl, android.support.v4.widget.SearchViewCompat.SearchViewCompatImpl
        public void setQueryHint(View view, CharSequence charSequence) {
            SearchViewCompatHoneycomb.setQueryHint(view, charSequence);
        }

        @Override // android.support.v4.widget.SearchViewCompat.SearchViewCompatStubImpl, android.support.v4.widget.SearchViewCompat.SearchViewCompatImpl
        public void setIconified(View view, boolean z) {
            SearchViewCompatHoneycomb.setIconified(view, z);
        }

        @Override // android.support.v4.widget.SearchViewCompat.SearchViewCompatStubImpl, android.support.v4.widget.SearchViewCompat.SearchViewCompatImpl
        public boolean isIconified(View view) {
            return SearchViewCompatHoneycomb.isIconified(view);
        }

        @Override // android.support.v4.widget.SearchViewCompat.SearchViewCompatStubImpl, android.support.v4.widget.SearchViewCompat.SearchViewCompatImpl
        public void setSubmitButtonEnabled(View view, boolean z) {
            SearchViewCompatHoneycomb.setSubmitButtonEnabled(view, z);
        }

        @Override // android.support.v4.widget.SearchViewCompat.SearchViewCompatStubImpl, android.support.v4.widget.SearchViewCompat.SearchViewCompatImpl
        public boolean isSubmitButtonEnabled(View view) {
            return SearchViewCompatHoneycomb.isSubmitButtonEnabled(view);
        }

        @Override // android.support.v4.widget.SearchViewCompat.SearchViewCompatStubImpl, android.support.v4.widget.SearchViewCompat.SearchViewCompatImpl
        public void setQueryRefinementEnabled(View view, boolean z) {
            SearchViewCompatHoneycomb.setQueryRefinementEnabled(view, z);
        }

        @Override // android.support.v4.widget.SearchViewCompat.SearchViewCompatStubImpl, android.support.v4.widget.SearchViewCompat.SearchViewCompatImpl
        public boolean isQueryRefinementEnabled(View view) {
            return SearchViewCompatHoneycomb.isQueryRefinementEnabled(view);
        }

        @Override // android.support.v4.widget.SearchViewCompat.SearchViewCompatStubImpl, android.support.v4.widget.SearchViewCompat.SearchViewCompatImpl
        public void setMaxWidth(View view, int i) {
            SearchViewCompatHoneycomb.setMaxWidth(view, i);
        }
    }

    /* loaded from: classes.dex */
    static class SearchViewCompatIcsImpl extends SearchViewCompatHoneycombImpl {
        SearchViewCompatIcsImpl() {
        }

        @Override // android.support.v4.widget.SearchViewCompat.SearchViewCompatHoneycombImpl, android.support.v4.widget.SearchViewCompat.SearchViewCompatStubImpl, android.support.v4.widget.SearchViewCompat.SearchViewCompatImpl
        public View newSearchView(Context context) {
            return SearchViewCompatIcs.newSearchView(context);
        }

        @Override // android.support.v4.widget.SearchViewCompat.SearchViewCompatStubImpl, android.support.v4.widget.SearchViewCompat.SearchViewCompatImpl
        public void setImeOptions(View view, int i) {
            SearchViewCompatIcs.setImeOptions(view, i);
        }

        @Override // android.support.v4.widget.SearchViewCompat.SearchViewCompatStubImpl, android.support.v4.widget.SearchViewCompat.SearchViewCompatImpl
        public void setInputType(View view, int i) {
            SearchViewCompatIcs.setInputType(view, i);
        }
    }

    static {
        if (Build.VERSION.SDK_INT >= 14) {
            IMPL = new SearchViewCompatIcsImpl();
        } else if (Build.VERSION.SDK_INT >= 11) {
            IMPL = new SearchViewCompatHoneycombImpl();
        } else {
            IMPL = new SearchViewCompatStubImpl();
        }
    }

    private SearchViewCompat(Context context) {
    }

    public static View newSearchView(Context context) {
        return IMPL.newSearchView(context);
    }

    public static void setSearchableInfo(View view, ComponentName componentName) {
        IMPL.setSearchableInfo(view, componentName);
    }

    public static void setImeOptions(View view, int i) {
        IMPL.setImeOptions(view, i);
    }

    public static void setInputType(View view, int i) {
        IMPL.setInputType(view, i);
    }

    public static void setOnQueryTextListener(View view, OnQueryTextListenerCompat onQueryTextListenerCompat) {
        IMPL.setOnQueryTextListener(view, onQueryTextListenerCompat.mListener);
    }

    public static void setOnCloseListener(View view, OnCloseListenerCompat onCloseListenerCompat) {
        IMPL.setOnCloseListener(view, onCloseListenerCompat.mListener);
    }

    public static CharSequence getQuery(View view) {
        return IMPL.getQuery(view);
    }

    public static void setQuery(View view, CharSequence charSequence, boolean z) {
        IMPL.setQuery(view, charSequence, z);
    }

    public static void setQueryHint(View view, CharSequence charSequence) {
        IMPL.setQueryHint(view, charSequence);
    }

    public static void setIconified(View view, boolean z) {
        IMPL.setIconified(view, z);
    }

    public static boolean isIconified(View view) {
        return IMPL.isIconified(view);
    }

    public static void setSubmitButtonEnabled(View view, boolean z) {
        IMPL.setSubmitButtonEnabled(view, z);
    }

    public static boolean isSubmitButtonEnabled(View view) {
        return IMPL.isSubmitButtonEnabled(view);
    }

    public static void setQueryRefinementEnabled(View view, boolean z) {
        IMPL.setQueryRefinementEnabled(view, z);
    }

    public static boolean isQueryRefinementEnabled(View view) {
        return IMPL.isQueryRefinementEnabled(view);
    }

    public static void setMaxWidth(View view, int i) {
        IMPL.setMaxWidth(view, i);
    }
}
