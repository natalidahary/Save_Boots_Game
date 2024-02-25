package com.example.savethemonkey.Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.savethemonkey.DB.Record;
import com.example.savethemonkey.DB.DataManager;
import com.example.savethemonkey.Interface.CallBackList;
import com.example.savethemonkey.R;
import com.example.savethemonkey.Utils.SharedPreferances;
import com.google.gson.Gson;

public class FragmentListUsers extends Fragment {
    private ListView listView;
    private CallBackList callBack_list;
    private DataManager allRecords;
    private final String RECORD = "records";


    public void setCallBack_list(CallBackList callBack_list){
        this.callBack_list = callBack_list;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fragment_list_users, container, false);
        findViews(view);
        initViews();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                double lat = allRecords.getScoreResults().get(position).getLat();
                double lon = allRecords.getScoreResults().get(position).getLon();
                String namePlayer = allRecords.getScoreResults().get(position).getName();
                callBack_list.setMapLocation(lat,lon,namePlayer);
            }
        });
        return view;
    }


    private void initViews() {
        allRecords = new Gson().fromJson(SharedPreferances.getInstance().getStrSP(RECORD, ""), DataManager.class);
        if (allRecords != null) {
            final Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.nightfishing); // Ensure your font is in the res/font folder

            ArrayAdapter<Record> adapter = new ArrayAdapter<Record>(getActivity(), android.R.layout.simple_expandable_list_item_1, allRecords.getScoreResults()) {
                @NonNull
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView textView = (TextView) view.findViewById(android.R.id.text1);
                    textView.setTypeface(typeface);
                    return view;
                }
            };

            listView.setAdapter(adapter);
        }
    }


    private void findViews(View view) {
        listView = view.findViewById(R.id.fragmentList_top10);
    }

}
