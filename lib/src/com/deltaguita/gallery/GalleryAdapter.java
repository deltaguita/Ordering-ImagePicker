package com.deltaguita.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.luminous.pick.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;


public class GalleryAdapter extends BaseAdapter {


    private LayoutInflater infalter;
    private ArrayList<PhotoItem> data = new ArrayList<PhotoItem>();
    private ArrayList<PhotoItem> selectedData = new ArrayList<PhotoItem>();
    private HashSet<ViewHolder> getViewHolderSet = new HashSet<ViewHolder>();
    ImageLoader imageLoader;


    public GalleryAdapter(Context c, ImageLoader imageLoader) {
        infalter = (LayoutInflater) c
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.imageLoader = imageLoader;
    }


    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public PhotoItem getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public ArrayList<PhotoItem> getSelected() {
        ArrayList<PhotoItem> dataT = new ArrayList<PhotoItem>();

        for (PhotoItem aData : data) {
            if (aData.isSeleted) {
                dataT.add(aData);
            }
        }

        return dataT;
    }

    public void addAll(ArrayList<PhotoItem> files) {

        try {
            this.data.clear();
            this.data.addAll(files);

        } catch (Exception e) {
            e.printStackTrace();
        }

        notifyDataSetChanged();
    }

    public void changeSelection(View v, int position) {
        Iterator<ViewHolder> iterator = getViewHolderSet.iterator();


        PhotoItem photoItem = data.get(position);
        if (photoItem.isSeleted) {
            photoItem.isSeleted = false;
            selectedData.remove(photoItem);
            v.setBackgroundResource(R.color.ff000000);
            v.findViewById(R.id.text_sort).setVisibility(View.INVISIBLE);

            while (iterator.hasNext()) {
                ViewHolder viewHolder = iterator.next();

                if (viewHolder.photoItem.isSeleted) {
                    int index = selectedData.indexOf(viewHolder.photoItem) + 1;
                    viewHolder.textOrder.setText(String.valueOf(index));

                } else {
                    iterator.remove();
                }
            }

        } else {
            photoItem.isSeleted = true;
            selectedData.add(photoItem);
            v.setBackgroundResource(R.color.f3d420);
            v.findViewById(R.id.text_sort).setVisibility(View.VISIBLE);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.textOrder = ((TextView) v.findViewById(R.id.text_sort));
            int index = selectedData.indexOf(photoItem) + 1;
            viewHolder.textOrder.setText(String.valueOf(index));
            viewHolder.photoItem = getItem(position);
            getViewHolderSet.add(viewHolder);
        }

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {

            convertView = infalter.inflate(R.layout.adapter_picture, parent, false);

            holder = new ViewHolder();
            holder.imgPhoto = (ImageView) convertView
                    .findViewById(R.id.img);

            holder.textOrder = (TextView) convertView
                    .findViewById(R.id.text_sort);

            holder.rootView = convertView;

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.imgPhoto.setTag(position);

        try {
            imageLoader.cancelDisplayTask(holder.imgPhoto);
            imageLoader.displayImage("file://" + data.get(position).sdcardPath,
                    holder.imgPhoto);

            holder.textOrder.setText(String.valueOf(selectedData.indexOf(data.get(position)) + 1));
            holder.textOrder.setVisibility(data.get(position).isSeleted ? View.VISIBLE : View.GONE);
            if (data.get(position).isSeleted) {
                holder.photoItem = data.get(position);
                getViewHolderSet.add(holder);
                holder.rootView.setBackgroundResource(R.color.f3d420);
            } else {
                getViewHolderSet.remove(holder);
                holder.rootView.setBackgroundResource(R.color.ff000000);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    public class ViewHolder {
        PhotoItem photoItem;
        View rootView;
        ImageView imgPhoto;
        TextView textOrder;
    }
}
