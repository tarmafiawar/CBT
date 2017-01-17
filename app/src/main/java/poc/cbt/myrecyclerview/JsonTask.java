package poc.cbt.myrecyclerview;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import model.Container;
import model.Item;
import utils.Constants;

/**
 * Created by taro on 1/17/17.
 */

public class JsonTask extends AsyncTask<String, String, String> {
    private ProgressDialog pd;
    private TextView txtJson;
    private Button btnHit;

    protected void onPreExecute() {
        super.onPreExecute();

        pd = new ProgressDialog(null);
//        pd = new ProgressDialog(BlankActivity.this);
        pd.setMessage("Please wait");
        pd.setCancelable(false);
        pd.show();


//        to use
//        btnHit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new JsonTask().execute("https://s3-ap-southeast-1.amazonaws.com/mobile-resource.tewm/wallet-app/consumer/home/config/home_container_template.json");
//
//            }
//        });
    }

    protected String doInBackground(String... params) {


        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buffer = new StringBuffer();
            String line = "";

            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
                Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)
            }

            return buffer.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (pd.isShowing()) {
            pd.dismiss();
        }

        try {
            List<Container> containerList = new ArrayList<Container>();
            JSONObject resultJSONObject = new JSONObject(result);
            JSONArray containers = resultJSONObject.getJSONArray("containers");

            for (int i = 0; i < containers.length(); i++) {
                JSONObject containerJson = containers.getJSONObject(i);

                String type = containerJson.getString("type");
                String titleEn = containerJson.getString("title_en");
                String titleTh = containerJson.getString("title_th");
                String itemWidth = containerJson.getString("item_width");
                String itemHeight = containerJson.getString("item_height");

                Container container = new Container();
                container.setTitleEn(titleEn);
                container.setTitleTh(titleTh);
                container.setItemWidth(itemWidth);
                container.setItemHeight(itemHeight);

                if (type.equals("grid"))
                    container.setType(Constants.GridType.FIX);
                else if (type.equals("scroll"))
                    container.setType(Constants.GridType.SWIPE);
                else if (type.equals("banner"))
                    container.setType(Constants.GridType.BANNER);

                JSONArray items = containerJson.getJSONArray("items");

                List<Item> itemList = new ArrayList<Item>();
                for (int j = 0; j < items.length(); j++) {
                    JSONObject itemJson = items.getJSONObject(i);

                    String nameThai = itemJson.getString("name_th");
                    String imageUrl = itemJson.getString("image_url");
                    String deepLink = itemJson.getString("deep_link");

                    Item item = new Item();
                    item.setNameTh(nameThai);
                    item.setImageUrl(imageUrl);
                    item.setDeepLink(deepLink);

                    itemList.add(item);
                }
                containerList.add(container);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
