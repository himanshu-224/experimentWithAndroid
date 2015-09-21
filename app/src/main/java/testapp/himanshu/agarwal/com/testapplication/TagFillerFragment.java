package testapp.himanshu.agarwal.com.testapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Vector;


public class TagFillerFragment extends Fragment {

    public final String LOG_TAG = TagFillerFragment.class.getSimpleName();

    private CustomAutoCompleteTextView autoCompleteTextView;
    private LinearLayout linearLayout;
    private HorizontalScrollView horizontalScrollView;
    private ActionMode mActionMode;
    View removedView;

    private ArrayList<Tag> tags = new ArrayList<>();

    private ImageView mainImageView;
    private Bitmap imageBitmap;
    private String curImagePath;
    private ScrollView scrollbar;

    public TagFillerFragment() {
    }

    public ArrayList<Tag> getTags()
    {
        return tags;
    }
    public boolean addTags(String tag)
    {
        /* Here we need to determine if that tag exists in our database or not.
         * If yes we need to populate the parameters for the TAG from our database.
         * Else we need to save the TAG in the database with some default
         * parameters and mark that it is not approved yet.
         * Someone needs to define the parameters for all the non-approved TAGs in
         * the database regularly. I guess this process has to be manual for now */
        Log.v(LOG_TAG, "Adding Tag " + tag);
        Tag thisTag = new Tag(tag, "taste", Tag.TYPE_NOUN, true);
        if (tags.contains(thisTag))
        {
            return false;
        }
        else
        {
            tags.add(thisTag);
            return true;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
        Log.v(LOG_TAG, "On Create for TagFillerFragment, fetch any saved tags from database");
        curImagePath = "/storage/emulated/0/Pictures/SplatterImages/IMG_20150921_002024_-278311816.jpg";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.v(LOG_TAG, "On onCreateView for TagFillerFragment");
        View rootView = inflater.inflate(R.layout.fragment_tag_filler, container, false);

        scrollbar = (ScrollView) rootView.findViewById(R.id.scrollbar);
        mainImageView = (ImageView) rootView.findViewById(R.id.main_image);
        autoCompleteTextView = (CustomAutoCompleteTextView) rootView.findViewById(R.id.auto_complete);
        linearLayout = (LinearLayout) rootView.findViewById(R.id.linear_layout);
        horizontalScrollView = (HorizontalScrollView) rootView.findViewById(R.id.hsv);
        horizontalScrollView.setVisibility(View.GONE);
        final TagAutocompleteAdapter adapter =
                new TagAutocompleteAdapter(getActivity(), R.layout.list_item_tag_suggestion);
        autoCompleteTextView.setAdapter(adapter);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tag thisTag = adapter.getItem(position);
                String s = thisTag.getTagName();

                /* Store the Tags locally, and also check if it has not already been entered
                 * by the user. If already entered, inform the user and return from here */
                if (!addTags(s.replace("\n", ""))) {
                    autoCompleteTextView.setText("");
                    Toast.makeText(getActivity(), "Tag Already Added", Toast.LENGTH_SHORT).show();
                    return;
                }
                View view1 = getActivity().getLayoutInflater().inflate(R.layout.list_item_tag, null);
                TextView textView = (TextView) view1.findViewById(R.id.tv);
                textView.setText(s.replace("\n", ""));
                textView.setTextColor(getActivity().getResources().getColor(R.color.white));
                if (/*tag.getTypeId() == Tag.TYPE_NOUN*/s.length() % 2 == 0) {
                    textView.getBackground().setColorFilter(getActivity().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);
                } else {
                    textView.getBackground().setColorFilter(getActivity().getResources().getColor(R.color.purple), PorterDuff.Mode.SRC_IN);
                }
                if (horizontalScrollView.getVisibility() == View.GONE) {
                    horizontalScrollView.setVisibility(View.VISIBLE);
                }
                horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                view1.setTag("test");
                linearLayout.addView(view1);
                autoCompleteTextView.setText("");
            }
        });

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before,
                                      int count) {
                //scrollbar.scrollTo(0,1000);
                Log.v(LOG_TAG, "Moved scrollbar");
            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (s.toString().contains("\n")) {

                /* Store the Tags locally, and also check if it has not already been entered
                 * by the user. If already entered, inform the user and return from here */
                    if (!addTags(s.toString().replace("\n", ""))) {
                        autoCompleteTextView.setText("");
                        Toast.makeText(getActivity(), "Tag Already Added", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //move to flow layout
                    View view1 = getActivity().getLayoutInflater().inflate(R.layout.list_item_tag, null);
                    TextView textView = (TextView) view1.findViewById(R.id.tv);
                    textView.setText(s.toString().replace("\n", ""));
                    textView.setTextColor(getActivity().getResources().getColor(R.color.white));
                    if (/*tag.getTypeId() == Tag.TYPE_NOUN*/s.toString().length() % 2 == 0) {
                        textView.getBackground().setColorFilter(getActivity().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);
                    } else {
                        textView.getBackground().setColorFilter(getActivity().getResources().getColor(R.color.purple), PorterDuff.Mode.SRC_IN);
                    }
                    if (horizontalScrollView.getVisibility() == View.GONE) {
                        horizontalScrollView.setVisibility(View.VISIBLE);
                    }
                    horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                    view1.setTag("test");
                    linearLayout.addView(view1);
                    autoCompleteTextView.setText("");
                }
            }
        });

        imageBitmap = null;
        setPic(curImagePath);
        if (imageBitmap != null)
            mainImageView.setImageBitmap(imageBitmap);

        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();

        /* Restore the tags on returning back to this activity */
        for (Tag tag:tags)
        {
            String s = tag.getTagName();
            View view1 = getActivity().getLayoutInflater().inflate(R.layout.list_item_tag, null);
            TextView textView = (TextView) view1.findViewById(R.id.tv);
            textView.setText(s);
            textView.setTextColor(getActivity().getResources().getColor(R.color.white));
            if (s.length() % 2 == 0) { /*tag.getTypeId() == Tag.TYPE_NOUN*/
                textView.getBackground().setColorFilter(getActivity().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);
            } else {
                textView.getBackground().setColorFilter(getActivity().getResources().getColor(R.color.purple), PorterDuff.Mode.SRC_IN);
            }
            if (horizontalScrollView.getVisibility() == View.GONE) {
                horizontalScrollView.setVisibility(View.VISIBLE);
            }
            horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            view1.setTag("test");
            linearLayout.addView(view1);
        }
        return rootView;
    }

    /*Helper function to get and set images */
    private void setPic(String imagePath)
    {
		/* There isn't enough memory to open up more than a couple camera photos
		 * So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
        int targetW = mainImageView.getWidth();
        int targetH = mainImageView.getHeight();

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
        imageBitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
    }
}

