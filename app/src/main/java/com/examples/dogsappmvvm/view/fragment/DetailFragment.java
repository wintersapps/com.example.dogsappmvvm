package com.examples.dogsappmvvm.view.fragment;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.examples.dogsappmvvm.R;
import com.examples.dogsappmvvm.databinding.FragmentDetailBinding;
import com.examples.dogsappmvvm.databinding.SendSmsLayoutBinding;
import com.examples.dogsappmvvm.model.DogBreed;
import com.examples.dogsappmvvm.model.DogPalette;
import com.examples.dogsappmvvm.model.SmsInfo;
import com.examples.dogsappmvvm.view.MainActivity;
import com.examples.dogsappmvvm.viewmodel.DetailViewModel;

import org.jetbrains.annotations.NotNull;

public class DetailFragment extends Fragment {

    private DetailViewModel viewModel;
    private FragmentDetailBinding binding;
    private int dogUid;

    private boolean sendSMSStarted = false;
    private DogBreed currentDog;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(DetailViewModel.class);
        if(getArguments() != null){
            dogUid = DetailFragmentArgs.fromBundle(getArguments()).getDogUid();
        }
        viewModel.fetch(dogUid);
        observeViewModel();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void observeViewModel(){
        viewModel.dogLiveData.observe(getViewLifecycleOwner(), dogBreed -> {
            if(dogBreed != null && getContext() != null){
                currentDog = dogBreed;
                binding.setDog(dogBreed);
                if(dogBreed.getImageUrl() != null){
                    setupBackgroundColor(dogBreed.getImageUrl());
                }
            }
        });
    }

    private void setupBackgroundColor(String url){
        Glide.with(this)
                .asBitmap()
                .load(url)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Palette.from(resource).generate(palette -> {
                            if(palette != null){
                                if(palette.getLightMutedSwatch() != null){
                                    int intColor = palette.getLightMutedSwatch().getRgb();
                                    DogPalette myPalette = new DogPalette(intColor);
                                    binding.setPalette(myPalette);
                                }
                            }
                        });
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {}
                });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.detail_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_send_sms:{
                if(!sendSMSStarted){
                    if(getActivity() != null){
                        sendSMSStarted = true;
                        ((MainActivity)getActivity()).checkSMSPermission();
                    }
                }
                break;
            }
            case R.id.action_share:{
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Check out this dog breed");
                intent.putExtra(Intent.EXTRA_TEXT, currentDog.getDogBreed() + " bred for " + currentDog.getBreedFor());
                intent.putExtra(Intent.EXTRA_STREAM, currentDog.getImageUrl());
                startActivity(Intent.createChooser(intent,"Share with"));
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void onPermissionResult(boolean permissionGranted){
        if(isAdded() && sendSMSStarted && permissionGranted){
            SmsInfo smsInfo = new SmsInfo("",currentDog.getDogBreed() + " bred for " + currentDog.getBreedFor(), currentDog.getImageUrl());

            SendSmsLayoutBinding dialogBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(getContext()),
                    R.layout.send_sms_layout,
                    null,
                    false
            );

            if(getContext() != null){
                new AlertDialog.Builder(getContext())
                        .setView(dialogBinding.getRoot())
                        .setPositiveButton("Send SMS", (dialog, which) -> {
                            if(dialogBinding.smsDestinationTextInputEditText.getText() != null){
                                if(!dialogBinding.smsDestinationTextInputEditText.getText().toString().isEmpty()){
                                    smsInfo.setSendTo(dialogBinding.smsDestinationTextInputEditText.getText().toString());
                                    sendSMS(smsInfo);
                                }
                            }
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {})
                        .show();
                sendSMSStarted = false;

                dialogBinding.setSmsInfo(smsInfo);
            }
        }
    }

    private void sendSMS(SmsInfo smsInfo){
        Intent intent = new Intent(getContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, 0);
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(smsInfo.getSendTo(), null, smsInfo.getText(), pendingIntent, null);
    }
}
