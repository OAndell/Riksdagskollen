package oscar.riksdagskollen.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RikdagskollenApp;
import oscar.riksdagskollen.Utilities.Callbacks.RepresentativeCallback;
import oscar.riksdagskollen.Utilities.JSONModels.Intressent;
import oscar.riksdagskollen.Utilities.JSONModels.Object;
import oscar.riksdagskollen.Utilities.JSONModels.Representative;
import oscar.riksdagskollen.Utilities.JSONModels.StringRequestCallback;

/**
 * Created by gustavaaro on 2018-03-27.
 */

public class DocumentReaderActivity extends AppCompatActivity{

    Object document;
    private String docBody;
    private RikdagskollenApp app;
    private LinearLayout portaitContainer;
    private ViewGroup loadingView;
    private LinearLayout contentView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_reader);

        document = getIntent().getParcelableExtra("document");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(document.getDokumentnamn());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadingView = findViewById(R.id.loading_view);
        contentView = findViewById(R.id.act_doc_reader_content_container);
        contentView.setVisibility(View.GONE);
        app = RikdagskollenApp.getInstance();

        // Fetch the document body from the API
        app.getRiksdagenAPIManager().getDocumentBody(document, new StringRequestCallback() {
            @Override
            public void onResponse(String response) {
                docBody = response;
                loadingView.setVisibility(View.GONE);
                contentView.setVisibility(View.VISIBLE);
                populateViewWithDocumentInformation();
            }

            @Override
            public void onFail(VolleyError error) {

            }
        });


    }


    /**
     * Fills contentview with content as soon as it has been fethced from the server
     */

    private void populateViewWithDocumentInformation(){
        TextView titleTV = findViewById(R.id.act_doc_reader_title);
        TextView authorTV = findViewById(R.id.act_doc_reader_author);
        final TextView recipientTV  = findViewById(R.id.act_doc_reader_recipient);
        TextView body = findViewById(R.id.act_doc_reader_body);
        portaitContainer = findViewById(R.id.act_doc_reader_portrait_container);

        for (Intressent i : document.getDokintressent().getIntressenter()){
            final View portraitView;
            TextView nameTv;

            if(i.getRoll().equals("undertecknare")){
                portraitView = LayoutInflater.from(this).inflate(R.layout.intressent_layout,null);
                final NetworkImageView portrait = portraitView.findViewById(R.id.intressent_portait);
                portrait.setDefaultImageResId(R.mipmap.ic_default_person);
                nameTv = portraitView.findViewById(R.id.intressent_name);
                nameTv.setText(i.getNamn() + " (" + i.getPartibet() + ")");


                app.getRiksdagenAPIManager().getRepresentative(i.getIntressent_id(), new RepresentativeCallback() {
                    @Override
                    public void onPersonFetched(Representative representative) {
                        portrait.setImageUrl(representative.getBild_url_192(),app.getRequestManager().getmImageLoader());
                    }

                    @Override
                    public void onFail(VolleyError error) {

                    }
                });
                portaitContainer.addView(portraitView);
            }

        }

        titleTV.setText(document.getTitel());
        authorTV.setText(String.format("av %s", document.getUndertitel()));

        //TODO: ta ut den riktiga mottagaren ur texten och sätt den här
        recipientTV.setText("Till en person");
        body.setText(docBody);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
