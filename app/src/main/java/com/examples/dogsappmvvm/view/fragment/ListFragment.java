package com.examples.dogsappmvvm.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.examples.dogsappmvvm.R;
import com.examples.dogsappmvvm.databinding.FragmentListBinding;
import com.examples.dogsappmvvm.view.adapter.DogsListRecyclerViewAdapter;
import com.examples.dogsappmvvm.viewmodel.ListViewModel;
import com.google.android.material.textview.MaterialTextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ListFragment extends Fragment {

    private ListViewModel viewModel;
    private DogsListRecyclerViewAdapter dogsListRecyclerViewAdapter = new DogsListRecyclerViewAdapter(new ArrayList<>());
    private FragmentListBinding binding;

    public ListFragment() {}

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentListBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(ListViewModel.class);
        viewModel.refresh();
        binding.dogsListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.dogsListRecyclerView.setAdapter(dogsListRecyclerViewAdapter);
        binding.refreshLayout.setOnRefreshListener(() -> {
            binding.dogsListRecyclerView.setVisibility(View.GONE);
            binding.listErrorMaterialTextView.setVisibility(View.GONE);
            binding.loadingProgressBar.setVisibility(View.VISIBLE);
            viewModel.refreshBypassCache();
            binding.refreshLayout.setRefreshing(false);
        });
        observeViewModel();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void observeViewModel(){
        viewModel.dogs.observe(getViewLifecycleOwner(), dogBreeds -> {
            if(dogBreeds != null){
                binding.dogsListRecyclerView.setVisibility(View.VISIBLE);
                dogsListRecyclerViewAdapter.updateDogsList(dogBreeds);
            }
        });

        viewModel.dogsLoadError.observe(getViewLifecycleOwner(), aBoolean -> {
            if(aBoolean != null){
                binding.listErrorMaterialTextView.setVisibility(aBoolean ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.loading.observe(getViewLifecycleOwner(), aBoolean -> {
            if(aBoolean != null){
                binding.loadingProgressBar.setVisibility(aBoolean ? View.VISIBLE : View.GONE);
                if(aBoolean){
                    binding.listErrorMaterialTextView.setVisibility(View.GONE);
                    binding.dogsListRecyclerView.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.actionSettings:{
                if(isAdded() && getView() != null){
                    Navigation.findNavController(getView()).navigate(ListFragmentDirections.actionSettings());
                }
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}