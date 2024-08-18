package com.safesoft.proapp.distribute.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.cursoradapter.widget.CursorAdapter;

import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.postData.PostData_Client;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListViewAdapterClientMaps extends CursorAdapter {

    private final List<PostData_Client> items;

    private TextView clientName;
    private TextView clientCode;

    public ListViewAdapterClientMaps(Context context, Cursor cursor, List<PostData_Client> items) {

        super(context, cursor, false);

        this.items = items;

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        clientName.setText(items.get(cursor.getPosition()).client);
        clientCode.setText("Code client : " + items.get(cursor.getPosition()).code_client);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.listview_client_maps, parent, false);

        clientName = view.findViewById(R.id.clientname);
        clientCode = view.findViewById(R.id.clientcode);

        return view;

    }

}