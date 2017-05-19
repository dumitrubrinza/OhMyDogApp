package ie.wit.ohmydog.fragments;

import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import ie.wit.ohmydog.R;
import ie.wit.ohmydog.models.Dog;


public class DogFragment extends Fragment implements AdapterView.OnItemSelectedListener {


    //public static DatabaseReference mDatabase;
    //protected ListView listView;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabase;
    private RecyclerView mDogList;
    private TextView emptyView;
    private ImageView mLostorFound;
    private String select2;

    public DogFragment() {
        // Required empty public constructor
    }


    public static DogFragment newInstance() {
        DogFragment fragment = new DogFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mDogList =(RecyclerView) getActivity().findViewById(R.id.doglist);
        mDogList.setHasFixedSize(true);
        mDogList.setLayoutManager(new LinearLayoutManager(getActivity()));


        emptyView = (TextView) getActivity().findViewById(R.id.empty_list_view);
        //listView = (ListView) v.findViewById(R.id.doglist);

        if (select2 == null) {
            Toast.makeText(getActivity(), "select2 == NULL", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getActivity(), select2, Toast.LENGTH_LONG).show();

        }
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Dogs");
        mDatabase.keepSynced(true);

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);

        //checkUserExist();

        final FirebaseRecyclerAdapter<Dog, MyViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Dog, MyViewHolder>(
                Dog.class,
                R.layout.dog_row,
                MyViewHolder.class,
                mDatabase

        ) {
            @Override
            protected void populateViewHolder(MyViewHolder viewHolder, Dog model, int position) {

                final String post_key = getRef(position).getKey();


                viewHolder.setBreed(model.getBreed());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(getActivity().getApplicationContext(), model.getImage());
                viewHolder.setLOrF(model.lostORfound);



                if (model.getBreed().isEmpty()) {
                    mDogList.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                }
                else {
                    mDogList.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                }


                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //  Toast.makeText(getActivity(), post_key, Toast.LENGTH_LONG ).show();
                        Bundle bundle = new Bundle();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        FullDetailDogFragment fragment = FullDetailDogFragment.newInstance();
                        bundle.putString("key",post_key);
                        fragment.setArguments(bundle);
                        ft.replace(R.id.basicFrame,fragment);
                        ft.addToBackStack(null);
                        ft.commit();

                    }
                });
            }




        };

      /*  FirebaseListAdapter<Dog, MyViewHolder> firebaseListAdapter = new FirebaseListAdapter<Dog, MyViewHolder>(
                getActivity(),
                Dog.class,
                R.layout.dog_row,
                databaseReference
        ) {
            @Override
            protected void populateView(View v, Dog model, int position) {

                TextView textView = (TextView) v.findViewById(R.id.doglistId);
                textView.setText(model.breed);

            }
        };*/


        // listView.setAdapter(firebase);

        mDogList.setAdapter(firebaseRecyclerAdapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = null;

        v = inflater.inflate(R.layout.fragment_main, container, false);

       // mDogList.setItemAnimator(new DefaultItemAnimator());
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

// ///////////////////////////------------
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String select = parent.getItemAtPosition(position).toString();

        if(select != null) {
            if (select.equals("All dogs")){
               //  Toast.makeText(getActivity(), select, Toast.LENGTH_LONG).show();
                select2 = select;

            }else if (select.equals("Lost dogs")){
               // Toast.makeText(getActivity(), select, Toast.LENGTH_LONG).show();
               // mDatabase.getRef().push();
                select2 = select;
                mDogList.getAdapter().notifyDataSetChanged();
                Toast.makeText(getActivity(), select, Toast.LENGTH_LONG).show();


            }else{
               // Toast.makeText(getActivity(), select, Toast.LENGTH_LONG).show();
                select2 = select;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(getActivity(), "onNothingSelected", Toast.LENGTH_LONG).show();

    }

// -----///////////////////////////////// --
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        View mView;


        public MyViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setLOrF(String lostORfound){
            ImageView mLostorFound = (ImageView) mView.findViewById(R.id.imageLandFId) ;

            if(lostORfound.equals("Lost")){
                mLostorFound.setImageResource(R.drawable.lost2);
            }else{
                mLostorFound.setImageResource(R.drawable.found);
            }

        }

        public void setBreed(String breed){
            TextView dog_breed = (TextView) mView.findViewById(R.id.doglistId);
            dog_breed.setText(breed);
        }

        public void setDesc(String desc){
            TextView dog_desc = (TextView) mView.findViewById(R.id.dogDescId);
            dog_desc.setText(desc);
        }

        public void setImage(final Context ctx, final String image){
            final ImageView dog_image = (ImageView) mView.findViewById(R.id.imageViewId);
            //Picasso.with(ctx).load(image).into(dog_image);

            Picasso.with(ctx).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(dog_image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(ctx).load(image).into(dog_image);
                }
            });
        }

    }



    @Override
    public void onResume() {
        super.onResume();

        TextView titleBar = (TextView) getActivity().findViewById(R.id.recentAddedBarTextView);
        titleBar.setText("Dog List");
      //  mDogList.
       // listView.setEmptyView(getActivity().findViewById(R.id.empty_list_view));
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
