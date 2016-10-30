package com.smartbuilders.smartsales.ecommerce;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.system.ErrnoException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.smartbuilders.synchronizer.ids.model.User;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by stein on 5/1/2016.
 */
public class DialogEditCompanyLogo extends DialogFragment {

    private static final String STATE_CURRENT_USER = "STATE_CURRENT_USER";

    private static final int PICK_IMAGE = 100;
    private User mUser;
    private CropImageView companyLogoCropImageView;

    public DialogEditCompanyLogo() {
        // Empty constructor required for DialogFragment
    }

    public interface Callback {
        void reloadCompanyLogoImageView();
    }

    public static DialogEditCompanyLogo newInstance(User user){
        DialogEditCompanyLogo dialogEditCompanyLogo = new DialogEditCompanyLogo();
        dialogEditCompanyLogo.setCancelable(false);
        dialogEditCompanyLogo.mUser = user;
        return dialogEditCompanyLogo;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(savedInstanceState!=null){
            if(savedInstanceState.containsKey(STATE_CURRENT_USER)){
                mUser = savedInstanceState.getParcelable(STATE_CURRENT_USER);
            }
        }

        final View view = inflater.inflate(R.layout.dialog_edit_company_logo, container);

        companyLogoCropImageView = (CropImageView) view.findViewById(R.id.company_logo_imageView);
        Bitmap image = Utils.getUserCompanyImage(getContext(), mUser);
        if (image != null) {
            companyLogoCropImageView.setImageBitmap(image);
        } else {
            companyLogoCropImageView.setImageResource(R.drawable.no_company_logo_image_available);
        }
        companyLogoCropImageView.setFixedAspectRatio(true);
        companyLogoCropImageView.setAspectRatio(230, 80);

        view.findViewById(R.id.choose_company_logo_image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, getString(R.string.choose_image));
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                startActivityForResult(chooserIntent, PICK_IMAGE);
            }
        });

        view.findViewById(R.id.crop_image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Bitmap cropped = companyLogoCropImageView.getCroppedImage();
                    if (cropped != null) {
                        companyLogoCropImageView.setImageBitmap(cropped);
                        Utils.saveUserCompanyImage(cropped, getContext(), mUser);
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        });

        view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        view.findViewById(R.id.accept_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.saveUserCompanyImage(companyLogoCropImageView.getDrawingCache(), getContext(), mUser);
                ((Callback) getTargetFragment()).reloadCompanyLogoImageView();
                dismiss();
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                try {
                    Utils.saveUserCompanyImage(getContext(), mUser,
                            getContext().getContentResolver().openInputStream(data.getData()));
                    //companyLogoCropImageView.setImageBitmap(Utils.getUserCompanyImage(getContext(), mUser));

                    Uri imageUri = getPickImageResultUri(data);

                    // For API >= 23 we need to check specifically that we have permissions to read external storage,
                    // but we don't know if we need to for the URI so the simplest is to try open the stream and see if we get error.
                    boolean requirePermissions = false;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                            getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                            isUriRequiresPermissions(imageUri)) {

                        // request permissions and handle the result in onRequestPermissionsResult()
                        requirePermissions = true;
                        mCropImageUri = imageUri;
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                    }

                    if (!requirePermissions) {
                        companyLogoCropImageView.setImageUriAsync(imageUri);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private Uri mCropImageUri;

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            companyLogoCropImageView.setImageUriAsync(mCropImageUri);
        } else {
            Toast.makeText(getContext(), R.string.error_permissions_not_granted, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Get URI to image received from capture by camera.
     */
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getActivity().getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
        }
        return outputFileUri;
    }

    /**
     * Get the URI of the selected image from {@link #/*getPickImageChooserIntent()}.<br/>
     * Will return the correct URI for camera and gallery image.
     *
     * @param data the returned data of the activity result
     */
    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    /**
     * Test if we can open the given Android URI to test if permission required error is thrown.<br>
     */
    public boolean isUriRequiresPermissions(Uri uri) {
        try {
            ContentResolver resolver = getActivity().getContentResolver();
            InputStream stream = resolver.openInputStream(uri);
            stream.close();
            return false;
        } catch (FileNotFoundException e) {
            if (e.getCause() instanceof ErrnoException) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_CURRENT_USER, mUser);
        super.onSaveInstanceState(outState);
    }
}
