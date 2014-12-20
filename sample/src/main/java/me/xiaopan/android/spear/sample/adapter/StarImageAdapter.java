package me.xiaopan.android.spear.sample.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import me.xiaoapn.android.spear.sample.R;
import me.xiaopan.android.spear.sample.DisplayOptionsType;
import me.xiaopan.android.spear.sample.net.request.StarImageRequest;
import me.xiaopan.android.spear.widget.SpearImageView;

/**
 * 明星图片适配器
 */
public class StarImageAdapter extends RecyclerView.Adapter{
    private static final int ITEM_TYPE_ITEM = 0;
    private static final int ITEM_TYPE_LOAD_MORE_FOOTER = 1;
    private static final int ITEM_TYPE_HEADER = 2;
    private int imageSize = -1;
    private int screenWidth;
    private int column = 3;
    private int marginBorder;
    private int margin;
    private Context context;
    private List<StarImageRequest.Image> imageList;
    private OnLoadMoreListener onLoadMoreListener;
    private String backgroundImageUrl;
    private View.OnClickListener itemClickListener;

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public StarImageAdapter(Context context, String backgroundImageUrl, List<StarImageRequest.Image> imageList, final OnItemClickListener onItemClickListener){
        this.context = context;
        this.backgroundImageUrl = backgroundImageUrl;
        this.imageList = imageList;

        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        margin = (int) context.getResources().getDimension(R.dimen.home_category_margin);
        marginBorder = (int) context.getResources().getDimension(R.dimen.home_category_margin_border);
        int maxSize = screenWidth - (marginBorder * 4);
        imageSize = maxSize/column;

        itemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null){
                    int position = (Integer) v.getTag();
                    if(position < StarImageAdapter.this.imageList.size()){
                        StarImageRequest.Image image = StarImageAdapter.this.imageList.get(position);
                        onItemClickListener.onItemClick(position, image);
                    }
                }
            }
        };
    }

    @Override
    public int getItemViewType(int position) {
        if(backgroundImageUrl != null && position == 0){
            return ITEM_TYPE_HEADER;
        }else if(onLoadMoreListener != null && position == getItemCount()-1){
            return ITEM_TYPE_LOAD_MORE_FOOTER;
        }else{
            return ITEM_TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        if(imageList == null){
            return 0;
        }
        return (imageList.size()%column!=0?(imageList.size()/column)+1:(imageList.size()/column))   // ITEM行数
                +(backgroundImageUrl!=null?1:0) // 加上头
                +(onLoadMoreListener!=null?1:0); // 加上尾巴
    }

    public List<StarImageRequest.Image> getImageList() {
        return imageList;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch(viewType){
            case ITEM_TYPE_HEADER :
                HeaderViewHolder headerViewHolder = new HeaderViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_star_image_header, viewGroup, false));
                headerViewHolder.spearImageView.setDisplayOptions(DisplayOptionsType.STAR_HOME_HEADER);
                headerViewHolder.spearImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                headerViewHolder.spearImageView.setLayoutParams(new FrameLayout.LayoutParams(screenWidth, (int) (screenWidth / 3.2f)));
                viewHolder = headerViewHolder;
                break;
            case ITEM_TYPE_ITEM :
                ItemViewHolder itemViewHolder = new ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_star_image, viewGroup, false));
                ViewGroup.LayoutParams params = itemViewHolder.oneSpearImageView.getLayoutParams();
                params.width = imageSize;
                params.height = imageSize;
                itemViewHolder.oneSpearImageView.setLayoutParams(params);

                params = itemViewHolder.twoSpearImageView.getLayoutParams();
                params.width = imageSize;
                params.height = imageSize;
                itemViewHolder.twoSpearImageView.setLayoutParams(params);

                params = itemViewHolder.threeSpearImageView.getLayoutParams();
                params.width = imageSize;
                params.height = imageSize;
                itemViewHolder.threeSpearImageView.setLayoutParams(params);

                itemViewHolder.oneSpearImageView.setDisplayOptions(DisplayOptionsType.STAR_HOME_ITEM);
                itemViewHolder.twoSpearImageView.setDisplayOptions(DisplayOptionsType.STAR_HOME_ITEM);
                itemViewHolder.threeSpearImageView.setDisplayOptions(DisplayOptionsType.STAR_HOME_ITEM);

                itemViewHolder.oneSpearImageView.setEnablePressRipple(true);
                itemViewHolder.twoSpearImageView.setEnablePressRipple(true);
                itemViewHolder.threeSpearImageView.setEnablePressRipple(true);

                itemViewHolder.oneSpearImageView.setOnClickListener(itemClickListener);
                itemViewHolder.twoSpearImageView.setOnClickListener(itemClickListener);
                itemViewHolder.threeSpearImageView.setOnClickListener(itemClickListener);
                viewHolder = itemViewHolder;
                break;
            case ITEM_TYPE_LOAD_MORE_FOOTER:
                viewHolder = new LoadMoreFooterViewHolder(LayoutInflater.from(context).inflate(R.layout.list_footer_load_more, viewGroup, false));
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch(getItemViewType(position)){
            case ITEM_TYPE_HEADER :
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
                headerViewHolder.spearImageView.setImageByUri(backgroundImageUrl);
                break;
            case ITEM_TYPE_ITEM :
                ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
                if(backgroundImageUrl != null){
                    position -= 1;
                }

                int topMargin;
                int bottomMargin;
                if(position == 0){
                    topMargin = backgroundImageUrl!=null?0:marginBorder;
                    bottomMargin = margin;
                }else if(position == getItemCount()-1){
                    topMargin = margin;
                    bottomMargin = marginBorder;
                }else{
                    topMargin = margin;
                    bottomMargin = margin;
                }

                int oneReadPosition = (position*column);
                bind(itemViewHolder.oneSpearImageView, oneReadPosition<imageList.size()?imageList.get(oneReadPosition):null, oneReadPosition);

                int twoReadPosition = (position*column)+1;
                bind(itemViewHolder.twoSpearImageView, twoReadPosition<imageList.size()?imageList.get(twoReadPosition):null, twoReadPosition);

                int threeReadPosition = (position*column)+2;
                bind(itemViewHolder.threeSpearImageView, threeReadPosition<imageList.size()?imageList.get(threeReadPosition):null, threeReadPosition);

                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) itemViewHolder.oneSpearImageView.getLayoutParams();
                params.topMargin = topMargin;
                params.bottomMargin = bottomMargin;
                itemViewHolder.oneSpearImageView.setLayoutParams(params);

                params = (ViewGroup.MarginLayoutParams) itemViewHolder.twoSpearImageView.getLayoutParams();
                params.topMargin = topMargin;
                params.bottomMargin = bottomMargin;
                itemViewHolder.twoSpearImageView.setLayoutParams(params);

                params = (ViewGroup.MarginLayoutParams) itemViewHolder.threeSpearImageView.getLayoutParams();
                params.topMargin = topMargin;
                params.bottomMargin = bottomMargin;
                itemViewHolder.threeSpearImageView.setLayoutParams(params);
                break;
            case ITEM_TYPE_LOAD_MORE_FOOTER :
                LoadMoreFooterViewHolder footerViewHolder = (LoadMoreFooterViewHolder) viewHolder;
                if(onLoadMoreListener.isEnable()){
                    footerViewHolder.progressBar.setVisibility(View.VISIBLE);
                    footerViewHolder.contextTextView.setText("快递马上就来，别急哦！");
                    onLoadMoreListener.onLoadMore();
                }else{
                    footerViewHolder.progressBar.setVisibility(View.GONE);
                    footerViewHolder.contextTextView.setText("快递已经全部送完了！");
                }
                break;
        }
    }

    private void bind(SpearImageView spearImageView, StarImageRequest.Image image, int position){
        if(image != null){
            spearImageView.setTag(position);
            spearImageView.setImageByUri(image.getSourceUrl());
            spearImageView.setVisibility(View.VISIBLE);
        }else{
            spearImageView.setVisibility(View.INVISIBLE);
        }
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder{
        private SpearImageView oneSpearImageView;
        private SpearImageView twoSpearImageView;
        private SpearImageView threeSpearImageView;

        @SuppressWarnings("deprecation")
        public ItemViewHolder(View itemView) {
            super(itemView);
            oneSpearImageView = (SpearImageView) itemView.findViewById(R.id.image_starImageItem_one);
            twoSpearImageView = (SpearImageView) itemView.findViewById(R.id.image_starImageItem_two);
            threeSpearImageView = (SpearImageView) itemView.findViewById(R.id.image_starImageItem_three);
        }
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder{
        private SpearImageView spearImageView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            spearImageView = (SpearImageView) itemView.findViewById(R.id.image_starImageHeaderItem);
        }
    }

    private static class LoadMoreFooterViewHolder extends RecyclerView.ViewHolder{
        private ProgressBar progressBar;
        private TextView contextTextView;

        public LoadMoreFooterViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_loadMoreFooter);
            contextTextView = (TextView) itemView.findViewById(R.id.text_loadMoreFooter_content);
        }
    }

    public interface OnItemClickListener{
        public void onItemClick(int position, StarImageRequest.Image image);
    }

    public interface OnLoadMoreListener{
        public void setEnable(boolean enable);
        public boolean isEnable();
        public void onLoadMore();
    }
}
