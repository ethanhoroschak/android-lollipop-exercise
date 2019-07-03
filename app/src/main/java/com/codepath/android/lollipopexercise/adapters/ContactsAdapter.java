package com.codepath.android.lollipopexercise.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.codepath.android.lollipopexercise.R;
import com.codepath.android.lollipopexercise.activities.DetailsActivity;
import com.codepath.android.lollipopexercise.models.Contact;

import org.parceler.Parcels;

import java.util.List;

// Provide the underlying view for an individual list item.
public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.VH> {
    private Activity mContext;
    private List<Contact> mContacts;

    public ContactsAdapter(Activity context, List<Contact> contacts) {
        mContext = context;
        if (contacts == null) {
            throw new IllegalArgumentException("contacts must not be null");
        }
        mContacts = contacts;
    }

    // Inflate the view based on the viewType provided.
    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new VH(itemView, mContext);
    }

    // Display data at the specified position
    @Override
    public void onBindViewHolder(VH holder, int position) {
        Contact contact = mContacts.get(position);
        holder.rootView.setTag(contact);
        holder.tvName.setText(contact.getName());
        //
        holder.ivProfile.clearColorFilter();
        // Store the target into the tag for the profile to ensure target isn't garbage collected prematurely
        holder.ivProfile.setTag(holder.target);
        // Instruct Picasso to load the bitmap into the target defined above
        Glide.with(mContext).load(contact.getThumbnailDrawable()).asBitmap().centerCrop().into(holder.target);
        //Glide.with(mContext).load(contact.getThumbnailDrawable()).centerCrop().into(holder.ivProfile);
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    // Provide a reference to the views for each contact item
    public class VH extends RecyclerView.ViewHolder {
        final View rootView;
        final ImageView ivProfile;
        final TextView tvName;
        final View vPalette;
        SimpleTarget<Bitmap> target;

        public VH(View itemView, final Context context) {
            super(itemView);
            rootView = itemView;
            ivProfile = (ImageView)itemView.findViewById(R.id.ivProfile);
            tvName = (TextView)itemView.findViewById(R.id.tvName);
            vPalette = itemView.findViewById(R.id.vPalette);

            // Define a listener for image loading
            target = new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    // 1. Insert the bitmap into the profile image view
                    ivProfile.setImageBitmap(resource);
                    //  2. Use generate() method from the Palette API to get the vibrant color from the bitmap
                    Palette palette = Palette.from(resource).generate();
                    Palette.Swatch vibrant = palette.getVibrantSwatch();
                    // Set the result as the background color for `R.id.vPalette` view containing the contact's name.
                    if (vibrant != null) {
                        vPalette.setBackgroundColor(vibrant.getRgb());
                    }
                }
            };

            // Navigate to contact details activity on click of card view.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Contact contact = (Contact)v.getTag();
                    if (contact != null) {
                        // Fire an intent when a contact is selected
                        Intent intent = new Intent(mContext, DetailsActivity.class);
                        // Pass contact object in the bundle and populate details activity.
                        intent.putExtra(DetailsActivity.EXTRA_CONTACT, Parcels.wrap(contact));
                        // activity transition
                        Pair<View, String> p1 = Pair.create((View) ivProfile, "profile");
                        Pair<View, String> p2 = Pair.create(vPalette, "palette");
                        Pair<View, String> p3 = Pair.create((View) tvName, "text");
                        ActivityOptionsCompat options = ActivityOptionsCompat
                                .makeSceneTransitionAnimation((Activity) mContext, p1, p2, p3);
                        mContext.startActivity(intent, options.toBundle());
                    }
                }
            });
        }
    }
}
