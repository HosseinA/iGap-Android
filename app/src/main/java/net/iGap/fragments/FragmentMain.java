package net.iGap.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.Theme;
import net.iGap.activities.ActivityMain;
import net.iGap.activities.ActivityRegisteration;
import net.iGap.adapter.items.chat.AbstractMessage;
import net.iGap.adapter.items.chat.ViewMaker;
import net.iGap.helper.GoToChatActivity;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperGetAction;
import net.iGap.helper.HelperImageBackColor;
import net.iGap.interfaces.OnAvatarGet;
import net.iGap.interfaces.OnChannelDeleteInRoomList;
import net.iGap.interfaces.OnChatDeleteInRoomList;
import net.iGap.interfaces.OnChatSendMessageResponse;
import net.iGap.interfaces.OnChatUpdateStatusResponse;
import net.iGap.interfaces.OnClientGetRoomListResponse;
import net.iGap.interfaces.OnClientGetRoomResponseRoomList;
import net.iGap.interfaces.OnComplete;
import net.iGap.interfaces.OnDateChanged;
import net.iGap.interfaces.OnGroupDeleteInRoomList;
import net.iGap.interfaces.OnNotifyTime;
import net.iGap.interfaces.OnRemoveFragment;
import net.iGap.interfaces.OnSetActionInRoom;
import net.iGap.interfaces.OnVersionCallBack;
import net.iGap.libs.MyRealmRecyclerViewAdapter;
import net.iGap.libs.Tuple;
import net.iGap.libs.floatingAddButton.ArcMenu;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.CircleImageView;
import net.iGap.module.EmojiTextViewE;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.MusicPlayer;
import net.iGap.module.MyDialog;
import net.iGap.module.PreCachingLayoutManager;
import net.iGap.module.enums.ChannelChatRole;
import net.iGap.module.enums.GroupChatRole;
import net.iGap.module.enums.RoomType;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoResponse;
import net.iGap.realm.RealmClientCondition;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRegisteredInfoFields;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.request.RequestChannelDelete;
import net.iGap.request.RequestChannelLeft;
import net.iGap.request.RequestChatDelete;
import net.iGap.request.RequestClientCondition;
import net.iGap.request.RequestClientGetRoomList;
import net.iGap.request.RequestClientMuteRoom;
import net.iGap.request.RequestClientPinRoom;
import net.iGap.request.RequestGroupDelete;
import net.iGap.request.RequestGroupLeft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.ObjectChangeSet;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollection;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmObjectChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

import static net.iGap.G.clientConditionGlobal;
import static net.iGap.G.context;
import static net.iGap.G.userId;
import static net.iGap.fragments.FragmentMain.MainType.all;
import static net.iGap.proto.ProtoGlobal.Room.Type.CHANNEL;
import static net.iGap.proto.ProtoGlobal.Room.Type.CHAT;
import static net.iGap.proto.ProtoGlobal.Room.Type.GROUP;
import static net.iGap.realm.RealmRoom.putChatToDatabase;


public class FragmentMain extends BaseFragment implements OnClientGetRoomListResponse, OnVersionCallBack, OnComplete, OnSetActionInRoom, OnRemoveFragment, OnChatUpdateStatusResponse, OnChatDeleteInRoomList, OnGroupDeleteInRoomList, OnChannelDeleteInRoomList, OnChatSendMessageResponse, OnClientGetRoomResponseRoomList, OnDateChanged {

    public static final String STR_MAIN_TYPE = "STR_MAIN_TYPE";
    public static HashMap<MainType, RoomAdapter> roomAdapterHashMap = new HashMap<>();
    public MainType mainType;
    boolean isSendRequestForLoading = false;
    boolean isThereAnyMoreItemToLoad = true;
    private ProgressBar progressBar;
    private int mOffset = 0;
    private View viewById;
    private RecyclerView mRecyclerView;
    private long tagId;
    private Realm realmFragmentMain;
    private RecyclerView.OnScrollListener onScrollListener;
    private View mView = null;
    private String switcher;
    private int channelSwitcher, allSwitcher, groupSwitcher, chatSwitcher = 0;
    private ProgressBar pbLoading;

    public static FragmentMain newInstance(MainType mainType) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(STR_MAIN_TYPE, mainType);
        FragmentMain fragment = new FragmentMain();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            switcher = String.valueOf(this.toString().charAt(this.toString().lastIndexOf(":") + 1));
            //   if (HelperCalander.isPersianUnicode) {

            if (switcher.equals("1") && channelSwitcher == 0) {
                channelSwitcher = 1;
                initRecycleView();
                initListener();
            } else if (switcher.equals("2") && groupSwitcher == 0) {
                groupSwitcher = 1;
                initRecycleView();
                initListener();
            } else if (switcher.equals("3") && chatSwitcher == 0) {
                chatSwitcher = 1;
                initRecycleView();
                initListener();
            } else if (switcher.equals("4") && allSwitcher == 0 && mView != null) {
                allSwitcher = 1;
                initRecycleView();
                initListener();
            } else if (switcher.equals("0") && allSwitcher == 0 && mView != null) {
                allSwitcher = 1;
                initRecycleView();
                initListener();
            }

        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isNeedResume = true;
        return inflater.inflate(R.layout.activity_main_rooms, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        G.onVersionCallBack = this;
        realmFragmentMain = Realm.getDefaultInstance();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("bagi" ,"FragmentMain:onViewCreated:start");

        //G.chatUpdateStatusUtil.setOnChatUpdateStatusResponse(this);
        this.mView = view;
        tagId = System.currentTimeMillis();

        mainType = (MainType) getArguments().getSerializable(STR_MAIN_TYPE);
        progressBar = view.findViewById(R.id.ac_progress_bar_waiting);
        viewById = view.findViewById(R.id.empty_icon);
        pbLoading = view.findViewById(R.id.pbLoading);
        pbLoading.setVisibility(View.VISIBLE);
        viewById.setVisibility(View.GONE);
        switcher = String.valueOf(this.toString().charAt(this.toString().lastIndexOf(":") + 1));
        if (switcher.equals("4") && allSwitcher == 0 && mView != null) {
            allSwitcher = 1;
            initRecycleView();
            initListener();
        } else if (switcher.equals("0") && allSwitcher == 0 && mView != null) {
            allSwitcher = 1;
            initRecycleView();
            initListener();
        }
        Log.d("bagi" ,"FragmentMain:onViewCreated:end");


    }

    private void initRecycleView() {

        if (mView != null) {
            mRecyclerView = (RecyclerView) mView.findViewById(R.id.cl_recycler_view_contact);
            // mRecyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0); // for avoid from show avatar and cloud view together
            mRecyclerView.setItemAnimator(null);
            mRecyclerView.setItemViewCacheSize(1000);
            mRecyclerView.setLayoutManager(new PreCachingLayoutManager(G.fragmentActivity, 3000));
        }

        RealmResults<RealmRoom> results = null;
        String[] fieldNames = {RealmRoomFields.IS_PINNED, RealmRoomFields.PIN_ID, RealmRoomFields.UPDATED_TIME};
        Sort[] sort = {Sort.DESCENDING, Sort.DESCENDING, Sort.DESCENDING};
        RealmQuery<RealmRoom> temp = getRealmFragmentMain().where(RealmRoom.class).equalTo(RealmRoomFields.KEEP_ROOM, false).equalTo(RealmRoomFields.IS_DELETED, false);

        switch (mainType) {
            case all:
                results = temp.sort(fieldNames, sort).findAllAsync();
                break;
            case chat:
                results = temp.equalTo(RealmRoomFields.TYPE, RoomType.CHAT.toString()).sort(fieldNames, sort).findAllAsync();
                break;
            case group:
                results = temp.equalTo(RealmRoomFields.TYPE, RoomType.GROUP.toString()).sort(fieldNames, sort).findAllAsync();
                break;
            case channel:
                results = temp.equalTo(RealmRoomFields.TYPE, RoomType.CHANNEL.toString()).sort(fieldNames, sort).findAllAsync();
                break;
        }


        final RoomAdapter roomsAdapter = new RoomAdapter(results, this, viewById, pbLoading);

        if (!G.multiTab) {
            onScrollListener = new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (isThereAnyMoreItemToLoad) {
                        if (!isSendRequestForLoading && mOffset > 0) {
                            int lastVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                            if (lastVisiblePosition + 10 >= mOffset) {
                                isSendRequestForLoading = true;
                                new RequestClientGetRoomList().clientGetRoomList(mOffset, Config.LIMIT_LOAD_ROOM, tagId + "");
                                G.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        }
                    } else {
                        mRecyclerView.removeOnScrollListener(onScrollListener);
                    }
                }
            };
            mRecyclerView.addOnScrollListener(onScrollListener);
        }

        mRecyclerView.setAdapter(roomsAdapter);

        if (roomAdapterHashMap == null) {
            roomAdapterHashMap = new HashMap<>();
        }
        roomAdapterHashMap.put(mainType, roomsAdapter);

        //fastAdapter
        //final RoomsAdapter roomsAdapter = new RoomsAdapter(getRealmFragmentMain());
        //for (RealmRoom realmRoom : results) {
        //    roomsAdapter.add(new RoomItem(this, mainType).setInfo(realmRoom).withIdentifier(realmRoom.getId()));
        //}
        //
        //// put adapters in hashMap
        //adapterHashMap.put(mainType, roomsAdapter);
        //
        //mRecyclerView.setAdapter(roomsAdapter);

        if (mainType == all) {
            getChatLists();
        }

        if (mView != null) {
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    try {
                        ArcMenu arcMenu = ((ActivityMain) G.fragmentActivity).arcMenu;
                        if (arcMenu.isMenuOpened()) {
                            arcMenu.toggleMenu();
                        }

                        if (dy > 0) {
                            // Scroll Down
                            if (arcMenu.fabMenu.isShown()) {
                                arcMenu.fabMenu.hide();
                            }
                        } else if (dy < 0) {
                            // Scroll Up
                            if (!arcMenu.fabMenu.isShown()) {
                                arcMenu.fabMenu.show();
                            }
                        }
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        G.onNotifyTime = new OnNotifyTime() {
            @Override
            public void notifyTime() {
                if (mRecyclerView != null) {
                    if (mRecyclerView.getAdapter() != null) {
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                    }
                }
            }
        };

    }

    //***************************************************************************************************************************

    private void initListener() {
        switch (mainType) {

            case all:

                ((ActivityMain) G.fragmentActivity).mainActionApp = new ActivityMain.MainInterface() {
                    @Override
                    public void onAction(ActivityMain.MainAction action) {
                        doAction(action);
                    }
                };

                break;
            case chat:
                ((ActivityMain) G.fragmentActivity).mainActionChat = new ActivityMain.MainInterface() {
                    @Override
                    public void onAction(ActivityMain.MainAction action) {
                        doAction(action);
                    }
                };
                break;
            case group:
                ((ActivityMain) G.fragmentActivity).mainActionGroup = new ActivityMain.MainInterface() {
                    @Override
                    public void onAction(ActivityMain.MainAction action) {
                        doAction(action);
                    }
                };
                break;
            case channel:
                ((ActivityMain) G.fragmentActivity).mainActionChannel = new ActivityMain.MainInterface() {
                    @Override
                    public void onAction(ActivityMain.MainAction action) {
                        doAction(action);
                    }
                };
                break;
        }
    }

    private void doAction(ActivityMain.MainAction action) {

        switch (action) {

            case downScrool:

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        int firstVisibleItem = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                        if (firstVisibleItem < 5) {
                            mRecyclerView.scrollToPosition(0);
                        }
                    }
                });

                break;
            case clinetCondition:
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                    }
                });

                break;
        }
    }


    private boolean heartBeatTimeOut() {

        long difference;

        long currentTime = System.currentTimeMillis();
        difference = (currentTime - G.latestHearBeatTime);

        if (difference >= Config.HEART_BEAT_CHECKING_TIME_OUT) {
            return true;
        }

        return false;
    }

    //***************************************************************************************************************************

    private void sendClientCondition() {
        if (clientConditionGlobal != null) {
            new RequestClientCondition().clientCondition(clientConditionGlobal);
        } else {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    sendClientCondition();
                }
            }, 1000);
        }
    }

    private void getChatLists() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (G.isSecure && G.userLogin) {

                    mOffset = 0;
                    new RequestClientGetRoomList().clientGetRoomList(mOffset, Config.LIMIT_LOAD_ROOM, tagId + "");
                    isSendRequestForLoading = true;
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    getChatLists();
                }
            }
        }, 1000);
    }

    private void onSelectRoomMenu(String message, RealmRoom item) {
        if (checkValidationForRealm(item)) {
            switch (message) {
                case "pinToTop":

                    pinToTop(item.getId(), item.isPinned());

                    break;
                case "txtMuteNotification":
                    muteNotification(item.getId(), item.getMute());
                    break;
                case "txtClearHistory":
                    clearHistory(item.getId());
                    break;
                case "txtDeleteChat":
                    if (item.getType() == CHAT) {
                        new RequestChatDelete().chatDelete(item.getId());
                    } else if (item.getType() == GROUP) {
                        if (item.getGroupRoom().getRole() == GroupChatRole.OWNER) {
                            new RequestGroupDelete().groupDelete(item.getId());
                        } else {
                            new RequestGroupLeft().groupLeft(item.getId());
                        }
                    } else if (item.getType() == CHANNEL) {

                        if (MusicPlayer.mainLayout != null) {
                            if (item.getId() == MusicPlayer.roomId) {
                                MusicPlayer.closeLayoutMediaPlayer();
                            }
                        }


                        if (item.getChannelRoom().getRole() == ChannelChatRole.OWNER) {
                            new RequestChannelDelete().channelDelete(item.getId());
                        } else {
                            new RequestChannelLeft().channelLeft(item.getId());
                        }
                    }
                    break;
            }
        }
    }

    private void muteNotification(final long roomId, final boolean mute) {
        //+Realm realm = Realm.getDefaultInstance();
        new RequestClientMuteRoom().muteRoom(roomId, !mute);

        //fastAdapter
        //G.handler.post(new Runnable() {
        //    @Override
        //    public void run() {
        //        adapterHashMap.get(all).updateItem(roomId);
        //        getAdapterMain(roomId).updateItem(roomId);
        //    }
        //});
        //realm.close();
    }

    private void clearHistory(final long roomId) {
        RealmRoomMessage.clearHistoryMessage(roomId);
        //fastAdapter
        //G.handler.post(new Runnable() {
        //    @Override
        //    public void run() {
        //        adapterHashMap.get(all).updateItem(roomId);
        //        getAdapterMain(roomId).updateItem(roomId);
        //    }
        //});
    }

    private void pinToTop(final long roomId, final boolean isPinned) {
        //+Realm realm = Realm.getDefaultInstance();

        new RequestClientPinRoom().pinRoom(roomId, !isPinned);
        if (!isPinned) {
            goToTop();
        }

        //fastAdapter
        //if (!isPinned) {
        //    G.handler.post(new Runnable() {
        //        @Override
        //        public void run() {
        //            adapterHashMap.get(all).goToTop(roomId, true);
        //            getAdapterMain(roomId).goToTop(roomId, true);
        //        }
        //    });
        //} else {
        //    updateUnPin(roomId, all);
        //
        //    ProtoGlobal.Room.Type type = getRoomType(roomId);
        //    if (type == CHAT) {
        //        updateUnPin(roomId, chat);
        //    } else if (type == GROUP) {
        //        updateUnPin(roomId, group);
        //    } else if (type == CHANNEL) {
        //        updateUnPin(roomId, channel);
        //    }
        //}
        //realm.close();
    }

    private void goToTop() {
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition() <= 1) {
                    mRecyclerView.smoothScrollToPosition(0);
                }
            }
        }, 50);
    }

    //fastAdapter
    //private void updateUnPin(final long roomId, final MainType type) {
    //    G.handler.post(new Runnable() {
    //        @Override
    //        public void run() {
    //            RealmResults<RealmRoom> results = null;
    //            String[] fieldNames = {RealmRoomFields.IS_PINNED, RealmRoomFields.UPDATED_TIME};
    //            Sort[] sort = {Sort.DESCENDING, Sort.DESCENDING};
    //            switch (type) {
    //                case all:
    //                    results = getRealmFragmentMain().where(RealmRoom.class).equalTo(RealmRoomFields.KEEP_ROOM, false).equalTo(RealmRoomFields.IS_DELETED, false).findAll().sort(fieldNames, sort);
    //                    break;
    //                case chat:
    //                    results = getRealmFragmentMain().where(RealmRoom.class).equalTo(RealmRoomFields.KEEP_ROOM, false).equalTo(RealmRoomFields.IS_DELETED, false).equalTo(RealmRoomFields.TYPE, RoomType.CHAT.toString()).findAll().sort(fieldNames, sort);
    //                    break;
    //                case group:
    //                    results = getRealmFragmentMain().where(RealmRoom.class).equalTo(RealmRoomFields.KEEP_ROOM, false).equalTo(RealmRoomFields.IS_DELETED, false).equalTo(RealmRoomFields.TYPE, RoomType.GROUP.toString()).findAll().sort(fieldNames, sort);
    //                    break;
    //                case channel:
    //                    results = getRealmFragmentMain().where(RealmRoom.class).equalTo(RealmRoomFields.KEEP_ROOM, false).equalTo(RealmRoomFields.IS_DELETED, false).equalTo(RealmRoomFields.TYPE, RoomType.CHANNEL.toString()).findAll().sort(fieldNames, sort);
    //                    break;
    //            }
    //
    //            int position = 0;
    //            for (RealmRoom room : results) {
    //                if (room.getId() == roomId) {
    //                    break;
    //                }
    //                position++;
    //            }
    //            adapterHashMap.get(type).goToPosition(roomId, position);
    //        }
    //    });
    //}

    private boolean checkValidationForRealm(RealmRoom realmRoom) {
        if (realmRoom != null && realmRoom.isManaged() && realmRoom.isValid() && realmRoom.isLoaded()) {
            return true;
        }
        return false;
    }

    //fastAdapter
    //private ProtoGlobal.Room.Type getRoomType(long roomId) {
    //    RealmRoom realmRoom = getRealmFragmentMain().where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
    //    if (realmRoom != null) {
    //        return realmRoom.getType();
    //    }
    //    return null;
    //}
    //
    //private RoomsAdapter getAdapterMain(long roomId) {
    //    ProtoGlobal.Room.Type roomType = getRoomType(roomId);
    //    if (roomType != null) {
    //        if (roomType == CHAT) {
    //            return adapterHashMap.get(chat);
    //        } else if (roomType == GROUP) {
    //            return adapterHashMap.get(group);
    //        } else if (roomType == CHANNEL) {
    //            return adapterHashMap.get(channel);
    //        }
    //    }
    //    return adapterHashMap.get(all);
    //}

    /**
     * ************************************ Callbacks ************************************
     */
    @Override
    public void onChange() {
        for (Map.Entry<MainType, RoomAdapter> entry : roomAdapterHashMap.entrySet()) {
            RoomAdapter requestWrapper = entry.getValue();
            requestWrapper.notifyDataSetChanged();
        }
    }

    @Override
    public void onSetAction(final long roomId, final long userId, final ProtoGlobal.ClientAction clientAction) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                RealmRoom.setAction(roomId, userId, HelperGetAction.getAction(roomId, RealmRoom.detectType(roomId), clientAction));
            }
        });
    }

    @Override
    public void onRemoveFragment(Fragment fragment) {
        removeFromBaseFragment(fragment);
    }

    @Override
    public void onChatUpdateStatus(final long roomId, long messageId, ProtoGlobal.RoomMessageStatus status, long statusVersion) {
        //fastAdapter
        //G.handler.postDelayed(new Runnable() {
        //    @Override
        //    public void run() {
        //        adapterHashMap.get(all).updateItem(roomId);
        //        getAdapterMain(roomId).updateItem(roomId);
        //    }
        //}, 200);
    }

    @Override
    public void onChatDelete(final long roomId) {
        //fastAdapter
        //adapterHashMap.get(all).removeChat(roomId);
        //adapterHashMap.get(chat).removeChat(roomId);
    }

    @Override
    public void onGroupDelete(long roomId) {
        //fastAdapter
        //adapterHashMap.get(all).removeChat(roomId);
        //adapterHashMap.get(group).removeChat(roomId);
    }

    @Override
    public void onGroupDeleteError(int majorCode, int minorCode) {

    }

    @Override
    public void onGroupDeleteTimeOut() {

    }

    @Override
    public void onChannelDelete(long roomId) {
        //fastAdapter
        //adapterHashMap.get(all).removeChat(roomId);
        //adapterHashMap.get(channel).removeChat(roomId);
    }

    @Override
    public void onChannelDeleteError(int majorCode, int minorCode) {

    }

    @Override
    public void onChannelDeleteTimeOut() {

    }

    @Override
    public void onMessageUpdate(final long roomId, long messageId, ProtoGlobal.RoomMessageStatus status, String identity, ProtoGlobal.RoomMessage roomMessage) {
        //fastAdapter
        //G.handler.postDelayed(new Runnable() {
        //    @Override
        //    public void run() {
        //        adapterHashMap.get(all).goToTop(roomId, false);
        //        getAdapterMain(roomId).goToTop(roomId, false);
        //    }
        //}, 200);
    }

    @Override
    public void onMessageReceive(final long roomId, String message, ProtoGlobal.RoomMessageType messageType, ProtoGlobal.RoomMessage roomMessage, ProtoGlobal.Room.Type roomType) {
        //fastAdapter
        //G.handler.post(new Runnable() {
        //    @Override
        //    public void run() {
        //        adapterHashMap.get(all).goToTop(roomId, false);
        //        getAdapterMain(roomId).goToTop(roomId, false);
        //    }
        //});
    }

    @Override
    public void onMessageFailed(long roomId, RealmRoomMessage roomMessage) {

    }

    @Override
    public void onClientGetRoomResponse(final long roomId) {
        //fastAdapter
        //G.handler.post(new Runnable() {
        //    @Override
        //    public void run() {
        //        RealmRoom realmRoom = getRealmFragmentMain().where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        //        if (realmRoom != null && getAdapterMain(roomId).getPosition(realmRoom.getId()) == -1) {
        //            adapterHashMap.get(all).add(0, new RoomItem(mComplete, all).setInfo(realmRoom).withIdentifier(realmRoom.getId()));
        //            MainType type = all;
        //            if (realmRoom.getType() == CHAT) {
        //                type = chat;
        //            } else if (realmRoom.getType() == GROUP) {
        //                type = group;
        //            } else if (realmRoom.getType() == CHANNEL) {
        //                type = channel;
        //            }
        //            getAdapterMain(roomId).add(0, new RoomItem(mComplete, type).setInfo(realmRoom).withIdentifier(realmRoom.getId()));
        //        }
        //    }
        //});
    }

    @Override
    public void onClientGetRoomList(List<ProtoGlobal.Room> roomList, ProtoResponse.Response response, String identity) {

        boolean fromLogin = false;
        // requst from login
        if (identity.equals("0")) {
            mOffset = 0;
            fromLogin = true;
        } else if (Long.parseLong(identity) < tagId) {
            return;
        }

        boolean deleteBefore = false;
        if (mOffset == 0) {
            deleteBefore = true;
        }

        boolean cleanAfter = false;

        if (roomList.size() == 0) {
            isThereAnyMoreItemToLoad = false;
            cleanAfter = true;
        } else {
            isThereAnyMoreItemToLoad = true;
        }

        putChatToDatabase(roomList, deleteBefore, cleanAfter);

        //fastAdapter
        //G.handler.postDelayed(new Runnable() {
        //    @Override
        //    public void run() {
        //        initRecycleView(null);
        //    }
        //}, 200);

        /**
         * to first enter to app , client first compute clientCondition then
         * getRoomList and finally send condition that before get clientCondition;
         * in else changeState compute new client condition with latest messaging changeState
         */
        if (!G.userLogin) {
            G.userLogin = true;
            sendClientCondition();
        } else if (fromLogin || mOffset == 0) {

            new Thread(new Runnable() {
                @Override
                public void run() {

                    if (G.clientConditionGlobal != null) {
                        new RequestClientCondition().clientCondition(G.clientConditionGlobal);
                    } else {
                        new RequestClientCondition().clientCondition(RealmClientCondition.computeClientCondition(null));
                    }


                }
            }).start();
        }

        mOffset += roomList.size();

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        });

        isSendRequestForLoading = false;

        if (isThereAnyMoreItemToLoad && G.multiTab) {
            isSendRequestForLoading = true;
            new RequestClientGetRoomList().clientGetRoomList(mOffset, Config.LIMIT_LOAD_ROOM, tagId + "");

            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.VISIBLE);
                }
            });
        }
        //else {
        //    mOffset = 0;
        //}


    }

    @Override
    public void onClientGetRoomListError(int majorCode, int minorCode) {
        isSendRequestForLoading = false;

        if (majorCode == 9) {
            if (G.currentActivity != null) {
                G.currentActivity.finish();
            }
            Intent intent = new Intent(context, ActivityRegisteration.class);
            intent.putExtra(ActivityRegisteration.showProfile, true);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

    }

    @Override
    public void onClientGetRoomListTimeout() {

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                getChatLists();
                isSendRequestForLoading = false;
            }
        });
    }


    @Override
    public void onChatDeleteError(int majorCode, int minorCode) {

    }

    @Override
    public void complete(boolean result, String messageOne, String MessageTow) {
        if (messageOne.equals("closeMenuButton")) {
            ((ActivityMain) G.fragmentActivity).arcMenu.toggleMenu();
        }
    }

    private Realm getRealmFragmentMain() {
        if (realmFragmentMain == null || realmFragmentMain.isClosed()) {
            realmFragmentMain = Realm.getDefaultInstance();
        }
        return realmFragmentMain;
    }

    //**************************************************************************************************************************************

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState); //No call for super(). Bug on API Level > 11.
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (realmFragmentMain != null && !realmFragmentMain.isClosed()) {
            realmFragmentMain.close();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("bagi" ,"FragmentMain:onResume:start");

        G.onSetActionInRoom = this;
        G.onDateChanged = this;
        if (G.isDepricatedApp)
            isDeprecated();

        //G.onSelectMenu = this;
        //G.onRemoveFragment = this;
        //G.onChatDeleteInRoomList = this;
        //G.onGroupDeleteInRoomList = this;
        //G.onChannelDeleteInRoomList = this;
        G.onClientGetRoomListResponse = this;
        //onClientGetRoomResponseRoomList = this;
        //G.chatUpdateStatusUtil.setOnChatUpdateStatusResponseFragmentMain(this);
        //G.chatSendMessageUtil.setOnChatSendMessageResponseFragmentMainRoomList(this);

        if (progressBar != null) {
            AppUtils.setProgresColler(progressBar);
        }

        boolean canUpdate = false;

        if (mainType != null) {
            switch (mainType) {

                case all:
                    if (G.isUpdateNotificaionColorMain) {
                        canUpdate = true;
                        G.isUpdateNotificaionColorMain = false;
                    }
                    break;
                case chat:
                    if (G.isUpdateNotificaionColorChat) {
                        canUpdate = true;
                        G.isUpdateNotificaionColorChat = false;
                    }
                    break;
                case group:
                    if (G.isUpdateNotificaionColorGroup) {
                        canUpdate = true;
                        G.isUpdateNotificaionColorGroup = false;
                    }
                    break;
                case channel:
                    if (G.isUpdateNotificaionColorChannel) {
                        canUpdate = true;
                        G.isUpdateNotificaionColorChannel = false;
                    }
                    break;
            }
        }

        if (canUpdate) {

            if (mRecyclerView != null) {
                if (mRecyclerView.getAdapter() != null) {
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                }
            }
        }
//        BotInit.checkDrIgap();

        Log.d("bagi" ,"FragmentMain:onResume:end");
    }

    @Override
    public void isDeprecated() {
        try {
            if (G.fragmentActivity.hasWindowFocus()) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        new MaterialDialog.Builder(getActivity())
                                .cancelable(false)
                                .title(R.string.new_version_alert).titleGravity(GravityEnum.CENTER)
                                .titleColor(Color.parseColor("#f44336"))
                                .content(R.string.deprecated)
                                .contentGravity(GravityEnum.CENTER)
                                .positiveText(R.string.startUpdate).itemsGravity(GravityEnum.START).onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                // HelperUrl.openBrowser("http://d.igap.net/update");
                                String url = "http://d.igap.net/update";
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                startActivity(i);
                            }
                        })
                                .show();
                    }
                });
            }
        } catch (Exception e) {
        }

    }

    @Override
    public void isUpdateAvailable() {
        try {
            if (G.fragmentActivity.hasWindowFocus()) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        new MaterialDialog.Builder(G.fragmentActivity)
                                .title(R.string.igap_update).titleColor(Color.parseColor("#1DE9B6"))
                                .titleGravity(GravityEnum.CENTER)
                                .buttonsGravity(GravityEnum.CENTER)
                                .content(R.string.new_version_avilable).contentGravity(GravityEnum.CENTER)
                                .negativeText(R.string.ignore).negativeColor(Color.parseColor("#798e89")).onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                dialog.dismiss();
                            }
                        }).positiveText(R.string.startUpdate).onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                // HelperUrl.openBrowser("http://d.igap.net/update");
                                String url = "http://d.igap.net/update";
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                startActivity(i);
                                dialog.dismiss();
                            }
                        })
                                .show();
                    }
                });

            }
        } catch (Exception e) {
        }
    }

    public enum MainType {
        all, chat, group, channel
    }

    /**
     * **********************************************************************************
     * ********************************** RealmAdapter **********************************
     * **********************************************************************************
     */

    public class RoomAdapter extends MyRealmRecyclerViewAdapter<RealmRoom, RoomAdapter.ViewHolder> {

        public OnComplete mComplete;
        public String action;
        private View emptyView;
        private View loadingView;

        public RoomAdapter(@Nullable OrderedRealmCollection<RealmRoom> data, OnComplete complete, View emptyView, View loadingView) {
            super(data, true);
            this.mComplete = complete;
            this.emptyView = emptyView;
            this.loadingView = loadingView;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // View v = inflater.inflate(R.layout.chat_sub_layout, parent, false);
            return new ViewHolder(ViewMaker.getViewItemRoom());
        }

        @Override
        protected OrderedRealmCollectionChangeListener createListener() {
            return new OrderedRealmCollectionChangeListener() {
                @Override
                public void onChange(Object collection, OrderedCollectionChangeSet changeSet) {
                    if (getData() != null && getData().size() > 0) {
                        emptyView.setVisibility(View.GONE);
                    } else {
                        emptyView.setVisibility(View.VISIBLE);
                    }
                    if (changeSet.getState() == OrderedCollectionChangeSet.State.INITIAL) {
                        loadingView.setVisibility(View.GONE);
                        notifyDataSetChanged();
                        return;
                    }
                    // For deletions, the adapter has to be notified in reverse order.
                    OrderedCollectionChangeSet.Range[] deletions = changeSet.getDeletionRanges();
                    for (int i = deletions.length - 1; i >= 0; i--) {
                        OrderedCollectionChangeSet.Range range = deletions[i];
                        notifyItemRangeRemoved(range.startIndex, range.length);
                    }

                    OrderedCollectionChangeSet.Range[] insertions = changeSet.getInsertionRanges();
                    for (OrderedCollectionChangeSet.Range range : insertions) {
                        notifyItemRangeInserted(range.startIndex, range.length);
                        goToTop();
                    }

                    if (!updateOnModification) {
                        return;
                    }

                    OrderedCollectionChangeSet.Range[] modifications = changeSet.getChangeRanges();
                    for (OrderedCollectionChangeSet.Range range : modifications) {
                        notifyItemRangeChanged(range.startIndex, range.length);
                    }
                }
            };
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int i) {


            final RealmRoom mInfo = holder.mInfo = getItem(i);
            if (mInfo == null) {
                return;
            }

            final boolean isMyCloud;

            if (mInfo.getChatRoom() != null && mInfo.getChatRoom().getPeerId() > 0 && mInfo.getChatRoom().getPeerId() == userId) {
                isMyCloud = true;
            } else {
                isMyCloud = false;
            }

            if (mInfo.isValid()) {

                setLastMessage(mInfo, holder, isMyCloud);

                if (isMyCloud) {

                    if (holder.txtCloud == null) {

                        MaterialDesignTextView cs_txt_contact_initials = new MaterialDesignTextView(context);
                        cs_txt_contact_initials.setId(R.id.cs_txt_contact_initials);
                        cs_txt_contact_initials.setGravity(Gravity.CENTER);
                        cs_txt_contact_initials.setText(context.getResources().getString(R.string.md_cloud));
                        if (G.isDarkTheme) {
                            cs_txt_contact_initials.setTextColor(Color.parseColor(G.textSubTheme));
                        } else {
                            cs_txt_contact_initials.setTextColor(Color.parseColor("#ad333333"));
                        }

                        ViewMaker.setTextSize(cs_txt_contact_initials, R.dimen.dp32);
                        LinearLayout.LayoutParams layout_936 = new LinearLayout.LayoutParams(ViewMaker.i_Dp(R.dimen.dp52), ViewMaker.i_Dp(R.dimen.dp52));
                        layout_936.gravity = Gravity.CENTER;
                        layout_936.setMargins(ViewMaker.i_Dp(R.dimen.dp6), ViewMaker.i_Dp(R.dimen.dp6), ViewMaker.i_Dp(R.dimen.dp6), ViewMaker.i_Dp(R.dimen.dp6));
                        cs_txt_contact_initials.setVisibility(View.GONE);
                        cs_txt_contact_initials.setLayoutParams(layout_936);

                        holder.txtCloud = cs_txt_contact_initials;

                        holder.rootChat.addView(cs_txt_contact_initials, 0);
                    }

                    holder.txtCloud.setVisibility(View.VISIBLE);
                    holder.image.setVisibility(View.GONE);
                } else {

                    if (holder.txtCloud != null) {
                        holder.txtCloud.setVisibility(View.GONE);
                    }

                    if (holder.image.getVisibility() == View.GONE) {
                        holder.image.setVisibility(View.VISIBLE);
                    }

                    setAvatar(mInfo, holder);
                }

                setChatIcon(mInfo, holder.txtChatIcon);

                holder.name.setText(mInfo.getTitle());

                if ((mInfo.getType() == CHAT) && mInfo.getChatRoom().isVerified()) {
                    holder.imgVerifyRoom.setVisibility(View.VISIBLE);
                } else if ((mInfo.getType() == CHANNEL) && mInfo.getChannelRoom().isVerified()) {
                    holder.imgVerifyRoom.setVisibility(View.VISIBLE);
                } else {
                    holder.imgVerifyRoom.setVisibility(View.INVISIBLE);
                }

                if (mInfo.getLastMessage() != null && mInfo.getLastMessage().getUpdateOrCreateTime() != 0) {
                    holder.txtTime.setText(HelperCalander.getTimeForMainRoom(mInfo.getLastMessage().getUpdateOrCreateTime()));
                }

                /**
                 * ********************* unread *********************
                 */

                if (mInfo.isPinned()) {
                    holder.rootChat.setBackgroundColor(Color.parseColor(G.backgroundTheme_2));

                    //if (mInfo.getChatRoom() != null && RealmRoom.isBot(mInfo.getChatRoom().getPeerId())) {

                    if (mInfo.isFromPromote()) {
                        //              holder.rootChat.setBackgroundColor(G.context.getResources().getColor(R.color.green_20));
                        holder.txtPinIcon.setVisibility(View.GONE);
                    } else {
                        holder.txtPinIcon.setVisibility(View.VISIBLE);
                    }

                } else {
                    holder.rootChat.setBackgroundColor(Color.parseColor(G.backgroundTheme));
                    holder.txtPinIcon.setVisibility(View.GONE);
                }

                if (mInfo.getUnreadCount() < 1) {

                    holder.txtUnread.setVisibility(View.GONE);

                } else {
                    holder.txtUnread.setVisibility(View.VISIBLE);
                    holder.txtPinIcon.setVisibility(View.GONE);
                    holder.txtUnread.setText(mInfo.getUnreadCount() + "");

                    if (HelperCalander.isPersianUnicode) {
                        holder.txtUnread.setBackgroundResource(R.drawable.rect_oval_red);
                    } else {
                        holder.txtUnread.setBackgroundResource(R.drawable.rect_oval_red_left);
                    }

                    if (mInfo.getMute()) {
                        AndroidUtils.setBackgroundShapeColor(holder.txtUnread, Color.parseColor("#c6c1c1"));
                    } else {
                        if (G.isDarkTheme) {
                            AndroidUtils.setBackgroundShapeColor(holder.txtUnread, Color.parseColor(Theme.default_notificationColor));
                        } else {
                            AndroidUtils.setBackgroundShapeColor(holder.txtUnread, Color.parseColor(G.notificationColor));
                        }
                    }
                }

                if (mInfo.getMute()) {
                    holder.mute.setVisibility(View.VISIBLE);
                } else {
                    holder.mute.setVisibility(View.GONE);
                }
            }

            /**
             * for change english number to persian number
             */
            if (HelperCalander.isPersianUnicode) {
                holder.txtLastMessage.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.txtLastMessage.getText().toString()));
                holder.txtUnread.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.txtUnread.getText().toString()));
            }
        }


   /*     private boolean isBot(long userId) {
            RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(getRealmFragmentMain(), userId);
            if (realmRegisteredInfo != null) {
                if (realmRegisteredInfo.isBot()) {
                    return true;
                } else
                    return false;
            } else
                return false;
        }*/


        private String subStringInternal(String text) {
            if (text == null || text.length() == 0) {
                return "";
            }

            ArrayList<Tuple<Integer, Integer>> boldPlaces = AbstractMessage.getBoldPlaces(text);
            text = AbstractMessage.removeBoldMark(text, boldPlaces);

            int subLength = 150;
            if (text.length() > subLength) {
                return text.substring(0, subLength);
            } else {
                return text;
            }
        }

        @Override
        public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
            super.onViewDetachedFromWindow(holder);
            if (holder.realmRegisteredInfo == null || !holder.realmRegisteredInfo.isValid())
                return;
            holder.realmRegisteredInfo.removeAllChangeListeners();
        }

        //*******************************************************************************************
        private void setLastMessage(RealmRoom mInfo, ViewHolder holder, boolean isMyCloud) {

            holder.txtTic.setVisibility(View.GONE);
            holder.txtLastMessageFileText.setVisibility(View.GONE);
            holder.txtLastMessage.setText("");

            if (mInfo.getActionState() != null && ((mInfo.getType() == GROUP || mInfo.getType() == CHANNEL) || ((isMyCloud || (mInfo.getActionStateUserId() != G.userId))))) {

                holder.lastMessageSender.setVisibility(View.GONE);
                holder.txtLastMessage.setText(mInfo.getActionState());
                holder.txtLastMessage.setTextColor(ContextCompat.getColor(context, R.color.room_message_blue));
                holder.txtLastMessage.setEllipsize(TextUtils.TruncateAt.MIDDLE);
            } else if (mInfo.getDraft() != null && !TextUtils.isEmpty(mInfo.getDraft().getMessage())) {
                ArrayList<Tuple<Integer, Integer>> boldPlaces = AbstractMessage.getBoldPlaces(mInfo.getDraft().getMessage());
                holder.txtLastMessage.setText(subStringInternal(AbstractMessage.removeBoldMark(mInfo.getDraft().getMessage(), boldPlaces)));
                holder.txtLastMessage.setTextColor(Color.parseColor(G.textSubTheme));

                holder.lastMessageSender.setVisibility(View.VISIBLE);
                holder.lastMessageSender.setText(R.string.txt_draft);
                holder.lastMessageSender.setTextColor(Color.parseColor(G.roomSenderTextColor));

                holder.lastMessageSender.setTypeface(G.typeface_IRANSansMobile);
            } else {

                if (mInfo.getLastMessage() != null) {
                    String lastMessage = AppUtils.rightLastMessage(RealmRoomMessage.getFinalMessage(mInfo.getLastMessage()));

                    if (lastMessage == null) {
                        lastMessage = mInfo.getLastMessage().getMessage();
                    }

                    if (lastMessage == null || lastMessage.isEmpty()) {

                        holder.lastMessageSender.setVisibility(View.GONE);
                    } else {
                        if (mInfo.getLastMessage().isAuthorMe()) {

                            holder.txtTic.setVisibility(View.VISIBLE);

                            ProtoGlobal.RoomMessageStatus status = ProtoGlobal.RoomMessageStatus.UNRECOGNIZED;
                            if (mInfo.getLastMessage().getStatus() != null) {
                                try {
                                    status = ProtoGlobal.RoomMessageStatus.valueOf(mInfo.getLastMessage().getStatus());
                                } catch (RuntimeException e) {
                                    e.printStackTrace();
                                }
                            }
                            AppUtils.rightMessageStatus(holder.txtTic, status, mInfo.getLastMessage().isAuthorMe());
                        }

                        if (mInfo.getType() == GROUP) {
                            /**
                             * here i get latest message from chat history with chatId and
                             * get DisplayName with that . when login app client get latest
                             * message for each group from server , if latest message that
                             * send server and latest message that exist in client for that
                             * room be different latest message sender showing will be wrong
                             */

                            String lastMessageSender = "";
                            if (mInfo.getLastMessage().isAuthorMe()) {
                                lastMessageSender = holder.itemView.getResources().getString(R.string.txt_you);
                            } else {
                                holder.realmRegisteredInfo = getRealmFragmentMain().where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, mInfo.getLastMessage().getUserId()).findFirstAsync();
                                holder.realmRegisteredInfo.addChangeListener(new RealmObjectChangeListener<RealmModel>() {
                                    @Override
                                    public void onChange(RealmModel realmModel, @javax.annotation.Nullable ObjectChangeSet changeSet) {
                                        if (changeSet == null || !changeSet.isDeleted()) {
                                            if (!((RealmRegisteredInfo) realmModel).isValid() || ((RealmRegisteredInfo) realmModel).getId() !=  mInfo.getLastMessage().getUserId())
                                                return;
                                            setSenderName(holder);
                                        }
                                    }
                                });

                                if (holder.realmRegisteredInfo.isLoaded() && holder.realmRegisteredInfo != null) {
                                    setSenderName(holder);
                                }
                            }

                            holder.lastMessageSender.setVisibility(View.VISIBLE);

                            holder.lastMessageSender.setText(lastMessageSender);
                            holder.lastMessageSender.setTextColor(Color.parseColor(G.roomSenderTextColor));

                        } else {
                            holder.lastMessageSender.setVisibility(View.GONE);
                        }

                        if (mInfo.getLastMessage() != null) {
                            ProtoGlobal.RoomMessageType _type, tmp;

                            _type = mInfo.getLastMessage().getMessageType();
                            String fileText = mInfo.getLastMessage().getMessage();

                            //don't use from reply , in reply message just get type and fileText from main message
                            //try {
                            //    if (mInfo.getLastMessage().getReplyTo() != null) {
                            //        tmp = mInfo.getLastMessage().getReplyTo().getMessageType();
                            //        if (tmp != null) {
                            //            _type = tmp;
                            //        }
                            //        //if (mInfo.getLastMessage().getReplyTo().getMessage() != null) {
                            //        //    fileText = mInfo.getLastMessage().getReplyTo().getMessage();
                            //        //}
                            //    }
                            //} catch (NullPointerException e) {
                            //    e.printStackTrace();
                            //}
                            //
                            try {
                                if (mInfo.getLastMessage().getForwardMessage() != null) {
                                    tmp = mInfo.getLastMessage().getForwardMessage().getMessageType();
                                    if (tmp != null) {
                                        _type = tmp;
                                    }
                                    if (mInfo.getLastMessage().getForwardMessage().getMessage() != null) {
                                        fileText = mInfo.getLastMessage().getForwardMessage().getMessage();
                                    }
                                }
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }

                            String result = AppUtils.conversionMessageType(_type, holder.txtLastMessage, G.roomMessageTypeColor);
                            if (result.isEmpty()) {
                                if (!HelperCalander.isPersianUnicode) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                        holder.txtLastMessage.setTextDirection(View.TEXT_DIRECTION_LTR);
                                    }
                                }
                                holder.txtLastMessage.setTextColor(Color.parseColor(G.textSubTheme));
                                holder.txtLastMessage.setText(subStringInternal(lastMessage));
                                holder.txtLastMessage.setEllipsize(TextUtils.TruncateAt.END);
                            } else {
                                if (fileText != null && !fileText.isEmpty()) {
                                    holder.txtLastMessageFileText.setVisibility(View.VISIBLE);
                                    holder.txtLastMessageFileText.setText(fileText);

                                    holder.txtLastMessage.setText(holder.txtLastMessage.getText() + " : ");
                                } else {
                                    holder.txtLastMessageFileText.setVisibility(View.GONE);
                                }
                            }
                        } else {
                            holder.txtLastMessage.setText(subStringInternal(lastMessage));
                        }
                    }
                } else {

                    holder.lastMessageSender.setVisibility(View.GONE);
                    holder.txtTime.setVisibility(View.GONE);
                }
            }
        }

        private void setSenderName(ViewHolder holder) {
            if (holder.realmRegisteredInfo.getDisplayName() != null) {

                String _name = holder.realmRegisteredInfo.getDisplayName();
                if (_name.length() > 0) {
                    String lastMessageSenderAsync;

                    if (Character.getDirectionality(_name.charAt(0)) == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC) {
                        if (HelperCalander.isPersianUnicode) {
                            lastMessageSenderAsync = _name + ": ";
                        } else {
                            lastMessageSenderAsync = " :" + _name;
                        }
                    } else {
                        if (HelperCalander.isPersianUnicode) {
                            lastMessageSenderAsync = " :" + _name;
                        } else {
                            lastMessageSenderAsync = _name + ": ";
                        }
                    }
                    holder.lastMessageSender.setText(lastMessageSenderAsync);
                }
            }
        }

        private void setAvatar(final RealmRoom mInfo, ViewHolder holder) {
            long idForGetAvatar;
            HelperAvatar.AvatarType avatarType;
            if (mInfo.getType() == CHAT) {
                idForGetAvatar = mInfo.getChatRoom().getPeerId();
                avatarType = HelperAvatar.AvatarType.USER;
            } else {
                idForGetAvatar = mInfo.getId();
                avatarType = HelperAvatar.AvatarType.ROOM;
            }

            final long idForGetAvatarOriginal = idForGetAvatar;

            HelperAvatar.getAvatar(idForGetAvatar, avatarType, false, new OnAvatarGet() {
                @Override
                public void onAvatarGet(String avatarPath, long idForGetAvatar) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (idForGetAvatar == idForGetAvatarOriginal) {
                                G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), holder.image);
                            }
                        }
                    });
                }

                @Override
                public void onShowInitials(String initials, String color, long idForGetAvatar) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (idForGetAvatar == idForGetAvatarOriginal) {
                                holder.image.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) context.getResources().getDimension(R.dimen.dp52), initials, color));
                            }
                        }
                    });
                }
            });
        }

        private void setChatIcon(RealmRoom mInfo, MaterialDesignTextView textView) {
            /**
             * ********************* chat icon *********************
             */
            if (mInfo.getType() == CHAT || mainType != all) {
                textView.setVisibility(View.GONE);
            } else {

                if (mInfo.getType() == GROUP) {
                    textView.setText(getStringChatIcon(RoomType.GROUP));
                } else if (mInfo.getType() == CHANNEL) {
                    textView.setText(getStringChatIcon(RoomType.CHANNEL));
                }
            }
        }

        /**
         * get string chat icon
         *
         * @param chatType chat type
         * @return String
         */
        private String getStringChatIcon(RoomType chatType) {
            switch (chatType) {
                case CHAT:
                    return "";
                case CHANNEL:
                    return context.getString(R.string.md_channel_icon);
                case GROUP:
                    return context.getString(R.string.md_users_social_symbol);
                default:
                    return null;
            }
        }

        //*******************************************************************************************

        public class ViewHolder extends RecyclerView.ViewHolder {

            protected CircleImageView image;
            protected EmojiTextViewE name;
            protected MaterialDesignTextView mute;
            protected RealmRoom mInfo;
            protected RealmRegisteredInfo realmRegisteredInfo;
            private ViewGroup rootChat;
            private EmojiTextViewE txtLastMessage;
            private EmojiTextViewE txtLastMessageFileText;
            private MaterialDesignTextView txtChatIcon;
            private TextView txtTime;
            private MaterialDesignTextView txtPinIcon;
            private AppCompatImageView imgVerifyRoom;
            private TextView txtUnread;
            private EmojiTextViewE lastMessageSender;
            private ImageView txtTic;
            private MaterialDesignTextView txtCloud;


            public ViewHolder(View view) {
                super(view);

                image = (CircleImageView) view.findViewById(R.id.cs_img_contact_picture);
                name = (EmojiTextViewE) view.findViewById(R.id.cs_txt_contact_name);
                name.setTypeface(G.typeface_IRANSansMobile_Bold);

                rootChat = (ViewGroup) view.findViewById(R.id.root_chat_sub_layout);
                txtLastMessage = (EmojiTextViewE) view.findViewById(R.id.cs_txt_last_message);
                txtLastMessageFileText = (EmojiTextViewE) view.findViewById(R.id.cs_txt_last_message_file_text);
                txtChatIcon = (MaterialDesignTextView) view.findViewById(R.id.cs_txt_chat_icon);

                txtTime = ((TextView) view.findViewById(R.id.cs_txt_contact_time));
                txtTime.setTypeface(G.typeface_IRANSansMobile);

                txtPinIcon = (MaterialDesignTextView) view.findViewById(R.id.cs_txt_pinned_message);
                txtPinIcon.setTypeface(G.typeface_Fontico);

                imgVerifyRoom = (AppCompatImageView) view.findViewById(R.id.cs_img_verify_room);

                txtUnread = (TextView) view.findViewById(R.id.cs_txt_unread_message);
                txtUnread.setTypeface(G.typeface_IRANSansMobile);

                mute = (MaterialDesignTextView) view.findViewById(R.id.cs_txt_mute);

                lastMessageSender = (EmojiTextViewE) view.findViewById(R.id.cs_txt_last_message_sender);
                lastMessageSender.setTypeface(G.typeface_IRANSansMobile);

                txtTic = (ImageView) view.findViewById(R.id.cslr_txt_tic);

                txtCloud = (MaterialDesignTextView) view.findViewById(R.id.cs_txt_contact_initials);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (ActivityMain.isMenuButtonAddShown) {
                            mComplete.complete(true, "closeMenuButton", "");
                        } else {
                            if (mInfo.isValid() && G.fragmentActivity != null) {

                                boolean openChat = true;

                                if (G.twoPaneMode) {
                                    Fragment fragment = G.fragmentManager.findFragmentByTag(FragmentChat.class.getName());
                                    if (fragment != null) {

                                        FragmentChat fm = (FragmentChat) fragment;
                                        if (fm.isAdded() && fm.mRoomId == mInfo.getId()) {
                                            openChat = false;
                                        } else {
                                            removeFromBaseFragment(fragment);
                                        }


                                    }
                                }

                                if (openChat) {
                                    new GoToChatActivity(mInfo.getId()).startActivity();

                                    if (((ActivityMain) G.fragmentActivity).arcMenu != null && ((ActivityMain) G.fragmentActivity).arcMenu.isMenuOpened()) {
                                        ((ActivityMain) G.fragmentActivity).arcMenu.toggleMenu();
                                    }
                                }
                            }
                        }
                    }
                });

                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        if (ActivityMain.isMenuButtonAddShown) {

                            if (mComplete != null) {
                                mComplete.complete(true, "closeMenuButton", "");
                            }

                        } else {
                            if (mInfo.isValid() && G.fragmentActivity != null) {
                                String role = null;
                                if (mInfo.getType() == GROUP) {
                                    role = mInfo.getGroupRoom().getRole().toString();
                                } else if (mInfo.getType() == CHANNEL) {
                                    role = mInfo.getChannelRoom().getRole().toString();
                                }

                                if (!G.fragmentActivity.isFinishing()) {
                                    long peerId = mInfo.getChatRoom() != null ? mInfo.getChatRoom().getPeerId() : 0;
                                    MyDialog.showDialogMenuItemRooms(G.fragmentActivity, mInfo.getTitle(), mInfo.getType(), mInfo.getMute(), role, peerId, mInfo, new OnComplete() {
                                        @Override
                                        public void complete(boolean result, String messageOne, String MessageTow) {
                                            onSelectRoomMenu(messageOne, mInfo);
                                        }
                                    }, mInfo.isPinned());
                                }
                            }
                        }
                        return true;
                    }
                });
            }
        }
    }


}
