package com.lupinemoon.boilerplate.presentation.utils;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;

import com.lupinemoon.boilerplate.MainApplication;
import com.lupinemoon.boilerplate.R;
import com.lupinemoon.boilerplate.data.models.KeyValue;
import com.lupinemoon.boilerplate.data.storage.interfaces.Storage;
import com.lupinemoon.boilerplate.databinding.FragmentCustomDialogBinding;
import com.lupinemoon.boilerplate.databinding.FragmentSelectionBinding;
import com.lupinemoon.boilerplate.presentation.ui.base.BaseActivity;
import com.lupinemoon.boilerplate.presentation.utils.adapters.SelectionAdapter;
import com.lupinemoon.boilerplate.presentation.widgets.DecoratorOnClickListener;
import com.lupinemoon.boilerplate.presentation.widgets.interfaces.GenericItemSelected;

import java.util.List;

public class DialogUtils {

    public enum AlertType {
        NONE, NETWORK, ERROR, WARNING
    }

    /**
     * A generic method that shows an alert dialog. This allows us to centrally style and manage the dialogs
     *
     * @param activity       The activity context
     * @param title          The dialog's title
     * @param message        The dialog's message
     * @param checkbox       The checkbox text to be used
     * @param checkboxKey    The checkbox shared prefs key
     * @param posButton      The dialog's positive button text
     * @param negButton      The dialog's negative button text
     * @param posButtonClick The dialog's positive button handler
     * @param negButtonClick The dialog's negative button handler
     * @param alertType      The type of alert. Use AlertType.NONE if not required
     */
    public static AlertDialog showCustomAlertDialog(
            @NonNull final Activity activity,
            @NonNull String title,
            @NonNull String message,
            @NonNull String checkbox,
            @NonNull final String checkboxKey,
            int posButton,
            int negButton,
            View.OnClickListener posButtonClick,
            View.OnClickListener negButtonClick,
            @NonNull AlertType alertType) {

        final Storage storage = MainApplication.getStorage(activity.getApplicationContext());

        if (((BaseActivity) activity).isBasicallyDestroyedCompat()) {
            return null;
        }

        final AlertDialog alertDialog;

        AlertDialog.Builder builder = new AlertDialog.Builder(
                activity,
                R.style.TransparentLowDimDialog);

        final FragmentCustomDialogBinding binding = DataBindingUtil.inflate(
                activity.getLayoutInflater(),
                R.layout.fragment_custom_dialog,
                null,
                false);

        builder.setView(binding.getRoot());
        builder.setCancelable(true);

        if (!TextUtils.isEmpty(title)) {
            binding.dialogTitle.setText(title);
        } else {
            binding.dialogTitle.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(message)) {
            binding.dialogMessage.setText(message);
        } else {
            binding.dialogMessage.setVisibility(View.GONE);
        }

        binding.yesButton.setVisibility(View.GONE);
        binding.noButton.setVisibility(View.GONE);

        switch (alertType) {
            case NONE:
                binding.dialogImageContainer.setVisibility(View.GONE);
                break;

            case NETWORK:
                binding.dialogImageContainer.setVisibility(View.VISIBLE);
                binding.dialogImageView.setImageResource(R.drawable.vd_no_connection);
                break;

            case ERROR:
                binding.dialogImageContainer.setVisibility(View.VISIBLE);
                binding.dialogImageView.setImageResource(R.drawable.vd_error);
                break;

            case WARNING:
                binding.dialogImageContainer.setVisibility(View.VISIBLE);
                binding.dialogImageView.setImageResource(R.drawable.vd_warning);
                break;
        }

        alertDialog = builder.show();

        if (!TextUtils.isEmpty(checkbox) && !TextUtils.isEmpty(checkboxKey)) {
            binding.dialogCheckboxRemember.setVisibility(View.VISIBLE);
            binding.dialogCheckboxRemember.setText(checkbox);
            binding.dialogCheckboxRemember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    storage.putBoolean(checkboxKey, !isChecked);
                }
            });
            binding.dialogCheckboxRemember.setChecked(false);
        } else {
            binding.dialogCheckboxRemember.setVisibility(View.GONE);
        }

        if (posButton > 0 && posButtonClick != null) {
            binding.yesButtonTextView.setText(posButton);
            binding.yesButton.setVisibility(View.VISIBLE);

            DecoratorOnClickListener decoratorOnClickListener = new DecoratorOnClickListener();
            decoratorOnClickListener.add(posButtonClick);
            decoratorOnClickListener.add(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (alertDialog != null) {
                                alertDialog.dismiss();
                            }
                        }
                    }, Constants.DEFAULT_SELECTOR_DELAY);
                }
            });

            binding.yesButton.setOnClickListener(decoratorOnClickListener);
        }

        if (negButton > 0 && negButtonClick != null) {
            binding.noButtonTextView.setText(negButton);
            binding.noButton.setVisibility(View.VISIBLE);

            DecoratorOnClickListener decoratorOnClickListener = new DecoratorOnClickListener();
            decoratorOnClickListener.add(negButtonClick);
            decoratorOnClickListener.add(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (alertDialog != null && !((BaseActivity) activity).isBasicallyDestroyedCompat()) {
                                alertDialog.dismiss();
                            }
                        }
                    }, Constants.DEFAULT_SELECTOR_DELAY);
                }
            });
            binding.noButton.setOnClickListener(decoratorOnClickListener);
        }
        return alertDialog;
    }

    /**
     * A generic method that shows an alert dialog. This allows us to centrally style and manage the dialogs
     *
     * @param activity       The activity context
     * @param title          The dialog's title
     * @param message        The dialog's message
     * @param posButton      The dialog's positive button text
     * @param negButton      The dialog's negative button text
     * @param posButtonClick The dialog's positive button handler
     * @param negButtonClick The dialog's negative button handler
     * @param alertType      The type of alert. Use AlertType.NONE if not required
     */
    public static AlertDialog showCustomAlertDialogSimple(
            @NonNull Activity activity,
            @NonNull String title,
            @NonNull String message,
            int posButton,
            int negButton,
            View.OnClickListener posButtonClick,
            View.OnClickListener negButtonClick,
            @NonNull AlertType alertType) {

        return showCustomAlertDialog(
                activity,
                title,
                message,
                "",
                "",
                posButton,
                negButton,
                posButtonClick,
                negButtonClick,
                alertType);
    }


    /**
     * A generic method that shows an alert dialog. This allows us to centrally style and manage the dialogs
     *
     * @param activity       The activity context
     * @param title          The dialog's title
     * @param selectionItems The list of options for the user to select from
     */
    public static AlertDialog showSelectionAlertDialog(
            @NonNull Activity activity,
            @NonNull String title,
            @NonNull List<KeyValue> selectionItems,
            @NonNull final GenericItemSelected<KeyValue> itemSelectedCallback) {

        if (((BaseActivity) activity).isBasicallyDestroyedCompat()) {
            return null;
        }

        final AlertDialog alertDialog;

        AlertDialog.Builder builder = new AlertDialog.Builder(
                activity,
                R.style.TransparentLowDimDialog);

        FragmentSelectionBinding binding = DataBindingUtil.inflate(
                activity.getLayoutInflater(),
                R.layout.fragment_selection,
                null,
                false);

        builder.setView(binding.getRoot());
        builder.setCancelable(true);

        if (!TextUtils.isEmpty(title)) {
            binding.dialogTitle.setText(title);
        } else {
            binding.dialogTitle.setVisibility(View.GONE);
        }

        SelectionAdapter selectionAdapter = new SelectionAdapter(
                itemSelectedCallback,
                selectionItems);

        binding.selectionRecyclerView.setLayoutManager(new LinearLayoutManager(
                activity,
                LinearLayoutManager.VERTICAL,
                false));
        binding.selectionRecyclerView.setAdapter(selectionAdapter);

        alertDialog = builder.show();

        binding.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (alertDialog != null) {
                    alertDialog.dismiss();
                }
            }
        });
        return alertDialog;
    }
}
