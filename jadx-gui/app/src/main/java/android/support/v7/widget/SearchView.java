package android.support.v7.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.app.SearchableInfo;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.view.KeyEventCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.appcompat.R;
import android.support.v7.view.CollapsibleActionView;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import java.lang.reflect.Method;
import java.util.WeakHashMap;

/* loaded from: classes.dex */
public class SearchView extends LinearLayoutCompat implements CollapsibleActionView {
    private static final boolean DBG = false;
    static final AutoCompleteTextViewReflector HIDDEN_METHOD_INVOKER;
    private static final String IME_OPTION_NO_MICROPHONE = "nm";
    private static final boolean IS_AT_LEAST_FROYO;
    private static final String LOG_TAG = "SearchView";
    private Bundle mAppSearchData;
    private boolean mClearingFocus;
    private final ImageView mCloseButton;
    private final ImageView mCollapsedIcon;
    private int mCollapsedImeOptions;
    private final CharSequence mDefaultQueryHint;
    private final View mDropDownAnchor;
    private boolean mExpandedInActionView;
    private final ImageView mGoButton;
    private boolean mIconified;
    private boolean mIconifiedByDefault;
    private int mMaxWidth;
    private CharSequence mOldQueryText;
    private final View.OnClickListener mOnClickListener;
    private OnCloseListener mOnCloseListener;
    private final TextView.OnEditorActionListener mOnEditorActionListener;
    private final AdapterView.OnItemClickListener mOnItemClickListener;
    private final AdapterView.OnItemSelectedListener mOnItemSelectedListener;
    private OnQueryTextListener mOnQueryChangeListener;
    private View.OnFocusChangeListener mOnQueryTextFocusChangeListener;
    private View.OnClickListener mOnSearchClickListener;
    private OnSuggestionListener mOnSuggestionListener;
    private final WeakHashMap<String, Drawable.ConstantState> mOutsideDrawablesCache;
    private CharSequence mQueryHint;
    private boolean mQueryRefinement;
    private Runnable mReleaseCursorRunnable;
    private final ImageView mSearchButton;
    private final View mSearchEditFrame;
    private final Drawable mSearchHintIcon;
    private final View mSearchPlate;
    private final SearchAutoComplete mSearchSrcTextView;
    private SearchableInfo mSearchable;
    private Runnable mShowImeRunnable;
    private final View mSubmitArea;
    private boolean mSubmitButtonEnabled;
    private final int mSuggestionCommitIconResId;
    private final int mSuggestionRowLayout;
    private CursorAdapter mSuggestionsAdapter;
    View.OnKeyListener mTextKeyListener;
    private TextWatcher mTextWatcher;
    private final TintManager mTintManager;
    private final Runnable mUpdateDrawableStateRunnable;
    private CharSequence mUserQuery;
    private final Intent mVoiceAppSearchIntent;
    private final ImageView mVoiceButton;
    private boolean mVoiceButtonEnabled;
    private final Intent mVoiceWebSearchIntent;

    /* loaded from: classes.dex */
    public interface OnCloseListener {
        boolean onClose();
    }

    /* loaded from: classes.dex */
    public interface OnQueryTextListener {
        boolean onQueryTextChange(String str);

        boolean onQueryTextSubmit(String str);
    }

    /* loaded from: classes.dex */
    public interface OnSuggestionListener {
        boolean onSuggestionClick(int i);

        boolean onSuggestionSelect(int i);
    }

    static {
        IS_AT_LEAST_FROYO = Build.VERSION.SDK_INT >= 8;
        HIDDEN_METHOD_INVOKER = new AutoCompleteTextViewReflector();
    }

    public SearchView(Context context) {
        this(context, null);
    }

    public SearchView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.searchViewStyle);
    }

    public SearchView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mShowImeRunnable = new Runnable() { // from class: android.support.v7.widget.SearchView.1
            @Override // java.lang.Runnable
            public void run() {
                InputMethodManager inputMethodManager = (InputMethodManager) SearchView.this.getContext().getSystemService("input_method");
                if (inputMethodManager != null) {
                    SearchView.HIDDEN_METHOD_INVOKER.showSoftInputUnchecked(inputMethodManager, SearchView.this, 0);
                }
            }
        };
        this.mUpdateDrawableStateRunnable = new Runnable() { // from class: android.support.v7.widget.SearchView.2
            @Override // java.lang.Runnable
            public void run() {
                SearchView.this.updateFocusedState();
            }
        };
        this.mReleaseCursorRunnable = new Runnable() { // from class: android.support.v7.widget.SearchView.3
            @Override // java.lang.Runnable
            public void run() {
                if (SearchView.this.mSuggestionsAdapter == null || !(SearchView.this.mSuggestionsAdapter instanceof SuggestionsAdapter)) {
                    return;
                }
                SearchView.this.mSuggestionsAdapter.changeCursor(null);
            }
        };
        this.mOutsideDrawablesCache = new WeakHashMap<>();
        this.mOnClickListener = new View.OnClickListener() { // from class: android.support.v7.widget.SearchView.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (view == SearchView.this.mSearchButton) {
                    SearchView.this.onSearchClicked();
                    return;
                }
                if (view == SearchView.this.mCloseButton) {
                    SearchView.this.onCloseClicked();
                    return;
                }
                if (view == SearchView.this.mGoButton) {
                    SearchView.this.onSubmitQuery();
                } else if (view == SearchView.this.mVoiceButton) {
                    SearchView.this.onVoiceClicked();
                } else if (view == SearchView.this.mSearchSrcTextView) {
                    SearchView.this.forceSuggestionQuery();
                }
            }
        };
        this.mTextKeyListener = new View.OnKeyListener() { // from class: android.support.v7.widget.SearchView.8
            @Override // android.view.View.OnKeyListener
            public boolean onKey(View view, int i2, KeyEvent keyEvent) {
                if (SearchView.this.mSearchable == null) {
                    return false;
                }
                if (SearchView.this.mSearchSrcTextView.isPopupShowing() && SearchView.this.mSearchSrcTextView.getListSelection() != -1) {
                    return SearchView.this.onSuggestionsKey(view, i2, keyEvent);
                }
                if (SearchView.this.mSearchSrcTextView.isEmpty() || !KeyEventCompat.hasNoModifiers(keyEvent) || keyEvent.getAction() != 1 || i2 != 66) {
                    return false;
                }
                view.cancelLongPress();
                SearchView.this.launchQuerySearch(0, null, SearchView.this.mSearchSrcTextView.getText().toString());
                return true;
            }
        };
        this.mOnEditorActionListener = new TextView.OnEditorActionListener() { // from class: android.support.v7.widget.SearchView.9
            @Override // android.widget.TextView.OnEditorActionListener
            public boolean onEditorAction(TextView textView, int i2, KeyEvent keyEvent) {
                SearchView.this.onSubmitQuery();
                return true;
            }
        };
        this.mOnItemClickListener = new AdapterView.OnItemClickListener() { // from class: android.support.v7.widget.SearchView.10
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i2, long j) {
                SearchView.this.onItemClicked(i2, 0, null);
            }
        };
        this.mOnItemSelectedListener = new AdapterView.OnItemSelectedListener() { // from class: android.support.v7.widget.SearchView.11
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i2, long j) {
                SearchView.this.onItemSelected(i2);
            }
        };
        this.mTextWatcher = new TextWatcher() { // from class: android.support.v7.widget.SearchView.12
            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
                SearchView.this.onTextChanged(charSequence);
            }
        };
        TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(context, attributeSet, R.styleable.SearchView, i, 0);
        this.mTintManager = obtainStyledAttributes.getTintManager();
        LayoutInflater.from(context).inflate(obtainStyledAttributes.getResourceId(R.styleable.SearchView_layout, R.layout.abc_search_view), (ViewGroup) this, true);
        this.mSearchSrcTextView = (SearchAutoComplete) findViewById(R.id.search_src_text);
        this.mSearchSrcTextView.setSearchView(this);
        this.mSearchEditFrame = findViewById(R.id.search_edit_frame);
        this.mSearchPlate = findViewById(R.id.search_plate);
        this.mSubmitArea = findViewById(R.id.submit_area);
        this.mSearchButton = (ImageView) findViewById(R.id.search_button);
        this.mGoButton = (ImageView) findViewById(R.id.search_go_btn);
        this.mCloseButton = (ImageView) findViewById(R.id.search_close_btn);
        this.mVoiceButton = (ImageView) findViewById(R.id.search_voice_btn);
        this.mCollapsedIcon = (ImageView) findViewById(R.id.search_mag_icon);
        this.mSearchPlate.setBackgroundDrawable(obtainStyledAttributes.getDrawable(R.styleable.SearchView_queryBackground));
        this.mSubmitArea.setBackgroundDrawable(obtainStyledAttributes.getDrawable(R.styleable.SearchView_submitBackground));
        this.mSearchButton.setImageDrawable(obtainStyledAttributes.getDrawable(R.styleable.SearchView_searchIcon));
        this.mGoButton.setImageDrawable(obtainStyledAttributes.getDrawable(R.styleable.SearchView_goIcon));
        this.mCloseButton.setImageDrawable(obtainStyledAttributes.getDrawable(R.styleable.SearchView_closeIcon));
        this.mVoiceButton.setImageDrawable(obtainStyledAttributes.getDrawable(R.styleable.SearchView_voiceIcon));
        this.mCollapsedIcon.setImageDrawable(obtainStyledAttributes.getDrawable(R.styleable.SearchView_searchIcon));
        this.mSearchHintIcon = obtainStyledAttributes.getDrawable(R.styleable.SearchView_searchHintIcon);
        this.mSuggestionRowLayout = obtainStyledAttributes.getResourceId(R.styleable.SearchView_suggestionRowLayout, R.layout.abc_search_dropdown_item_icons_2line);
        this.mSuggestionCommitIconResId = obtainStyledAttributes.getResourceId(R.styleable.SearchView_commitIcon, 0);
        this.mSearchButton.setOnClickListener(this.mOnClickListener);
        this.mCloseButton.setOnClickListener(this.mOnClickListener);
        this.mGoButton.setOnClickListener(this.mOnClickListener);
        this.mVoiceButton.setOnClickListener(this.mOnClickListener);
        this.mSearchSrcTextView.setOnClickListener(this.mOnClickListener);
        this.mSearchSrcTextView.addTextChangedListener(this.mTextWatcher);
        this.mSearchSrcTextView.setOnEditorActionListener(this.mOnEditorActionListener);
        this.mSearchSrcTextView.setOnItemClickListener(this.mOnItemClickListener);
        this.mSearchSrcTextView.setOnItemSelectedListener(this.mOnItemSelectedListener);
        this.mSearchSrcTextView.setOnKeyListener(this.mTextKeyListener);
        this.mSearchSrcTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: android.support.v7.widget.SearchView.4
            @Override // android.view.View.OnFocusChangeListener
            public void onFocusChange(View view, boolean z) {
                if (SearchView.this.mOnQueryTextFocusChangeListener != null) {
                    SearchView.this.mOnQueryTextFocusChangeListener.onFocusChange(SearchView.this, z);
                }
            }
        });
        setIconifiedByDefault(obtainStyledAttributes.getBoolean(R.styleable.SearchView_iconifiedByDefault, true));
        int dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(R.styleable.SearchView_android_maxWidth, -1);
        if (dimensionPixelSize != -1) {
            setMaxWidth(dimensionPixelSize);
        }
        this.mDefaultQueryHint = obtainStyledAttributes.getText(R.styleable.SearchView_defaultQueryHint);
        this.mQueryHint = obtainStyledAttributes.getText(R.styleable.SearchView_queryHint);
        int i2 = obtainStyledAttributes.getInt(R.styleable.SearchView_android_imeOptions, -1);
        if (i2 != -1) {
            setImeOptions(i2);
        }
        int i3 = obtainStyledAttributes.getInt(R.styleable.SearchView_android_inputType, -1);
        if (i3 != -1) {
            setInputType(i3);
        }
        setFocusable(obtainStyledAttributes.getBoolean(R.styleable.SearchView_android_focusable, true));
        obtainStyledAttributes.recycle();
        this.mVoiceWebSearchIntent = new Intent("android.speech.action.WEB_SEARCH");
        this.mVoiceWebSearchIntent.addFlags(268435456);
        this.mVoiceWebSearchIntent.putExtra("android.speech.extra.LANGUAGE_MODEL", "web_search");
        this.mVoiceAppSearchIntent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
        this.mVoiceAppSearchIntent.addFlags(268435456);
        this.mDropDownAnchor = findViewById(this.mSearchSrcTextView.getDropDownAnchor());
        if (this.mDropDownAnchor != null) {
            if (Build.VERSION.SDK_INT >= 11) {
                addOnLayoutChangeListenerToDropDownAnchorSDK11();
            } else {
                addOnLayoutChangeListenerToDropDownAnchorBase();
            }
        }
        updateViewsVisibility(this.mIconifiedByDefault);
        updateQueryHint();
    }

    @TargetApi(11)
    private void addOnLayoutChangeListenerToDropDownAnchorSDK11() {
        this.mDropDownAnchor.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: android.support.v7.widget.SearchView.5
            @Override // android.view.View.OnLayoutChangeListener
            public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                SearchView.this.adjustDropDownSizeAndPosition();
            }
        });
    }

    private void addOnLayoutChangeListenerToDropDownAnchorBase() {
        this.mDropDownAnchor.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: android.support.v7.widget.SearchView.6
            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public void onGlobalLayout() {
                SearchView.this.adjustDropDownSizeAndPosition();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getSuggestionRowLayout() {
        return this.mSuggestionRowLayout;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getSuggestionCommitIconResId() {
        return this.mSuggestionCommitIconResId;
    }

    public void setSearchableInfo(SearchableInfo searchableInfo) {
        this.mSearchable = searchableInfo;
        if (this.mSearchable != null) {
            if (IS_AT_LEAST_FROYO) {
                updateSearchAutoComplete();
            }
            updateQueryHint();
        }
        this.mVoiceButtonEnabled = IS_AT_LEAST_FROYO && hasVoiceSearch();
        if (this.mVoiceButtonEnabled) {
            this.mSearchSrcTextView.setPrivateImeOptions(IME_OPTION_NO_MICROPHONE);
        }
        updateViewsVisibility(isIconified());
    }

    public void setAppSearchData(Bundle bundle) {
        this.mAppSearchData = bundle;
    }

    public void setImeOptions(int i) {
        this.mSearchSrcTextView.setImeOptions(i);
    }

    public int getImeOptions() {
        return this.mSearchSrcTextView.getImeOptions();
    }

    public void setInputType(int i) {
        this.mSearchSrcTextView.setInputType(i);
    }

    public int getInputType() {
        return this.mSearchSrcTextView.getInputType();
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean requestFocus(int i, Rect rect) {
        if (this.mClearingFocus || !isFocusable()) {
            return false;
        }
        if (!isIconified()) {
            boolean requestFocus = this.mSearchSrcTextView.requestFocus(i, rect);
            if (requestFocus) {
                updateViewsVisibility(false);
            }
            return requestFocus;
        }
        return super.requestFocus(i, rect);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void clearFocus() {
        this.mClearingFocus = true;
        setImeVisibility(false);
        super.clearFocus();
        this.mSearchSrcTextView.clearFocus();
        this.mClearingFocus = false;
    }

    public void setOnQueryTextListener(OnQueryTextListener onQueryTextListener) {
        this.mOnQueryChangeListener = onQueryTextListener;
    }

    public void setOnCloseListener(OnCloseListener onCloseListener) {
        this.mOnCloseListener = onCloseListener;
    }

    public void setOnQueryTextFocusChangeListener(View.OnFocusChangeListener onFocusChangeListener) {
        this.mOnQueryTextFocusChangeListener = onFocusChangeListener;
    }

    public void setOnSuggestionListener(OnSuggestionListener onSuggestionListener) {
        this.mOnSuggestionListener = onSuggestionListener;
    }

    public void setOnSearchClickListener(View.OnClickListener onClickListener) {
        this.mOnSearchClickListener = onClickListener;
    }

    public CharSequence getQuery() {
        return this.mSearchSrcTextView.getText();
    }

    public void setQuery(CharSequence charSequence, boolean z) {
        this.mSearchSrcTextView.setText(charSequence);
        if (charSequence != null) {
            this.mSearchSrcTextView.setSelection(this.mSearchSrcTextView.length());
            this.mUserQuery = charSequence;
        }
        if (!z || TextUtils.isEmpty(charSequence)) {
            return;
        }
        onSubmitQuery();
    }

    public void setQueryHint(CharSequence charSequence) {
        this.mQueryHint = charSequence;
        updateQueryHint();
    }

    public CharSequence getQueryHint() {
        if (this.mQueryHint != null) {
            return this.mQueryHint;
        }
        if (IS_AT_LEAST_FROYO && this.mSearchable != null && this.mSearchable.getHintId() != 0) {
            return getContext().getText(this.mSearchable.getHintId());
        }
        return this.mDefaultQueryHint;
    }

    public void setIconifiedByDefault(boolean z) {
        if (this.mIconifiedByDefault == z) {
            return;
        }
        this.mIconifiedByDefault = z;
        updateViewsVisibility(z);
        updateQueryHint();
    }

    public boolean isIconfiedByDefault() {
        return this.mIconifiedByDefault;
    }

    public void setIconified(boolean z) {
        if (z) {
            onCloseClicked();
        } else {
            onSearchClicked();
        }
    }

    public boolean isIconified() {
        return this.mIconified;
    }

    public void setSubmitButtonEnabled(boolean z) {
        this.mSubmitButtonEnabled = z;
        updateViewsVisibility(isIconified());
    }

    public boolean isSubmitButtonEnabled() {
        return this.mSubmitButtonEnabled;
    }

    public void setQueryRefinementEnabled(boolean z) {
        this.mQueryRefinement = z;
        if (this.mSuggestionsAdapter instanceof SuggestionsAdapter) {
            ((SuggestionsAdapter) this.mSuggestionsAdapter).setQueryRefinement(z ? 2 : 1);
        }
    }

    public boolean isQueryRefinementEnabled() {
        return this.mQueryRefinement;
    }

    public void setSuggestionsAdapter(CursorAdapter cursorAdapter) {
        this.mSuggestionsAdapter = cursorAdapter;
        this.mSearchSrcTextView.setAdapter(this.mSuggestionsAdapter);
    }

    public CursorAdapter getSuggestionsAdapter() {
        return this.mSuggestionsAdapter;
    }

    public void setMaxWidth(int i) {
        this.mMaxWidth = i;
        requestLayout();
    }

    public int getMaxWidth() {
        return this.mMaxWidth;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.widget.LinearLayoutCompat, android.view.View
    public void onMeasure(int i, int i2) {
        if (isIconified()) {
            super.onMeasure(i, i2);
            return;
        }
        int mode = View.MeasureSpec.getMode(i);
        int size = View.MeasureSpec.getSize(i);
        if (mode == Integer.MIN_VALUE) {
            size = this.mMaxWidth > 0 ? Math.min(this.mMaxWidth, size) : Math.min(getPreferredWidth(), size);
        } else if (mode != 0) {
            if (mode == 1073741824 && this.mMaxWidth > 0) {
                size = Math.min(this.mMaxWidth, size);
            }
        } else {
            size = this.mMaxWidth > 0 ? this.mMaxWidth : getPreferredWidth();
        }
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(size, 1073741824), i2);
    }

    private int getPreferredWidth() {
        return getContext().getResources().getDimensionPixelSize(R.dimen.abc_search_view_preferred_width);
    }

    private void updateViewsVisibility(boolean z) {
        this.mIconified = z;
        int i = 8;
        int i2 = z ? 0 : 8;
        boolean z2 = !TextUtils.isEmpty(this.mSearchSrcTextView.getText());
        this.mSearchButton.setVisibility(i2);
        updateSubmitButton(z2);
        this.mSearchEditFrame.setVisibility(z ? 8 : 0);
        if (this.mCollapsedIcon.getDrawable() != null && !this.mIconifiedByDefault) {
            i = 0;
        }
        this.mCollapsedIcon.setVisibility(i);
        updateCloseButton();
        updateVoiceButton(z2 ? false : true);
        updateSubmitArea();
    }

    @TargetApi(8)
    private boolean hasVoiceSearch() {
        if (this.mSearchable != null && this.mSearchable.getVoiceSearchEnabled()) {
            Intent intent = null;
            if (this.mSearchable.getVoiceSearchLaunchWebSearch()) {
                intent = this.mVoiceWebSearchIntent;
            } else if (this.mSearchable.getVoiceSearchLaunchRecognizer()) {
                intent = this.mVoiceAppSearchIntent;
            }
            return (intent == null || getContext().getPackageManager().resolveActivity(intent, 65536) == null) ? false : true;
        }
        return false;
    }

    private boolean isSubmitAreaEnabled() {
        return (this.mSubmitButtonEnabled || this.mVoiceButtonEnabled) && !isIconified();
    }

    private void updateSubmitButton(boolean z) {
        this.mGoButton.setVisibility((this.mSubmitButtonEnabled && isSubmitAreaEnabled() && hasFocus() && (z || !this.mVoiceButtonEnabled)) ? 0 : 8);
    }

    private void updateSubmitArea() {
        this.mSubmitArea.setVisibility((isSubmitAreaEnabled() && (this.mGoButton.getVisibility() == 0 || this.mVoiceButton.getVisibility() == 0)) ? 0 : 8);
    }

    private void updateCloseButton() {
        boolean z = true;
        boolean z2 = !TextUtils.isEmpty(this.mSearchSrcTextView.getText());
        if (!z2 && (!this.mIconifiedByDefault || this.mExpandedInActionView)) {
            z = false;
        }
        this.mCloseButton.setVisibility(z ? 0 : 8);
        Drawable drawable = this.mCloseButton.getDrawable();
        if (drawable != null) {
            drawable.setState(z2 ? ENABLED_STATE_SET : EMPTY_STATE_SET);
        }
    }

    private void postUpdateFocusedState() {
        post(this.mUpdateDrawableStateRunnable);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateFocusedState() {
        int[] iArr = this.mSearchSrcTextView.hasFocus() ? FOCUSED_STATE_SET : EMPTY_STATE_SET;
        Drawable background = this.mSearchPlate.getBackground();
        if (background != null) {
            background.setState(iArr);
        }
        Drawable background2 = this.mSubmitArea.getBackground();
        if (background2 != null) {
            background2.setState(iArr);
        }
        invalidate();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        removeCallbacks(this.mUpdateDrawableStateRunnable);
        post(this.mReleaseCursorRunnable);
        super.onDetachedFromWindow();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setImeVisibility(boolean z) {
        if (z) {
            post(this.mShowImeRunnable);
            return;
        }
        removeCallbacks(this.mShowImeRunnable);
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService("input_method");
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onQueryRefine(CharSequence charSequence) {
        setQuery(charSequence);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean onSuggestionsKey(View view, int i, KeyEvent keyEvent) {
        if (this.mSearchable != null && this.mSuggestionsAdapter != null && keyEvent.getAction() == 0 && KeyEventCompat.hasNoModifiers(keyEvent)) {
            if (i == 66 || i == 84 || i == 61) {
                return onItemClicked(this.mSearchSrcTextView.getListSelection(), 0, null);
            }
            if (i == 21 || i == 22) {
                this.mSearchSrcTextView.setSelection(i == 21 ? 0 : this.mSearchSrcTextView.length());
                this.mSearchSrcTextView.setListSelection(0);
                this.mSearchSrcTextView.clearListSelection();
                HIDDEN_METHOD_INVOKER.ensureImeVisible(this.mSearchSrcTextView, true);
                return true;
            }
            if (i != 19 || this.mSearchSrcTextView.getListSelection() == 0) {
                return false;
            }
        }
        return false;
    }

    private CharSequence getDecoratedHint(CharSequence charSequence) {
        if (!this.mIconifiedByDefault || this.mSearchHintIcon == null) {
            return charSequence;
        }
        double textSize = this.mSearchSrcTextView.getTextSize();
        Double.isNaN(textSize);
        int i = (int) (textSize * 1.25d);
        this.mSearchHintIcon.setBounds(0, 0, i, i);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("   ");
        spannableStringBuilder.setSpan(new ImageSpan(this.mSearchHintIcon), 1, 2, 33);
        spannableStringBuilder.append(charSequence);
        return spannableStringBuilder;
    }

    private void updateQueryHint() {
        CharSequence queryHint = getQueryHint();
        SearchAutoComplete searchAutoComplete = this.mSearchSrcTextView;
        if (queryHint == null) {
            queryHint = "";
        }
        searchAutoComplete.setHint(getDecoratedHint(queryHint));
    }

    @TargetApi(8)
    private void updateSearchAutoComplete() {
        this.mSearchSrcTextView.setThreshold(this.mSearchable.getSuggestThreshold());
        this.mSearchSrcTextView.setImeOptions(this.mSearchable.getImeOptions());
        int inputType = this.mSearchable.getInputType();
        if ((inputType & 15) == 1) {
            inputType &= -65537;
            if (this.mSearchable.getSuggestAuthority() != null) {
                inputType = inputType | 65536 | 524288;
            }
        }
        this.mSearchSrcTextView.setInputType(inputType);
        if (this.mSuggestionsAdapter != null) {
            this.mSuggestionsAdapter.changeCursor(null);
        }
        if (this.mSearchable.getSuggestAuthority() != null) {
            this.mSuggestionsAdapter = new SuggestionsAdapter(getContext(), this, this.mSearchable, this.mOutsideDrawablesCache);
            this.mSearchSrcTextView.setAdapter(this.mSuggestionsAdapter);
            ((SuggestionsAdapter) this.mSuggestionsAdapter).setQueryRefinement(this.mQueryRefinement ? 2 : 1);
        }
    }

    private void updateVoiceButton(boolean z) {
        int i;
        if (this.mVoiceButtonEnabled && !isIconified() && z) {
            i = 0;
            this.mGoButton.setVisibility(8);
        } else {
            i = 8;
        }
        this.mVoiceButton.setVisibility(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onTextChanged(CharSequence charSequence) {
        Editable text = this.mSearchSrcTextView.getText();
        this.mUserQuery = text;
        boolean z = !TextUtils.isEmpty(text);
        updateSubmitButton(z);
        updateVoiceButton(z ? false : true);
        updateCloseButton();
        updateSubmitArea();
        if (this.mOnQueryChangeListener != null && !TextUtils.equals(charSequence, this.mOldQueryText)) {
            this.mOnQueryChangeListener.onQueryTextChange(charSequence.toString());
        }
        this.mOldQueryText = charSequence.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSubmitQuery() {
        Editable text = this.mSearchSrcTextView.getText();
        if (text == null || TextUtils.getTrimmedLength(text) <= 0) {
            return;
        }
        if (this.mOnQueryChangeListener == null || !this.mOnQueryChangeListener.onQueryTextSubmit(text.toString())) {
            if (this.mSearchable != null) {
                launchQuerySearch(0, null, text.toString());
            }
            setImeVisibility(false);
            dismissSuggestions();
        }
    }

    private void dismissSuggestions() {
        this.mSearchSrcTextView.dismissDropDown();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onCloseClicked() {
        if (TextUtils.isEmpty(this.mSearchSrcTextView.getText())) {
            if (this.mIconifiedByDefault) {
                if (this.mOnCloseListener == null || !this.mOnCloseListener.onClose()) {
                    clearFocus();
                    updateViewsVisibility(true);
                    return;
                }
                return;
            }
            return;
        }
        this.mSearchSrcTextView.setText("");
        this.mSearchSrcTextView.requestFocus();
        setImeVisibility(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSearchClicked() {
        updateViewsVisibility(false);
        this.mSearchSrcTextView.requestFocus();
        setImeVisibility(true);
        if (this.mOnSearchClickListener != null) {
            this.mOnSearchClickListener.onClick(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @TargetApi(8)
    public void onVoiceClicked() {
        if (this.mSearchable == null) {
            return;
        }
        SearchableInfo searchableInfo = this.mSearchable;
        try {
            if (searchableInfo.getVoiceSearchLaunchWebSearch()) {
                getContext().startActivity(createVoiceWebSearchIntent(this.mVoiceWebSearchIntent, searchableInfo));
            } else if (searchableInfo.getVoiceSearchLaunchRecognizer()) {
                getContext().startActivity(createVoiceAppSearchIntent(this.mVoiceAppSearchIntent, searchableInfo));
            }
        } catch (ActivityNotFoundException unused) {
            Log.w(LOG_TAG, "Could not find voice search activity");
        }
    }

    void onTextFocusChanged() {
        updateViewsVisibility(isIconified());
        postUpdateFocusedState();
        if (this.mSearchSrcTextView.hasFocus()) {
            forceSuggestionQuery();
        }
    }

    @Override // android.view.View
    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        postUpdateFocusedState();
    }

    @Override // android.support.v7.view.CollapsibleActionView
    public void onActionViewCollapsed() {
        setQuery("", false);
        clearFocus();
        updateViewsVisibility(true);
        this.mSearchSrcTextView.setImeOptions(this.mCollapsedImeOptions);
        this.mExpandedInActionView = false;
    }

    @Override // android.support.v7.view.CollapsibleActionView
    public void onActionViewExpanded() {
        if (this.mExpandedInActionView) {
            return;
        }
        this.mExpandedInActionView = true;
        this.mCollapsedImeOptions = this.mSearchSrcTextView.getImeOptions();
        this.mSearchSrcTextView.setImeOptions(this.mCollapsedImeOptions | 33554432);
        this.mSearchSrcTextView.setText("");
        setIconified(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void adjustDropDownSizeAndPosition() {
        int i;
        if (this.mDropDownAnchor.getWidth() > 1) {
            Resources resources = getContext().getResources();
            int paddingLeft = this.mSearchPlate.getPaddingLeft();
            Rect rect = new Rect();
            boolean isLayoutRtl = ViewUtils.isLayoutRtl(this);
            int dimensionPixelSize = this.mIconifiedByDefault ? resources.getDimensionPixelSize(R.dimen.abc_dropdownitem_icon_width) + resources.getDimensionPixelSize(R.dimen.abc_dropdownitem_text_padding_left) : 0;
            this.mSearchSrcTextView.getDropDownBackground().getPadding(rect);
            if (isLayoutRtl) {
                i = -rect.left;
            } else {
                i = paddingLeft - (rect.left + dimensionPixelSize);
            }
            this.mSearchSrcTextView.setDropDownHorizontalOffset(i);
            this.mSearchSrcTextView.setDropDownWidth((((this.mDropDownAnchor.getWidth() + rect.left) + rect.right) + dimensionPixelSize) - paddingLeft);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean onItemClicked(int i, int i2, String str) {
        if (this.mOnSuggestionListener != null && this.mOnSuggestionListener.onSuggestionClick(i)) {
            return false;
        }
        launchSuggestion(i, 0, null);
        setImeVisibility(false);
        dismissSuggestions();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean onItemSelected(int i) {
        if (this.mOnSuggestionListener != null && this.mOnSuggestionListener.onSuggestionSelect(i)) {
            return false;
        }
        rewriteQueryFromSuggestion(i);
        return true;
    }

    private void rewriteQueryFromSuggestion(int i) {
        Editable text = this.mSearchSrcTextView.getText();
        Cursor cursor = this.mSuggestionsAdapter.getCursor();
        if (cursor == null) {
            return;
        }
        if (cursor.moveToPosition(i)) {
            CharSequence convertToString = this.mSuggestionsAdapter.convertToString(cursor);
            if (convertToString != null) {
                setQuery(convertToString);
                return;
            } else {
                setQuery(text);
                return;
            }
        }
        setQuery(text);
    }

    private boolean launchSuggestion(int i, int i2, String str) {
        Cursor cursor = this.mSuggestionsAdapter.getCursor();
        if (cursor == null || !cursor.moveToPosition(i)) {
            return false;
        }
        launchIntent(createIntentFromSuggestion(cursor, i2, str));
        return true;
    }

    private void launchIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        try {
            getContext().startActivity(intent);
        } catch (RuntimeException e) {
            Log.e(LOG_TAG, "Failed launch activity: " + intent, e);
        }
    }

    private void setQuery(CharSequence charSequence) {
        this.mSearchSrcTextView.setText(charSequence);
        this.mSearchSrcTextView.setSelection(TextUtils.isEmpty(charSequence) ? 0 : charSequence.length());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void launchQuerySearch(int i, String str, String str2) {
        getContext().startActivity(createIntent("android.intent.action.SEARCH", null, null, str2, i, str));
    }

    private Intent createIntent(String str, Uri uri, String str2, String str3, int i, String str4) {
        Intent intent = new Intent(str);
        intent.addFlags(268435456);
        if (uri != null) {
            intent.setData(uri);
        }
        intent.putExtra("user_query", this.mUserQuery);
        if (str3 != null) {
            intent.putExtra("query", str3);
        }
        if (str2 != null) {
            intent.putExtra("intent_extra_data_key", str2);
        }
        if (this.mAppSearchData != null) {
            intent.putExtra("app_data", this.mAppSearchData);
        }
        if (i != 0) {
            intent.putExtra("action_key", i);
            intent.putExtra("action_msg", str4);
        }
        if (IS_AT_LEAST_FROYO) {
            intent.setComponent(this.mSearchable.getSearchActivity());
        }
        return intent;
    }

    @TargetApi(8)
    private Intent createVoiceWebSearchIntent(Intent intent, SearchableInfo searchableInfo) {
        Intent intent2 = new Intent(intent);
        ComponentName searchActivity = searchableInfo.getSearchActivity();
        intent2.putExtra("calling_package", searchActivity == null ? null : searchActivity.flattenToShortString());
        return intent2;
    }

    @TargetApi(8)
    private Intent createVoiceAppSearchIntent(Intent intent, SearchableInfo searchableInfo) {
        String str;
        String str2;
        String str3;
        ComponentName searchActivity = searchableInfo.getSearchActivity();
        Intent intent2 = new Intent("android.intent.action.SEARCH");
        intent2.setComponent(searchActivity);
        PendingIntent activity = PendingIntent.getActivity(getContext(), 0, intent2, 1073741824);
        Bundle bundle = new Bundle();
        if (this.mAppSearchData != null) {
            bundle.putParcelable("app_data", this.mAppSearchData);
        }
        Intent intent3 = new Intent(intent);
        str = "free_form";
        int i = 1;
        if (Build.VERSION.SDK_INT >= 8) {
            Resources resources = getResources();
            str = searchableInfo.getVoiceLanguageModeId() != 0 ? resources.getString(searchableInfo.getVoiceLanguageModeId()) : "free_form";
            str3 = searchableInfo.getVoicePromptTextId() != 0 ? resources.getString(searchableInfo.getVoicePromptTextId()) : null;
            str2 = searchableInfo.getVoiceLanguageId() != 0 ? resources.getString(searchableInfo.getVoiceLanguageId()) : null;
            if (searchableInfo.getVoiceMaxResults() != 0) {
                i = searchableInfo.getVoiceMaxResults();
            }
        } else {
            str2 = null;
            str3 = null;
        }
        intent3.putExtra("android.speech.extra.LANGUAGE_MODEL", str);
        intent3.putExtra("android.speech.extra.PROMPT", str3);
        intent3.putExtra("android.speech.extra.LANGUAGE", str2);
        intent3.putExtra("android.speech.extra.MAX_RESULTS", i);
        intent3.putExtra("calling_package", searchActivity != null ? searchActivity.flattenToShortString() : null);
        intent3.putExtra("android.speech.extra.RESULTS_PENDINGINTENT", activity);
        intent3.putExtra("android.speech.extra.RESULTS_PENDINGINTENT_BUNDLE", bundle);
        return intent3;
    }

    private Intent createIntentFromSuggestion(Cursor cursor, int i, String str) {
        int i2;
        String columnString;
        try {
            String columnString2 = SuggestionsAdapter.getColumnString(cursor, "suggest_intent_action");
            if (columnString2 == null && Build.VERSION.SDK_INT >= 8) {
                columnString2 = this.mSearchable.getSuggestIntentAction();
            }
            if (columnString2 == null) {
                columnString2 = "android.intent.action.SEARCH";
            }
            String str2 = columnString2;
            String columnString3 = SuggestionsAdapter.getColumnString(cursor, "suggest_intent_data");
            if (IS_AT_LEAST_FROYO && columnString3 == null) {
                columnString3 = this.mSearchable.getSuggestIntentData();
            }
            if (columnString3 != null && (columnString = SuggestionsAdapter.getColumnString(cursor, "suggest_intent_data_id")) != null) {
                columnString3 = columnString3 + "/" + Uri.encode(columnString);
            }
            return createIntent(str2, columnString3 == null ? null : Uri.parse(columnString3), SuggestionsAdapter.getColumnString(cursor, "suggest_intent_extra_data"), SuggestionsAdapter.getColumnString(cursor, "suggest_intent_query"), i, str);
        } catch (RuntimeException e) {
            try {
                i2 = cursor.getPosition();
            } catch (RuntimeException unused) {
                i2 = -1;
            }
            Log.w(LOG_TAG, "Search suggestions cursor at row " + i2 + " returned exception.", e);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void forceSuggestionQuery() {
        HIDDEN_METHOD_INVOKER.doBeforeTextChanged(this.mSearchSrcTextView);
        HIDDEN_METHOD_INVOKER.doAfterTextChanged(this.mSearchSrcTextView);
    }

    static boolean isLandscapeMode(Context context) {
        return context.getResources().getConfiguration().orientation == 2;
    }

    /* loaded from: classes.dex */
    public static class SearchAutoComplete extends AppCompatAutoCompleteTextView {
        private SearchView mSearchView;
        private int mThreshold;

        @Override // android.widget.AutoCompleteTextView
        public void performCompletion() {
        }

        @Override // android.widget.AutoCompleteTextView
        protected void replaceText(CharSequence charSequence) {
        }

        public SearchAutoComplete(Context context) {
            this(context, null);
        }

        public SearchAutoComplete(Context context, AttributeSet attributeSet) {
            this(context, attributeSet, R.attr.autoCompleteTextViewStyle);
        }

        public SearchAutoComplete(Context context, AttributeSet attributeSet, int i) {
            super(context, attributeSet, i);
            this.mThreshold = getThreshold();
        }

        void setSearchView(SearchView searchView) {
            this.mSearchView = searchView;
        }

        @Override // android.widget.AutoCompleteTextView
        public void setThreshold(int i) {
            super.setThreshold(i);
            this.mThreshold = i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isEmpty() {
            return TextUtils.getTrimmedLength(getText()) == 0;
        }

        @Override // android.widget.AutoCompleteTextView, android.widget.TextView, android.view.View
        public void onWindowFocusChanged(boolean z) {
            super.onWindowFocusChanged(z);
            if (z && this.mSearchView.hasFocus() && getVisibility() == 0) {
                ((InputMethodManager) getContext().getSystemService("input_method")).showSoftInput(this, 0);
                if (SearchView.isLandscapeMode(getContext())) {
                    SearchView.HIDDEN_METHOD_INVOKER.ensureImeVisible(this, true);
                }
            }
        }

        @Override // android.widget.AutoCompleteTextView, android.widget.TextView, android.view.View
        protected void onFocusChanged(boolean z, int i, Rect rect) {
            super.onFocusChanged(z, i, rect);
            this.mSearchView.onTextFocusChanged();
        }

        @Override // android.widget.AutoCompleteTextView
        public boolean enoughToFilter() {
            return this.mThreshold <= 0 || super.enoughToFilter();
        }

        @Override // android.widget.AutoCompleteTextView, android.widget.TextView, android.view.View
        public boolean onKeyPreIme(int i, KeyEvent keyEvent) {
            if (i == 4) {
                if (keyEvent.getAction() == 0 && keyEvent.getRepeatCount() == 0) {
                    KeyEvent.DispatcherState keyDispatcherState = getKeyDispatcherState();
                    if (keyDispatcherState != null) {
                        keyDispatcherState.startTracking(keyEvent, this);
                    }
                    return true;
                }
                if (keyEvent.getAction() == 1) {
                    KeyEvent.DispatcherState keyDispatcherState2 = getKeyDispatcherState();
                    if (keyDispatcherState2 != null) {
                        keyDispatcherState2.handleUpEvent(keyEvent);
                    }
                    if (keyEvent.isTracking() && !keyEvent.isCanceled()) {
                        this.mSearchView.clearFocus();
                        this.mSearchView.setImeVisibility(false);
                        return true;
                    }
                }
            }
            return super.onKeyPreIme(i, keyEvent);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class AutoCompleteTextViewReflector {
        private Method doAfterTextChanged;
        private Method doBeforeTextChanged;
        private Method ensureImeVisible;
        private Method showSoftInputUnchecked;

        AutoCompleteTextViewReflector() {
            try {
                this.doBeforeTextChanged = AutoCompleteTextView.class.getDeclaredMethod("doBeforeTextChanged", new Class[0]);
                this.doBeforeTextChanged.setAccessible(true);
            } catch (NoSuchMethodException unused) {
            }
            try {
                this.doAfterTextChanged = AutoCompleteTextView.class.getDeclaredMethod("doAfterTextChanged", new Class[0]);
                this.doAfterTextChanged.setAccessible(true);
            } catch (NoSuchMethodException unused2) {
            }
            try {
                this.ensureImeVisible = AutoCompleteTextView.class.getMethod("ensureImeVisible", Boolean.TYPE);
                this.ensureImeVisible.setAccessible(true);
            } catch (NoSuchMethodException unused3) {
            }
            try {
                this.showSoftInputUnchecked = InputMethodManager.class.getMethod("showSoftInputUnchecked", Integer.TYPE, ResultReceiver.class);
                this.showSoftInputUnchecked.setAccessible(true);
            } catch (NoSuchMethodException unused4) {
            }
        }

        void doBeforeTextChanged(AutoCompleteTextView autoCompleteTextView) {
            if (this.doBeforeTextChanged != null) {
                try {
                    this.doBeforeTextChanged.invoke(autoCompleteTextView, new Object[0]);
                } catch (Exception unused) {
                }
            }
        }

        void doAfterTextChanged(AutoCompleteTextView autoCompleteTextView) {
            if (this.doAfterTextChanged != null) {
                try {
                    this.doAfterTextChanged.invoke(autoCompleteTextView, new Object[0]);
                } catch (Exception unused) {
                }
            }
        }

        void ensureImeVisible(AutoCompleteTextView autoCompleteTextView, boolean z) {
            if (this.ensureImeVisible != null) {
                try {
                    this.ensureImeVisible.invoke(autoCompleteTextView, Boolean.valueOf(z));
                } catch (Exception unused) {
                }
            }
        }

        void showSoftInputUnchecked(InputMethodManager inputMethodManager, View view, int i) {
            if (this.showSoftInputUnchecked != null) {
                try {
                    this.showSoftInputUnchecked.invoke(inputMethodManager, Integer.valueOf(i), null);
                    return;
                } catch (Exception unused) {
                }
            }
            inputMethodManager.showSoftInput(view, i);
        }
    }
}
