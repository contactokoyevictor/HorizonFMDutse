package ng.horizonfm.horizonfm;


import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MoreFragment extends ListFragment implements OnItemClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    View view;
    String[] items = {"About", "News", "Lifestyle", "Facebook", "Twitter", "Youtube", "Feedback"};
    int[] images = {R.drawable.ic_about, R.drawable.ic_news, R.drawable.ic_livejournal, R.drawable.ic_facebook,
            R.drawable.ic_twitter, R.drawable.ic_youtube, R.drawable.ic_feedback};
    ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
    SimpleAdapter adapter;
    private String mParam1;
    private String mParam2;
    private ImageView mImageView;
    private TextView mTitleTextView;

    public MoreFragment() {
        // Required empty public constructor
    }

    public static MoreFragment newInstance(String param1, String param2)
    {
        MoreFragment fragment = new MoreFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        if (!MainActivity.getInstance().chechConnection()) {
            MainActivity.getInstance().snackbar = Snackbar.make(MainActivity.getInstance().findViewById(R.id.fab),
                    MainActivity.getInstance().message = "Sorry! Not connected to internet", Snackbar.LENGTH_LONG);
            MainActivity.getInstance().sbView = MainActivity.getInstance().snackbar.getView();

            MainActivity.getInstance().textView = (TextView) MainActivity.getInstance().sbView.findViewById(android.support.design.R.id.snackbar_text);
            MainActivity.getInstance().showSnack(false, MainActivity.getInstance().message = "Sorry! Not connected to internet");
        }
    }

    Dialog dialog;

    private void showDialog(){
        dialog = new Dialog(getActivity());
        TextView txtclose;
        dialog.setContentView(R.layout.aboutus);
        txtclose = (TextView) dialog.findViewById(R.id.txtclose);
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HashMap<String, String> map = new HashMap<String, String>();

        //FILL
        for (int i = 0; i < items.length; i++) {
            map = new HashMap<String, String>();
            map.put("Item", items[i]);
            map.put("Image", Integer.toString(images[i]));
            data.add(map);
        }

        //KEYS IN MAP
        String[] from = {"Item", "Image"};

        //IDS OF VIEWS
        int[] to = {R.id.nameTxt, R.id.imageView1};

        //ADAPTER
        adapter = new SimpleAdapter(getActivity(), data, R.layout.program_list_model, from, to);
        setListAdapter(adapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //FacebookFragment
        Toast.makeText(getActivity(), "Item: " + position, Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

        getListView().setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                switch (data.get(pos).get("Item")) {
                    case "Facebook":
                        setFragment(new FacebookFragment());
                        break;
                    case "About":
                        showDialog();
                        break;
                    case "News":
                        setFragment(new NewsFragment());
                        break;
                    case "Lifestyle":
                        setFragment(new BlogFragment());
                        break;
                    case "Twitter":
                        setFragment(new TwitterFragment());
                        break;
                    case "Youtube":
                        setFragment(new YoutubeFragment());
                        break;
                    case "Feedback":
                        setFragment(new FeedbackFragment());
                }
            }
        });
    }


    // class for being re-used by several instances
    protected void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (getFragmentManager().findFragmentById(R.id.frame_container) == null) {
            fragmentTransaction.replace(R.id.frame_container, fragment)
                    .addToBackStack(fragment.getClass().getName())
                    .commit();
        } else {
            fragmentTransaction.add(R.id.frame_container, fragment)
                    .addToBackStack(fragment.getClass().getName())
                    .commit();
        }
    }

}
