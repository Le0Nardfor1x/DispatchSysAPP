package com.example.dispatchsysapp.faultDocument;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.example.dispatchsysapp.R;

import java.util.List;

public class DisplayFaultActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private FaultDocumentAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_display_fault);

        Intent intent = getIntent();
        List<FaultDocument> faultDocuments = intent.getParcelableArrayListExtra("myList");
        Log.d("List", String.valueOf(faultDocuments.size()));
        for (int i = 0; i < faultDocuments.size(); i++) {
            faultDocuments.get(i).setId(String.valueOf(i+1));
        }

        mRecyclerView = findViewById(R.id.fault_document_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        mAdapter = new FaultDocumentAdapter(faultDocuments);
        mRecyclerView.setAdapter(mAdapter);
    }
}