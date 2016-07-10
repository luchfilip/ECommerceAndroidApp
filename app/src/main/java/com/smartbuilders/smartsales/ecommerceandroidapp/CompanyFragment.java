package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.system.ErrnoException;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.UserCompanyDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Company;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class CompanyFragment extends Fragment {

    private static final String STATE_COMPANY = "STATE_COMPANY";
    private static final int PICK_IMAGE = 100;

    private User mUser;
    private CropImageView companyLogoCropImageView;
    private Company mCompany;

    public CompanyFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_company, container, false);

        mUser = Utils.getCurrentUser(getContext());
        final UserCompanyDB userCompanyDB = new UserCompanyDB(getContext(), mUser);

        new Thread() {
            @Override
            public void run() {
                try {
                    if(savedInstanceState!=null){
                        if (savedInstanceState.containsKey(STATE_COMPANY)) {
                            mCompany = savedInstanceState.getParcelable(STATE_COMPANY);
                        }
                    }

                    if (mCompany==null) {
                        mCompany = userCompanyDB.getUserCompany();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final EditText companyName = (EditText) rootView.findViewById(R.id.business_partner_name_editText);
                                final EditText companyCommercialName = (EditText) rootView.findViewById(R.id.business_partner_commercial_name_editText);
                                final EditText companyTaxId = (EditText) rootView.findViewById(R.id.business_partner_tax_id_editText);
                                final EditText companyAddress = (EditText) rootView.findViewById(R.id.business_partner_address_editText);
                                final EditText companyContactPerson = (EditText) rootView.findViewById(R.id.business_partner_contact_person_name_editText);
                                final EditText companyEmailAddress = (EditText) rootView.findViewById(R.id.business_partner_email_address_editText);
                                final EditText companyPhoneNumber = (EditText) rootView.findViewById(R.id.business_partner_phone_number_editText);
                                final Button saveButton = (Button) rootView.findViewById(R.id.save_button);

                                companyLogoCropImageView = (CropImageView) rootView.findViewById(R.id.company_logo_imageView);
                                companyLogoCropImageView.setImageBitmap(Utils.getImageFromUserCompanyDir(getContext(), mUser));
                                companyLogoCropImageView.setFixedAspectRatio(true);
                                companyLogoCropImageView.setAspectRatio(230, 80);

                                rootView.findViewById(R.id.choose_company_logo_image_button).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                       pickImage();
                                    }
                                });

                                rootView.findViewById(R.id.crop_image_button).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Bitmap cropped = companyLogoCropImageView.getCroppedImage(230, 80);
                                        if (cropped != null){
                                            companyLogoCropImageView.setImageBitmap(cropped);
                                            Utils.createFileInUserCompanyDir(cropped, getContext(), mUser);
                                        }
                                    }
                                });

                                if (mCompany!=null){
                                    companyName.setText(mCompany.getName());
                                    companyName.addTextChangedListener(new TextWatcher(){
                                        public void afterTextChanged(Editable s) {
                                            mCompany.setName(s.toString());
                                        }
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                                        public void onTextChanged(CharSequence s, int start, int before, int count){}
                                    });

                                    companyCommercialName.setText(mCompany.getCommercialName());
                                    companyCommercialName.addTextChangedListener(new TextWatcher(){
                                        public void afterTextChanged(Editable s) {
                                            mCompany.setCommercialName(s.toString());
                                        }
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                                        public void onTextChanged(CharSequence s, int start, int before, int count){}
                                    });

                                    companyTaxId.setText(mCompany.getTaxId());
                                    companyTaxId.addTextChangedListener(new TextWatcher(){
                                        public void afterTextChanged(Editable s) {
                                            mCompany.setTaxId(s.toString());
                                        }
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                                        public void onTextChanged(CharSequence s, int start, int before, int count){}
                                    });

                                    companyAddress.setText(mCompany.getAddress());
                                    companyAddress.addTextChangedListener(new TextWatcher(){
                                        public void afterTextChanged(Editable s) {
                                            mCompany.setAddress(s.toString());
                                        }
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                                        public void onTextChanged(CharSequence s, int start, int before, int count){}
                                    });

                                    companyContactPerson.setText(mCompany.getContactPerson());
                                    companyContactPerson.addTextChangedListener(new TextWatcher(){
                                        public void afterTextChanged(Editable s) {
                                            mCompany.setContactPerson(s.toString());
                                        }
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                                        public void onTextChanged(CharSequence s, int start, int before, int count){}
                                    });

                                    companyEmailAddress.setText(mCompany.getEmailAddress());
                                    companyEmailAddress.addTextChangedListener(new TextWatcher(){
                                        public void afterTextChanged(Editable s) {
                                            mCompany.setEmailAddress(s.toString());
                                        }
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                                        public void onTextChanged(CharSequence s, int start, int before, int count){}
                                    });

                                    companyPhoneNumber.setText(mCompany.getPhoneNumber());
                                    companyPhoneNumber.addTextChangedListener(new TextWatcher(){
                                        public void afterTextChanged(Editable s) {
                                            mCompany.setPhoneNumber(s.toString());
                                        }
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                                        public void onTextChanged(CharSequence s, int start, int before, int count){}
                                    });

                                    saveButton.setText(getString(R.string.update));
                                }

                                if (saveButton!=null) {
                                    saveButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (mCompany!=null) {
                                                String result = userCompanyDB.insertUpdateUserCompany(mCompany);
                                                if (result==null){
                                                    saveButton.setText(getString(R.string.update));
                                                    Toast.makeText(getContext(), getString(R.string.company_updated_successfully), Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(getContext(), String.valueOf(result), Toast.LENGTH_LONG).show();
                                                }
                                            } else {
                                                Company company = new Company();
                                                company.setName(companyName.getText().toString());
                                                company.setCommercialName(companyCommercialName.getText().toString());
                                                company.setTaxId(companyTaxId.getText().toString());
                                                company.setAddress(companyAddress.getText().toString());
                                                company.setContactPerson(companyContactPerson.getText().toString());
                                                company.setEmailAddress(companyEmailAddress.getText().toString());
                                                company.setPhoneNumber(companyPhoneNumber.getText().toString());

                                                String result = userCompanyDB.insertUpdateUserCompany(company);
                                                if (result==null){
                                                    saveButton.setText(getString(R.string.update));
                                                    Toast.makeText(getContext(), getString(R.string.company_updated_successfully), Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(getContext(), String.valueOf(result), Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                rootView.findViewById(R.id.progressContainer).setVisibility(View.GONE);
                                rootView.findViewById(R.id.main_layout).setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            }
        }.start();
        return rootView;
    }

    private void pickImage(){
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, getString(R.string.choose_image));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    //@Override
    //public void onActivityResult(int requestCode, int resultCode, Intent data) {
    //    super.onActivityResult(requestCode, resultCode, data);
    //    if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
    //        if (data != null) {
    //            try {
    //                Utils.createFileInUserCompanyDir(getContext(), mUser,
    //                        getContext().getContentResolver().openInputStream(data.getData()));
    //                companyLogoCropImageView.setImageBitmap(Utils.getImageFromUserCompanyDir(getContext(), mUser));
    //            } catch (IOException e) {
    //                e.printStackTrace();
    //            }
    //        }
    //    }
    //}

    /**********************************************************************************************/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                try {
                    Utils.createFileInUserCompanyDir(getContext(), mUser,
                            getContext().getContentResolver().openInputStream(data.getData()));
                    //companyLogoCropImageView.setImageBitmap(Utils.getImageFromUserCompanyDir(getContext(), mUser));

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
            Toast.makeText(getContext(), "Required permissions are not granted", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Create a chooser intent to select the source to get image from.<br/>
     * The source can be camera's (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br/>
     * All possible sources are added to the intent chooser.
     */
    public Intent getPickImageChooserIntent() {

        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getActivity().getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        // the main intent is the last in the list (fucking android) so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
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
     * Get the URI of the selected image from {@link #getPickImageChooserIntent()}.<br/>
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
    /**********************************************************************************************/

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_COMPANY, mCompany);
        super.onSaveInstanceState(outState);
    }
}
