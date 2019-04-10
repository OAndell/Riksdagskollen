package oscar.riksdagskollen.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Adapter.DebateAdapter;
import oscar.riksdagskollen.Util.JSONModel.DebateSpeech;

public class DebateActivity extends AppCompatActivity {

    public static final String SPEECHES = "speeches";
    public static final String PROTOCOL_ID = "protocol_id";
    public static final String DEBATE_INITIATOR_ID = "debate_initiator_id";
    public static final String DEBATE_NAME = "debate_name";


    private RecyclerView.LayoutManager layoutManager;
    private String debateProtocolId;
    private String debateInitiatorId;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(RiksdagskollenApp.getInstance().getThemeManager().getCurrentTheme(true));
        setContentView(R.layout.activity_debate);

        ArrayList<DebateSpeech> speeches = getIntent().getExtras().getParcelableArrayList(SPEECHES);
        debateProtocolId = getIntent().getExtras().getString(PROTOCOL_ID);
        debateInitiatorId = getIntent().getExtras().getString(DEBATE_INITIATOR_ID);
        String debateName = getIntent().getExtras().getString(DEBATE_NAME);
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (debateName != null) {
            toolbar.setTitle(debateName);
        } else {
            toolbar.setTitle("Debatt");
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = findViewById(R.id.activity_debate_recyclerview);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DebateAdapter adapter = new DebateAdapter(this, speeches, debateProtocolId, debateInitiatorId);
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
