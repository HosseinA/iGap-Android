package net.iGap.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.AdapterListContact;
import net.iGap.helper.HelperNumerical;
import net.iGap.helper.HelperString;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.OnPhoneContact;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.module.Contacts;
import net.iGap.module.EndlessRecyclerViewScrollListener;
import net.iGap.module.structs.StructListOfContact;
import net.iGap.realm.RealmContacts;
import net.iGap.realm.RealmContactsFields;

import org.paygear.model.Contact;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import io.realm.RealmResults;

public class LocalContactFragment extends BaseFragment implements ToolbarListener, OnPhoneContact , Contacts.ContactCallback {

    public List<StructListOfContact> phoneContactsList = new ArrayList<>();
    private View rootView;
    private HelperToolbar mHelperToolbar;
    private AdapterListContact adapterListContact;
    private LinearLayout toolbarLayout;
    private ProgressBar loadingPb;
    private FastItemAdapter fastItemAdapter;
    private RecyclerView recyclerView;
    private boolean inSearchMode = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_local_contact, container, false);
        G.onPhoneContact = this;
        Contacts.localPhoneContactId = 0;
        Contacts.getContact = true;
        fastItemAdapter = new FastItemAdapter();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbarLayout = rootView.findViewById(R.id.ll_localContact_toolbar);
        loadingPb = rootView.findViewById(R.id.pb_localContact);
        toolbarInit();

        recyclerView = rootView.findViewById(R.id.rv_localContact);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapterListContact = new AdapterListContact(phoneContactsList, getContext());
        recyclerView.setAdapter(adapterListContact);

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (!inSearchMode || mHelperToolbar.getEditTextSearch().getText().toString().trim().equals("")){
                    if (Contacts.isEndLocal) {
                        return;
                    }
                    loadingPb.setVisibility(View.VISIBLE);
                    new Contacts.FetchContactForClient().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        });

        mHelperToolbar.getEditTextSearch().setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        mHelperToolbar.getEditTextSearch().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){

                    cancelSearchTimer();
                    String text = mHelperToolbar.getEditTextSearch().getText().toString().trim();
                    if (text.equals("")) return true;
                    new SearchAsync(text).execute();

                    return true;
                }
                return false;
            }
        });

    }

    private Timer mTimerSearch ;
    private TimerTask mTimerTaskSearch ;
    private byte mSearchCurrentTime = 0 ;

    private void startOrReStartSearchTimer(){

        mSearchCurrentTime = 0 ;
        cancelSearchTimer();

        if (mTimerSearch == null) {

            mTimerTaskSearch = new TimerTask() {
                @Override
                public void run() {
                    if (mSearchCurrentTime > 2){

                        String text = mHelperToolbar.getEditTextSearch().getText().toString().trim();
                        if (!text.equals(""))
                            G.handler.post(()-> new SearchAsync(text).execute());

                        cancelSearchTimer();

                    }else {
                        mSearchCurrentTime ++ ;
                    }
                }
            };

            mTimerSearch = new Timer();
            mTimerSearch.schedule(mTimerTaskSearch , 1000 , 5);
        }
    }

    private void cancelSearchTimer() {

        if (mTimerSearch != null) {
            mSearchCurrentTime = 0 ;
            mTimerSearch.cancel();
            mTimerTaskSearch = null ;
            mTimerSearch = null ;
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        new Contacts.FetchContactForClient().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void toolbarInit() {
        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLeftIcon(R.string.back_icon)
                .setRightIcons(R.string.share_icon)
                .setSearchBoxShown(true)
                .setDefaultTitle(getString(R.string.Invite_Friends))
                .setLogoShown(true);
        mHelperToolbar.setListener(this);

        toolbarLayout.addView(mHelperToolbar.getView());
    }

    @Override
    public void onSearchClickListener(View view) {
        inSearchMode = true;
        openKeyBoard();

    }

    @Override
    public void onBtnClearSearchClickListener(View view) {
        recyclerView.setAdapter(adapterListContact);
        cancelSearchTimer();
        inSearchMode = false;
    }

    @Override
    public void onRightIconClickListener(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey Join iGap : https://www.igap.net I'm waiting for you!");
        sendIntent.setType("text/plain");
        Intent openInChooser = Intent.createChooser(sendIntent, "Open in...");
        getActivity().startActivity(openInChooser);
    }

    @Override
    public void onLeftIconClickListener(View view) {
        cancelSearchTimer();
        getActivity().onBackPressed();
    }

    @Override
    public void onSearchTextChangeListener(View view, String text) {
        inSearchMode = true;
       /* List<StructListOfContact> searchContact = new ArrayList<>();

        fastItemAdapter.filter(text.toLowerCase());

        for (int i = 0; i < phoneContactsList.size(); i++) {
            if (phoneContactsList.get(i).displayName.toLowerCase().contains(text.toLowerCase()))
                searchContact.add(phoneContactsList.get(i));
        }*/
        if (text.trim().equals("")){
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(adapterListContact);
            cancelSearchTimer();
        }else {
            startOrReStartSearchTimer();
        }
    }

    @Override
    public void onPhoneContact(ArrayList<StructListOfContact> contacts, boolean isEnd) {
        new AddAsync(contacts, false).execute();
    }

    @Override
    public void onLocalContactRetriveForSearch(ArrayList<StructListOfContact> contacts) {
        new AddAsync(contacts, true).execute();
    }

    private class SearchAsync extends AsyncTask<Void , Void , Void>{

        private String text ;

        public SearchAsync(String text) {
            this.text = text;
        }

        @Override
        protected void onPreExecute() {
            recyclerView.setVisibility(View.GONE);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Contacts.getSearchContact(text.trim() , LocalContactFragment.this);

            return null;
        }
    }

    private class AddAsync extends AsyncTask<Void, Void, ArrayList<StructListOfContact>> {

        private ArrayList<StructListOfContact> contacts;
        private boolean isSearch;

        public AddAsync(ArrayList<StructListOfContact> contacts, boolean isSearch) {
            this.contacts = contacts;
            this.isSearch = isSearch ;
        }

        @Override
        protected ArrayList<StructListOfContact> doInBackground(Void... params) {
            for (int i = 0; i < contacts.size(); i++) {

                String s = contacts.get(i).getPhone();
                s = s.replaceAll("\\A0|\\+|\\-?", "");
                if (s.contains(" "))
                    s = s.replace(" ", "");
                if (!s.startsWith("98"))
                    s = "98" + s;
                if (isSearch){
                    if (HelperString.isInteger(s)){
                        contacts.get(i).setPhone(s);
                    }else {
                        contacts.remove(contacts.get(i));
                    }
                }else {
                    contacts.get(i).setPhone(s);
                }
            }
            try (Realm realm = Realm.getDefaultInstance()) {
                RealmResults<RealmContacts> mList = realm.where(RealmContacts.class).findAll().sort(RealmContactsFields.DISPLAY_NAME);

                ArrayList<StructListOfContact> slc = new ArrayList();

                for (int i = 0; i < contacts.size(); i++) {
                    boolean helpIndex = false;
                    for (int j = 0; j < mList.size(); j++) {
                        if (contacts.get(i).getPhone().equalsIgnoreCase(String.valueOf(mList.get(j).getPhone()))) {
                            helpIndex = true;
                            break;
                        }
                    }
                    if (!helpIndex) {
                        slc.add(contacts.get(i));
                    }
                }
                return slc;
            }

        }

        @Override
        protected void onPostExecute(ArrayList<StructListOfContact> slc) {

            if (isSearch){
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setAdapter(new AdapterListContact(slc, getContext()));
                loadingPb.setVisibility(View.GONE);

            }else {
                phoneContactsList.addAll(slc);
                adapterListContact.notifyDataSetChanged();
                loadingPb.setVisibility(View.GONE);
            }
            super.onPostExecute(slc);
        }
    }
}
