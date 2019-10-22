package com.example.manager_client;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


class BookItemExtended {
    BookItem item;
    Drawable image;
    public BookItemExtended(BookItem item)
    {
        this.item = item;
        this.image = Utils.drawableFromUrl(item.getBookImg());
    }
}

public class ListViewAdapter extends BaseAdapter {
    private ArrayList<BookItemExtended> listViewItemList = new ArrayList<>();

    public ListViewAdapter() {

    }

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_layout, parent, false);
        }
        ImageView thumbView = convertView.findViewById(R.id.imageView);
        TextView title = convertView.findViewById(R.id.title);
        TextView desc = convertView.findViewById(R.id.desc);

        BookItemExtended item = listViewItemList.get(pos);

        thumbView.setImageDrawable(item.image);
        title.setText(item.item.getTitle());
        desc.setText(item.item.getAuthor() + " 저 / ISBN - " + item.item.getIsbn() + " / 출판일자 " + item.item.getPublishedAt());
        return convertView;
    }

    @Override
    public long getItemId(int pos)  {
        return pos;
    }

    @Override
    public BookItem getItem(int pos) {
        return listViewItemList.get(pos).item;
    }

    public void clearItems() {
        listViewItemList.clear();
    }

    public void addItem(BookItem book)
    {
        listViewItemList.add(new BookItemExtended(book));
    }
}
