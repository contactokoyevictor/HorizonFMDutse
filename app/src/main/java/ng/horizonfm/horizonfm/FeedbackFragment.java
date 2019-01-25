package ng.horizonfm.horizonfm;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedbackFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedbackFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    EditText editTextEmail, editTextSubject, editTextMessage;
    Button btnSend, btnAttachment;
    String email, subject, message, attachmentFile;
    Uri URI = null;
    private static final int PICK_FROM_GALLERY = 101;
    int columnIndex;
    final boolean[] status = {false};

    public FeedbackFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FeedbackFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FeedbackFragment newInstance(String param1, String param2) {
        FeedbackFragment fragment = new FeedbackFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);

        editTextEmail = (EditText) view.findViewById(R.id.editTextTo);
        editTextSubject = (EditText) view.findViewById(R.id.editTextSubject);
        editTextMessage = (EditText) view.findViewById(R.id.editTextMessage);
        btnAttachment = (Button) view.findViewById(R.id.buttonAttachment);
        btnSend = (Button) view.findViewById(R.id.buttonSend);

        btnSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    email = editTextEmail.getText().toString();
                    subject = editTextSubject.getText().toString();
                    message = editTextMessage.getText().toString();
                    if(email.isEmpty()
                            || !Patterns.EMAIL_ADDRESS.matcher(email.toString()).matches())
                    {
                        editTextEmail.setError("Please enter valid email address");
                        editTextEmail.requestFocus();
                        return;
                    }
                    else if(subject.isEmpty())
                    {
                        editTextSubject.setError("Please enter subject");
                        editTextSubject.requestFocus();
                        return;
                    }
                    else if(message.isEmpty())
                    {
                        editTextMessage.setError("Feedback message cannot be empty");
                        editTextMessage.requestFocus();
                        return;
                    }
                    else if(message.length() > 1000)
                    {
                        editTextMessage.setError("Feedback message is too long");
                        editTextMessage.requestFocus();
                        return;
                    }
                    else {
                        sendMessage(email, subject, message);

                        if (status[0]) {
                            Log.e("msq", "Message sent ");
                            editTextEmail.setText("");
                            editTextSubject.setText("");
                            editTextMessage.setText("");
                            Toast.makeText(getContext(), " Message sent successfully ", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), " Request failed try again ", Toast.LENGTH_LONG).show();
                        }
                    }


                } catch (Throwable t) {
                    Log.i("msq", "Request failed try again: " + t.toString());
                }

            }
        });



        btnAttachment.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        return view;
    }


    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra("return-data", true);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"),PICK_FROM_GALLERY);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK) {
            /**
             * Get Path
             */
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            attachmentFile = cursor.getString(columnIndex);
            Log.e("Attachment Path:", attachmentFile);
            URI = Uri.parse("file://" + attachmentFile);
            cursor.close();
        }
    }


    private void sendMessage(final String senderEmail, final String subject, final String message) {
        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setTitle("Sending message");
        dialog.setMessage("Please wait");
        dialog.show();

        Thread sender = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GmailSender sender = new GmailSender("horizonfmdutsejigawa@gmail.com", "J1gawa$tat3");
                    sender.sendMail(""+subject,
                            ""+message+"\n\n\n\n\n"+"sender email :"+senderEmail,
                            ""+senderEmail,
                            "frcn.horizonfm.dutse@gmail.com");
//                    frcn.horizonfm.dutse@gmail.com
                    dialog.dismiss();
                    status[0] = true;
                } catch (Exception e) {
                    Log.e("msq", "Error: " + e.getMessage());
                    status[0] = false;
                }
            }
        });
        sender.start();
    }


}
