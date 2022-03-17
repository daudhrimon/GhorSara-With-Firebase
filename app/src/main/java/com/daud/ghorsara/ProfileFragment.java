package com.daud.ghorsara;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private ImageView coverIv, updateName, updatePhone;
    private FloatingActionButton updateProfileIv;
    private TextView nameTv, phoneTv, emailTv;
    private CircleImageView profileIv;
    private MaterialButton signOutBtn;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Uri imageUri;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment = inflater.inflate(R.layout.fragment_profile, container, false);
        //Initial
        init(fragment);

        String userId = firebaseAuth.getCurrentUser().getUid();

        DatabaseReference dataRef = databaseReference.child("UserInfo");
        dataRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    ProfileMc profileData = snapshot.getValue(ProfileMc.class);
                    nameTv.setText(profileData.getUserName());
                    phoneTv.setText(profileData.getUserPhone());
                    emailTv.setText(profileData.getUserEmail());

                    Picasso.get()
                            .load(profileData.getUserImage())
                            .placeholder(R.drawable.ic_baseline_person_24)
                            .error(R.drawable.ic_baseline_person_24)
                            .into(profileIv);
                }else{
                    nameTv.setText("Empty");
                    phoneTv.setText("Empty");
                    emailTv.setText("Empty");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //////////////////////////////
        //SignOut OnClick //

        signOutBtn.setOnClickListener(view -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle("Sign-Out Alert !").setMessage("Are You Sure ? Do You Really Want To Sign-Out ?").setCancelable(false);
            alert.setPositiveButton("YES", (dialogInterface, i) -> {
                FirebaseAuth.getInstance().signOut();
                editor.putString("LogInStatus","false");
                editor.commit();
                startActivity(new Intent(getContext(),SignInActivity.class));
                getActivity().finish();
            });
            alert.setNegativeButton("NO",(dialogInterface, i) -> {
               Toast.makeText(getContext(),"Ok ! We Got It",Toast.LENGTH_SHORT).show();
            });
            alert.show();
        });


        /////////////////////////////////////////////////////////////////////////////////////////////////


        ///////////////////////////////
        //UpDate ProfilePic OnClick //

        updateProfileIv.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*").setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent,22);
        });

        return fragment;
    }

    private void init(View fragment) {
        coverIv = fragment.findViewById(R.id.coverIv);
        updateName = fragment.findViewById(R.id.updateName);
        updatePhone = fragment.findViewById(R.id.updatePhone);
        updateProfileIv = fragment.findViewById(R.id.updateProfileIv);
        nameTv = fragment.findViewById(R.id.nameTv);
        phoneTv = fragment.findViewById(R.id.phoneTv);
        emailTv = fragment.findViewById(R.id.emailTv);
        profileIv = fragment.findViewById(R.id.profileIv);
        signOutBtn = fragment.findViewById(R.id.signOutBtnF);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        sharedPreferences = getContext().getSharedPreferences("MyShared", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==22 && data!=null && data.getData()!=null){
            imageUri = data.getData();

            firebaseAuth = FirebaseAuth.getInstance();
            storageReference = FirebaseStorage.getInstance().getReference();
            String userId = firebaseAuth.getCurrentUser().getUid();
            StorageReference imageUriRef = storageReference.child("UserImage").child(userId);
            imageUriRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){
                        imageUriRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                               String imageUriLink = uri.toString();
                               databaseReference = FirebaseDatabase.getInstance().getReference();
                               DatabaseReference imageDataRef = databaseReference.child("UserInfo").child(userId).child("UserImage");
                               imageDataRef.setValue(imageUriLink).addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {
                                       if (task.isSuccessful()){
                                           Toast.makeText(getContext(),"Done",Toast.LENGTH_SHORT).show();
                                       }else{
                                           Toast.makeText(getContext(), ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                       }
                                   }
                               });
                            }
                        });
                    }else{
                        Toast.makeText(getContext(), ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
