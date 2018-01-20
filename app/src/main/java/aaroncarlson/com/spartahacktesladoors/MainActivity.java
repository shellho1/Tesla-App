package aaroncarlson.com.spartahacktesladoors;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String DRIVER_DOOR_OPEN = "openDriverDoors";

    private static final String DRIVER_DOOR_CLOSE = "closeDriverDoors";

    private static final String URL = "http://hackathon.intrepidcs.com/api/data";

    private static final String PRIVATE_KEY = "785a2fc6f53cc57c3898c77efe4f9000f78ddae1d3209acf65d617c864673d6f";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openDriverDoor(final View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (post(DRIVER_DOOR_OPEN)) {
                    view.post(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(view.getContext(), "success", Toast.LENGTH_SHORT).show();
                        }

                    });
                } else {
                    view.post(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(view.getContext(), "fail", Toast.LENGTH_SHORT).show();
                        }

                    });
                }
            }
        }).start();
    }

    public void closeDriverDoor(final View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (post(DRIVER_DOOR_CLOSE)) {
                    view.post(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(view.getContext(), "success", Toast.LENGTH_SHORT).show();
                        }

                    });
                } else {
                    view.post(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(view.getContext(), "fail", Toast.LENGTH_SHORT).show();
                        }

                    });
                }
            }
        }).start();
    }

    private boolean post(final String key) {
        InputStream stream = null;
        try {
            URL url = new URL(URL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + PRIVATE_KEY);
            conn.setRequestProperty("command", key);
            conn.setUseCaches(false);

            OutputStream outputStream = conn.getOutputStream();

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return false;
            }

        } catch (MalformedURLException e) {
            return false;
        } catch (IOException ex) {
            return false;
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ex) {
                    // Fail silently
                }
            }
        }
        return true;
    }

    private EditText getEditKey()
    {
        return (EditText)findViewById(R.id.keyInputText);
    }
}
