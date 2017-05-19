package ie.wit.ohmydog.fragments;

import android.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import ie.wit.ohmydog.R;
import ie.wit.ohmydog.models.Dog;
import ie.wit.ohmydog.models.User;

import static android.content.ContentValues.TAG;

public class FullDetailDogFragment extends Fragment {


    boolean isImageFitToScreen;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseArg;
    private DatabaseReference mDatabaseUsers;
    private RecyclerView mDogList;
    private FirebaseAuth mAuth;
    private ImageView mFullImage;
    private TextView mFullLostOrFound;
    private TextView mFullDogBreed;
    private TextView mFullDogDesc;
    private TextView mFullUsername;
    private CircleImageView mFullUsernamePic;
    private Button mSingeRemoveBtn;
    private Uri uri;
    String post_uid;




    public FullDetailDogFragment() {
        // Required empty public constructor
    }


    public static FullDetailDogFragment newInstance() {
        FullDetailDogFragment fragment = new FullDetailDogFragment();

        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        TextView titleBar = (TextView) getActivity().findViewById(R.id.recentAddedBarTextView);
        titleBar.setText("Full Dog Detail");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Toast.makeText(getActivity(), arg, Toast.LENGTH_LONG ).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle args = new Bundle();

        args = getArguments();
        final String arg =  args.getString("key");

        View v = null;

        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.full_detail_dog_row2, container, false);


        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("Dogs");
        mDatabaseArg = mDatabase.child(arg);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");



       // Toast.makeText(getActivity(), arg,Toast.LENGTH_SHORT).show();

        mFullLostOrFound = (TextView) v.findViewById(R.id.fullLandFid);
        mFullDogBreed = (TextView) v.findViewById(R.id.fullBreedId);
        mFullDogDesc = (TextView) v.findViewById(R.id.fullDogDscId);
        mFullImage = (ImageView) v.findViewById(R.id.fullDogImgId);
        mFullUsername = (TextView) v.findViewById(R.id.fullUsernameId);
        mFullUsernamePic = (CircleImageView) v.findViewById(R.id.fullUsernamePicId);
        mSingeRemoveBtn = (Button)v.findViewById(R.id.singeRemove);

        mSingeRemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child(arg).removeValue();

                Toast.makeText(getActivity(), mDatabase.child(arg).toString(), Toast.LENGTH_SHORT).show();

                Fragment fragment;
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                fragment = DogFragment.newInstance();
                ft.replace(R.id.basicFrame, fragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI

                if (dataSnapshot.exists()) {

                    final Dog post = dataSnapshot.getValue(Dog.class);


                    mFullLostOrFound.setText(post.lostORfound);
                    mFullDogBreed.setText(post.breed);
                    mFullDogDesc.setText(post.desc);
                    mFullUsername.setText(post.username);

                     post_uid = (String)dataSnapshot.child("uid").getValue();
                 //  Toast.makeText(getActivity(), mDatabaseUsers.child(post.uid).toString(), Toast.LENGTH_SHORT).show();

                    if(mAuth.getCurrentUser().getUid().equals(post_uid)){
                        //  Toast.makeText(getActivity(), arg, Toast.LENGTH_SHORT).show();
                        mSingeRemoveBtn.setVisibility(View.VISIBLE);
                    }

                    mDatabaseUsers.child(post.uid).addValueEventListener(new ValueEventListener() {


                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()) {


                            final User data = dataSnapshot.getValue(User.class);
                            Picasso.with(getActivity().getApplicationContext()).load(data.image).into(mFullUsernamePic);


                        }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    Picasso.with(getActivity().getApplicationContext()).load(post.image).into(mFullImage);


                }
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mDatabaseArg.addValueEventListener(postListener);

       return v;
    }

    public static class MyViewHolder2 extends RecyclerView.ViewHolder {

        View mView;


        public MyViewHolder2(View itemView) {
            super(itemView);

            mView = itemView;

        }

        @Override
        public String toString() {
            return super.toString();
        }

        public void setLostOrFound(String lostORfound){
            TextView dog_LostOrFound = (TextView) mView.findViewById(R.id.fullLandFid);
            dog_LostOrFound.setText(lostORfound);
        }

        public void setBreed(String breed){
            TextView dog_breed = (TextView) mView.findViewById(R.id.fullBreedId);
            dog_breed.setText(breed);
        }

        public void setDesc(String desc){
            TextView dog_desc = (TextView) mView.findViewById(R.id.fullDogDscId);
            dog_desc.setText(desc);
        }

        public void setImage(Context ctx, String image){
            ImageView dog_image = (ImageView) mView.findViewById(R.id.fullDogImgId);
            Picasso.with(ctx).load(image).into(dog_image);
        }

    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }



}
