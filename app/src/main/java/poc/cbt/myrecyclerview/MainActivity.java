package poc.cbt.myrecyclerview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import model.Container;
import model.Item;
import utils.Constants;

public class MainActivity extends AppCompatActivity {


    private ListView listView;
    private List<Container> containerList;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);
        containerList = genMockData();
        ListViewContainerAdapter listViewAdapter = new ListViewContainerAdapter(this, containerList);

        listView.setAdapter(listViewAdapter);

        /*
        setContentView(R.layout.recycler_view);
        mRecyclerView = (RecyclerView) findViewById(R.id.rccv);

//        mLayoutManager = new LinearLayoutManager(this);

//        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setLayoutManager(
                new GridLayoutManager(mRecyclerView.getContext(), 2, GridLayoutManager.HORIZONTAL, false));

        List<String> stringList = new ArrayList<String>();
        for(int i=0; i< 20; i++){
            stringList.add("App"+(i+1));
        }

        mAdapter = new RecyclerViewItemAdapter(stringList);
        mRecyclerView.setAdapter(mAdapter);
        */
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

//            if(container.getType() == Constants.GridType.BANNER)
//                new ImageLoadTask("https://www.mx7.com/i/d8e/KIfqnH.jpg", viewHolder.ib).execute();
//            else
//                new ImageLoadTask("https://s3-ap-southeast-1.amazonaws.com/mobile-resource.tewm/wallet-app/consumer/home/images/ic-topup.png", viewHolder.ib).execute();



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

        private String[] iconNames = {"icon1", "icon2"};
        private int[] images = {android.R.drawable.ic_menu_save, android.R.drawable.ic_menu_save};

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

//            rowView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Toast.makeText(context, "You Clicked " + containerList.get(position).getTitleEn(), Toast.LENGTH_SHORT).show();
//                }
//            });
//            holder.imb = (ImageButton) rowView.findViewById(R.id.imageButton);
//            holder.tv = (TextView) rowView.findViewById(R.id.textView);
//            holder.horizontalLinearLayout = (LinearLayout) rowView.findViewById(R.id.horizontalLinearLayout);

            holder.tv = (TextView) rowView.findViewById(R.id.containerTextView);
            holder.rView = (RecyclerView) rowView.findViewById(R.id.rccv);

            Container container = containerList.get(position);
            List<Item> itemList = container.getItemList();

            holder.tv.setText(container.getTitleEn());

            RecyclerView.LayoutManager layoutManager = null;


            if(container.getType() == Constants.GridType.FIX){
                layoutManager = new GridLayoutManager(holder.rView.getContext(), 2, GridLayoutManager.HORIZONTAL, false){
                    @Override
                    public boolean canScrollHorizontally() {
                        return false;
                    }
                };

            }else{//container.getType() == Constants.GridType.SWIPE
                layoutManager = new GridLayoutManager(holder.rView.getContext(), 1, GridLayoutManager.HORIZONTAL, false);
            }

            holder.rView.setLayoutManager(layoutManager);

//            List<String> stringList = new ArrayList<String>();
//            for(int i=0; i< 20; i++){
//                stringList.add("App"+(i+1));
//            }

            mAdapter = new RecyclerViewItemAdapter(container, itemList);
            holder.rView.setAdapter(mAdapter);

//            holder.horizontalLinearLayout.setAd

//            holder.tv.setText(iconNames[position]);
//            holder.imb.setImageResource(images[position]);
//
//            rowView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Toast.makeText(context, "You Clicked " + iconNames[position], Toast.LENGTH_LONG).show();
//                }
//            });

            return rowView;
        }
    }

    class Holder{
        TextView tv;
//        ImageButton imb;
//        LinearLayout horizontalLinearLayout;
        RecyclerView rView;
    }

//    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
//
//        private List<String> stringList;
//
//        MyAdapter(List<String> stringList){
//            this.stringList = stringList;
//        }
//
//        @Override
//        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            // create a new view
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_text_view, parent, false);
//            TextView textView = (TextView) view.findViewById(R.id.textView);
////            textView.setText("xxx");
//            // set the view's size, margins, paddings and layout parameters\
//            ViewHolder vh = new ViewHolder(textView);
////            vh.mTextView = textView;
//            return vh;
//        }
//
//        @Override
//        public void onBindViewHolder(ViewHolder holder, int position) {
//            holder.mTextView.setText(stringList.get(position));
//        }
//
//
//        @Override
//        public int getItemCount() {
//            return stringList.size();
//        }
//
//        public class ViewHolder extends RecyclerView.ViewHolder {
//            public TextView mTextView;
//
//            public ViewHolder(TextView v){
//                super(v);
//                mTextView = v;
//            }
//        }
//    }

    public class RecyclerViewItemAdapter extends
            RecyclerView.Adapter
                    <RecyclerViewItemAdapter.ListItemViewHolder> {

        private Container container;
        private List<Item> itemList;

        RecyclerViewItemAdapter(Container container, List<Item> itemList) {
            if (itemList == null) {
                throw new IllegalArgumentException(
                        "itemList must not be null");
            }

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

            viewHolder.label.setText(itemList.get(position).getNameEn());
//            viewHolder.ib.setImageResource(android.R.drawable.ic_menu_save);
//                URL newurl = new URL("https://www.mx7.com/i/d8e/KIfqnH.jpg");
//                Bitmap mIcon_val = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());

//                Bitmap mIcon_val = viewHolder.ib.setImageBitmap(mIcon_val);
//            viewHolder.ib.setImageBitmap();

            new ImageLoadTask(itemList.get(position).getImageUrl(), viewHolder.ib).execute();
//            if(container.getType() == Constants.GridType.BANNER)
//                new ImageLoadTask("https://www.mx7.com/i/d8e/KIfqnH.jpg", viewHolder.ib).execute();
//            else
//                new ImageLoadTask("https://s3-ap-southeast-1.amazonaws.com/mobile-resource.tewm/wallet-app/consumer/home/images/ic-topup.png", viewHolder.ib).execute();

//            Bitmap mIcon_val = getBitmapFromURL("https://www.mx7.com/i/d8e/KIfqnH.jpg");
//            viewHolder.ib.setImageBitmap(mIcon_val);

//            viewHolder.ib.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(MainActivity.this, BlankActivity.class);
//                    startActivity(intent);

//                    Toast.makeText(getApplicationContext(), "You Clicked " + itemList.get(position).getNameEn() + ", " + container.getTitleEn(), Toast.LENGTH_SHORT).show();
//                }
//            });

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

    public static Bitmap getBitmapFromURL(String src) {
        try {
            Log.e("src",src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap","returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            return null;
        }
    }

    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageButton imageButton;

        public ImageLoadTask(String url, ImageButton imageButton) {
            this.url = url;
            this.imageButton = imageButton;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageButton.setImageBitmap(result);
        }

    }


}
