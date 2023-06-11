package com.example.dispatchsysapp.faultDocument;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dispatchsysapp.R;

import java.util.List;

public class FaultDocumentAdapter extends RecyclerView.Adapter<FaultDocumentAdapter.ViewHolder>{

    private List<FaultDocument> documents;

    public FaultDocumentAdapter(List<FaultDocument> documents) {
        this.documents = documents;
    }


    @NonNull
    @Override
    public FaultDocumentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_fault_document, parent, false);
        return new FaultDocumentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FaultDocumentAdapter.ViewHolder holder, int position) {
        FaultDocument faultDocument = documents.get(position);
        holder.FaultDocumentIdText.setText(faultDocument.getId());
        holder.FaultDocumentDescriptionText.setText(faultDocument.getFaultDescription());
        holder.FaultDocumentStatusText.setText(faultDocument.getStatus());
    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView FaultDocumentIdText;
        public TextView FaultDocumentDescriptionText;
        public TextView FaultDocumentStatusText;

        public ViewHolder(View view){
            super(view);
            FaultDocumentIdText = view.findViewById(R.id.fault_document_id_text);
            FaultDocumentDescriptionText = view.findViewById(R.id.fault_document_description_text);
            FaultDocumentStatusText = view.findViewById(R.id.fault_document_status_text);
        }
    }
}
