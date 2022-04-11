package com.example.app_chat.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app_chat.MessageActivity;
import com.example.app_chat.R;
import com.example.app_chat.model.Chat;
import com.example.app_chat.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    private Context context;
    private List<User> mListUser;
    private boolean ischat;

    String theLastMessage;

    public UserAdapter(Context context, List<User> mListUser, boolean ischat) {
        this.context = context;
        this.mListUser = mListUser;
        this.ischat = ischat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        User user = mListUser.get(position);
        holder.tvItemUsername.setText(user.getUsername());


        if (user.getImageURL().equals("default")){
            holder.imgItemUser.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(context).load(user.getImageURL()).into(holder.imgItemUser);
        }

        if (ischat){
            lastMessage(user.getId(), holder.tvLastMessage);
        } else {
            holder.tvLastMessage.setVisibility(View.GONE);
        }

        if (ischat){
            if (user.getStatus().equals("online")){
                holder.imgOn.setVisibility(View.VISIBLE);
                holder.imgOff.setVisibility(View.GONE);
            } else {
                holder.imgOn.setVisibility(View.GONE);
                holder.imgOff.setVisibility(View.VISIBLE);
            }
        } else {
            holder.imgOn.setVisibility(View.GONE);
            holder.imgOff.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("userid", user.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListUser.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView tvItemUsername;
        private final ImageView imgItemUser;
        private ImageView imgOn, imgOff;
        private TextView tvLastMessage;

        public ViewHolder(View itemView) {
            super(itemView);

            tvItemUsername = itemView.findViewById(R.id.tv_item_username);
            imgItemUser = itemView.findViewById(R.id.img_item_user);
            imgOn = itemView.findViewById(R.id.img_on);
            imgOff = itemView.findViewById(R.id.img_off);
            tvLastMessage = itemView.findViewById(R.id.tv_lass_message);
        }
    }

    // Check fpr last message
     private void lastMessage(String userid, TextView last_msg){
        theLastMessage = "default";
         FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
         DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

         reference.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                     Chat chat = dataSnapshot.getValue(Chat.class);
                     assert chat != null;
                     if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                     chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())){
                         theLastMessage = chat.getMessage();
                     }
                 }

                 switch (theLastMessage){
                     case "default":
                         last_msg.setText("No Message");
                         break;
                     default:
                         last_msg.setText(theLastMessage);
                         break;
                 }

                 theLastMessage = "default";
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });
     }
}
