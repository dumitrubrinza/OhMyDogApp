package ie.wit.ohmydog.fragments;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
//import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import ie.wit.ohmydog.R;
import ie.wit.ohmydog.activities.MainActivity;
import ie.wit.ohmydog.activities.SetupActivity;
import ie.wit.ohmydog.main.OhMyDogApp;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class AddDog extends Fragment implements CompoundButton.OnCheckedChangeListener, OnMapReadyCallback, AdapterView.OnItemSelectedListener {

    private ImageButton mSelectImage;
    private EditText mDogBreed;
    private EditText mDogDesc;

    private String mLostOrFound;
    private static final int GALARY_REQUEST = 1;

    private LocationSettingsRequest.Builder builder;

    public OhMyDogApp app = OhMyDogApp.getmInstance();

    private Uri mImageUri = null;
    private Button mSubmitBtn;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUsers;
   // private ProgressDialog mProgress;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private String mSpinerResult;



    public static AddDog newInstance() {
        AddDog fragment = new AddDog();

        return fragment;
    }

    public AddDog() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Dogs");
        mSelectImage = (ImageButton) getActivity().findViewById(R.id.imageSelect);
// --------
        Spinner spinner = (Spinner) getActivity().findViewById(R.id.dogs_spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.dogs_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        // -------------
        //mDogBreed = (EditText) getActivity().findViewById(R.id.dogBreedField);
        mDogDesc = (EditText) getActivity().findViewById(R.id.dogDescField);

        ((RadioButton) getActivity().findViewById(R.id.radioLostId)).setOnCheckedChangeListener(this);
        ((RadioButton) getActivity().findViewById(R.id.radioFoundId)).setOnCheckedChangeListener(this);


        mSubmitBtn = (Button) getActivity().findViewById(R.id.submitBtn);


        // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

        //mProgress = new ProgressDialog(getActivity());

        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galaryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galaryIntent.setType("image/*");
                startActivityForResult(galaryIntent, GALARY_REQUEST);
            }
        });


        // Toast.makeText(getActivity(), "checking googleApi " + String.valueOf(currentLat), Toast.LENGTH_SHORT).show();


        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startPosting();



                //
                //     startActivity(new Intent(getActivity(), MainActivity.class));
                //ft.replace(R.id.add_newDog, new NewFragmentToReplace(), "NewFragmentTag");
            }
        });

        /*
*/

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       // super.onCreate(savedInstanceState);
       // setContentView(R.layout.fragment_add_dog);


        View view = inflater.inflate(R.layout.fragment_add_dog, container, false);


        return view;
    }

    // onStart method
    @Override
    public void onStart() {
        super.onStart();
       }

    // onPause method
    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.v("TTOO", " Google Client Connected ONDESTROYVIEW() MapsFragment screen: "
                + (app.mGoogleApiClient.isConnected() ? "YES" : "NO") + app.mGoogleApiClient);

    }

    // onResume method
    @Override
    public void onResume() {
        super.onResume();

        TextView titleBar = (TextView) getActivity().findViewById(R.id.recentAddedBarTextView);
        titleBar.setText("Add Dog");
    }

    // onStop method
    @Override
    public void onStop() {

        super.onStop();
    }

    private void startPosting() {

      //  mProgress.setMessage("Sending ... ");
        final String breed_val = mSpinerResult.trim();
       // final String breed_val = mDogBreed.getText().toString().trim();
        final String desc_val = mDogDesc.getText().toString().trim();
        final String ratioLF_val = mLostOrFound.toString().trim();

        if (!TextUtils.isEmpty(breed_val) && !TextUtils.isEmpty(desc_val) && mImageUri != null) {
//            mProgress.show();

            StorageReference filepath = mStorage.child("Dog_Images").child(mImageUri.getLastPathSegment());

            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    final Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    final DatabaseReference newPost = mDatabase.push();

                    mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            newPost.child("breed").setValue(breed_val);
                            newPost.child("desc").setValue(desc_val);
                            newPost.child("lostORfound").setValue(ratioLF_val); //.setValue(ratioLF_val));
                            newPost.child("uid").setValue(mCurrentUser.getUid());
                            newPost.child("image").setValue(downloadUrl.toString());

                           // newPost.child("lat").setValue(String.valueOf(currentLat));
                            newPost.child("lat").setValue(String.valueOf(app.mCurrentLocation.getLatitude()));
                            newPost.child("lon").setValue(String.valueOf(app.mCurrentLocation.getLongitude()));
                          //  newPost.child("lon").setValue(String.valueOf(currentLong));

                            newPost.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        resetFields();

                                       /* Fragment fragment;
                                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                                        fragment = DogFragment.newInstance();
                                        ft.replace(R.id.basicFrame, fragment);
                                        ft.addToBackStack(null);
                                        ft.commit();*/

                                    } else {
                                       // Toast.makeText(getActivity(), "Database error! Task.notSuccessful", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    // newPost.child("latLong").setValue(locationListener);


                  //  mProgress.dismiss();
                }

            });
        }


    }
    private void resetFields(){
        mDogDesc.setText("");
        mSelectImage.setImageURI(null);
        mLostOrFound.equals(null);
        mSpinerResult.equals(null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if((requestCode == GALARY_REQUEST && requestCode == RESULT_OK)){

        mImageUri = data.getData();
        mSelectImage.setImageURI(mImageUri);
        // }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


        // Is the button now checked?
        //  boolean checked = ((RadioButton) buttonView).isChecked();

        // Check which radio button was clicked
        switch (buttonView.getId()) {
            case R.id.radioLostId:
                if (isChecked)
                    mLostOrFound = "Lost"; //="lost";
                // Pirates are the best
                break;
            case R.id.radioFoundId:
                if (isChecked)
                    mLostOrFound = "Found"; // = "found";
                // Ninjas rule
                break;
        }


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
         googleMap.clear();

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mSpinerResult = (String) parent.getItemAtPosition(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}