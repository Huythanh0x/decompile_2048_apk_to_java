package android.support.v4.speech.tts;

import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import java.util.Locale;
import java.util.Set;

/* loaded from: classes.dex */
class TextToSpeechICSMR1 {
    public static final String KEY_FEATURE_EMBEDDED_SYNTHESIS = "embeddedTts";
    public static final String KEY_FEATURE_NETWORK_SYNTHESIS = "networkTts";

    /* loaded from: classes.dex */
    interface UtteranceProgressListenerICSMR1 {
        void onDone(String str);

        void onError(String str);

        void onStart(String str);
    }

    TextToSpeechICSMR1() {
    }

    static Set<String> getFeatures(TextToSpeech textToSpeech, Locale locale) {
        if (Build.VERSION.SDK_INT >= 15) {
            return textToSpeech.getFeatures(locale);
        }
        return null;
    }

    static void setUtteranceProgressListener(TextToSpeech textToSpeech, final UtteranceProgressListenerICSMR1 utteranceProgressListenerICSMR1) {
        if (Build.VERSION.SDK_INT >= 15) {
            textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() { // from class: android.support.v4.speech.tts.TextToSpeechICSMR1.1
                @Override // android.speech.tts.UtteranceProgressListener
                public void onStart(String str) {
                    UtteranceProgressListenerICSMR1.this.onStart(str);
                }

                @Override // android.speech.tts.UtteranceProgressListener
                public void onError(String str) {
                    UtteranceProgressListenerICSMR1.this.onError(str);
                }

                @Override // android.speech.tts.UtteranceProgressListener
                public void onDone(String str) {
                    UtteranceProgressListenerICSMR1.this.onDone(str);
                }
            });
        } else {
            textToSpeech.setOnUtteranceCompletedListener(new TextToSpeech.OnUtteranceCompletedListener() { // from class: android.support.v4.speech.tts.TextToSpeechICSMR1.2
                @Override // android.speech.tts.TextToSpeech.OnUtteranceCompletedListener
                public void onUtteranceCompleted(String str) {
                    UtteranceProgressListenerICSMR1.this.onStart(str);
                    UtteranceProgressListenerICSMR1.this.onDone(str);
                }
            });
        }
    }
}
