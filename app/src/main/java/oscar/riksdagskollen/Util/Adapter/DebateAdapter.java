package oscar.riksdagskollen.Util.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;

import oscar.riksdagskollen.Activity.RepresentativeDetailActivity;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Enum.CurrentParties;
import oscar.riksdagskollen.Util.JSONModel.DebateSpeech;
import oscar.riksdagskollen.Util.JSONModel.RepresentativeModels.Representative;
import oscar.riksdagskollen.Util.JSONModel.Speech;
import oscar.riksdagskollen.Util.RiksdagenCallback.RepresentativeCallback;
import oscar.riksdagskollen.Util.RiksdagenCallback.SpeechCallback;
import oscar.riksdagskollen.Util.View.CircularImageView;

public class DebateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<DebateSpeech> debateSpeeches;
    private String protocolId;
    private String debateInitiatior;
    private HashMap<String, Speech> speechDetails = new HashMap<>();

    private static final int TYPE_OUTGOING = 111;
    private static final int TYPE_INCOMING = 222;


    public DebateAdapter(Context context, ArrayList<DebateSpeech> speeches, String protocolId, String debateInitiator) {
        this.debateSpeeches = speeches;
        this.context = context;
        this.protocolId = protocolId;
        this.debateInitiatior = debateInitiator;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        if (viewType == TYPE_INCOMING) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.speech_item_incoming, parent, false);
            return new DebateViewHolderItem(itemView, context);

        } else {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.speech_item_outgoing, parent, false);
            return new DebateViewHolderItem(itemView, context);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final DebateViewHolderItem debateView = (DebateViewHolderItem) holder;
        debateView.speakerName.setText(debateSpeeches.get(position).getTalare());
        debateView.time.setText(debateSpeeches.get(position).getAnf_klockslag());
        int drawableResource = CurrentParties.getParty(debateSpeeches.get(position).getParti()).getDrawableLogo();
        debateView.partyLogo.setImageResource(drawableResource);
        debateView.setLoading(true);

        debateView.speakerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        Speech speechDetail = speechDetails.get(debateSpeeches.get(position).getAnf_nummer());
        if (speechDetail != null) {
            debateView.setLoading(false);
            debateView.speech.setText(Html.fromHtml(cleanupSpeech(speechDetail.getAnforandetext())));
        } else {
            RiksdagskollenApp.getInstance().getRiksdagenAPIManager()
                    .getSpeech(protocolId, debateSpeeches.get(position).getAnf_nummer(), new SpeechCallback() {
                        @Override
                        public void onSpeechFetched(Speech speech) {
                            speechDetails.put(debateSpeeches.get(position).getAnf_nummer(), speech);
                            debateView.setLoading(false);
                            debateView.speech.setText(Html.fromHtml(cleanupSpeech(speech.getAnforandetext())));
                        }

                        @Override
                        public void onFail(VolleyError error) {

                        }
                    });
        }


        RiksdagskollenApp.getInstance().getRiksdagenAPIManager()
                .getRepresentative(debateSpeeches.get(position).getIntressent_id(), new RepresentativeCallback() {
                    @Override
                    public void onPersonFetched(final Representative representative) {

                        Glide
                                .with(context.getApplicationContext())
                                .load(representative.getBild_url_80())
                                .into(debateView.portrait);
                        debateView.portrait.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent repDetailsIntent = new Intent(context, RepresentativeDetailActivity.class);
                                repDetailsIntent.putExtra("representative", representative);
                                context.startActivity(repDetailsIntent);
                            }
                        });

                        debateView.speakerName.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent repDetailsIntent = new Intent(context, RepresentativeDetailActivity.class);
                                repDetailsIntent.putExtra("representative", representative);
                                context.startActivity(repDetailsIntent);
                            }
                        });
                    }

                    @Override
                    public void onFail(VolleyError error) {

                    }
                });
    }

    @Override
    public int getItemViewType(int position) {
        //check what type our position is, based on the assumption that the order is headers > sortedList > footers
        if (debateSpeeches.get(position).getIntressent_id().equals(debateInitiatior)) {
            return TYPE_OUTGOING;
        } else if (debateInitiatior != null) {
            return TYPE_INCOMING;
        } else if (position % 2 == 0) {
            return TYPE_OUTGOING;
        }
        return TYPE_INCOMING;
    }

    private String cleanupSpeech(String speech) {
        speech = speech.replaceAll("STYLEREF Kantrubrik \\\\\\* MERGEFORMAT Svar på interpellationer", "");
        return speech;
    }


    @Override
    public int getItemCount() {
        return debateSpeeches.size();
    }


    static class DebateViewHolderItem extends RecyclerView.ViewHolder {
        TextView speakerName;
        TextView time;
        TextView speech;
        CircularImageView portrait;
        ImageView partyLogo;
        Context context;
        ProgressBar loadingView;
        LinearLayout speechInfoView;

        DebateViewHolderItem(View itemView, Context context) {
            super(itemView);
            this.context = context;
            speakerName = itemView.findViewById(R.id.debate_item_speaker);
            time = itemView.findViewById(R.id.debate_item_time);
            speech = itemView.findViewById(R.id.debate_item_text);
            loadingView = itemView.findViewById(R.id.debate_item_loading_view);
            speechInfoView = itemView.findViewById(R.id.debate_item_info);
            portrait = itemView.findViewById(R.id.debate_item_portrait);
            partyLogo = itemView.findViewById(R.id.debate_item_portrait_party_logo);
        }

        void setLoading(boolean loading) {
            if (loading) {
                speechInfoView.setVisibility(View.GONE);
                speech.setVisibility(View.GONE);
                loadingView.setVisibility(View.VISIBLE);
            } else {
                speechInfoView.setVisibility(View.VISIBLE);
                speech.setVisibility(View.VISIBLE);
                loadingView.setVisibility(View.GONE);
            }
        }


    }


}
