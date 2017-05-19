package ie.wit.ohmydog.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import ie.wit.ohmydog.fragments.AddDog;
import ie.wit.ohmydog.R;
import ie.wit.ohmydog.fragments.DogFragment;
import ie.wit.ohmydog.fragments.Lost_Dogs_Fragment;
import ie.wit.ohmydog.fragments.MyMapFragment;
import ie.wit.ohmydog.main.OhMyDogApp;
import ie.wit.ohmydog.models.User;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
                                                                NavigationView.OnNavigationItemSelectedListener,
                                                                    AdapterView.OnItemSelectedListener
{

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseUsers_Nav;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //public OhMyDogApp app = OhMyDogApp.getmInstance();

    private ImageView userPhoto;
    private ListView mListView;

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayoutId);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() == null){

                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }

            }
        };

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Dogs");
        mDatabase.keepSynced(true);
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);

        if(mAuth.getCurrentUser() != null) {
            mDatabaseUsers_Nav = mDatabaseUsers.child(mAuth.getCurrentUser().getUid());
        }
        checkUserExist();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayoutId);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        userPhoto = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.nav_user_imageId);
        final TextView navUserName = (TextView)navigationView.getHeaderView(0).findViewById(R.id.nav_usernameId);

        if(mAuth.getCurrentUser() != null) {
            mDatabaseUsers_Nav.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        final User data = dataSnapshot.getValue(User.class);

                        navUserName.setText(data.getName());
                        Picasso.with(getApplicationContext()).load(data.image).into(userPhoto);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

       // navUserName.setText(mAuth.getCurrentUser().getDisplayName());
        if(mAuth.getCurrentUser() != null) {
            TextView navUserEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_userEmailId);
            navUserEmail.setText(mAuth.getCurrentUser().getEmail());
        }

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        DogFragment fragment = DogFragment.newInstance();
        ft.replace(R.id.basicFrame,fragment);
        ft.commit();

    }

    @Override
    protected void onStart() {
        super.onStart();
       /* if(mAuth.getCurrentUser() != null) {
            if (!app.mGoogleApiClient.isConnected()) {
                app.mGoogleApiClient.connect();
            }
        }*/

        //checkUserExist();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
       /*  if(mAuth.getCurrentUser() != null) {
            if (!app.mGoogleApiClient.isConnected()) {
                app.mGoogleApiClient.connect();
            }
         }*/

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       /* if(app.mGoogleApiClient != null && app.mGoogleApiClient.isConnected()){
            app.mGoogleApiClient.disconnect();
        }*/
    }

    private void checkUserExist() {

       // mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {


            final String user_id = mAuth.getCurrentUser().getUid();

            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.hasChild(user_id)){

                        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setupIntent);

                        // startActivity(new Intent(getActivity(),SetupActivity.class));
                    }
                    else{
                       /* Intent mainIntent = new Intent(getActivity(), MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);*/
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_logout){
            logout();
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        mAuth.signOut();
    }

    protected void goToActivity(Activity current,
                                Class<? extends Activity> activityClass,
                                Bundle bundle) {
        Intent newActivity = new Intent(current, activityClass);

        if (bundle != null) newActivity.putExtras(bundle);

        current.startActivity(newActivity);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment;
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (id ==R.id.home){
            fragment = DogFragment.newInstance();
            ft.replace(R.id.basicFrame, fragment);
            ft.addToBackStack(null);
            ft.commit();
        } else if (id == R.id.add_newDog){
            fragment = AddDog.newInstance();
             //((AddDog)fragment).favourites = false;
            ft.replace(R.id.basicFrame , fragment);
            ft.addToBackStack(null);
            ft.commit();
        } else if(id == R.id.nav_map){
            fragment = MyMapFragment.newInstance();
            ft.replace(R.id.basicFrame, fragment);
            ft.addToBackStack(null);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayoutId);
        drawer.closeDrawer(GravityCompat.START);
        return true;
        }
// ------------------------------
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v("ohMyDog", "Connection Fail Error in MainActivity");
    }

}
