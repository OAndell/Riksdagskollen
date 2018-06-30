package oscar.riksdagskollen.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import oscar.riksdagskollen.Fragment.VoteListFragment;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.Util.JSONModel.DecisionDocument;
import oscar.riksdagskollen.Util.JSONModel.Vote;

/**
 * Created by gustavaaro on 2018-06-26.
 */

public class SearchedVoteAcitivity extends AppCompatActivity{


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searched_votes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ArrayList<Vote> votes = getIntent().getParcelableArrayListExtra("votes");
        DecisionDocument document = getIntent().getParcelableExtra("document");
        VoteListFragment fragment = VoteListFragment.newInstance(votes);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Voteringar för " + document.getRm() + ":" + document.getBeteckning());

    }


    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
