package com.kaku.weac.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kaku.weac.R;
import com.kaku.weac.util.RecyclerViewOnItemClickListener;
import com.yingyongduoduo.ad.bean.ADBean;

import java.util.List;

/**
 * Created by dkli on 2017/12/6.
 */

public class TuiJianAdapter extends RecyclerView.Adapter<TuiJianAdapter.ViewHolder> {
    private RecyclerViewOnItemClickListener listener;
    private List<ADBean> beans;

    public TuiJianAdapter(RecyclerViewOnItemClickListener listener, List<ADBean> beans) {
        this.listener = listener;
        this.beans = beans;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_tuijian,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ADBean bean=beans.get(position);
        holder.tv_name.setText(bean.getAd_name());
        holder.tv_content.setText(bean.getAd_description());
        holder.sdv.setImageURI(bean.getAd_iconurl());
        if (bean.isAd_have()) {
            holder.download.setText("打开");
        } else {
            if (bean.getAd_type() == 1) {
                holder.download.setText("下载");
            } else if (bean.getAd_type() == 2){
                holder.download.setText("进入");
            }
            else  if (bean.getAd_type() == 3){
                holder.download.setText("添加");
            }
        }
    }

    @Override
    public int getItemCount() {
        return beans!=null?beans.size():0;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private SimpleDraweeView sdv;
        private Button download;
        private TextView tv_name;
        private TextView tv_content;
        public ViewHolder(View itemView) {
            super(itemView);
            sdv= (SimpleDraweeView) itemView.findViewById(R.id.sdv);
            download= (Button) itemView.findViewById(R.id.btn_download);
            tv_name= (TextView) itemView.findViewById(R.id.tv_name);
            tv_content= (TextView) itemView.findViewById(R.id.tv_content);

            itemView.setOnClickListener(this);
            download.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onItemClick(view,getLayoutPosition());
        }
    }
}
