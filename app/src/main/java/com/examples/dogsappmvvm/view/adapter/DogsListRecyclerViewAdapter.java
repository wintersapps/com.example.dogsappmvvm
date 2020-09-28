package com.examples.dogsappmvvm.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.examples.dogsappmvvm.R;
import com.examples.dogsappmvvm.databinding.ItemDogBinding;
import com.examples.dogsappmvvm.model.DogBreed;
import com.examples.dogsappmvvm.view.fragment.ListFragmentDirections;
import com.examples.dogsappmvvm.view.listeners.DogClickListener;

import java.util.List;

public class DogsListRecyclerViewAdapter extends RecyclerView.Adapter<DogsListRecyclerViewAdapter.ViewHolder> implements DogClickListener {

    private List<DogBreed> dogsList;

    public DogsListRecyclerViewAdapter(List<DogBreed> dogsList) {
        this.dogsList = dogsList;
    }

    public void updateDogsList(List<DogBreed> dogsList){
        this.dogsList.clear();
        this.dogsList.addAll(dogsList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemDogBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_dog, parent, false);
        return new DogsListRecyclerViewAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setDog(dogsList.get(position));
        holder.binding.setListener(this);
    }

    @Override
    public int getItemCount() {
        return dogsList.size();
    }

    @Override
    public void onDogClicked(View view) {
        String uidString = ((TextView)view.findViewById(R.id.dogId)).getText().toString();
        int uid = Integer.parseInt(uidString);
        ListFragmentDirections.ActionDetail action = ListFragmentDirections.actionDetail();
        action.setDogUid(uid);
        Navigation.findNavController(view).navigate(action);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private ItemDogBinding binding;

        public ViewHolder(@NonNull ItemDogBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
