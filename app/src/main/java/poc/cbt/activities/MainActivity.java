package poc.cbt.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

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
        containerList = genMockData();
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

            mAdapter = new RecyclerViewItemAdapter(container, itemList);
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
            new ImageLoadTask(itemList.get(position).getImageUrl(), viewHolder.ib).execute();

            viewHolder.ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, BlankActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("NAME_EN", itemList.get(position).getNameEn());
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

}
