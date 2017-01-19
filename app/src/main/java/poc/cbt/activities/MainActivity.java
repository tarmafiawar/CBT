package poc.cbt.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
import poc.cbt.poc.cbt.constants.Constants;
import poc.cbt.utils.SimpleDividerItemDecoration;
import poc.cbt.utils.WindowUtil;

public class MainActivity extends AppCompatActivity {


    private RecyclerView containerRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        containerRecyclerView = (RecyclerView) findViewById(R.id.containerRecyclerView);

        new JsonTask().execute("https://s3-ap-southeast-1.amazonaws.com/mobile-resource.tewm-alpha/wallet-app/consumer/home/config/home_container_template.json");

    }

    public void setAdapterToList(List<Container> containerList){
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);

        RecyclerViewContainerAdapter containerAdapter = new RecyclerViewContainerAdapter(this, containerList);

        containerRecyclerView.setLayoutManager(layoutManager);
        containerRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        containerRecyclerView.setAdapter(containerAdapter);
    }

    class RecyclerViewContainerAdapter extends RecyclerView.Adapter <RecyclerViewContainerAdapter.ListContainerViewHolder> {

        private List<Container> containerList;
        private Context context;

        RecyclerViewContainerAdapter(Context context, List<Container> containerList) {
            if (containerList == null) {
                throw new IllegalArgumentException("itemList must not be null");
            }

            this.context = context;
            this.containerList = containerList;
        }

        @Override
        public ListContainerViewHolder onCreateViewHolder( ViewGroup viewGroup, int viewType) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view, null, false);
            return new ListContainerViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ListContainerViewHolder viewHolder, final int position) {

            Container container = containerList.get(position);
            List<Item> itemList = container.getItemList();
            viewHolder.containerTextView.setText(containerList.get(position).getTitleEn());

            RecyclerView.Adapter adapter = new RecyclerViewItemAdapter(context, container, itemList);
            viewHolder.rccv.setAdapter(adapter);

            RecyclerView.LayoutManager layoutManager = null;

            if(container.getType() == Constants.GridType.FIX){
                int spanCount = container.getNumberOfItemsPerRow();
                if(itemList.size() % container.getNumberOfItemsPerRow() == 0)
                    spanCount = itemList.size() / container.getNumberOfItemsPerRow();
                layoutManager = new GridLayoutManager(context, spanCount, GridLayoutManager.HORIZONTAL, false){
                    @Override
                    public boolean canScrollHorizontally() {
                        return false;
                    }
                };

            }else{//container.getType() == Constants.GridType.SWIPE
                layoutManager = new GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false);
            }

            viewHolder.rccv.setLayoutManager(layoutManager);

            RecyclerView.Adapter mAdapter = new RecyclerViewItemAdapter(context, container, itemList);
            viewHolder.rccv.setAdapter(mAdapter);

        }

        @Override
        public int getItemCount() {
            return containerList.size();
        }

        public final class ListContainerViewHolder extends RecyclerView.ViewHolder {
            TextView containerTextView;
            RecyclerView rccv;


            public ListContainerViewHolder(View itemView) {
                super(itemView);
                containerTextView = (TextView) itemView.findViewById(R.id.containerTextView);
                rccv = (RecyclerView) itemView.findViewById(R.id.rccv);

            }
        }
    }

     class RecyclerViewItemAdapter extends
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

            int screenWidthPixel = WindowUtil.getDeviceScreenWidthPixel(context);
            int screenHeightPixel = WindowUtil.getDeviceScreenHeightPixel(context);
            Log.d("screenWidthPixel", "screenWidthPixel:" + screenWidthPixel);

            viewHolder.label.setText(itemList.get(position).getNameTh());

            Picasso.with(getApplicationContext()).load(itemList.get(position).getImageUrl()).into(viewHolder.iv);

            if(container.getType() == Constants.GridType.FIX){
                if(container.getItemList().size()%container.getNumberOfItemsPerRow()==0 && container.getItemWidth() > 0 && container.getNumberOfItemsPerRow() > 0){
                    int calWidth = container.getItemWidth()/container.getNumberOfItemsPerRow();
                    ViewGroup.LayoutParams params = viewHolder.iv.getLayoutParams();
                    params.width = screenWidthPixel * (calWidth+3) / 100;
//                  params.height = screenHeightPixel;
                    viewHolder.iv.setLayoutParams(params);
                }else{
                    int calWidth = (100/container.getNumberOfItemsPerRow())-10;
                    ViewGroup.LayoutParams params = viewHolder.iv.getLayoutParams();
                    params.width = screenWidthPixel * (calWidth+3) / 100;
//                  params.height = screenHeightPixel;
                    viewHolder.iv.setLayoutParams(params);
                }
            }


            viewHolder.iv.setOnClickListener(new View.OnClickListener() {
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
            ImageView iv;
            LinearLayout layout;


            public ListItemViewHolder(View itemView) {
                super(itemView);
                label = (TextView) itemView.findViewById(R.id.textView);
                iv = (ImageView) itemView.findViewById(R.id.imageView);
                layout = (LinearLayout) itemView.findViewById(R.id.layout);

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

//            Log.d("PRINT+++", "----- onPostExecute ");

            try {
                List<Container> containerList = new ArrayList<Container>();
                JSONObject resultJSONObject = new JSONObject(result);
                JSONArray containers = resultJSONObject.optJSONArray("containers");

                for (int i = 0; i < containers.length(); i++) {
//                    Log.d("PRINT+++", "----- i " + i);
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

//                    Log.d("PRINT+++", " items.length() : " + items.length());

                    List<Item> itemList = new ArrayList<Item>();
                    for (int j = 0; j < items.length(); j++) {

//                        Log.d("PRINT+++", "before----- j " + j);

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

//                            Log.d("PRINT+++", "after----- j " + j);
                        }

                    }
                    container.setItemList(itemList);
                    containerList.add(container);
//                    Log.d("PRINT+++", "after----- i " + i);
                }

                setAdapterToList(containerList);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
