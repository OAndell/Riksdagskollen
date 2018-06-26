package oscar.riksdagskollen.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

import oscar.riksdagskollen.Fragments.VoteListFragment;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.Utilities.JSONModels.Vote;

/**
 * Created by gustavaaro on 2018-06-26.
 */

public class SearchedVoteAcitivity extends AppCompatActivity{


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searched_votes);
        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("Voteringssökning");

        ArrayList<Vote> votes = getIntent().getParcelableArrayListExtra("votes");
        VoteListFragment fragment = VoteListFragment.newInstance(votes);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
    }


    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }
}
