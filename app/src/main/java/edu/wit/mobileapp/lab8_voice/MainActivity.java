package edu.wit.mobileapp.lab8_voice;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    final int REQUEST_CODE = 100;
    final String SPEECH_TAG = "SPEECH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button speechToTextBtn = (Button) findViewById(R.id.ButtonRecord);
        final Button textToSpeechBtn = (Button) findViewById(R.id.ButtonRead);
        final EditText editTextField = (EditText) findViewById(R.id.editTextField);

        // Speech To Text
        speechToTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Start speaking");
                try {
                    startActivityForResult(intent, REQUEST_CODE);
                    Log.i(SPEECH_TAG, "Listening");
                } catch (ActivityNotFoundException exception) {
                    Log.e(SPEECH_TAG, "Could not start activity", exception);
                }
            }
        });

        // Text to Speech
        editTextField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing. We don't care what the text was before.
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().isEmpty()) {
                    textToSpeechBtn.setEnabled(false);
                } else {
                    textToSpeechBtn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Do nothing. The state of the button is set anytime the text is changed. Not just when the user is done.
            }
        });

        final TextToSpeech textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(final int status) {
                if(status == TextToSpeech.ERROR) {
                    Log.e(SPEECH_TAG, "Failed to initialize Text-To-Speech");
                }
            }
        });
        textToSpeech.setLanguage(Locale.getDefault());

        textToSpeechBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Log.i(SPEECH_TAG, "Reading aloud");
                final String text = editTextField.getText().toString();
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, String.valueOf(this.hashCode()));
            }
        });
    }

    @Override
    // Called when the speech to text activity finishes
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final TextView textSaidView = (TextView) findViewById(R.id.TextSaid);
        switch (requestCode) {
            case REQUEST_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    final ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    textSaidView.setText(result.get(0));
                }
                break;
            }
        }
    }
}
