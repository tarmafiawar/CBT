package poc.cbt.myrecyclerview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


public class BlankActivity extends AppCompatActivity {

    private TextView txtJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank_activity);

        txtJson = (TextView) findViewById(R.id.tvJsonItem);

    }
}