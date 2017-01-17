//package poc.cbt.myrecyclerview;
//
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import java.util.List;
//
///**
// * Created by taro on 1/13/17.
// */
//
//class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
//
//    private List<String> stringList;
//
//    MyAdapter(List<String> stringList){
//        this.stringList = stringList;
//    }
//
//    @Override
//    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        // create a new view
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_text_view, parent, false);
//        TextView textView = (TextView) view.findViewById(R.id.textView);
//        // set the view's size, margins, paddings and layout parameters\
//        ViewHolder vh = new ViewHolder(textView);
//        return vh;
//    }
//
//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//
//    }
//
//
//    @Override
//    public int getItemCount() {
//        return 0;
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        public TextView mTextView;
//
//        public ViewHolder(TextView v){
//            super(v);
//            mTextView = v;
//        }
//    }
//}
//
