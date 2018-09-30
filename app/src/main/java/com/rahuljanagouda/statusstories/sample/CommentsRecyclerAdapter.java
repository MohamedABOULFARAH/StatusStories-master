package com.rahuljanagouda.statusstories.sample;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by AkshayeJH on 04/01/18.
 */

public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder> {

    private final Context context;
    private List<Commentaire> commentsList;
    private FirebaseFirestore mFirestore;
    private List<Users> usersList;






    public CommentsRecyclerAdapter(Context context, List<Commentaire> commentsList){

        this.commentsList = commentsList;
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item, parent, false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Date comment_time = commentsList.get(position).getCommentaireTime();
        Calendar cal = Calendar.getInstance();
        cal.setTime(comment_time);
        int year= cal.get(Calendar.YEAR);
        int month= cal.get(Calendar.MONTH);
        int day= cal.get(Calendar.DAY_OF_MONTH);
        int hours= cal.get(Calendar.HOUR_OF_DAY);
        int minuts= cal.get(Calendar.MINUTE);
        Date date = new Date(year, month, day, hours, minuts);
        String times=getTimestampDiff(date)+"h";

        //main();

        holder.comment_time_view.setText(times);
        holder.comment_list_view.setText(commentsList.get(position).getCommentaire());
        usersList = new ArrayList<>();
        mFirestore = FirebaseFirestore.getInstance();


        DocumentReference docRef = mFirestore.collection("Users").document(commentsList.get(position).getUserId());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Users users = documentSnapshot.toObject(Users.class);
                usersList.clear();
                usersList.add(users);

                holder.comment_name_view.setText(usersList.get(0).getName());
                CircleImageView comment_image_view = holder.comment_image_view;
                Picasso.get().load(usersList.get(0).getImage()).resize(250,250).into(comment_image_view);
            }

        });





        final String photo_id = commentsList.get(position).getUserId();


    }


    @Override
    public int getItemCount() {
        return commentsList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private CircleImageView comment_image_view;
        private TextView comment_name_view,comment_time_view,comment_list_view;


        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            comment_image_view = (CircleImageView) mView.findViewById(R.id.comment_list_image);
            comment_name_view = (TextView) mView.findViewById(R.id.comment_list_name);
            comment_time_view = (TextView) mView.findViewById(R.id.comment_list_time);
            comment_list_view = (TextView) mView.findViewById(R.id.comments_list);


        }
    }








        public static int getTimestampDiff(Date d) {

            Calendar now = Calendar.getInstance();
            Date date2 = new Date(
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH),
                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE));

            final DateTime start = new DateTime(d.getTime());
            final DateTime end = new DateTime(date2);


            Period p = new Period(start, end);
            System.out.println(p.getHours());
            System.out.println("hour "+now.get(Calendar.HOUR_OF_DAY));
            PeriodFormatter formatter = new PeriodFormatterBuilder()
                    .printZeroAlways().minimumPrintedDigits(2).appendYears()
                    .appendSuffix(" year", " years").appendSeparator(", ")
                    .appendMonths().appendSuffix(" month", " months")
                    .appendSeparator(", ").appendDays()
                    .appendSuffix(" day", " days").appendSeparator(" and ")
                    .appendHours().appendLiteral(":").appendMinutes()
                    .appendLiteral(":").appendSeconds().toFormatter();
            return p.getHours();
        }

        public static void main() {
            Calendar now = Calendar.getInstance();

           // String diff = getTimestampDiff(new Timestamp(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_WEEK), now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), now.get(Calendar.SECOND), 0));
           // System.out.println(diff);
            System.out.println(now.get(Calendar.YEAR)+" "+now.get(Calendar.MONTH)+" "+now.get(Calendar.DAY_OF_MONTH)+" "+now.get(Calendar.HOUR_OF_DAY)+" "+now.get(Calendar.MINUTE)+" "+now.get(Calendar.SECOND));
        }
    }

