package com.example.lubble;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/*import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.simple.SimpleLoggerContextFactory;*/
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;

public class ChatActivity extends AppCompatActivity implements AIListener {

    private AIService aiService;
    EditText userInput;
    RecyclerView recyclerView;
    MessageAdapter messageAdapter;
    List<ResponseMessage> responseMessageList;

    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;
    /*static {
        LogManager.setFactory(new SimpleLoggerContextFactory());
        // but SimpleLoggerContextFactory does not allow to override log level in runtime
        // I guess you would need to play with root logger, but it's easier to set it in resource file
    }*/
    private DatabaseReference databaseReference;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        /*int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);

        if (permission != PackageManager.PERMISSION_GRANTED) {

            makeRequest();
        }

        final AIConfiguration config = new AIConfiguration("79a814cf40a04414bcdc456e20e4e775",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        aiService = AIService.getService(this, config);
        aiService.setListener(this);

        userInput = findViewById(R.id.userInput);
        recyclerView = findViewById(R.id.conversation);
        responseMessageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(responseMessageList, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(messageAdapter);

        userInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEND) {
                    ResponseMessage responseMessage = new ResponseMessage(userInput.getText().toString(), true);
                    responseMessageList.add(responseMessage);
                    userInput.setText("");
                    userInput.clearComposingText();
                    messageAdapter.notifyDataSetChanged();
                    RetrieveFeedTask task = new RetrieveFeedTask();
                    task.execute(responseMessage.text);
                   *//* if (!isLastVisible())
                        recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);*//*
                }
                return false;
            }
        });*/
        gson = new Gson();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userInput = findViewById(R.id.userInput);
        recyclerView = findViewById(R.id.conversation);
        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        fetch();

        userInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    final String userMessage = userInput.getText().toString();
                    Long time = System.currentTimeMillis();
                    ResponseMessage responseMessage = new ResponseMessage(userMessage,true);

                    String json = gson.toJson(responseMessage);
                    databaseReference.child("posts").child(time.toString()).setValue(responseMessage)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    userInput.setText("");
                                    //userInput.clearComposingText();
                                    //messageAdapter.notifyDataSetChanged();
                                    RetrieveFeedTask task = new RetrieveFeedTask();
                                    task.execute(userMessage);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("error",e.toString());
                                }
                            });
                    //recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
                }
                return false;
            }
        });
    }

    private void fetch() {
        DatabaseReference query = FirebaseDatabase.getInstance()
                .getReference()
                .child("posts");

        final FirebaseRecyclerOptions<ResponseMessage> options =
                new FirebaseRecyclerOptions.Builder<ResponseMessage>()
                        .setQuery(query, new SnapshotParser<ResponseMessage>() {
                            @NonNull
                            @Override
                            public ResponseMessage parseSnapshot(@NonNull DataSnapshot snapshot) {
                                Log.d("parseSnapshot",snapshot.toString());

                                //ResponseMessage responseMessage = gson.fromJson((JsonElement) snapshot.getValue(),ResponseMessage.class);
                                ResponseMessage responseMessage = snapshot.getValue(ResponseMessage.class);
                                Log.d("parse",responseMessage.toString());
                                return responseMessage;
                            }
                        }).build();


        adapter = new FirebaseRecyclerAdapter<ResponseMessage, ViewHolder>(options) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(viewType, parent, false);

                return new ViewHolder(view);
            }

            @Override
            public int getItemCount() {
                return super.getItemCount();
            }

            @Override
            protected void onBindViewHolder(ViewHolder holder, final int position, ResponseMessage model) {
                Log.d("itemcount",getItemCount()+"!");
                //recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
                holder.setTxtTitle(model.getText());

               /* if (!isLastVisible()) {
                    Log.d("lastVisible","yo");
                    recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
                }*/
                /*holder.setTxtDesc(model.getmDesc());

                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(ChatActivity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
                    }
                });*/
            }

            @Override
            public int getItemViewType(int position) {
                /*return super.getItemViewType(position);*/
                if(options.getSnapshots().get(position).isMe)
                {
                    return R.layout.me_bubble;
                }
                return R.layout.bot_bubble;
            }

        };
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                linearLayoutManager.smoothScrollToPosition(recyclerView, null, adapter.getItemCount());
            }
        });
        recyclerView.setAdapter(adapter);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout root;
        public TextView txtTitle;
        public TextView txtDesc;

        public ViewHolder(View itemView) {
            super(itemView);
            /*root = itemView.findViewById(R.id.list_root);*/
            txtTitle = itemView.findViewById(R.id.textMessage);
            /*txtDesc = itemView.findViewById(R.id.list_desc);*/
        }

        public void setTxtTitle(String string) {
            txtTitle.setText(string);
        }


        /*public void setTxtDesc(String string) {
            txtDesc.setText(string);
        }*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    class RetrieveFeedTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... voids) {
            String s = null;
            try {

                s = GetText(voids[0]);


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Log.d("karma", "Exception occurred " + e);
            }

            return s;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //outputText.setText(s);
            Log.d("onPOstExecute",s+"!");
            /*ResponseMessage responseMessage2 = new ResponseMessage(s, false);
            responseMessageList.add(responseMessage2);
            messageAdapter.notifyDataSetChanged();

            if (!isLastVisible())
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);*/
        }
    }

    public String GetText(String query) throws UnsupportedEncodingException {

        String text = "";
        BufferedReader reader = null;

        // Send data
        try {

            // Defined URL  where to send data
            URL url = new URL("https://api.dialogflow.com/v1/query?v=20150910");

            // Send POST data request

            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);

            conn.setRequestProperty("Authorization", "Bearer " + "79a814cf40a04414bcdc456e20e4e775");
            conn.setRequestProperty("Content-Type", "application/json");

            //Create JSONObject here
            JSONObject jsonParam = new JSONObject();
            JSONArray queryArray = new JSONArray();
            queryArray.put(query);
            jsonParam.put("query", queryArray);
//            jsonParam.put("name", "order a medium pizza");
            jsonParam.put("lang", "en");
            jsonParam.put("sessionId", "1234567890");


            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            Log.d("karma", "after conversion is " + jsonParam.toString());
            wr.write(jsonParam.toString());
            wr.flush();
            Log.d("karma", "json is " + jsonParam);

            // Get the server response

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;


            // Read Server Response
            while ((line = reader.readLine()) != null) {
                // Append server response in string
                sb.append(line + "\n");
            }


            text = sb.toString();


            JSONObject object1 = new JSONObject(text);
            JSONObject object = object1.getJSONObject("result");
            JSONObject fulfillment = null;
            String speech = null;
//            if (object.has("fulfillment")) {
            fulfillment = object.getJSONObject("fulfillment");
//                if (fulfillment.has("speech")) {
            speech = fulfillment.optString("speech");
//                }
//            }


            Log.d("karma ", "response is " + text);
            return speech;

        } catch (Exception ex) {
            Log.d("karma", "exception at last " + ex);
        } finally {
            try {

                reader.close();
            } catch (Exception ex) {
            }
        }

        return null;
    }

    boolean isLastVisible() {
        LinearLayoutManager layoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
        int pos = layoutManager.findLastCompletelyVisibleItemPosition();
        int numItems = recyclerView.getAdapter().getItemCount();
        return (pos >= numItems);
    }

    protected void makeRequest() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 101: {

                if (grantResults.length == 0
                        || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {


                } else {

                }
                return;
            }
        }
    }


    @Override
    public void onResult(AIResponse result) {

    }

    @Override
    public void onError(AIError error) {

    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }
}
