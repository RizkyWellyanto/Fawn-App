package akiniyalocts.imgurapiexample.activities;

import akiniyalocts.imgurapiexample.CaffeConnectAsyncTask;
import akiniyalocts.imgurapiexample.R;
import akiniyalocts.imgurapiexample.helpers.DocumentHelper;
import akiniyalocts.imgurapiexample.helpers.IntentHelper;
import akiniyalocts.imgurapiexample.imgurmodel.ImageResponse;
import akiniyalocts.imgurapiexample.imgurmodel.Upload;
import akiniyalocts.imgurapiexample.services.OnImageUploadedListener;
import akiniyalocts.imgurapiexample.services.UploadService;
import akiniyalocts.imgurapiexample.utils.aLog;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class MainActivity extends ActionBarActivity implements OnImageUploadedListener{
  public final static String TAG = MainActivity.class.getSimpleName();

  /*
    These annotations are for ButterKnife by Jake Wharton

    https://github.com/JakeWharton/butterknife

   */
  @InjectView(R.id.upload_image)ImageView uploadImage;
  @InjectView(R.id.upload_btn)Button uploadBtn;
  @InjectView(R.id.upload_title)EditText uploadTitle;
  @InjectView(R.id.upload_desc)EditText uploadDesc;

  private Upload upload; // Upload object containging image and meta data
  private File chosenFile; //chosen file from intent

  ImageView imageView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.inject(this);
    imageView = (ImageView) findViewById(R.id.upload_image);
  }



  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Uri returnUri;

    if(requestCode == IntentHelper.FILE_PICK) {
      if (resultCode == RESULT_OK) {
        returnUri = data.getData();
        chosenFile = new File(DocumentHelper.getPath(this, returnUri));

        /*
          Picasso is a wonderful image loading tool from square inc.

          https://github.com/square/picasso
         */
        Picasso.with(this).load(chosenFile).fit().into(uploadImage);
        /*
          Enable the upload button after the file has been chosen
         */
        uploadBtn.setEnabled(true);
      }
    }
    if (requestCode == 10 && resultCode == RESULT_OK) {
      Bundle extras = data.getExtras();
      Bitmap imageBitmap = (Bitmap) extras.get("data");
      imageView.setImageBitmap(imageBitmap);
      uploadBtn.setEnabled(true);
      Uri tempUri = getImageUri(getApplicationContext(), imageBitmap);

      // CALL THIS METHOD TO GET THE ACTUAL PATH
      File finalFile = new File(getRealPathFromURI(tempUri));
      chosenFile = new File(DocumentHelper.getPath(this, tempUri));
      uploadBtn.setEnabled(true);
    }

  }
  public Uri getImageUri(Context inContext, Bitmap inImage) {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
    String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
    return Uri.parse(path);
  }

  public String getRealPathFromURI(Uri uri) {
    Cursor cursor = getContentResolver().query(uri, null, null, null, null);
    cursor.moveToFirst();
    int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
    return cursor.getString(idx);
  }
  @OnClick(R.id.choose_btn)
  public void onChooseImage(){
    //IntentHelper.chooseFileIntent(this);
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
      startActivityForResult(takePictureIntent, 10);
    }
  }

  @OnClick(R.id.upload_btn)
  public void uploadImage(){
    /*
      Create the @Upload object
     */
    createUpload(chosenFile);

    /*
      Start upload
     */
    new UploadService(upload, this).execute();
  }

  @Override public void onImageUploaded(ImageResponse response) {
    /*
      Logging the response from the image upload.
     */
    aLog.w(TAG, response.toString());
    String url = response.data.link.toString();
  }

  private void createUpload(File image){
    upload = new Upload();

    upload.image = image;
    upload.title = uploadTitle.getText().toString();
    upload.description = uploadDesc.getText().toString();

  }
}
