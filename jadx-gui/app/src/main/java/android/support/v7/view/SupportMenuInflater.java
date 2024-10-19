package android.support.v7.view;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.view.ActionProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.appcompat.R;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuItemWrapperICS;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import android.view.InflateException;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: classes.dex */
public class SupportMenuInflater extends MenuInflater {
    private static final String LOG_TAG = "SupportMenuInflater";
    private static final int NO_ID = 0;
    private static final String XML_GROUP = "group";
    private static final String XML_ITEM = "item";
    private static final String XML_MENU = "menu";
    private final Object[] mActionProviderConstructorArguments;
    private final Object[] mActionViewConstructorArguments;
    private Context mContext;
    private Object mRealOwner;
    private static final Class<?>[] ACTION_VIEW_CONSTRUCTOR_SIGNATURE = {Context.class};
    private static final Class<?>[] ACTION_PROVIDER_CONSTRUCTOR_SIGNATURE = ACTION_VIEW_CONSTRUCTOR_SIGNATURE;

    public SupportMenuInflater(Context context) {
        super(context);
        this.mContext = context;
        this.mActionViewConstructorArguments = new Object[]{context};
        this.mActionProviderConstructorArguments = this.mActionViewConstructorArguments;
    }

    @Override // android.view.MenuInflater
    public void inflate(int i, Menu menu) {
        if (!(menu instanceof SupportMenu)) {
            super.inflate(i, menu);
            return;
        }
        XmlResourceParser xmlResourceParser = null;
        try {
            try {
                XmlResourceParser layout = this.mContext.getResources().getLayout(i);
                try {
                    parseMenu(layout, Xml.asAttributeSet(layout), menu);
                    if (layout != null) {
                        layout.close();
                    }
                } catch (IOException e) {
                    e = e;
                    throw new InflateException("Error inflating menu XML", e);
                } catch (XmlPullParserException e2) {
                    e = e2;
                    throw new InflateException("Error inflating menu XML", e);
                } catch (Throwable th) {
                    th = th;
                    xmlResourceParser = layout;
                    if (xmlResourceParser != null) {
                        xmlResourceParser.close();
                    }
                    throw th;
                }
            } catch (IOException e3) {
                e = e3;
            } catch (XmlPullParserException e4) {
                e = e4;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x0048, code lost:
    
        r4 = r9.getName();
     */
    /* JADX WARN: Code restructure failed: missing block: B:11:0x004c, code lost:
    
        if (r5 == false) goto L21;
     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x0052, code lost:
    
        if (r4.equals(r6) == false) goto L21;
     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x0054, code lost:
    
        r6 = null;
        r5 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x00cf, code lost:
    
        r4 = r9.next();
     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x005e, code lost:
    
        if (r4.equals(android.support.v7.view.SupportMenuInflater.XML_GROUP) == false) goto L24;
     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x0060, code lost:
    
        r0.resetGroup();
     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x006b, code lost:
    
        if (r4.equals(android.support.v7.view.SupportMenuInflater.XML_ITEM) == false) goto L34;
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x0071, code lost:
    
        if (r0.hasAddedItem() != false) goto L60;
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x0077, code lost:
    
        if (r0.itemActionProvider == null) goto L33;
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x0081, code lost:
    
        if (r0.itemActionProvider.hasSubMenu() == false) goto L33;
     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x0083, code lost:
    
        r0.addSubMenuItem();
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x0087, code lost:
    
        r0.addItem();
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x0091, code lost:
    
        if (r4.equals(android.support.v7.view.SupportMenuInflater.XML_MENU) == false) goto L63;
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x0093, code lost:
    
        r11 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x0095, code lost:
    
        if (r5 == false) goto L39;
     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x0098, code lost:
    
        r4 = r9.getName();
     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x00a2, code lost:
    
        if (r4.equals(android.support.v7.view.SupportMenuInflater.XML_GROUP) == false) goto L42;
     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x00a4, code lost:
    
        r0.readGroup(r10);
     */
    /* JADX WARN: Code restructure failed: missing block: B:45:0x00ae, code lost:
    
        if (r4.equals(android.support.v7.view.SupportMenuInflater.XML_ITEM) == false) goto L45;
     */
    /* JADX WARN: Code restructure failed: missing block: B:46:0x00b0, code lost:
    
        r0.readItem(r10);
     */
    /* JADX WARN: Code restructure failed: missing block: B:49:0x00ba, code lost:
    
        if (r4.equals(android.support.v7.view.SupportMenuInflater.XML_MENU) == false) goto L48;
     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x00bc, code lost:
    
        parseMenu(r9, r10, r0.addSubMenuItem());
     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x00c4, code lost:
    
        r6 = r4;
        r5 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:57:0x00ce, code lost:
    
        throw new java.lang.RuntimeException("Unexpected end of document");
     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x00d5, code lost:
    
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:7:0x003b, code lost:
    
        r4 = r11;
        r6 = null;
        r11 = false;
        r5 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x0041, code lost:
    
        if (r11 != false) goto L56;
     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x0043, code lost:
    
        switch(r4) {
            case 1: goto L57;
            case 2: goto L37;
            case 3: goto L16;
            default: goto L69;
        };
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void parseMenu(org.xmlpull.v1.XmlPullParser r9, android.util.AttributeSet r10, android.view.Menu r11) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            r8 = this;
            android.support.v7.view.SupportMenuInflater$MenuState r0 = new android.support.v7.view.SupportMenuInflater$MenuState
            r0.<init>(r11)
            int r11 = r9.getEventType()
        L9:
            r1 = 2
            r2 = 1
            if (r11 != r1) goto L35
            java.lang.String r11 = r9.getName()
            java.lang.String r1 = "menu"
            boolean r1 = r11.equals(r1)
            if (r1 == 0) goto L1e
            int r11 = r9.next()
            goto L3b
        L1e:
            java.lang.RuntimeException r9 = new java.lang.RuntimeException
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r0 = "Expecting menu, got "
            r10.append(r0)
            r10.append(r11)
            java.lang.String r10 = r10.toString()
            r9.<init>(r10)
            throw r9
        L35:
            int r11 = r9.next()
            if (r11 != r2) goto L9
        L3b:
            r1 = 0
            r3 = 0
            r4 = r11
            r6 = r1
            r11 = 0
            r5 = 0
        L41:
            if (r11 != 0) goto Ld5
            switch(r4) {
                case 1: goto Lc7;
                case 2: goto L95;
                case 3: goto L48;
                default: goto L46;
            }
        L46:
            goto Lcf
        L48:
            java.lang.String r4 = r9.getName()
            if (r5 == 0) goto L58
            boolean r7 = r4.equals(r6)
            if (r7 == 0) goto L58
            r6 = r1
            r5 = 0
            goto Lcf
        L58:
            java.lang.String r7 = "group"
            boolean r7 = r4.equals(r7)
            if (r7 == 0) goto L65
            r0.resetGroup()
            goto Lcf
        L65:
            java.lang.String r7 = "item"
            boolean r7 = r4.equals(r7)
            if (r7 == 0) goto L8b
            boolean r4 = r0.hasAddedItem()
            if (r4 != 0) goto Lcf
            android.support.v4.view.ActionProvider r4 = android.support.v7.view.SupportMenuInflater.MenuState.access$000(r0)
            if (r4 == 0) goto L87
            android.support.v4.view.ActionProvider r4 = android.support.v7.view.SupportMenuInflater.MenuState.access$000(r0)
            boolean r4 = r4.hasSubMenu()
            if (r4 == 0) goto L87
            r0.addSubMenuItem()
            goto Lcf
        L87:
            r0.addItem()
            goto Lcf
        L8b:
            java.lang.String r7 = "menu"
            boolean r4 = r4.equals(r7)
            if (r4 == 0) goto Lcf
            r11 = 1
            goto Lcf
        L95:
            if (r5 == 0) goto L98
            goto Lcf
        L98:
            java.lang.String r4 = r9.getName()
            java.lang.String r7 = "group"
            boolean r7 = r4.equals(r7)
            if (r7 == 0) goto La8
            r0.readGroup(r10)
            goto Lcf
        La8:
            java.lang.String r7 = "item"
            boolean r7 = r4.equals(r7)
            if (r7 == 0) goto Lb4
            r0.readItem(r10)
            goto Lcf
        Lb4:
            java.lang.String r7 = "menu"
            boolean r7 = r4.equals(r7)
            if (r7 == 0) goto Lc4
            android.view.SubMenu r4 = r0.addSubMenuItem()
            r8.parseMenu(r9, r10, r4)
            goto Lcf
        Lc4:
            r6 = r4
            r5 = 1
            goto Lcf
        Lc7:
            java.lang.RuntimeException r9 = new java.lang.RuntimeException
            java.lang.String r10 = "Unexpected end of document"
            r9.<init>(r10)
            throw r9
        Lcf:
            int r4 = r9.next()
            goto L41
        Ld5:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.view.SupportMenuInflater.parseMenu(org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, android.view.Menu):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Object getRealOwner() {
        if (this.mRealOwner == null) {
            this.mRealOwner = findRealOwner(this.mContext);
        }
        return this.mRealOwner;
    }

    private Object findRealOwner(Object obj) {
        return (!(obj instanceof Activity) && (obj instanceof ContextWrapper)) ? findRealOwner(((ContextWrapper) obj).getBaseContext()) : obj;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class InflatedOnMenuItemClickListener implements MenuItem.OnMenuItemClickListener {
        private static final Class<?>[] PARAM_TYPES = {MenuItem.class};
        private Method mMethod;
        private Object mRealOwner;

        public InflatedOnMenuItemClickListener(Object obj, String str) {
            this.mRealOwner = obj;
            Class<?> cls = obj.getClass();
            try {
                this.mMethod = cls.getMethod(str, PARAM_TYPES);
            } catch (Exception e) {
                InflateException inflateException = new InflateException("Couldn't resolve menu item onClick handler " + str + " in class " + cls.getName());
                inflateException.initCause(e);
                throw inflateException;
            }
        }

        @Override // android.view.MenuItem.OnMenuItemClickListener
        public boolean onMenuItemClick(MenuItem menuItem) {
            try {
                if (this.mMethod.getReturnType() == Boolean.TYPE) {
                    return ((Boolean) this.mMethod.invoke(this.mRealOwner, menuItem)).booleanValue();
                }
                this.mMethod.invoke(this.mRealOwner, menuItem);
                return true;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class MenuState {
        private static final int defaultGroupId = 0;
        private static final int defaultItemCategory = 0;
        private static final int defaultItemCheckable = 0;
        private static final boolean defaultItemChecked = false;
        private static final boolean defaultItemEnabled = true;
        private static final int defaultItemId = 0;
        private static final int defaultItemOrder = 0;
        private static final boolean defaultItemVisible = true;
        private int groupCategory;
        private int groupCheckable;
        private boolean groupEnabled;
        private int groupId;
        private int groupOrder;
        private boolean groupVisible;
        private ActionProvider itemActionProvider;
        private String itemActionProviderClassName;
        private String itemActionViewClassName;
        private int itemActionViewLayout;
        private boolean itemAdded;
        private char itemAlphabeticShortcut;
        private int itemCategoryOrder;
        private int itemCheckable;
        private boolean itemChecked;
        private boolean itemEnabled;
        private int itemIconResId;
        private int itemId;
        private String itemListenerMethodName;
        private char itemNumericShortcut;
        private int itemShowAsAction;
        private CharSequence itemTitle;
        private CharSequence itemTitleCondensed;
        private boolean itemVisible;
        private Menu menu;

        public MenuState(Menu menu) {
            this.menu = menu;
            resetGroup();
        }

        public void resetGroup() {
            this.groupId = 0;
            this.groupCategory = 0;
            this.groupOrder = 0;
            this.groupCheckable = 0;
            this.groupVisible = true;
            this.groupEnabled = true;
        }

        public void readGroup(AttributeSet attributeSet) {
            TypedArray obtainStyledAttributes = SupportMenuInflater.this.mContext.obtainStyledAttributes(attributeSet, R.styleable.MenuGroup);
            this.groupId = obtainStyledAttributes.getResourceId(R.styleable.MenuGroup_android_id, 0);
            this.groupCategory = obtainStyledAttributes.getInt(R.styleable.MenuGroup_android_menuCategory, 0);
            this.groupOrder = obtainStyledAttributes.getInt(R.styleable.MenuGroup_android_orderInCategory, 0);
            this.groupCheckable = obtainStyledAttributes.getInt(R.styleable.MenuGroup_android_checkableBehavior, 0);
            this.groupVisible = obtainStyledAttributes.getBoolean(R.styleable.MenuGroup_android_visible, true);
            this.groupEnabled = obtainStyledAttributes.getBoolean(R.styleable.MenuGroup_android_enabled, true);
            obtainStyledAttributes.recycle();
        }

        public void readItem(AttributeSet attributeSet) {
            TypedArray obtainStyledAttributes = SupportMenuInflater.this.mContext.obtainStyledAttributes(attributeSet, R.styleable.MenuItem);
            this.itemId = obtainStyledAttributes.getResourceId(R.styleable.MenuItem_android_id, 0);
            this.itemCategoryOrder = (obtainStyledAttributes.getInt(R.styleable.MenuItem_android_menuCategory, this.groupCategory) & SupportMenu.CATEGORY_MASK) | (obtainStyledAttributes.getInt(R.styleable.MenuItem_android_orderInCategory, this.groupOrder) & SupportMenu.USER_MASK);
            this.itemTitle = obtainStyledAttributes.getText(R.styleable.MenuItem_android_title);
            this.itemTitleCondensed = obtainStyledAttributes.getText(R.styleable.MenuItem_android_titleCondensed);
            this.itemIconResId = obtainStyledAttributes.getResourceId(R.styleable.MenuItem_android_icon, 0);
            this.itemAlphabeticShortcut = getShortcut(obtainStyledAttributes.getString(R.styleable.MenuItem_android_alphabeticShortcut));
            this.itemNumericShortcut = getShortcut(obtainStyledAttributes.getString(R.styleable.MenuItem_android_numericShortcut));
            if (obtainStyledAttributes.hasValue(R.styleable.MenuItem_android_checkable)) {
                this.itemCheckable = obtainStyledAttributes.getBoolean(R.styleable.MenuItem_android_checkable, false) ? 1 : 0;
            } else {
                this.itemCheckable = this.groupCheckable;
            }
            this.itemChecked = obtainStyledAttributes.getBoolean(R.styleable.MenuItem_android_checked, false);
            this.itemVisible = obtainStyledAttributes.getBoolean(R.styleable.MenuItem_android_visible, this.groupVisible);
            this.itemEnabled = obtainStyledAttributes.getBoolean(R.styleable.MenuItem_android_enabled, this.groupEnabled);
            this.itemShowAsAction = obtainStyledAttributes.getInt(R.styleable.MenuItem_showAsAction, -1);
            this.itemListenerMethodName = obtainStyledAttributes.getString(R.styleable.MenuItem_android_onClick);
            this.itemActionViewLayout = obtainStyledAttributes.getResourceId(R.styleable.MenuItem_actionLayout, 0);
            this.itemActionViewClassName = obtainStyledAttributes.getString(R.styleable.MenuItem_actionViewClass);
            this.itemActionProviderClassName = obtainStyledAttributes.getString(R.styleable.MenuItem_actionProviderClass);
            boolean z = this.itemActionProviderClassName != null;
            if (z && this.itemActionViewLayout == 0 && this.itemActionViewClassName == null) {
                this.itemActionProvider = (ActionProvider) newInstance(this.itemActionProviderClassName, SupportMenuInflater.ACTION_PROVIDER_CONSTRUCTOR_SIGNATURE, SupportMenuInflater.this.mActionProviderConstructorArguments);
            } else {
                if (z) {
                    Log.w(SupportMenuInflater.LOG_TAG, "Ignoring attribute 'actionProviderClass'. Action view already specified.");
                }
                this.itemActionProvider = null;
            }
            obtainStyledAttributes.recycle();
            this.itemAdded = false;
        }

        private char getShortcut(String str) {
            if (str == null) {
                return (char) 0;
            }
            return str.charAt(0);
        }

        private void setItem(MenuItem menuItem) {
            boolean z = false;
            menuItem.setChecked(this.itemChecked).setVisible(this.itemVisible).setEnabled(this.itemEnabled).setCheckable(this.itemCheckable >= 1).setTitleCondensed(this.itemTitleCondensed).setIcon(this.itemIconResId).setAlphabeticShortcut(this.itemAlphabeticShortcut).setNumericShortcut(this.itemNumericShortcut);
            if (this.itemShowAsAction >= 0) {
                MenuItemCompat.setShowAsAction(menuItem, this.itemShowAsAction);
            }
            if (this.itemListenerMethodName != null) {
                if (SupportMenuInflater.this.mContext.isRestricted()) {
                    throw new IllegalStateException("The android:onClick attribute cannot be used within a restricted context");
                }
                menuItem.setOnMenuItemClickListener(new InflatedOnMenuItemClickListener(SupportMenuInflater.this.getRealOwner(), this.itemListenerMethodName));
            }
            boolean z2 = menuItem instanceof MenuItemImpl;
            if (z2) {
            }
            if (this.itemCheckable >= 2) {
                if (z2) {
                    ((MenuItemImpl) menuItem).setExclusiveCheckable(true);
                } else if (menuItem instanceof MenuItemWrapperICS) {
                    ((MenuItemWrapperICS) menuItem).setExclusiveCheckable(true);
                }
            }
            if (this.itemActionViewClassName != null) {
                MenuItemCompat.setActionView(menuItem, (View) newInstance(this.itemActionViewClassName, SupportMenuInflater.ACTION_VIEW_CONSTRUCTOR_SIGNATURE, SupportMenuInflater.this.mActionViewConstructorArguments));
                z = true;
            }
            if (this.itemActionViewLayout > 0) {
                if (!z) {
                    MenuItemCompat.setActionView(menuItem, this.itemActionViewLayout);
                } else {
                    Log.w(SupportMenuInflater.LOG_TAG, "Ignoring attribute 'itemActionViewLayout'. Action view already specified.");
                }
            }
            if (this.itemActionProvider != null) {
                MenuItemCompat.setActionProvider(menuItem, this.itemActionProvider);
            }
        }

        public void addItem() {
            this.itemAdded = true;
            setItem(this.menu.add(this.groupId, this.itemId, this.itemCategoryOrder, this.itemTitle));
        }

        public SubMenu addSubMenuItem() {
            this.itemAdded = true;
            SubMenu addSubMenu = this.menu.addSubMenu(this.groupId, this.itemId, this.itemCategoryOrder, this.itemTitle);
            setItem(addSubMenu.getItem());
            return addSubMenu;
        }

        public boolean hasAddedItem() {
            return this.itemAdded;
        }

        private <T> T newInstance(String str, Class<?>[] clsArr, Object[] objArr) {
            try {
                Constructor<?> constructor = SupportMenuInflater.this.mContext.getClassLoader().loadClass(str).getConstructor(clsArr);
                constructor.setAccessible(true);
                return (T) constructor.newInstance(objArr);
            } catch (Exception e) {
                Log.w(SupportMenuInflater.LOG_TAG, "Cannot instantiate class: " + str, e);
                return null;
            }
        }
    }
}
