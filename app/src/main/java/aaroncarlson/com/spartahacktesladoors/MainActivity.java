package aaroncarlson.com.spartahacktesladoors;

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.ProcessingInstruction;

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

    private static final String OPEN_PASSENGER_DOORS = "openPassengerDoors";

    private static final String OPEN_FRONT_DRIVER_DOOR = "openFrontDriverDoor";

    private static final String OPEN_BACK_DRIVER_DOOR = "openBackDriverDoor";

    private static final String OPEN_FRONT_PASSENGER_DOOR = "openFrontPassengerDoor";

    private static final String OPEN_BACK_PASSENGER_DOOR = "openBackPassengerDoor";

    private static final String DRIVER_DOOR_CLOSE = "closeDriverDoors";

    private static final String CLOSE_PASSENGER_DOORS = "closePassengerDoors";

    private static final String CLOSE_FRONT_DRIVER_DOOR = "closeFrontDriverDoor";

    private static final String CLOSE_BACK_DRIVER_DOOR = "closeBackDriverDoor";

    private static final String CLOSE_FRONT_PASSENGER_DOOR = "closeFrontPassengerDoor";

    private static final String CLOSE_BACK_PASSENGER_DOOR = "closeBackPassengerDoor";

    private static final String OPEN_ALL_WINDOWS = "openAllWindows";

    private static final String CLOSE_ALL_WINDOWS = "closeAllWindows";

    private static final String TRUNK = "trunk";

    private static final String FRUNK = "frunk";

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
                    final String cmd = parseSpeech(text);
                    if (cmd != null) {
                        command(findViewById(R.id.speechTextButton), cmd);
                        v.setText(cmd);
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getBaseContext(), "Try again", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                break;
            }
        }
    }

    private String parseSpeech(String speech) {
        speech = speech.toLowerCase();

        if (speech.contains("window")) {
            if (speech.contains("open")) {
                return OPEN_ALL_WINDOWS;
            } else if (speech.contains("close")) {
                return CLOSE_ALL_WINDOWS;
            }
        }
        if (speech.contains("door")) {
            boolean passenger = speech.contains("passenger");
            boolean driver = !passenger && speech.contains("driver");
            boolean open = speech.contains("open");
            boolean close = !open && speech.contains("close");
            boolean rear = speech.contains("rear") || speech.contains("back");
            boolean front = !rear && speech.contains("front");

            if (passenger) {
                if (open) {
                    if (rear) {
                        return OPEN_BACK_PASSENGER_DOOR;
                    } else if (front) {
                        return OPEN_FRONT_PASSENGER_DOOR;
                    } else {
                        return OPEN_PASSENGER_DOORS;
                    }
                }
                if (close) {
                    if (rear) {
                        return CLOSE_BACK_PASSENGER_DOOR;
                    } else if (front) {
                        return CLOSE_FRONT_PASSENGER_DOOR;
                    } else {
                        return CLOSE_PASSENGER_DOORS;
                    }
                }
                return null;
            } else if (driver) {
                if (open) {
                    if (rear) {
                        return OPEN_BACK_DRIVER_DOOR;
                    } else if (front) {
                        return OPEN_FRONT_DRIVER_DOOR;
                    } else {
                        return DRIVER_DOOR_OPEN;
                    }
                }
                if (close) {
                    if (rear) {
                        return CLOSE_BACK_DRIVER_DOOR;
                    } else if (front) {
                        return CLOSE_FRONT_DRIVER_DOOR;
                    } else {
                        return DRIVER_DOOR_CLOSE;
                    }
                }
                return null;
            }
        }
        if (!speech.contains("front") && speech.contains("trunk")) {
            return TRUNK;
        }
        if (speech.contains("front") && speech.contains("trunk")) {
            return FRUNK;
        }
        return null;
    }

    private void command(final View view, final String command) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String resp = post(command);
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(view.getContext(), resp, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    public void openDriverDoor(final View view) {
        command(view, DRIVER_DOOR_OPEN);
    }

    public void closeDriverDoor(final View view) {
        command(view, DRIVER_DOOR_CLOSE);
    }

    public void openAllWindows(final View view) {
        command(view, OPEN_ALL_WINDOWS);
    }

    public void closeAllWindows(final View view) {
        command(view, CLOSE_ALL_WINDOWS);
    }

    private String post(final String command) {
        InputStream stream = null;
        try {
            URL url = new URL(URL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + PRIVATE_KEY);
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
