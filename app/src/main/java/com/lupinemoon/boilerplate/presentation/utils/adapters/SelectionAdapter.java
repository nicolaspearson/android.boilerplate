package com.lupinemoon.boilerplate.presentation.utils.adapters;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import com.lupinemoon.boilerplate.R;
import com.lupinemoon.boilerplate.data.models.KeyValue;
import com.lupinemoon.boilerplate.databinding.ListItemSelectionBinding;
import com.lupinemoon.boilerplate.presentation.widgets.interfaces.GenericItemSelected;

public class SelectionAdapter extends RecyclerView.Adapter<SelectionAdapter.SelectionItemViewHolder> {

    private List<KeyValue> items;

    private GenericItemSelected<KeyValue> itemSelectedCallback;

    public SelectionAdapter(
            GenericItemSelected<KeyValue> itemSelectedCallback,
            List<KeyValue> items) {
        this.itemSelectedCallback = itemSelectedCallback;
        setSelectionItems(items != null ? items : new ArrayList<KeyValue>());
    }

    private List<KeyValue> getSelectionItems() {
        return items;
    }

    private void setSelectionItems(List<KeyValue> items) {
        this.items = items;
    }

    @Override
    public SelectionAdapter.SelectionItemViewHolder onCreateViewHolder(
            ViewGroup parent,
            int viewType) {
        ListItemSelectionBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.list_item_selection,
                parent,
                false);
        return new SelectionAdapter.SelectionItemViewHolder(binding, itemSelectedCallback);
    }

    @Override
    public void onBindViewHolder(
            SelectionAdapter.SelectionItemViewHolder selectionItemViewHolder,
            int position) {
        KeyValue item = getSelectionItems().get(position);
        selectionItemViewHolder.bindItem(item);
    }

    @Override
    public int getItemCount() {
        return getSelectionItems().size();
    }

    class SelectionItemViewHolder extends RecyclerView.ViewHolder {

        private ListItemSelectionBinding binding;
        private SelectionItemViewModel selectionItemViewModel;

        SelectionItemViewHolder(
                ListItemSelectionBinding binding,
                GenericItemSelected<KeyValue> itemSelectedCallback) {
            super(binding.getRoot());
            this.binding = binding;
            selectionItemViewModel = new SelectionItemViewModel(itemSelectedCallback);
            this.binding.setViewModel(selectionItemViewModel);
        }

        void bindItem(KeyValue item) {
            selectionItemViewModel.setItem(item);
            this.binding.executePendingBindings();
        }
    }
}
