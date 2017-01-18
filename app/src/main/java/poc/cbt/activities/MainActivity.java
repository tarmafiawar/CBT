package poc.cbt.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import poc.cbt.models.Container;
import poc.cbt.models.Item;
import poc.cbt.tasks.ImageLoadTask;
import poc.cbt.poc.cbt.constants.Constants;

public class MainActivity extends AppCompatActivity {


    private ListView listView;
    private List<Container> containerList;

    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);

        new JsonTask().execute("https://s3-ap-southeast-1.amazonaws.com/mobile-resource.tewm-alpha/wallet-app/consumer/home/config/home_container_template.json");

    }

    public void setAdapterToList(List<Container> containerList){
        ListViewContainerAdapter listViewAdapter = new ListViewContainerAdapter(this, containerList);
        listView.setAdapter(listViewAdapter);
    }

    private List<Container> genMockData(){

        List<Container> containerList = new ArrayList<Container>();

        for(int c=0; c < 10; c++){

            Container container = new Container();
            container.setTitleEn("Container : " + (c + 1));

            int itemSize = 0;
            if(c%3 == 1) {
                container.setType(Constants.GridType.FIX);
                itemSize = 6;
                container.setTitleEn("Fix Container" );
            }
            else if(c%3 == 2){
                container.setType(Constants.GridType.SWIPE);
                itemSize = 8;
                container.setTitleEn("Swipe Container" );
            }else if(c%3 == 0){
                container.setType(Constants.GridType.BANNER);
                itemSize = 8;
                container.setTitleEn("Banner Container" );
            }

            List<Item> itemList = new ArrayList<Item>();

            for(int i=0; i< itemSize; i++){
                Item it = new Item();
                it.setNameEn("Icon" + i);
                it.setNameTh("NameTh" + i);
                it.setDeepLink("Deeplink" + i);
                it.setResourceId(android.R.drawable.ic_menu_save);

                if(container.getType() == Constants.GridType.SWIPE || container.getType() == Constants.GridType.FIX)
                    it.setImageUrl("https://s3-ap-southeast-1.amazonaws.com/mobile-resource.tewm/wallet-app/consumer/home/images/ic-topup.png");
                else
                    it.setImageUrl("https://www.mx7.com/i/d8e/KIfqnH.jpg");

                itemList.add(it);
            }

            container.setItemList(itemList);

            containerList.add(container);
        }

        return containerList;
    }


    class ListViewContainerAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private Context context;

        private List<Container> containerList;


        ListViewContainerAdapter(Context context, List<Container> containerList){
            this.context = context;
            this.containerList = containerList;

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            return containerList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {
            Holder holder = new Holder();

            View rowView = inflater.inflate(R.layout.recycler_view, null);

            holder.tv = (TextView) rowView.findViewById(R.id.containerTextView);
            holder.rView = (RecyclerView) rowView.findViewById(R.id.rccv);

            Container container = containerList.get(position);
            List<Item> itemList = container.getItemList();

            holder.tv.setText(container.getTitleEn());

            RecyclerView.LayoutManager layoutManager = null;

            if(container.getType() == Constants.GridType.FIX){
                int spanCount = itemList.size() / container.getNumberOfItemsPerRow();
                layoutManager = new GridLayoutManager(holder.rView.getContext(), spanCount, GridLayoutManager.HORIZONTAL, false){
                    @Override
                    public boolean canScrollHorizontally() {
                        return false;
                    }
                };

            }else{//container.getType() == Constants.GridType.SWIPE
                layoutManager = new GridLayoutManager(holder.rView.getContext(), 1, GridLayoutManager.HORIZONTAL, false);
            }

            holder.rView.setLayoutManager(layoutManager);

            mAdapter = new RecyclerViewItemAdapter(context, container, itemList);
            holder.rView.setAdapter(mAdapter);

            return rowView;
        }
    }

    class Holder{
        TextView tv;
        RecyclerView rView;
    }

    public class RecyclerViewItemAdapter extends
            RecyclerView.Adapter
                    <RecyclerViewItemAdapter.ListItemViewHolder> {

        private Container container;
        private List<Item> itemList;
        private Context context;

        RecyclerViewItemAdapter(Context context, Container container, List<Item> itemList) {
            if (itemList == null) {
                throw new IllegalArgumentException("itemList must not be null");
            }

            this.context = context;
            this.container = container;
            this.itemList = itemList;
        }

        @Override
        public ListItemViewHolder onCreateViewHolder( ViewGroup viewGroup, int viewType) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.icon_layout, null, false);
            return new ListItemViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ListItemViewHolder viewHolder, final int position) {

            viewHolder.label.setText(itemList.get(position).getNameTh());
            new ImageLoadTask(itemList.get(position).getImageUrl(), viewHolder.ib).execute();

//            viewHolder.ib.getLayoutParams().height = container.getItemHeight();
//            viewHolder.ib.getLayoutParams().width = container.getItemWidth();

            viewHolder.ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, BlankActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("NAME_EN", itemList.get(position).getNameTh());
                    intent.putExtras(bundle);

                    startActivity(intent);

                }
            });

        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        public final class ListItemViewHolder extends RecyclerView.ViewHolder {
            TextView label;
            ImageButton ib;


            public ListItemViewHolder(View itemView) {
                super(itemView);
                label = (TextView) itemView.findViewById(R.id.textView);
                ib = (ImageButton) itemView.findViewById(R.id.imageButton);

            }
        }
    }

    class JsonTask extends AsyncTask<String, String, String> {
        private ProgressDialog pd;
        private TextView txtJson;
        private Button btnHit;

        protected void onPreExecute() {
            super.onPreExecute();

//            pd = new ProgressDialog(getApplicationContext());
//            pd.setMessage("Please wait");
//            pd.setCancelable(false);
//            pd.show();
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
//            if (pd.isShowing()) {
//                pd.dismiss();
//            }

            Log.d("PRINT+++", "----- onPostExecute ");

            try {
                List<Container> containerList = new ArrayList<Container>();
                JSONObject resultJSONObject = new JSONObject(result);
                JSONArray containers = resultJSONObject.optJSONArray("containers");

                for (int i = 0; i < containers.length(); i++) {
                    Log.d("PRINT+++", "----- i " + i);
                    JSONObject containerJson = containers.optJSONObject(i);

                    String type = containerJson.optString("type");
                    String titleEn = containerJson.optString("title_en");
                    String titleTh = containerJson.optString("title_th");
                    int itemWidth = containerJson.optInt("item_width");
                    int itemHeight = containerJson.optInt("item_height");
                    int numberOfItemsPerRow = containerJson.optInt("number_of_items_per_row");

                    Container container = new Container();
                    container.setTitleEn(titleEn);
                    container.setTitleTh(titleTh);
                    container.setItemWidth(itemWidth);
                    container.setItemHeight(itemHeight);
                    container.setNumberOfItemsPerRow(numberOfItemsPerRow);

                    if (type.equals("grid"))
                        container.setType(Constants.GridType.FIX);
                    else if (type.equals("scroll"))
                        container.setType(Constants.GridType.SWIPE);
                    else if (type.equals("banner"))
                        container.setType(Constants.GridType.BANNER);

                    JSONArray items = containerJson.optJSONArray("items");

                    Log.d("PRINT+++", " items.length() : " + items.length());

                    List<Item> itemList = new ArrayList<Item>();
                    for (int j = 0; j < items.length(); j++) {

                        Log.d("PRINT+++", "before----- j " + j);

                        if(items.optJSONObject(j) != null){
                            JSONObject itemJson = items.optJSONObject(j);

                            String nameThai = itemJson.optString("name_th");
                            String imageUrl = itemJson.optString("image_url");
                            String deepLink = itemJson.optString("deep_link");

                            Item item = new Item();
                            item.setNameTh(nameThai);
                            item.setImageUrl(imageUrl);
                            item.setDeepLink(deepLink);

                            itemList.add(item);

                            Log.d("PRINT+++", "after----- j " + j);
                        }

                    }
                    container.setItemList(itemList);
                    containerList.add(container);
                    Log.d("PRINT+++", "after----- i " + i);
                }

                setAdapterToList(containerList);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
