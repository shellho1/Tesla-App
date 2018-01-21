package aaroncarlson.com.spartahacktesladoors;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String DRIVER_DOOR_OPEN = "openDriverDoors";

    private static final String DRIVER_DOOR_CLOSE = "closeDriverDoors";

    private static final String OPEN_ALL_WINDOWS = "openAllWindows";

    private static final String CLOSE_ALL_WINDOWS = "closeAllWindows";

    private static final String URL = "http://hackathon.intrepidcs.com/api/data";

    private static final String PRIVATE_KEY = "785a2fc6f53cc57c3898c77efe4f9000f78ddae1d3209acf65d617c864673d6f";

    private final int SPEECH_RECOGNITION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onSpeech(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak something...");
        try {
            startActivityForResult(intent, SPEECH_RECOGNITION_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Speech recognition is not supported in this device.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Callback for speech recognition activity
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SPEECH_RECOGNITION_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String text = result.get(0);
                    TextView v = (TextView)findViewById(R.id.speechTextView);
                    v.setText(text);

                }
                break;
            }
        }
    }

    public void openDriverDoor(final View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String resp = post(DRIVER_DOOR_OPEN);
                    view.post(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(view.getContext(), resp, Toast.LENGTH_SHORT).show();
                        }

                    });
            }
        }).start();
    }

    public void closeDriverDoor(final View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String resp = post(DRIVER_DOOR_CLOSE);
                    view.post(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(view.getContext(), resp, Toast.LENGTH_SHORT).show();
                        }

                    });
            }
        }).start();
    }

    public void openAllWindows(final View view) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                final String resp = post(OPEN_ALL_WINDOWS);
                    view.post(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(view.getContext(), resp, Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        }).start();
    }

    public void closeAllWindows(final View view) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                final String resp = post(CLOSE_ALL_WINDOWS);
                    view.post(new Runnable() {

                        @Override
                        public void run() { //test
                            Toast.makeText(view.getContext(), resp, Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        }).start();
    }

    private String post(final String command) {
        InputStream stream = null;
        try {
            URL url = new URL(URL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + PRIVATE_KEY);
//            conn.setRequestProperty("command", command);
            conn.setUseCaches(false);

            DataOutputStream wr = new DataOutputStream(
                    conn.getOutputStream());
            wr.writeBytes("command=" + command);
            wr.flush();
            wr.close();

            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return "failed";
            }
            stream = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String s = reader.readLine();
            return s != null ? s : "failed";

        } catch (MalformedURLException e) {
            return "Failed";
        } catch (IOException ex) {
            return "Failed";
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ex) {
                    // Fail silently
                }
            }
        }
    }
}
