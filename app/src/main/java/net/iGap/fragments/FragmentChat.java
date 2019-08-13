package net.iGap.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ViewStubCompat;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.lalongooo.videocompressor.video.MediaController;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.listeners.OnEmojiBackspaceClickListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupDismissListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupShownListener;
import com.vanniktech.emoji.listeners.OnSoftKeyboardCloseListener;
import com.vanniktech.emoji.listeners.OnSoftKeyboardOpenListener;
import com.vanniktech.emoji.sticker.OnDownloadStickerListener;
import com.vanniktech.emoji.sticker.OnOpenPageStickerListener;
import com.vanniktech.emoji.sticker.OnStickerAvatarDownloaded;
import com.vanniktech.emoji.sticker.OnStickerItemDownloaded;
import com.vanniktech.emoji.sticker.OnStickerListener;
import com.vanniktech.emoji.sticker.struct.StructGroupSticker;
import com.vanniktech.emoji.sticker.struct.StructItemSticker;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.Theme;
import net.iGap.activities.ActivityMain;
import net.iGap.activities.ActivityTrimVideo;
import net.iGap.adapter.AdapterDrBot;
import net.iGap.adapter.BottomSheetItem;
import net.iGap.adapter.MessagesAdapter;
import net.iGap.adapter.items.AdapterCamera;
import net.iGap.adapter.items.ItemBottomSheetForward;
import net.iGap.adapter.items.chat.AbstractMessage;
import net.iGap.adapter.items.chat.AudioItem;
import net.iGap.adapter.items.chat.BadgeView;
import net.iGap.adapter.items.chat.CardToCardItem;
import net.iGap.adapter.items.chat.ContactItem;
import net.iGap.adapter.items.chat.FileItem;
import net.iGap.adapter.items.chat.GifWithTextItem;
import net.iGap.adapter.items.chat.ImageWithTextItem;
import net.iGap.adapter.items.chat.LocationItem;
import net.iGap.adapter.items.chat.LogItem;
import net.iGap.adapter.items.chat.LogWallet;
import net.iGap.adapter.items.chat.LogWalletCardToCard;
import net.iGap.adapter.items.chat.NewChatItemHolder;
import net.iGap.adapter.items.chat.ProgressWaiting;
import net.iGap.adapter.items.chat.StickerItem;
import net.iGap.adapter.items.chat.TextItem;
import net.iGap.adapter.items.chat.TimeItem;
import net.iGap.adapter.items.chat.UnreadMessage;
import net.iGap.adapter.items.chat.VideoWithTextItem;
import net.iGap.adapter.items.chat.ViewMaker;
import net.iGap.adapter.items.chat.VoiceItem;
import net.iGap.databinding.PaymentDialogBinding;
import net.iGap.dialog.bottomsheet.BottomSheetFragment;
import net.iGap.dialog.topsheet.TopSheetDialog;
import net.iGap.eventbus.PaymentFragment;
import net.iGap.fragments.chatMoneyTransfer.ChatMoneyTransferFragment;
import net.iGap.fragments.emoji.HelperDownloadSticker;
import net.iGap.fragments.emoji.OnUpdateSticker;
import net.iGap.fragments.emoji.add.DialogAddSticker;
import net.iGap.fragments.emoji.add.FragmentSettingAddStickers;
import net.iGap.fragments.emoji.remove.FragmentSettingRemoveStickers;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperDownloadFile;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperGetAction;
import net.iGap.helper.HelperGetDataFromOtherApp;
import net.iGap.helper.HelperGetMessageState;
import net.iGap.helper.HelperImageBackColor;
import net.iGap.helper.HelperLog;
import net.iGap.helper.HelperMimeType;
import net.iGap.helper.HelperNotification;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperSaveFile;
import net.iGap.helper.HelperSetAction;
import net.iGap.helper.HelperString;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.HelperTracker;
import net.iGap.helper.HelperUploadFile;
import net.iGap.helper.HelperUrl;
import net.iGap.helper.ImageHelper;
import net.iGap.helper.LayoutCreator;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.helper.avatar.ParamWithInitBitmap;
import net.iGap.interfaces.IDispatchTochEvent;
import net.iGap.interfaces.IMessageItem;
import net.iGap.interfaces.IOnBackPressed;
import net.iGap.interfaces.IPickFile;
import net.iGap.interfaces.IResendMessage;
import net.iGap.interfaces.ISendPosition;
import net.iGap.interfaces.IUpdateLogItem;
import net.iGap.interfaces.LocationListener;
import net.iGap.interfaces.OnActivityChatStart;
import net.iGap.interfaces.OnBackgroundChanged;
import net.iGap.interfaces.OnBotClick;
import net.iGap.interfaces.OnChannelAddMessageReaction;
import net.iGap.interfaces.OnChannelGetMessagesStats;
import net.iGap.interfaces.OnChannelUpdateReactionStatus;
import net.iGap.interfaces.OnChatClearMessageResponse;
import net.iGap.interfaces.OnChatDelete;
import net.iGap.interfaces.OnChatDeleteMessageResponse;
import net.iGap.interfaces.OnChatEditMessageResponse;
import net.iGap.interfaces.OnChatMessageRemove;
import net.iGap.interfaces.OnChatMessageSelectionChanged;
import net.iGap.interfaces.OnChatSendMessage;
import net.iGap.interfaces.OnChatSendMessageResponse;
import net.iGap.interfaces.OnChatUpdateStatusResponse;
import net.iGap.interfaces.OnClearChatHistory;
import net.iGap.interfaces.OnClickCamera;
import net.iGap.interfaces.OnClientGetRoomMessage;
import net.iGap.interfaces.OnClientJoinByUsername;
import net.iGap.interfaces.OnComplete;
import net.iGap.interfaces.OnConnectionChangeStateChat;
import net.iGap.interfaces.OnDeleteChatFinishActivity;
import net.iGap.interfaces.OnForwardBottomSheet;
import net.iGap.interfaces.OnGetFavoriteMenu;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.interfaces.OnGroupAvatarResponse;
import net.iGap.interfaces.OnHelperSetAction;
import net.iGap.interfaces.OnLastSeenUpdateTiming;
import net.iGap.interfaces.OnMessageReceive;
import net.iGap.interfaces.OnPathAdapterBottomSheet;
import net.iGap.interfaces.OnPinedMessage;
import net.iGap.interfaces.OnSetAction;
import net.iGap.interfaces.OnUpdateUserOrRoomInfo;
import net.iGap.interfaces.OnUpdateUserStatusInChangePage;
import net.iGap.interfaces.OnUserContactsBlock;
import net.iGap.interfaces.OnUserContactsUnBlock;
import net.iGap.interfaces.OnUserInfoResponse;
import net.iGap.interfaces.OnUserUpdateStatus;
import net.iGap.interfaces.OnVoiceRecord;
import net.iGap.interfaces.OpenBottomSheetItem;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.libs.MyWebViewClient;
import net.iGap.libs.Tuple;
import net.iGap.libs.bottomNavigation.Util.Utils;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.BotInit;
import net.iGap.module.ChatSendMessageUtil;
import net.iGap.module.CircleImageView;
import net.iGap.module.ContactUtils;
import net.iGap.module.DialogAnimation;
import net.iGap.module.EmojiTextViewE;
import net.iGap.module.FileListerDialog.FileListerDialog;
import net.iGap.module.FileListerDialog.OnFileSelectedListener;
import net.iGap.module.FileUploadStructure;
import net.iGap.module.FileUtils;
import net.iGap.module.FontIconTextView;
import net.iGap.module.IntentRequests;
import net.iGap.module.LastSeenTimeUtil;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.MessageLoader;
import net.iGap.module.MusicPlayer;
import net.iGap.module.MyLinearLayoutManager;
import net.iGap.module.MyType;
import net.iGap.module.ResendMessage;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.SUID;
import net.iGap.module.TimeUtils;
import net.iGap.module.VoiceRecord;
import net.iGap.module.additionalData.AdditionalType;
import net.iGap.module.enums.Additional;
import net.iGap.module.enums.ChannelChatRole;
import net.iGap.module.enums.ConnectionState;
import net.iGap.module.enums.GroupChatRole;
import net.iGap.module.enums.LocalFileType;
import net.iGap.module.enums.ProgressState;
import net.iGap.module.enums.SendingStep;
import net.iGap.module.structs.StructBackGroundSeen;
import net.iGap.module.structs.StructBottomSheet;
import net.iGap.module.structs.StructBottomSheetForward;
import net.iGap.module.structs.StructChannelExtra;
import net.iGap.module.structs.StructCompress;
import net.iGap.module.structs.StructMessageAttachment;
import net.iGap.module.structs.StructMessageInfo;
import net.iGap.module.structs.StructSendSticker;
import net.iGap.module.structs.StructUploadVideo;
import net.iGap.module.structs.StructWebView;
import net.iGap.proto.ProtoChannelGetMessagesStats;
import net.iGap.proto.ProtoClientGetRoomHistory;
import net.iGap.proto.ProtoClientRoomReport;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoResponse;
import net.iGap.proto.ProtoSignalingOffer;
import net.iGap.realm.RealmAdditional;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmAttachmentFields;
import net.iGap.realm.RealmCallConfig;
import net.iGap.realm.RealmChannelExtra;
import net.iGap.realm.RealmChannelRoom;
import net.iGap.realm.RealmClientCondition;
import net.iGap.realm.RealmClientConditionFields;
import net.iGap.realm.RealmContacts;
import net.iGap.realm.RealmContactsFields;
import net.iGap.realm.RealmGroupRoom;
import net.iGap.realm.RealmOfflineSeen;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomDraft;
import net.iGap.realm.RealmRoomFields;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageContact;
import net.iGap.realm.RealmRoomMessageFields;
import net.iGap.realm.RealmStickers;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestChannelEditMessage;
import net.iGap.request.RequestChannelPinMessage;
import net.iGap.request.RequestChannelUpdateDraft;
import net.iGap.request.RequestChatDelete;
import net.iGap.request.RequestChatEditMessage;
import net.iGap.request.RequestChatGetRoom;
import net.iGap.request.RequestChatUpdateDraft;
import net.iGap.request.RequestClientGetFavoriteMenu;
import net.iGap.request.RequestClientGetRoomMessage;
import net.iGap.request.RequestClientJoinByUsername;
import net.iGap.request.RequestClientMuteRoom;
import net.iGap.request.RequestClientRoomReport;
import net.iGap.request.RequestClientSubscribeToRoom;
import net.iGap.request.RequestClientUnsubscribeFromRoom;
import net.iGap.request.RequestFileDownload;
import net.iGap.request.RequestGroupEditMessage;
import net.iGap.request.RequestGroupPinMessage;
import net.iGap.request.RequestGroupUpdateDraft;
import net.iGap.request.RequestQueue;
import net.iGap.request.RequestSignalingGetConfiguration;
import net.iGap.request.RequestUserContactsBlock;
import net.iGap.request.RequestUserContactsUnblock;
import net.iGap.request.RequestUserInfo;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import io.fabric.sdk.android.services.concurrency.AsyncTask;
import io.fotoapparat.Fotoapparat;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

import static android.app.Activity.RESULT_CANCELED;
import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.CLIPBOARD_SERVICE;
import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_SWIPE;
import static io.fotoapparat.parameter.selector.LensPositionSelectors.back;
import static io.fotoapparat.parameter.selector.SizeSelectors.biggestSize;
import static java.lang.Long.parseLong;
import static net.iGap.G.chatSendMessageUtil;
import static net.iGap.G.context;
import static net.iGap.R.id.ac_ll_parent;
import static net.iGap.R.string.item;
import static net.iGap.adapter.items.chat.ViewMaker.i_Dp;
import static net.iGap.helper.HelperCalander.convertToUnicodeFarsiNumber;
import static net.iGap.module.AttachFile.getFilePathFromUri;
import static net.iGap.module.AttachFile.request_code_VIDEO_CAPTURED;
import static net.iGap.module.AttachFile.request_code_pic_file;
import static net.iGap.module.MessageLoader.getLocalMessage;
import static net.iGap.module.enums.ProgressState.HIDE;
import static net.iGap.module.enums.ProgressState.SHOW;
import static net.iGap.proto.ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction.DOWN;
import static net.iGap.proto.ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction.UP;
import static net.iGap.proto.ProtoGlobal.Room.Type.CHANNEL;
import static net.iGap.proto.ProtoGlobal.Room.Type.CHAT;
import static net.iGap.proto.ProtoGlobal.Room.Type.GROUP;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.CONTACT;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.GIF;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.GIF_TEXT;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.IMAGE;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.IMAGE_TEXT;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.LOG;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.VIDEO;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.VIDEO_TEXT;
import static net.iGap.realm.RealmRoomMessage.makeUnreadMessage;

public class FragmentChat extends BaseFragment
        implements IMessageItem, OnChatClearMessageResponse, OnPinedMessage, OnChatSendMessageResponse, OnChatUpdateStatusResponse, OnChatMessageSelectionChanged<AbstractMessage>, OnChatMessageRemove, OnVoiceRecord,
        OnUserInfoResponse, OnSetAction, OnUserUpdateStatus, OnLastSeenUpdateTiming, OnGroupAvatarResponse, OnChannelAddMessageReaction, OnChannelGetMessagesStats, OnChatDelete, OnBackgroundChanged, LocationListener,
        OnConnectionChangeStateChat, OnChannelUpdateReactionStatus, OnBotClick, ToolbarListener {

    public static OnComplete onMusicListener;
    public static IUpdateLogItem iUpdateLogItem;
    public static OnPathAdapterBottomSheet onPathAdapterBottomSheet;
    public static OnForwardBottomSheet onForwardBottomSheet;
    public static OnClickCamera onClickCamera;
    public static OnComplete hashListener;
    public static OnComplete onComplete;
    public static OnUpdateUserOrRoomInfo onUpdateUserOrRoomInfo;
    public static ArrayList<Long> resentedMessageId = new ArrayList<>();
    public static ArrayMap<Long, HelperUploadFile.StructUpload> compressingFiles = new ArrayMap<>();
    public static int forwardMessageCount = 0;
    public static ArrayList<Parcelable> mForwardMessages;
    public static boolean canClearForwardList = true;
    public static boolean isInSelectionMode = false;
    public static Realm realmChat; // static for FragmentTest
    public static boolean canUpdateAfterDownload = false;
    public static String titleStatic;
    public static long messageId;
    public static long mRoomIdStatic = 0;
    public static long lastChatRoomId = 0;
    public static List<StructGroupSticker> data = new ArrayList<>();
    public static ArrayList<String> listPathString;
    public static OnUpdateSticker onUpdateSticker;
    private static List<StructBottomSheet> contacts;
    private static ArrayMap<String, Boolean> compressedPath = new ArrayMap<>(); // keep compressedPath and also keep video path that never be won't compressed
    private static ArrayList<StructUploadVideo> structUploadVideos = new ArrayList<>();
    private EmojiPopup emojiPopup;
    private boolean isPaused;

    private String cardNumber = "";
    private String description = "";
    private String amount = "";




    /**
     * *************************** common method ***************************
     */

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private final int END_CHAT_LIMIT = 5;
    public Runnable gongingRunnable;
    public Handler gongingHandler;
    public MusicPlayer musicPlayer;
    public String title;
    public String phoneNumber;
    public long mRoomId = 0;
    public CardView cardFloatingTime;
    public TextView txtFloatingTime;
    public boolean rcTouchListener;
    BotInit botInit;
    PaymentDialogBinding paymentDialogBinding;
    PaymentFragment paymentDialog;
    boolean isAnimateStart = false;
    boolean isScrollEnd = false;
    private boolean isShareOk = true;
    private boolean isRepley = false;
    private boolean swipeBack = false;
    private AttachFile attachFile;
    private EditText edtSearchMessage;
    private SharedPreferences sharedPreferences;
    private net.iGap.module.EmojiEditTextE edtChat;
    private MaterialDesignTextView imvSendButton;
    private MaterialDesignTextView imvAttachFileButton;
    private MaterialDesignTextView imvMicButton;
    private MaterialDesignTextView sendMoney;
    //  private MaterialDesignTextView btnReplaySelected;
    private MaterialDesignTextView btnCancelSendingFile;
    private CircleImageView imvUserPicture;
    private TextView txtVerifyRoomIcon;
    private ImageView imgBackGround;
    private RecyclerView recyclerView;
    private RealmRoom managedRoom;
    private RealmRoom unmanagedRoom;

    private WebView webViewChatPage;
    private boolean isStopBot;
    private String urlWebViewForSpecialUrlChat;
    private RelativeLayout rootWebView;
    private ProgressBar progressWebView;
    private MaterialDesignTextView imvSmileButton;
    private LocationManager locationManager;
    private OnComplete complete;
    private View viewAttachFile;
    private View viewMicRecorder;
    private VoiceRecord voiceRecord;
    private MaterialDesignTextView txtClearMessageSearch;
    private MaterialDesignTextView btnHashLayoutClose;
    private SearchHash searchHash;
    private MessagesAdapter<AbstractMessage> mAdapter;
    private ProtoGlobal.Room.Type chatType;
    private GroupChatRole groupRole;
    private ChannelChatRole channelRole;
    private PopupWindow popupWindow;
    private MaterialDialog dialogWait;
    private Uri latestUri;
    private Calendar lastDateCalendar = Calendar.getInstance();
    private TextView iconMute;
    private LinearLayout ll_Search;
    private LinearLayout layoutAttachBottom;
    private LinearLayout ll_attach_text;
    private ConstraintLayout ll_AppBarSelected;
    private MaterialDesignTextView mBtnCopySelected, mBtnForwardSelected, mBtnReplySelected, mBtnDeleteSelected;
    private TextView mTxtSelectedCounter;
    // private LinearLayout ll_navigate_Message;
    private LinearLayout ll_navigateHash;
    private LinearLayout mReplayLayout;
    private LinearLayout pinedMessageLayout;
    private ProgressBar prgWaiting;
    //  private AVLoadingIndicatorView avi;
    private ViewGroup vgSpamUser;
    private RecyclerView.OnScrollListener scrollListener;
    private RecyclerView rcvBottomSheet;
    private RecyclerView rcvDrBot;
    private FrameLayout llScrollNavigate;
    private FastItemAdapter fastItemAdapter;
    private FastItemAdapter fastItemAdapterForward;
    private BottomSheetDialog bottomSheetDialog;
    private BottomSheetDialog bottomSheetDialogForward;
    private View viewBottomSheet;
    private View viewBottomSheetForward;
    private Fotoapparat fotoapparatSwitcher;
    private RealmRoomMessage firstUnreadMessage;
    private RealmRoomMessage firstUnreadMessageInChat; // when user is in this room received new message
    private RealmRoomMessage voiceLastMessage = null;
    private boolean showVoteChannel = true;
    private RealmResults<RealmRoom> results = null;
    private RealmResults<RealmContacts> resultsContact = null;
    private ArrayList<StructBackGroundSeen> backGroundSeenList = new ArrayList<>();
    private TextView txtSpamUser;
    private TextView txtSpamClose;
    private TextView send;
    private TextView txtCountItem;
    private BadgeView txtNewUnreadMessage;
    private TextView imvCancelForward;
    private TextView btnUp;
    private TextView btnDown;
    private TextView txtChannelMute;
    private TextView btnUpHash;
    private TextView btnDownHash;
    private TextView txtHashCounter;
    private TextView txtFileNameForSend;
    private EmojiTextViewE txtName;
    private TextView txtLastSeen;
    private TextView txtEmptyMessages;
    private String userName = "";
    private String latestFilePath;
    private String mainVideoPath = "";
    private String color;
    private String initialize;
    private String groupParticipantsCountLabel;
    private String channelParticipantsCountLabel;
    private String userStatus;
    private Boolean isGoingFromUserLink = false;
    private Boolean isNotJoin = false; // this value will be trued when come to this chat with username
    private boolean firsInitScrollPosition = false;
    private boolean initHash = false;
    private boolean initAttach = false;
    private boolean hasDraft = false;
    private boolean hasForward = false;
    private boolean blockUser = false;
    private boolean isChatReadOnly = false;
    private boolean isMuteNotification;
    private boolean sendByEnter = false;
    private boolean isShowLayoutUnreadMessage = false;
    private boolean isCloudRoom;
    private boolean isEditMessage = false;
    private boolean isBot = false;
    private long biggestMessageId = 0;
    private long lastMessageId = 0;
    private long replyToMessageId = 0;
    private long userId;
    private boolean isShowStartButton = false;
    private long lastSeen;
    private long chatPeerId;
    private long userTime;
    private long savedScrollMessageId;
    private long latestButtonClickTime; // use from this field for avoid from show button again after click it
    private int countNewMessage = 0;
    private int lastPosition = 0;
    private int unreadCount = 0;
    private int latestRequestCode;
    private int messageCounter = 0;
    private int selectedPosition = 0;
    private boolean isNoMessage = true;
    private boolean isEmojiSHow = false;
    private boolean isCameraStart = false;
    private boolean isCameraAttached = false;
    private boolean isPermissionCamera = false;
    private boolean isPublicGroup = false;
    private ArrayList<Long> bothDeleteMessageId;
    private RelativeLayout layoutMute;
    private String report = "";
    private View rootView;
    private boolean isAllSenderId = true;
    private ArrayList<Long> multiForwardList = new ArrayList<>();
    private ArrayList<StructBottomSheetForward> mListForwardNotExict = new ArrayList<>();
    private boolean isNewBottomSheet = true;
    private ArrayList<StructGroupSticker> stickerArrayList = new ArrayList<>();
    /**
     * **********************************************************************
     * *************************** Message Loader ***************************
     * **********************************************************************
     */

    private boolean addToView; // allow to message for add to recycler view or no
    private boolean topMore = true; // more message exist in local for load in up direction (topMore default value is true for allowing that try load top message )
    private boolean bottomMore; // more message exist in local for load in bottom direction
    private boolean isWaitingForHistoryUp; // client send request for getHistory, avoid for send request again
    private boolean isWaitingForHistoryDown; // client send request for getHistory, avoid for send request again
    private boolean allowGetHistoryUp = true; // after insuring for get end of message from server set this false. (set false in history error maybe was wrong , because maybe this was for another error not end  of message, (hint: can check error code for end of message from history))
    private boolean allowGetHistoryDown = true; // after insuring for get end of message from server set this false. (set false in history error maybe was wrong , because maybe this was for another error not end  of message, (hint: can check error code for end of message from history))
    private boolean firstUp = true; // if is firstUp getClientRoomHistory with low limit in UP direction
    private boolean firstDown = true; // if is firstDown getClientRoomHistory with low limit in DOWN direction
    private String lastRandomRequestIdUp = ""; // last RandomRequestId Up
    private String lastRandomRequestIdDown = ""; // last RandomRequestId Down
    private long gapMessageIdUp; // messageId that maybe lost in local
    private long gapMessageIdDown; // messageId that maybe lost in local
    private long reachMessageIdUp; // messageId that will be checked after getHistory for detect reached to that or no
    private long reachMessageIdDown; // messageId that will be checked after getHistory for detect reached to that or no
    private long startFutureMessageIdUp; // for get history from local or online in next step use from this param, ( hint : don't use from adapter items, because maybe this item was deleted and in this changeState messageId for get history won't be detected.
    private long startFutureMessageIdDown; // for get history from local or online in next step use from this param, ( hint : don't use from adapter items, because maybe this item was deleted and in this changeState messageId for get history won't be detected.
    private long progressIdentifierUp = 0; // store identifier for Up progress item and use it if progress not removed from view after check 'instanceOf' in 'progressItem' method
    private long progressIdentifierDown = 0; // store identifier for Down progress item and use it if progress not removed from view after check 'instanceOf' in 'progressItem' method
    private int firstVisiblePosition; // difference between start of adapter item and items that Showing.
    private int firstVisiblePositionOffset; // amount of offset from top of view for first visible item in adapter
    private int visibleItemCount; // visible item in recycler view
    private int totalItemCount; // all item in recycler view
    private int scrollEnd = 80; // (hint: It should be less than MessageLoader.LOCAL_LIMIT ) to determine the limits to get to the bottom or top of the list
    private boolean isCardToCardMessage = false;

    private HelperToolbar mHelperToolbar;
    private ViewGroup layoutToolbar;
    private boolean isPinAvailable = false;

    private SoundPool soundPool;
    private boolean soundInChatPlay = false;
    private boolean sendMessageLoaded;
    private boolean receiveMessageLoaded;
    private int sendMessageSound;
    private int receiveMessageSound;
    private String TAG = "messageSound";

    public static Realm getRealmChat() {
        if (realmChat == null || realmChat.isClosed()) {
            realmChat = Realm.getDefaultInstance();
        }
        return realmChat;
    }

    public static boolean allowResendMessage(long messageId) {
        if (resentedMessageId == null) {
            resentedMessageId = new ArrayList<>();
        }

        if (resentedMessageId.contains(messageId)) {
            return false;
        }

        resentedMessageId.add(messageId);
        return true;
    }

    public static void removeResendList(long messageId) {
        FragmentChat.resentedMessageId.remove(messageId);
    }

    /**
     * get images for show in bottom sheet
     */
    public static ArrayList<StructBottomSheet> getAllShownImagesPath(Activity activity) {
        ArrayList<StructBottomSheet> listOfAllImages = new ArrayList<>();
        Uri uri;
        Cursor cursor;
        int column_index_data = 0, column_index_folder_name;
        String absolutePathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN
        };

        cursor = activity.getContentResolver().query(uri, projection, null, null, MediaStore.Images.ImageColumns.DATE_TAKEN);

        if (cursor != null) {
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

            column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data);

                StructBottomSheet item = new StructBottomSheet();
                item.setId(listOfAllImages.size());
                item.setPath(absolutePathOfImage);
                item.isSelected = true;
                listOfAllImages.add(0, item);
            }
            cursor.close();
        }
        return listOfAllImages;
    }

    public static void isUiThread(String name, int line) {
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            Log.i("UUU", name + " in line : " + line + " is UI Thread");
        } else {
            Log.i("UUU", name + " in line : " + line + " is NOT UI Thread");
        }
    }

    public void fillStickerList() {

        data.clear();
        data = RealmStickers.getAllStickers(true);
        if (data != null && emojiPopup != null) {
            emojiPopup.updateStickerAdapter((ArrayList<StructGroupSticker>) data);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isNeedResume = true;
        G.locationListener = this;
        rootView = inflater.inflate(R.layout.activity_chat, container, false);

        ViewGroup chatBoxRootView = rootView.findViewById(R.id.layout_attach_file);

        if (G.isDarkTheme)
            chatBoxRootView.setBackground(getResources().getDrawable(R.drawable.backround_chatroom_root_dark));
        else
            chatBoxRootView.setBackground(getResources().getDrawable(R.drawable.backround_chatroom_root));
        sendMoney = rootView.findViewById(R.id.btn_chatRoom_wallet);

        /**
         * init chat box edit text and send item because we need change this color in dark mode!
         * */

        edtChat = rootView.findViewById(R.id.et_chatRoom_writeMessage);
        imvSendButton = rootView.findViewById(R.id.btn_chatRoom_send);

        if (G.isDarkTheme) {
            imvSendButton.setTextColor(inflater.getContext().getResources().getColor(R.color.green));
            edtChat.setBackground(ContextCompat.getDrawable(inflater.getContext(), R.drawable.backround_chatroom_edittext_dark));
            edtChat.setHintTextColor(ContextCompat.getColor(inflater.getContext(),R.color.white));
            edtChat.setTextColor(inflater.getContext().getResources().getColor(R.color.white));
        } else {
            imvSendButton.setTextColor(inflater.getContext().getResources().getColor(R.color.md_green_700));
            edtChat.setBackground(ContextCompat.getDrawable(inflater.getContext(), R.drawable.backround_chatroom_edittext));
            edtChat.setHintTextColor(ContextCompat.getColor(inflater.getContext(),R.color.gray_4c));
        }

        return attachToSwipeBack(rootView);
    }

    public void exportChat() {


        RealmResults<RealmRoomMessage> realmRoomMessages = getRealmChat().where(RealmRoomMessage.class).equalTo("roomId", mRoomId).sort("createTime").findAll();
        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/iGap", "iGap Messages");

        if (!root.exists()) {
            root.mkdir();
        }


        FileListerDialog fileListerDialog = FileListerDialog.createFileListerDialog(G.fragmentActivity);
        fileListerDialog.setDefaultDir(root);
        fileListerDialog.setFileFilter(FileListerDialog.FILE_FILTER.DIRECTORY_ONLY);
        fileListerDialog.show();

        fileListerDialog.setOnFileSelectedListener(new OnFileSelectedListener() {
            @Override
            public void onFileSelected(File file, String path) {
                final MaterialDialog[] dialog = new MaterialDialog[1];
                if (realmRoomMessages.size() != 0 && chatType != CHANNEL) {

                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            dialog[0] = new MaterialDialog.Builder(G.currentActivity)
                                    .title(R.string.export_chat)
                                    .content(R.string.just_wait_en)
                                    .progress(false, realmRoomMessages.size(), true)
                                    .show();
                        }
                    });
                    try {
                        File filepath = new File(file, title + ".txt");
                        FileWriter writer = new FileWriter(filepath);

                        for (RealmRoomMessage export : realmRoomMessages) {

                            if (export.getMessageType().toString().equalsIgnoreCase("TEXT")) {

                                writer.append(RealmRegisteredInfo.getNameWithId(export.getUserId()) + "  text message " + "  :  " + export.getMessage() + "  date  :" + HelperCalander.milladyDate(export.getCreateTime()) + "\n");

                            } else {
                                writer.append(RealmRegisteredInfo.getNameWithId(export.getUserId()) + "  text message " + export.getMessage() + "  :  message in format " + export.getMessageType() + "  date  :" + HelperCalander.milladyDate(export.getCreateTime()) + "\n");
                            }
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    dialog[0].incrementProgress(1);
                                }
                            });

                        }
                        writer.flush();
                        writer.close();
                        G.handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog[0].dismiss();
                            }
                        }, 500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

//

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imvSendButton = rootView.findViewById(R.id.btn_chatRoom_send);

        cardFloatingTime = rootView.findViewById(R.id.cardFloatingTime);
        txtFloatingTime = rootView.findViewById(R.id.txtFloatingTime);
        txtChannelMute = rootView.findViewById(R.id.chl_txt_mute_channel);
        layoutMute = rootView.findViewById(R.id.chl_ll_channel_footer);

        realmChat = Realm.getDefaultInstance();
        gongingRunnable = new Runnable() {
            @Override
            public void run() {
                cardFloatingTime.setVisibility(View.GONE);
            }
        };
        gongingHandler = new Handler(Looper.getMainLooper());

        startPageFastInitialize();
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null && !getActivity().isFinishing() && isAdded()) {
                    initMain();
                }
            }
        }, Config.LOW_START_PAGE_TIME);

        if (G.isWalletActive && G.isWalletRegister && (chatType == CHAT) && !isCloudRoom && !isBot){
            sendMoney.setVisibility(View.VISIBLE);
        }
    }

    private void soundInChatInit(){
        if (soundInChatPlay){
            try {
                if (soundPool == null){
                    soundPool = new SoundPool(3, AudioManager.STREAM_SYSTEM, 0);
                }

                if (sendMessageSound == 0 && !sendMessageLoaded) {
                    sendMessageLoaded = true;
                    sendMessageSound = soundPool.load(getContext(), R.raw.send_message_sound, 1);
                }

                if (receiveMessageSound == 0 && !receiveMessageLoaded) {
                    receiveMessageLoaded = true;
                    receiveMessageSound = soundPool.load(getContext(), R.raw.receive_message_sound, 1);
                }

            } catch (Exception e) {
                Log.i(TAG, "soundPool error: " + e.getMessage());
            }

        }
    }

    @Override
    public void onStart() {
        super.onStart();

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                RealmRoomMessage.fetchMessages(getRealmChat(), mRoomId, new OnActivityChatStart() {
                    @Override
                    public void resendMessage(final RealmRoomMessage message) {
                        if (!allowResendMessage(message.getMessageId())) {
                            return;
                        }
                        chatSendMessageUtil.build(chatType, message.getRoomId(), message);
                    }

                    @Override
                    public void resendMessageNeedsUpload(final RealmRoomMessage message, final long messageId) {
                        if (!allowResendMessage(message.getMessageId())) {
                            return;
                        }
                        HelperUploadFile.startUploadTaskChat(mRoomId, chatType, message.getAttachment().getLocalFilePath(), message.getMessageId(), message.getMessageType(), message.getMessage(), RealmRoomMessage.getReplyMessageId(message), new HelperUploadFile.UpdateListener() {
                            @Override
                            public void OnProgress(int progress, FileUploadStructure struct) {
                                if (canUpdateAfterDownload) {
                                    insertItemAndUpdateAfterStartUpload(progress, struct);
                                }
                            }

                            @Override
                            public void OnError() {

                            }
                        });
                        G.handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyItemChanged(mAdapter.findPositionByMessageId(messageId));
                            }
                        }, 300);
                    }

                    @Override
                    public void sendSeenStatus(RealmRoomMessage message) {

                        if (!isNotJoin) {
                            G.chatUpdateStatusUtil.sendUpdateStatus(chatType, mRoomId, message.getMessageId(), ProtoGlobal.RoomMessageStatus.SEEN);
                        }
                    }
                });


            }
        }, 500);
    }

    @Override
    public void onResume() {
        isPaused = false;
        super.onResume();

        if (FragmentShearedMedia.list != null && FragmentShearedMedia.list.size() > 0) {
            deleteSelectedMessageFromAdapter(FragmentShearedMedia.list);
            FragmentShearedMedia.list.clear();
        }
        canUpdateAfterDownload = true;

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                initLayoutHashNavigationCallback();
                showSpamBar();

                updateShowItemInScreen();


                if (isGoingFromUserLink) {
                    new RequestClientSubscribeToRoom().clientSubscribeToRoom(mRoomId);
                }

                //+final Realm updateUnreadCountRealm = Realm.getDefaultInstance();
                getRealmChat().executeTransactionAsync(new Realm.Transaction() {//ASYNC
                    @Override
                    public void execute(Realm realm) {
                        final RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
                        if (room != null) {
                            room.setUnreadCount(0);
                            if (G.connectionState == ConnectionState.CONNECTING || G.connectionState == ConnectionState.WAITING_FOR_NETWORK) {
                                setConnectionText(G.connectionState);
                            } else {
                                if (room.getType() != CHAT) {
                                    /**
                                     * set member count
                                     * set this code in onResume for update this value when user
                                     * come back from profile activities
                                     */

                                    String members = null;
                                    if (room.getType() == GROUP && room.getGroupRoom() != null) {
                                        members = room.getGroupRoom().getParticipantsCountLabel();
                                    } else if (room.getType() == CHANNEL && room.getChannelRoom() != null) {
                                        members = room.getChannelRoom().getParticipantsCountLabel();
                                    }

                                    final String finalMembers = members;
                                    if (finalMembers != null) {
                                        G.handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (finalMembers != null && HelperString.isNumeric(finalMembers) && Integer.parseInt(finalMembers) == 1) {
                                                    txtLastSeen.setText(finalMembers + " " + G.fragmentActivity.getResources().getString(R.string.one_member_chat));
                                                } else {
                                                    txtLastSeen.setText(finalMembers + " " + G.fragmentActivity.getResources().getString(R.string.member_chat));
                                                }
                                                //    avi.setVisibility(View.GONE);

                                                if (HelperCalander.isPersianUnicode)
                                                    txtLastSeen.setText(convertToUnicodeFarsiNumber(txtLastSeen.getText().toString()));
                                            }
                                        });
                                    }
                                } else {
                                    RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, room.getChatRoom().getPeerId());
                                    if (realmRegisteredInfo != null) {
                                        setUserStatus(realmRegisteredInfo.getStatus(), realmRegisteredInfo.getLastSeen());
                                    }
                                }

                            }
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        /**
                         * hint: should use from this method here because we need checkAction
                         * changeState after set members count for avoid from hide action if exist
                         */
                        checkAction();

                        RealmRoom room = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
                        if (room != null) {
                            if (txtName == null) {
                                txtName = mHelperToolbar.getTextViewChatUserName();
                            }
                            txtName.setText(room.getTitle());
                            checkToolbarNameSize();
                        }
                    }
                });

                try {
                    mHelperToolbar.checkIsAvailableOnGoingCall();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, Config.LOW_START_PAGE_TIME);

        mRoomIdStatic = mRoomId;
        lastChatRoomId = mRoomId;
        titleStatic = title;

        G.clearMessagesUtil.setOnChatClearMessageResponse(this);
        G.onUserInfoResponse = this;
        G.onChannelAddMessageReaction = this;
        G.onChannelGetMessagesStats = this;
        G.onSetAction = this;
        G.onUserUpdateStatus = this;
        G.onLastSeenUpdateTiming = this;
        G.onChatDelete = this;
        G.onBackgroundChanged = this;
        G.onConnectionChangeStateChat = this;
        HelperNotification.getInstance().cancelNotification();
        G.onChannelUpdateReactionStatusChat = this;
        G.onPinedMessage = this;
        G.onBotClick = this;

        /*finishActivity = new FinishActivity() {
            @Override
            public void finishActivity() {
                // ActivityChat.this.finish();
                finishChat();
            }
        };*/

        initCallbacks();
        HelperNotification.getInstance().isChatRoomNow = true;

        onUpdateUserOrRoomInfo = new OnUpdateUserOrRoomInfo() {
            @Override
            public void onUpdateUserOrRoomInfo(final String messageId) {

                if (messageId != null && messageId.length() > 0) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            int start = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                            if (start < 0) {
                                start = 0;
                            }

                            for (int i = start; i < mAdapter.getItemCount() && i < start + 15; i++) {
                                try {
                                    if (mAdapter.getItem(i).mMessage != null && mAdapter.getItem(i).mMessage.messageID.equals(messageId)) {
                                        mAdapter.notifyItemChanged(i);
                                        break;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
            }
        };

        if (backGroundSeenList != null && backGroundSeenList.size() > 0) {
            for (int i = 0; i < backGroundSeenList.size(); i++) {

                G.chatUpdateStatusUtil.sendUpdateStatus(backGroundSeenList.get(i).roomType, mRoomId, backGroundSeenList.get(i).messageID, ProtoGlobal.RoomMessageStatus.SEEN);
            }

            backGroundSeenList.clear();
        }

        if (isCloudRoom) {
            mHelperToolbar.getCloudChatIcon().setVisibility(View.VISIBLE);
            mHelperToolbar.getCloudChatIcon().setImageResource(R.drawable.ic_cloud_space_blue);

            mHelperToolbar.getUserAvatarChat().setVisibility(View.GONE);
        } else {
            mHelperToolbar.getCloudChatIcon().setVisibility(View.GONE);
            mHelperToolbar.getUserAvatarChat().setVisibility(View.VISIBLE);
            setAvatar();
        }

        if (mForwardMessages == null) {
            rootView.findViewById(R.id.ac_ll_forward).setVisibility(View.GONE);
        }

        RealmRoom realmRoom = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
        if (realmRoom != null) {

            isMuteNotification = realmRoom.getMute() ;
            if (!isBot){
                txtChannelMute.setText(isMuteNotification ? R.string.unmute : R.string.mute);
            }
            iconMute.setVisibility(isMuteNotification ? View.VISIBLE : View.GONE);

        }

        registerListener();
    }

    private void checkToolbarNameSize() {

        if (!mHelperToolbar.getRightButton().isShown()){
            txtName.setMaxWidth(i_Dp(R.dimen.toolbar_txt_name_max_width4));
        }else if (!mHelperToolbar.getSecondRightButton().isShown()){
            txtName.setMaxWidth(i_Dp(R.dimen.toolbar_txt_name_max_width3));
        }/*else if (mHelperToolbar.getThirdRightButton().isShown()){
            txtName.setMaxWidth(i_Dp(R.dimen.toolbar_txt_name_max_width2));
        }*/ else {
            txtName.setMaxWidth(i_Dp(R.dimen.toolbar_txt_name_max_width));
        }

    }

    @Override
    public void onPause() {
        isPaused = true;
        storingLastPosition();
        super.onPause();

        lastChatRoomId = 0;

        if (isGoingFromUserLink && isNotJoin) {
            new RequestClientUnsubscribeFromRoom().clientUnsubscribeFromRoom(mRoomId);
        }
        onMusicListener = null;
        iUpdateLogItem = null;

        unRegisterListener();
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoom.setCount(realm, mRoomId, 0);
            }
        });
        realm.close();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (emojiPopup != null)
            emojiPopup.releaseMemory();
    }

    @Override
    public void onStop() {
        if (emojiPopup != null) {
            emojiPopup.dismiss();
        }

        canUpdateAfterDownload = false;
        if (G.onChatSendMessage != null)
            G.onChatSendMessage = null;
        setDraft();
        HelperNotification.getInstance().isChatRoomNow = false;

        //if (isNotJoin) { // hint : commented this code, because when going to profile and return can't load message
        //
        //    /**
        //     * delete all  deleted row from database
        //     */
        //    RealmRoom.deleteRoom(mRoomId);
        //}


        // room id have to be set to default, otherwise I'm in the room always!

        //MusicPlayer.chatLayout = null;
        //ActivityCall.stripLayoutChat = null;

        try {
            MusicPlayer.chatLayout = null;
            MusicPlayer.shearedMediaLayout = null;

            if (!G.isInCall && MusicPlayer.mp != null && MusicPlayer.mainLayout != null) {
                MusicPlayer.initLayoutTripMusic(MusicPlayer.mainLayout);
                MusicPlayer.mainLayout.setVisibility(View.VISIBLE);
                MusicPlayer.playerStateChangeListener.postValue(false);
            }
        }catch (Exception ex){

        }

        super.onStop();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        mRoomId = -1;

        if (webViewChatPage != null) closeWebViewForSpecialUrlChat(true);

        if (G.fragmentActivity instanceof ActivityMain) {
            ((ActivityMain) G.fragmentActivity).resume();
        }
   /*     if (G.locationListenerResponse != null)
            G.locationListenerResponse = null;*/

        if (G.locationListener != null)
            G.locationListener = null;


        if (realmChat != null && !realmChat.isClosed()) {
            realmChat.close();
        }


    }

    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * If it's in the app and the screen lock is activated after receiving the result of the camera and .... The page code is displayed.
         * The wizard will  be set ActivityMain.isUseCamera = true to prevent the page from being opened....
         */
        if (G.isPassCode) ActivityMain.isUseCamera = true;

        if (resultCode == RESULT_CANCELED) {
            HelperSetAction.sendCancel(messageId);

            hideProgress();
        }

        if (requestCode == AttachFile.request_code_position && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            try {
                attachFile.requestGetPosition(complete, FragmentChat.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        if (resultCode == Activity.RESULT_OK) {

            HelperSetAction.sendCancel(messageId);

            if (requestCode == AttachFile.request_code_contact_phone) {
                latestUri = data.getData();
                sendMessage(requestCode, "");
                return;
            }

            listPathString = null;
            if (AttachFile.request_code_TAKE_PICTURE == requestCode) {

                listPathString = new ArrayList<>();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    listPathString.add(AttachFile.mCurrentPhotoPath);
                } else {
                    listPathString.add(AttachFile.imagePath);
                }

                latestUri = null; // check
            } else if (request_code_VIDEO_CAPTURED == requestCode) {

                listPathString = new ArrayList<>();
                listPathString.add(AttachFile.videoPath);

                latestUri = null; // check
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    if (data != null && data.getClipData() != null) { // multi select file
                        listPathString = AttachFile.getClipData(data.getClipData());

                        if (listPathString != null) {
                            for (int i = 0; i < listPathString.size(); i++) {
                                if (listPathString.get(i) != null) {
                                    listPathString.set(i, getFilePathFromUri(Uri.fromFile(new File(listPathString.get(i)))));
                                }
                            }
                        }
                    }
                }

                if (listPathString == null || listPathString.size() < 1) {
                    listPathString = new ArrayList<>();

                    if (data.getData() != null) {
                        listPathString.add(getFilePathFromUri(data.getData()));
                    }
                }
            }
            latestRequestCode = requestCode;

            /**
             * compress video if BuildVersion is bigger that 18
             */
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (requestCode == request_code_VIDEO_CAPTURED) {
                    if (sharedPreferences.getInt(SHP_SETTING.KEY_TRIM, 1) == 1) {
                        Intent intent = new Intent(G.fragmentActivity, ActivityTrimVideo.class);
                        intent.putExtra("PATH", listPathString.get(0));
                        startActivityForResult(intent, AttachFile.request_code_trim_video);
                        return;
                    } else if (sharedPreferences.getInt(SHP_SETTING.KEY_COMPRESS, 1) == 1) {

                        File mediaStorageDir = new File(G.DIR_VIDEOS);
                        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "video_" + HelperString.getRandomFileName(3) + ".mp4");
                        listPathString = new ArrayList<>();

                        Uri uri = Uri.fromFile(new File(AttachFile.videoPath));
                        File tempFile = com.lalongooo.videocompressor.file.FileUtils.saveTempFile(G.DIR_TEMP, HelperString.getRandomFileName(5) + ".mp4", G.fragmentActivity, uri);
                        mainVideoPath = tempFile.getPath();
                        //                        String savePathVideoCompress = Environment.getExternalStorageDirectory() + File.separator + com.lalongooo.videocompressor.Config.VIDEO_COMPRESSOR_APPLICATION_DIR_NAME + com.lalongooo.videocompressor.Config.VIDEO_COMPRESSOR_COMPRESSED_VIDEOS_DIR + "VIDEO_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".mp4";
                        //                        String savePathVideoCompress = getCacheDir() + File.separator + com.lalongooo.videocompressor.Config.VIDEO_COMPRESSOR_APPLICATION_DIR_NAME + com.lalongooo.videocompressor.Config.VIDEO_COMPRESSOR_COMPRESSED_VIDEOS_DIR + "/VIDEO_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".mp4";
                        String savePathVideoCompress = G.DIR_TEMP + "/VIDEO_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".mp4";

                        listPathString.add(savePathVideoCompress);

                        new VideoCompressor().execute(tempFile.getPath(), savePathVideoCompress);
                        showDraftLayout();
                        setDraftMessage(requestCode);
                        latestRequestCode = requestCode;
                        return;
                    } else {
                        compressedPath.put(listPathString.get(0), true);
                    }
                }

                if (requestCode == AttachFile.request_code_trim_video) {
                    latestRequestCode = request_code_VIDEO_CAPTURED;
                    showDraftLayout();
                    setDraftMessage(request_code_VIDEO_CAPTURED);
                    if ((sharedPreferences.getInt(SHP_SETTING.KEY_COMPRESS, 1) == 1)) {
                        File mediaStorageDir = new File(G.DIR_VIDEOS);
                        listPathString = new ArrayList<>();

                        //                        String savePathVideoCompress = Environment.getExternalStorageDirectory() + File.separator + com.lalongooo.videocompressor.Config.VIDEO_COMPRESSOR_APPLICATION_DIR_NAME + com.lalongooo.videocompressor.Config.VIDEO_COMPRESSOR_COMPRESSED_VIDEOS_DIR + "VIDEO_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".mp4";
                        //                        String savePathVideoCompress = getCacheDir() + File.separator + com.lalongooo.videocompressor.Config.VIDEO_COMPRESSOR_APPLICATION_DIR_NAME + com.lalongooo.videocompressor.Config.VIDEO_COMPRESSOR_COMPRESSED_VIDEOS_DIR + "VIDEO_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".mp4";
                        String savePathVideoCompress = G.DIR_TEMP + "/VIDEO_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".mp4";

                        listPathString.add(savePathVideoCompress);
                        mainVideoPath = data.getData().getPath();
                        new VideoCompressor().execute(data.getData().getPath(), savePathVideoCompress);
                    } else {
                        compressedPath.put(data.getData().getPath(), true);
                    }
                    return;
                }
            }

            if (listPathString.size() == 1) {
                /**
                 * compress video if BuildVersion is bigger that 18
                 */
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (requestCode == AttachFile.requestOpenGalleryForVideoMultipleSelect) {
                        boolean isGif = listPathString.get(0).toLowerCase().endsWith(".gif");
                        if (sharedPreferences.getInt(SHP_SETTING.KEY_TRIM, 1) == 1 && !isGif) {
                            Intent intent = new Intent(G.fragmentActivity, ActivityTrimVideo.class);
                            intent.putExtra("PATH", listPathString.get(0));
                            startActivityForResult(intent, AttachFile.request_code_trim_video);
                            return;
                        } else if ((sharedPreferences.getInt(SHP_SETTING.KEY_COMPRESS, 1) == 1 && !isGif)) {

                            mainVideoPath = listPathString.get(0);

                            //                            String savePathVideoCompress = Environment.getExternalStorageDirectory() + File.separator + com.lalongooo.videocompressor.Config.VIDEO_COMPRESSOR_APPLICATION_DIR_NAME + com.lalongooo.videocompressor.Config.VIDEO_COMPRESSOR_COMPRESSED_VIDEOS_DIR + "VIDEO_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".mp4";
                            String savePathVideoCompress = G.DIR_TEMP + "/VIDEO_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".mp4";

                            listPathString.set(0, savePathVideoCompress);

                            new VideoCompressor().execute(mainVideoPath, savePathVideoCompress);

                            showDraftLayout();
                            setDraftMessage(requestCode);
                        } else {
                            compressedPath.put(listPathString.get(0), true);
                            showDraftLayout();
                            setDraftMessage(requestCode);
                        }
                    }

                } else {
                    /**
                     * set compressed true for use this path
                     */
                    compressedPath.put(listPathString.get(0), true);

                    showDraftLayout();
                    setDraftMessage(requestCode);
                }
            } else if (listPathString.size() > 1) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        for (final String path : listPathString) {
                            /**
                             * set compressed true for use this path
                             */
                            compressedPath.put(path, true);

                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (requestCode == AttachFile.requestOpenGalleryForImageMultipleSelect && !path.toLowerCase().endsWith(".gif")) {
                                        String localPathNew = attachFile.saveGalleryPicToLocal(path);
                                        sendMessage(requestCode, localPathNew);
                                    } else {
                                        sendMessage(requestCode, path);
                                    }
                                }
                            });
                        }
                    }
                }).start();
            }

            if (listPathString.size() == 1 && listPathString.get(0) != null) {

                if (sharedPreferences.getInt(SHP_SETTING.KEY_CROP, 1) == 1) {

                    if (requestCode == AttachFile.requestOpenGalleryForImageMultipleSelect) {
                        if (!listPathString.get(0).toLowerCase().endsWith(".gif")) {

                            if (FragmentEditImage.itemGalleryList == null) {
                                FragmentEditImage.itemGalleryList = new ArrayList<>();
                            }

                            FragmentEditImage.itemGalleryList.clear();
                            FragmentEditImage.textImageList.clear();
                            Uri uri = Uri.parse(listPathString.get(0));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                FragmentEditImage.insertItemList(AttachFile.getFilePathFromUriAndCheckForAndroid7(uri, HelperGetDataFromOtherApp.FileType.image), true);
                            } else {
                                FragmentEditImage.insertItemList(uri.toString(), true);
                            }
                            if (getActivity() != null) {
                                new HelperFragment(getActivity().getSupportFragmentManager(), FragmentEditImage.newInstance(null, true, false, 0)).setReplace(false).load();
                            }
                            hideProgress();
                        } else {
                            //# get gif there

                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (prgWaiting != null) {
                                        try {

                                            sendMessage(requestCode, listPathString.get(0));
                                            prgWaiting.setVisibility(View.GONE);
                                            visibilityTextEmptyMessages();

                                        } catch (Exception e) {
                                        }

                                    }
                                }
                            });
                        }
                    } else if (requestCode == AttachFile.request_code_TAKE_PICTURE) {

                        if (FragmentEditImage.itemGalleryList == null) {
                            FragmentEditImage.itemGalleryList = new ArrayList<>();
                        }

                        FragmentEditImage.itemGalleryList.clear();
                        FragmentEditImage.textImageList.clear();
                        ImageHelper.correctRotateImage(listPathString.get(0), true);
                        FragmentEditImage.insertItemList(listPathString.get(0), true);
                        if (getActivity() != null) {
                            new HelperFragment(getActivity().getSupportFragmentManager(), FragmentEditImage.newInstance(null, true, false, 0)).setReplace(false).load();
                        }
                        hideProgress();
                    } else {
                        showDraftLayout();
                        setDraftMessage(requestCode);
                    }
                } else {

                    if (requestCode == AttachFile.request_code_TAKE_PICTURE) {

                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ImageHelper.correctRotateImage(listPathString.get(0), true);
                                showDraftLayout();
                                setDraftMessage(requestCode);
                            }
                        });
                        thread.start();
                    } else if (requestCode == AttachFile.requestOpenGalleryForImageMultipleSelect && !listPathString.get(0).toLowerCase().endsWith(".gif")) {
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                listPathString.set(0, attachFile.saveGalleryPicToLocal(listPathString.get(0)));
                                showDraftLayout();
                                setDraftMessage(requestCode);
                            }
                        });
                        thread.start();
                    } else {
                        showDraftLayout();
                        setDraftMessage(requestCode);

                    }
                }
            }
        }
    }

    /**
     * set just important item to view in onCreate and load another objects in onResume
     * actions : set app color, load avatar, set background, set title, set status chat or member for group or channel
     */
    private void startPageFastInitialize() {
        Bundle extras = getArguments();
        if (extras != null) {
            mRoomId = extras.getLong("RoomId");
            isGoingFromUserLink = extras.getBoolean("GoingFromUserLink");
            isNotJoin = extras.getBoolean("ISNotJoin");
            userName = extras.getString("UserName");
            messageId = extras.getLong("MessageId");
            chatPeerId = extras.getLong("peerId");
        }

        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLeftIcon(R.string.back_icon)
                .setRightIcons(R.string.more_icon, R.string.voice_call_icon, R.string.video_call_icon)
                .setLogoShown(false)
                .setChatRoom(true)
                .setPlayerEnable(true)
                .setListener(this);

        layoutToolbar = rootView.findViewById(R.id.ac_layout_toolbar);
        layoutToolbar.addView(mHelperToolbar.getView());


        attachFile = new AttachFile(G.fragmentActivity);
        backGroundSeenList.clear();

        //+Realm realm = Realm.getDefaultInstance();

        RealmRoom realmRoom = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
        pageSettings();

        // avi = (AVLoadingIndicatorView)  rootView.findViewById(R.id.avi);
        txtName = mHelperToolbar.getTextViewChatUserName();
        txtLastSeen = mHelperToolbar.getTextViewChatSeenStatus();
        imvUserPicture = mHelperToolbar.getUserAvatarChat();
        txtVerifyRoomIcon = mHelperToolbar.getChatVerify();
        txtVerifyRoomIcon.setVisibility(View.GONE);

        //set layout direction to views

        //todo : set gravity right for arabic and persian
        if (G.selectedLanguage.equals("en")) {
            txtName.setGravity(Gravity.LEFT);
            txtLastSeen.setGravity(Gravity.LEFT);
        } else {
            txtName.setGravity(Gravity.LEFT);
            txtLastSeen.setGravity(Gravity.LEFT);
        }

        /**
         * need this info for load avatar
         */
        if (realmRoom != null) {
            chatType = realmRoom.getType();
            if (chatType == CHAT) {
                chatPeerId = realmRoom.getChatRoom().getPeerId();
                RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(getRealmChat(), chatPeerId);
                if (realmRegisteredInfo != null) {
                    title = realmRegisteredInfo.getDisplayName();
                    lastSeen = realmRegisteredInfo.getLastSeen();
                    userStatus = realmRegisteredInfo.getStatus();
                    isBot = realmRegisteredInfo.isBot();

                    if (isBot) {

                        if (getMessagesCount() == 0) {
                            layoutMute.setVisibility(View.VISIBLE);
                            txtChannelMute.setText(R.string.start);

                            View layoutAttach = rootView.findViewById(R.id.layout_attach_file);
                            layoutAttach.setVisibility(View.GONE);

                            layoutMute.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (!isChatReadOnly) {
                                        edtChat.setText("/Start");
                                        imvSendButton.performClick();
                                    }
                                }
                            });
                            isShowStartButton = true;
                        }

                    }

                    if (realmRegisteredInfo.isVerified()) {
                        txtVerifyRoomIcon.setVisibility(View.VISIBLE);
                    }
                } else {
                    /**
                     * when userStatus isn't EXACTLY lastSeen time not used so don't need
                     * this time and also this time not exist in room info
                     */
                    title = realmRoom.getTitle();
                    userStatus = G.fragmentActivity.getResources().getString(R.string.last_seen_recently);
                }
            } else {
                mRoomId = realmRoom.getId();
                title = realmRoom.getTitle();
                if (chatType == GROUP) {
                    groupParticipantsCountLabel = realmRoom.getGroupRoom().getParticipantsCountLabel();
                    isPublicGroup = !realmRoom.getGroupRoom().isPrivate();
                } else {
                    groupParticipantsCountLabel = realmRoom.getChannelRoom().getParticipantsCountLabel();
                    showVoteChannel = realmRoom.getChannelRoom().isReactionStatus();
                    if (realmRoom.getChannelRoom().isVerified()) {
                        txtVerifyRoomIcon.setVisibility(View.VISIBLE);
                    }

                }
            }

            if (chatType == CHAT) {
                setUserStatus(userStatus, lastSeen);
            } else if ((chatType == GROUP) || (chatType == CHANNEL)) {
                if (groupParticipantsCountLabel != null) {

                    if (HelperString.isNumeric(groupParticipantsCountLabel) && Integer.parseInt(groupParticipantsCountLabel) == 1) {
                        txtLastSeen.setText(groupParticipantsCountLabel + " " + G.fragmentActivity.getResources().getString(R.string.one_member_chat));
                    } else {
                        txtLastSeen.setText(groupParticipantsCountLabel + " " + G.fragmentActivity.getResources().getString(R.string.member_chat));
                    }
                    // avi.setVisibility(View.GONE);

                }
            }
        } else if (chatPeerId != 0) {
            /**
             * when user start new chat this block will be called
             */
            chatType = CHAT;
            RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(getRealmChat(), chatPeerId);
            title = realmRegisteredInfo.getDisplayName();
            lastSeen = realmRegisteredInfo.getLastSeen();
            userStatus = realmRegisteredInfo.getStatus();
            setUserStatus(userStatus, lastSeen);
        }

        if (title != null) {
            txtName.setText(title);
        }
        /**
         * change english number to persian number
         */
        if (HelperCalander.isPersianUnicode) {
            txtName.setText(txtName.getText().toString());
            txtLastSeen.setText(convertToUnicodeFarsiNumber(txtLastSeen.getText().toString()));
        }

        /**
         * hint: don't check isCloudRoom with (( RealmRoom.isCloudRoom(mRoomId, realm); ))
         * because in first time room not exist in RealmRoom and value is false always.
         * so just need to check this value with chatPeerId
         */
        if (chatPeerId == G.userId) {
            isCloudRoom = true;
        }
        //+realm.close();

        viewAttachFile = rootView.findViewById(R.id.layout_attach_file);
        iconMute = mHelperToolbar.getChatMute();
        iconMute.setVisibility(realmRoom.getMute() ? View.VISIBLE : View.GONE);
        isMuteNotification = realmRoom.getMute();
        isChatReadOnly = realmRoom.getReadOnly();
        //gone video , voice button call then if status was ok visible them
        mHelperToolbar.getSecondRightButton().setVisibility(View.GONE);
        mHelperToolbar.getThirdRightButton().setVisibility(View.GONE);

        if (isChatReadOnly) {
            viewAttachFile.setVisibility(View.GONE);
            (rootView.findViewById(R.id.chl_recycler_view_chat)).setPadding(0, 0, 0, 0);
        } else if (chatType == CHAT && G.userId != chatPeerId && !isBot) {
            // gone or visible view call
            RealmCallConfig callConfig = getRealmChat().where(RealmCallConfig.class).findFirst();
            if (callConfig != null) {
                if (callConfig.isVoice_calling()) {
                    mHelperToolbar.getSecondRightButton().setVisibility(View.VISIBLE);

                } else {
                    mHelperToolbar.getSecondRightButton().setVisibility(View.GONE);
                }

                if (callConfig.isVideo_calling()) {
                    mHelperToolbar.getThirdRightButton().setVisibility(View.VISIBLE);

                } else {
                    mHelperToolbar.getThirdRightButton().setVisibility(View.GONE);
                }

            } else {
                new RequestSignalingGetConfiguration().signalingGetConfiguration();
            }
        }
        checkToolbarNameSize();
        manageExtraLayout();
    }

    private void goneCallButtons(){
        mHelperToolbar.getThirdRightButton().setVisibility(View.GONE);
        mHelperToolbar.getSecondRightButton().setVisibility(View.GONE);
    }

    private long getMessagesCount() {
        return getRealmChat().where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, mRoomId).findAll().size();
    }

    private void initDrBot() {
        llScrollNavigate = rootView.findViewById(R.id.ac_ll_scrool_navigate);
        FrameLayout.LayoutParams param = (FrameLayout.LayoutParams) llScrollNavigate.getLayoutParams();
        param.bottomMargin = (int) getResources().getDimension(R.dimen.dp60);

        rcvDrBot = rootView.findViewById(R.id.rcvDrBot);
        rcvDrBot.setLayoutManager(new LinearLayoutManager(G.context, LinearLayoutManager.HORIZONTAL, false));
        rcvDrBot.setItemViewCacheSize(200);

        new RequestClientGetFavoriteMenu().clientGetFavoriteMenu(new OnGetFavoriteMenu() {
            @Override
            public void onGetList(List<ProtoGlobal.Favorite> favoriteList) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        AdapterDrBot adapterDrBot = new AdapterDrBot(favoriteList, new AdapterDrBot.OnHandleDrBot() {
                            @Override
                            public void goToRoomBot(ProtoGlobal.Favorite favorite) {
                                G.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        HelperUrl.checkUsernameAndGoToRoom(getActivity(), favorite.getValue().replace("@", ""), HelperUrl.ChatEntry.chat);
                                    }
                                });
                            }

                            @Override
                            public void sendMessageBOt(ProtoGlobal.Favorite favorite) {
                                G.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!isChatReadOnly) {
                                            if (favorite.getValue().equals("$financial")) {
                                                if (getActivity() != null) {
                                                    new HelperFragment(getActivity().getSupportFragmentManager(), FragmentPayment.newInstance()).setReplace(false).load();
                                                    return;
                                                }
                                            }
                                            edtChat.setText(favorite.getValue());
                                            imvSendButton.performClick();
                                            scrollToEnd();
                                        }
                                    }
                                });
                            }
                        });
                        rcvDrBot.setAdapter(adapterDrBot);
                        rcvDrBot.setVisibility(View.VISIBLE);
                    }
                });
            }
        });


    }

    private void checkConnection(String action) {
        if (action != null && !isBot) {
            txtLastSeen.setText(action);
        } else {

            if (chatType == CHAT) {
                if (isCloudRoom) {
                    txtLastSeen.setText(G.fragmentActivity.getResources().getString(R.string.chat_with_yourself));
                    goneCallButtons();
                } else if (isBot) {
                    txtLastSeen.setText(G.fragmentActivity.getResources().getString(R.string.bot));
                } else {
                    if (userStatus != null) {
                        if (userStatus.equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                            txtLastSeen.setText(LastSeenTimeUtil.computeTime(chatPeerId, userTime, true, false));
                        } else {
                            txtLastSeen.setText(userStatus);
                        }
                    }
                }
            } else if (chatType == GROUP) {
                if (groupParticipantsCountLabel != null && HelperString.isNumeric(groupParticipantsCountLabel) && Integer.parseInt(groupParticipantsCountLabel) == 1) {
                    txtLastSeen.setText(groupParticipantsCountLabel + " " + G.fragmentActivity.getResources().getString(R.string.one_member_chat));
                } else {
                    txtLastSeen.setText(groupParticipantsCountLabel + " " + G.fragmentActivity.getResources().getString(R.string.member_chat));
                }

            } else if (chatType == CHANNEL) {
                if (groupParticipantsCountLabel != null && HelperString.isNumeric(groupParticipantsCountLabel) && Integer.parseInt(groupParticipantsCountLabel) == 1) {
                    txtLastSeen.setText(groupParticipantsCountLabel + " " + G.fragmentActivity.getResources().getString(R.string.one_member_chat));
                } else {
                    txtLastSeen.setText(groupParticipantsCountLabel + " " + G.fragmentActivity.getResources().getString(R.string.member_chat));
                }

            }
        }

        if (HelperCalander.isPersianUnicode) {
            txtLastSeen.setText(convertToUnicodeFarsiNumber(txtLastSeen.getText().toString()));
        }

    }

    private void setConnectionText(final ConnectionState connectionState) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                G.connectionState = connectionState;
                if (connectionState == ConnectionState.WAITING_FOR_NETWORK) {
                    checkConnection(G.context.getResources().getString(R.string.waiting_for_network));
                } else if (connectionState == ConnectionState.CONNECTING) {
                    checkConnection(G.context.getResources().getString(R.string.connecting));
                } else if (connectionState == ConnectionState.UPDATING) {
                    checkConnection(null);
                } else if (connectionState == ConnectionState.IGAP) {
                    checkConnection(null);
                }
            }
        });
    }

    private void updateUnmanagedRoom() {
        unmanagedRoom = getRealmChat().copyFromRealm(managedRoom);
    }

    private void initMain() {
        HelperGetMessageState.clearMessageViews();

        /**
         * define views
         */
        initPinedMessage();

        viewMicRecorder = rootView.findViewById(R.id.layout_mic_recorde);
        prgWaiting = rootView.findViewById(R.id.chl_prgWaiting);
        AppUtils.setProgresColler(prgWaiting);
        voiceRecord = new VoiceRecord(G.fragmentActivity, viewMicRecorder, viewAttachFile, this);

        prgWaiting.setVisibility(View.VISIBLE);

        txtEmptyMessages = rootView.findViewById(R.id.empty_messages);

        if (isBot) {
            txtEmptyMessages.setText(G.fragmentActivity.getResources().getString(R.string.empty_text_dr_bot));
            txtChannelMute.setText(R.string.start);
        }

        lastDateCalendar.clear();

        locationManager = (LocationManager) G.fragmentActivity.getSystemService(LOCATION_SERVICE);

        /**
         * Hint: don't need to get info here. currently do this action in {{@link #startPageFastInitialize()}}
         Bundle extras = getArguments();
         if (extras != null) {
         mRoomId = extras.getLong("RoomId");
         isGoingFromUserLink = extras.getBoolean("GoingFromUserLink");
         isNotJoin = extras.getBoolean("ISNotJoin");
         userName = extras.getString("UserName");
         messageId = extras.getLong("MessageId");
         }
         */

        /**
         * get userId . use in chat set action.
         */

        //+Realm realm = Realm.getDefaultInstance();

        RealmUserInfo realmUserInfo = getRealmChat().where(RealmUserInfo.class).findFirst();
        if (realmUserInfo == null) {
            //finish();
            finishChat();
            return;
        }
        userId = realmUserInfo.getUserId();

            managedRoom = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
            if (managedRoom != null) { // room exist

                unmanagedRoom = getRealmChat().copyFromRealm(managedRoom);
                title = managedRoom.getTitle();
                initialize = managedRoom.getInitials();
                color = managedRoom.getColor();
                isChatReadOnly = managedRoom.getReadOnly();
                unreadCount = managedRoom.getUnreadCount();
                firstUnreadMessage = managedRoom.getFirstUnreadMessage();
                savedScrollMessageId = managedRoom.getLastScrollPositionMessageId();
                firstVisiblePositionOffset = managedRoom.getLastScrollPositionOffset();

                if (messageId != 0) {
                    savedScrollMessageId = messageId;
                    firstVisiblePositionOffset = 0;
                }

                if (chatType == CHAT) {

                    RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(getRealmChat(), chatPeerId);
                    if (realmRegisteredInfo != null) {
                        initialize = realmRegisteredInfo.getInitials();
                        color = realmRegisteredInfo.getColor();
                        phoneNumber = realmRegisteredInfo.getPhoneNumber();

                        if (realmRegisteredInfo.getId() == Config.drIgapPeerId) {
                            // if (realmRegisteredInfo.getUsername().equalsIgnoreCase("")) {
                            initDrBot();
                        }

                    } else {
                        title = managedRoom.getTitle();
                        initialize = managedRoom.getInitials();
                        color = managedRoom.getColor();
                        userStatus = G.fragmentActivity.getResources().getString(R.string.last_seen_recently);
                    }
                } else if (chatType == GROUP) {
                    RealmGroupRoom realmGroupRoom = managedRoom.getGroupRoom();
                    groupRole = realmGroupRoom.getRole();
                    groupParticipantsCountLabel = realmGroupRoom.getParticipantsCountLabel();
                } else if (chatType == CHANNEL) {
                    RealmChannelRoom realmChannelRoom = managedRoom.getChannelRoom();
                    channelRole = realmChannelRoom.getRole();
                    channelParticipantsCountLabel = realmChannelRoom.getParticipantsCountLabel();
                }
        } else {
            //chatPeerId = extras.getLong("peerId");
            chatType = CHAT;
            RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(getRealmChat(), chatPeerId);
            if (realmRegisteredInfo != null) {
                title = realmRegisteredInfo.getDisplayName();
                initialize = realmRegisteredInfo.getInitials();
                color = realmRegisteredInfo.getColor();
                lastSeen = realmRegisteredInfo.getLastSeen();
                userStatus = realmRegisteredInfo.getStatus();
            }
        }

        if (G.isDarkTheme)
            rootView.findViewById(R.id.ac_ll_selected_and_pin).setBackground(context.getResources().getDrawable(R.drawable.shape_multi_select_bg_dark));

        initComponent();
        initAppbarSelected();
        getDraft();
        getUserInfo();
        insertShearedData();

        RealmRoomMessage rm = null;
        RealmResults<RealmRoomMessage> result = getRealmChat().where(RealmRoomMessage.class).
                equalTo(RealmRoomMessageFields.ROOM_ID, mRoomId).findAll();
        if (result.size() > 0) {
            rm = result.last();
            if (rm != null && rm.getMessage() != null) {
                if (rm.getRealmAdditional() != null && (rm.getRealmAdditional().getAdditionalType() == Additional.WEB_VIEW.getAdditional())) {
                    String additionalData = rm.getRealmAdditional().getAdditionalData();
                    if (!additionalData.isEmpty()) openWebViewForSpecialUrlChat(additionalData);
                }
            }
        }


        FragmentShearedMedia.goToPositionFromShardMedia = new FragmentShearedMedia.GoToPositionFromShardMedia() {
            @Override
            public void goToPosition(Long messageId) {

                if (messageId != 0) {
                    savedScrollMessageId = messageId;
                    firstVisiblePositionOffset = 0;

                    if (goToPositionWithAnimation(savedScrollMessageId, 2000)) {
                        savedScrollMessageId = 0;
                    } else {
                        RealmRoomMessage rm = getRealmChat().where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId).findFirst();
                        rm = RealmRoomMessage.getFinalMessage(rm);
                        if (rm != null) {
                            resetMessagingValue();
                            savedScrollMessageId = FragmentChat.messageId = messageId;
                            firstVisiblePositionOffset = 0;
                            getMessages();
                        }
                    }
                }
            }
        };

        sendChatTracker();
    }

    private void sendChatTracker() {
        if (chatType == CHAT) {
            if (isBot) {
                HelperTracker.sendTracker(HelperTracker.TRACKER_BOT_VIEW);
            } else {
                HelperTracker.sendTracker(HelperTracker.TRACKER_CHAT_VIEW);
            }
        } else if (chatType == GROUP) {
            HelperTracker.sendTracker(HelperTracker.TRACKER_GROUP_VIEW);
        } else if (chatType == CHANNEL) {
            HelperTracker.sendTracker(HelperTracker.TRACKER_CHANNEL_VIEW);
        }
    }

    /**
     * show join/mute layout if needed
     */
    private void manageExtraLayout() {
        if (isNotJoin) {
            final LinearLayout layoutJoin = rootView.findViewById(R.id.ac_ll_join);

            layoutJoin.setBackgroundColor(Color.parseColor(G.appBarColor));
            layoutJoin.setVisibility(View.VISIBLE);
            layoutMute.setVisibility(View.GONE);
            viewAttachFile.setVisibility(View.GONE);

            layoutJoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HelperUrl.showIndeterminateProgressDialog(getActivity());
                    G.onClientJoinByUsername = new OnClientJoinByUsername() {
                        @Override
                        public void onClientJoinByUsernameResponse() {

                            isNotJoin = false;
                            HelperUrl.closeDialogWaiting();
                            RealmRoom.joinRoom(mRoomId);

                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    layoutJoin.setVisibility(View.GONE);
                                    if (chatType == CHANNEL) {
                                        layoutMute.setVisibility(View.VISIBLE);
                                        initLayoutChannelFooter();
                                    }
                                    rootView.findViewById(ac_ll_parent).invalidate();


                                    if (chatType == GROUP) {
                                        viewAttachFile.setVisibility(View.VISIBLE);
                                        isChatReadOnly = false;
                                    }

                                }
                            });
                        }

                        @Override
                        public void onError(int majorCode, int minorCode) {
                            HelperUrl.dialogWaiting.dismiss();
                        }
                    };

                    /**
                     * if user joined to this room set lastMessage for that
                     */
                    RealmRoom.setLastMessage(mRoomId);
                    new RequestClientJoinByUsername().clientJoinByUsername(userName);
                }
            });
        }
    }

    private void initPinedMessage() {
        final long pinMessageId = RealmRoom.hasPinedMessage(mRoomId);
        pinedMessageLayout = rootView.findViewById(R.id.ac_ll_strip_Pin);
        if (pinMessageId > 0) {
            RealmRoomMessage realmRoomMessage = getRealmChat().where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, pinMessageId).findFirst();
            if (realmRoomMessage != null && realmRoomMessage.isValid() && !realmRoomMessage.isDeleted()) {
                realmRoomMessage = RealmRoomMessage.getFinalMessage(realmRoomMessage);
                isPinAvailable = true ;
                pinedMessageLayout.setVisibility(View.VISIBLE);
                TextView txtPinMessage = rootView.findViewById(R.id.pl_txt_pined_Message);
                MaterialDesignTextView iconPinClose = rootView.findViewById(R.id.pl_btn_close);

                String text = realmRoomMessage.getMessage();
                if (text == null || text.length() == 0) {
                    text = AppUtils.conversionMessageType(realmRoomMessage.getMessageType());
                }
                txtPinMessage.setText(text);
                iconPinClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (channelRole == ChannelChatRole.MEMBER || groupRole == GroupChatRole.MEMBER || isNotJoin) {
                            RealmRoom.updatePinedMessageDeleted(mRoomId, false);
                            pinedMessageLayout.setVisibility(View.GONE);
                        } else {
                            sendRequestPinMessage(0);
                        }
                        isPinAvailable = false ;
                    }
                });

                pinedMessageLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!goToPositionWithAnimation(pinMessageId, 1000)) {

                            RealmRoomMessage rm = getRealmChat().where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, pinMessageId).findFirst();
                            rm = RealmRoomMessage.getFinalMessage(rm);
                            if (rm != null) {
                                resetMessagingValue();
                                savedScrollMessageId = pinMessageId;
                                firstVisiblePositionOffset = 0;
                                getMessages();
                            } else {
                                new RequestClientGetRoomMessage().clientGetRoomMessage(mRoomId, pinMessageId, new OnClientGetRoomMessage() {
                                    @Override
                                    public void onClientGetRoomMessageResponse(ProtoGlobal.RoomMessage message) {
                                        G.handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                resetMessagingValue();
                                                savedScrollMessageId = pinMessageId;
                                                firstVisiblePositionOffset = 0;
                                                setGapAndGetMessage(pinMessageId);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onError(int majorCode, int minorCode) {

                                    }
                                });
                            }
                        }
                    }
                });

            } else {
                pinedMessageLayout.setVisibility(View.GONE);
            }
        } else {
            pinedMessageLayout.setVisibility(View.GONE);
        }
    }

    private void sendRequestPinMessage(final long id) {
        if (id == 0) {
            new MaterialDialog.Builder(G.fragmentActivity).title(R.string.igap)
                    .content(String.format(context.getString(R.string.pin_messages_content), context.getString(R.string.unpin)))
                    .neutralText(R.string.all_member)
                    .onNeutral(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            if (chatType == CHANNEL) {
                                new RequestChannelPinMessage().channelPinMessage(mRoomId, id);
                            } else {
                                new RequestGroupPinMessage().groupPinMessage(mRoomId, id);
                            }
                        }
                    })
                    .positiveText(R.string.this_page)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            if (pinedMessageLayout != null) {
                                pinedMessageLayout.setVisibility(View.GONE);
                            }
                            RealmRoom.updatePinedMessageDeleted(mRoomId, false);
                        }
                    }).negativeText(R.string.cancel).show();
        } else {
            new MaterialDialog.Builder(G.fragmentActivity).title(R.string.igap)
                    .content(String.format(context.getString(R.string.pin_messages_content), context.getString(R.string.PIN)))
                    .positiveText(R.string.ok).
                    onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            if (chatType == CHANNEL) {
                                new RequestChannelPinMessage().channelPinMessage(mRoomId, id);
                            } else {
                                new RequestGroupPinMessage().groupPinMessage(mRoomId, id);
                            }
                        }
                    }).negativeText(R.string.cancel).show();
        }

    }

    private boolean goToPositionWithAnimation(long messageId, int time) {

        int position = mAdapter.findPositionByMessageId(messageId);
        if (position != -1) {
            LinearLayoutManager linearLayout = (LinearLayoutManager) recyclerView.getLayoutManager();
            linearLayout.scrollToPositionWithOffset(position, 0);

            mAdapter.getItem(position).mMessage.isSelected = true;
            mAdapter.notifyItemChanged(position);

            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        int position = mAdapter.findPositionByMessageId(messageId);
                        if (position != -1) {
                            mAdapter.getItem(position).mMessage.isSelected = false;
                            mAdapter.notifyItemChanged(position);
                        }
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                    }
                }
            }, time);

            return true;
        }
        return false;
    }

    private void registerListener() {

        G.dispatchTochEventChat = new IDispatchTochEvent() {
            @Override
            public void getToch(MotionEvent event) {
                if (voiceRecord != null) {
                    voiceRecord.dispatchTouchEvent(event);
                }
            }
        };

        G.onBackPressedChat = new IOnBackPressed() {
            @Override
            public boolean onBack() {
                return onBackPressed();
            }
        };

        G.iSendPositionChat = new ISendPosition() {
            @Override
            public void send(Double latitude, Double longitude, String imagePath) {
            /*    if (isBot) {
                    if (G.locationListenerResponse != null)
                        G.locationListenerResponse.setLocationResponse(latitude, longitude);
                } else*/
                sendPosition(latitude, longitude, imagePath);
            }
        };
    }

    private void unRegisterListener() {

        G.dispatchTochEventChat = null;
        G.onBackPressedChat = null;
        G.iSendPositionChat = null;
    }

    public boolean onBackPressed() {
        boolean stopSuperPress = true;
        try {

            if (webViewChatPage != null) {

                closeWebViewForSpecialUrlChat(false);
                return stopSuperPress;
            }

            FragmentShowImage fragment = (FragmentShowImage) G.fragmentActivity.getSupportFragmentManager().findFragmentByTag(FragmentShowImage.class.getName());
            if (fragment != null) {
                removeFromBaseFragment(fragment);
            } else if (mAdapter != null && mAdapter.getSelections().size() > 0) {
                mAdapter.deselect();
            } else if (emojiPopup != null && emojiPopup.isShowing()) {
                emojiPopup.dismiss();
            }else if (ll_Search != null && ll_Search.isShown()){
                goneSearchBox(edtSearchMessage);
            }else if (isEditMessage){
                removeEditedMessage();
            }else if(ll_navigateHash != null && btnHashLayoutClose != null && ll_navigateHash.isShown()){
                btnHashLayoutClose.performClick();
            }else {
                stopSuperPress = false;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return stopSuperPress;
    }

    private void closeWebViewForSpecialUrlChat(boolean isStopBot) {

        if (webViewChatPage != null) {
            if (webViewChatPage.canGoBack() && (!webViewChatPage.getUrl().trim().toLowerCase().equals(urlWebViewForSpecialUrlChat.trim().toLowerCase())) && !isStopBot) {
                webViewChatPage.goBack();
            } else {
                makeWebViewGone();
                //        if (!isStopBot) popBackStackFragment();
            }
        }
    }

    private void makeWebViewGone() {
        recyclerView.setVisibility(View.VISIBLE);
        viewAttachFile.setVisibility(View.VISIBLE);
        rootWebView.setVisibility(View.GONE);
        webViewChatPage = null;
    }

    /**
     * get settings changeState and change view
     */
    private void pageSettings() {
        /**
         * get sendByEnter action from setting value
         */
        sharedPreferences = G.fragmentActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        sendByEnter = sharedPreferences.getInt(SHP_SETTING.KEY_SEND_BT_ENTER, 0) == 1;

        soundInChatPlay = sharedPreferences.getInt(SHP_SETTING.KEY_PLAY_SOUND_IN_CHAT, 1) == 1;

        if (soundInChatPlay)
            soundInChatInit();

        /**
         * set background
         */

        recyclerView = rootView.findViewById(R.id.chl_recycler_view_chat);

        String backGroundPath = sharedPreferences.getString(SHP_SETTING.KEY_PATH_CHAT_BACKGROUND, "");
        imgBackGround = rootView.findViewById(R.id.chl_img_view_chat);
        if (backGroundPath.length() > 0) {
            File f = new File(backGroundPath);
            if (f.exists()) {
                try {
                    Drawable d = Drawable.createFromPath(f.getAbsolutePath());
                    imgBackGround.setImageDrawable(d);
                } catch (OutOfMemoryError e) {
                    ActivityManager activityManager = (ActivityManager) G.context.getSystemService(ACTIVITY_SERVICE);
                    ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
                    activityManager.getMemoryInfo(memoryInfo);
                    Crashlytics.logException(new Exception("FragmentChat -> Device Name : " + Build.BRAND + " || memoryInfo.availMem : " + memoryInfo.availMem + " || memoryInfo.totalMem : " + memoryInfo.totalMem + " || memoryInfo.lowMemory : " + memoryInfo.lowMemory));
                }
            } else {
                try {
                    imgBackGround.setBackgroundColor(Color.parseColor(backGroundPath));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else{
            if (G.themeColor == Theme.DARK) {
                imgBackGround.setImageResource(R.drawable.chat_bg_dark);
            }
            else{
                //todo: fixed load default background in light mode
            }
        }

    }

    /**
     * initialize some callbacks that used in this page
     */
    public void initCallbacks() {
        chatSendMessageUtil.setOnChatSendMessageResponseChatPage(this);
        G.chatUpdateStatusUtil.setOnChatUpdateStatusResponse(this);

        G.onChatSendMessage = new OnChatSendMessage() {
            @Override
            public void Error(int majorCode, int minorCode, final int waitTime) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (G.fragmentActivity.hasWindowFocus()) {
                                showErrorDialog(waitTime);
                            }
                        } catch (Exception e) {
                        }

                    }
                });
            }
        };

        G.onChatEditMessageResponse = new OnChatEditMessageResponse() {
            @Override
            public void onChatEditMessage(long roomId, final long messageId, long messageVersion, final String message, ProtoResponse.Response response) {
                if (mRoomId == roomId && mAdapter != null) {
                    // I'm in the room
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // update message text in adapter
                            mAdapter.updateMessageText(messageId, message);
                        }
                    });
                }
            }

            @Override
            public void onError(int majorCode, int minorCode) {

            }
        };

        G.onChatDeleteMessageResponse = new OnChatDeleteMessageResponse() {
            @Override
            public void onChatDeleteMessage(long deleteVersion, final long messageId, long roomId, ProtoResponse.Response response) {
                if (response.getId().isEmpty()) { // another account deleted this message

                    // if deleted message is for current room clear from adapter
                    if (roomId == mRoomId) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // remove deleted message from adapter
                                if (mAdapter == null) {
                                    return;
                                }

                                ArrayList list = new ArrayList();
                                list.add(messageId);
                                deleteSelectedMessageFromAdapter(list);

                            }
                        });
                    }
                }
            }

            @Override
            public void onError(int majorCode, int minorCode) {

            }
        };

        /**
         * call from ActivityGroupProfile for update group member number or clear history
         */
        onComplete = new OnComplete() {
            @Override
            public void complete(boolean result, String messageOne, String MessageTow) {
                clearHistory(parseLong(messageOne));
            }
        };

        onMusicListener = new OnComplete() {
            @Override
            public void complete(boolean result, String messageID, String beforeMessageID) {

                if (result) {
                    updateShowItemInScreen();
                } else {
                    onPlayMusic(messageID);
                }
            }
        };

        iUpdateLogItem = new IUpdateLogItem() {
            @Override
            public void onUpdate(byte[] log, long messageId) {
                if (getActivity() == null || getActivity().isFinishing())
                    return;

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mAdapter == null) {
                            return;
                        }
                        for (int i = mAdapter.getAdapterItemCount() - 1; i >= 0; i--) {

                            try {
                                AbstractMessage item = mAdapter.getAdapterItem(i);

                                if (item.mMessage != null && item.mMessage.messageID.equals(messageId + "")) {
                                    item.mMessage.logs = log;
                                    mAdapter.notifyAdapterItemChanged(i);
                                    break;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                HelperLog.setErrorLog(e);
                            }
                        }
                    }
                });
            }
        };

        /**
         * after get position from gps
         */
        complete = new OnComplete() {
            @Override
            public void complete(boolean result, final String messageOne, String MessageTow) {
                try {
                    if (getActivity() != null) {
                        String[] split = messageOne.split(",");
                        Double latitude = Double.parseDouble(split[0]);
                        Double longitude = Double.parseDouble(split[1]);
                        FragmentMap fragment = FragmentMap.getInctance(latitude, longitude, FragmentMap.Mode.sendPosition);
                        new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
                    }
                } catch (Exception e) {
                    HelperLog.setErrorLog(e);
                }
            }
        };

        G.onHelperSetAction = new OnHelperSetAction() {
            @Override
            public void onAction(ProtoGlobal.ClientAction ClientAction) {
                HelperSetAction.setActionFiles(mRoomId, messageId, ClientAction, chatType);
            }
        };

        G.onClearChatHistory = new OnClearChatHistory() {
            @Override
            public void onClearChatHistory() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.clear();
                        recyclerView.removeAllViews();

                        /**
                         * remove tag from edtChat if the message has deleted
                         */
                        if (edtChat.getTag() != null && edtChat.getTag() instanceof StructMessageInfo) {
                            edtChat.setTag(null);
                        }
                    }
                });
            }

            @Override
            public void onError(int majorCode, int minorCode) {

            }
        };

        G.onDeleteChatFinishActivity = new OnDeleteChatFinishActivity() {
            @Override
            public void onFinish() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //finish();
                        finishChat();
                    }
                });
            }
        };

        G.onUpdateUserStatusInChangePage = new OnUpdateUserStatusInChangePage() {
            @Override
            public void updateStatus(long peerId, String status, long lastSeen) {
                if (chatType == CHAT) {
                    setUserStatus(status, lastSeen);
                    new RequestUserInfo().userInfo(peerId);
                }
            }
        };
    }

    private void initComponent() {

        iconMute = mHelperToolbar.getChatMute();

        final RealmRoom realmRoom = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();

        ll_attach_text = rootView.findViewById(R.id.ac_ll_attach_text);

        txtFileNameForSend = rootView.findViewById(R.id.ac_txt_file_neme_for_sending);
        btnCancelSendingFile = rootView.findViewById(R.id.ac_btn_cancel_sending_file);
        btnCancelSendingFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isCardToCardMessage = false;
                ll_attach_text.setVisibility(View.GONE);
                edtChat.setFilters(new InputFilter[]{});
                edtChat.setText(edtChat.getText());
                edtChat.setSelection(edtChat.getText().length());

                if (edtChat.getText().length() == 0) {
                    sendButtonVisibility(false);
                }
            }
        });

        // final int screenWidth = (int) (getResources().getDisplayMetrics().widthPixels / 1.2);

        imvSmileButton = rootView.findViewById(R.id.tv_chatRoom_emoji);
        if (emojiPopup == null) {
            setUpEmojiPopup();
        }
        edtChat.requestFocus();

        edtChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmojiSHow) {

                    imvSmileButton.performClick();
                }

                if (botInit != null) botInit.close();
            }
        });


        imvAttachFileButton = rootView.findViewById(R.id.vtn_chatRoom_attach);
        layoutAttachBottom = rootView.findViewById(R.id.ll_chatRoom_send);
        imvMicButton = rootView.findViewById(R.id.btn_chatRoom_mic);


        if (isBot) {
            botInit = new BotInit(rootView, false);
            sendButtonVisibility(false);


            RealmResults<RealmRoomMessage> result;
            RealmRoomMessage rm = null;
            String lastMessage = "";
            boolean backToMenu = true;

            result = getRealmChat().where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, mRoomId).notEqualTo(RealmRoomMessageFields.AUTHOR_HASH, G.authorHash).findAll();
            if (result.size() > 0) {
                rm = result.last();
                if (rm.getMessage() != null) {
                    lastMessage = rm.getMessage();
                }
            }

            try {
                if (rm.getRealmAdditional() != null && rm.getRealmAdditional().getAdditionalType() == AdditionalType.UNDER_KEYBOARD_BUTTON) {
                    botInit.updateCommandList(false, lastMessage, getActivity(), backToMenu, rm, rm.getRoomId());
                }

            } catch (Exception e) {
            }

        }


        mAdapter = new MessagesAdapter<>(this, this, this, avatarHandler);

        mAdapter.getItemFilter().withFilterPredicate(new IItemAdapter.Predicate<AbstractMessage>() {
            @Override
            public boolean filter(AbstractMessage item, CharSequence constraint) {
                return !item.mMessage.messageText.toLowerCase().contains(constraint.toString().toLowerCase());
            }
        });

        //FragmentMain.PreCachingLayoutManager layoutManager = new FragmentMain.PreCachingLayoutManager(ActivityChat.this, 7500);
        MyLinearLayoutManager layoutManager = new MyLinearLayoutManager(G.fragmentActivity);
        layoutManager.setStackFromEnd(true);

        if (recyclerView == null) {
            recyclerView = rootView.findViewById(R.id.chl_recycler_view_chat);
        }

        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setItemViewCacheSize(20);


        if (realmRoom != null && !realmRoom.getReadOnly()) {
            ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return true;
                }

                @Override
                public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                    super.clearView(recyclerView, viewHolder);
                    try {
                        if (isRepley)
                            replay((mAdapter.getItem(viewHolder.getAdapterPosition())).mMessage , false);
                    } catch (Exception ignored) {
                    }
                    isRepley = false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                }

                @Override
                public void onChildDraw(Canvas c,
                                        RecyclerView recyclerView,
                                        RecyclerView.ViewHolder viewHolder,
                                        float dX, float dY,
                                        int actionState, boolean isCurrentlyActive) {

                    if (actionState == ACTION_STATE_SWIPE && isCurrentlyActive) {
                        setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }
                    dX = dX + ViewMaker.dpToPixel(25);
                    if (dX > 0)
                        dX = 0;

                    if (dX < -ViewMaker.dpToPixel(150)) {
                        dX = -ViewMaker.dpToPixel(150);
                    }

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }

                @Override
                public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                    return super.getSwipeThreshold(viewHolder);
                }

                @Override
                public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                    if (viewHolder instanceof NewChatItemHolder) {
                        return super.getSwipeDirs(recyclerView, viewHolder);
                    }
                    // we disable swipe with returning Zero
                    return 0;
                }

                @Override
                public int convertToAbsoluteDirection(int flags, int layoutDirection) {
                    if (swipeBack) {
                        swipeBack = false;
                        return 0;
                    }
                    return super.convertToAbsoluteDirection(flags, layoutDirection);
                }

                @Override
                public boolean isItemViewSwipeEnabled() {
                    return !FragmentChat.isInSelectionMode;
                }
            };
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
            itemTouchHelper.attachToRecyclerView(recyclerView);
        }
        /**
         * load message , use handler for load async
         */

        visibilityTextEmptyMessages();

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                visibilityTextEmptyMessages();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                visibilityTextEmptyMessages();
            }
        });

        //added run time -> counter of un read messages
        llScrollNavigate = rootView.findViewById(R.id.ac_ll_scrool_navigate);
        txtNewUnreadMessage = new BadgeView(getContext());
        txtNewUnreadMessage.getTextView().setTypeface(G.typeface_IRANSansMobile);
        txtNewUnreadMessage.getTextView().setSingleLine();
        txtNewUnreadMessage.getTextView().setFilters(new InputFilter[] { new InputFilter.LengthFilter(5) });//set max length
        txtNewUnreadMessage.setBadgeColor(G.isDarkTheme ? Color.parseColor(Theme.default_notificationColor) : Color.parseColor(G.notificationColor));
        llScrollNavigate.addView(txtNewUnreadMessage,LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT , LayoutCreator.WRAP_CONTENT , Gravity.CENTER | Gravity.TOP));

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                getMessages();
                manageForwardedMessage();
            }
        });

        MaterialDesignTextView txtNavigationLayout = rootView.findViewById(R.id.ac_txt_down_navigation);
        AndroidUtils.setBackgroundShapeColor(txtNavigationLayout, Color.parseColor(G.appBarColor));

        llScrollNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAllRequestFetchHistory();

                latestButtonClickTime = System.currentTimeMillis();
                /**
                 * have unread
                 */
                if (countNewMessage > 0 && firstUnreadMessageInChat != null) {
                    /**
                     * if unread message is exist in list set position to this item and create
                     * unread layout otherwise should clear list and load from unread again
                     */

                    firstUnreadMessage = firstUnreadMessageInChat;
                    if (!firstUnreadMessage.isValid() || firstUnreadMessage.isDeleted()) {
                        resetAndGetFromEnd();
                        return;
                    }

                    int position = mAdapter.findPositionByMessageId(firstUnreadMessage.getMessageId());
                    if (position > 0) {
                        mAdapter.add(position, new UnreadMessage(mAdapter, FragmentChat.this).setMessage(StructMessageInfo.convert(getRealmChat(), makeUnreadMessage(countNewMessage))).withIdentifier(SUID.id().get()));
                        isShowLayoutUnreadMessage = true;
                        LinearLayoutManager linearLayout = (LinearLayoutManager) recyclerView.getLayoutManager();
                        linearLayout.scrollToPositionWithOffset(position, 0);
                    } else {
                        resetMessagingValue();
                        unreadCount = countNewMessage;
                        firstUnreadMessage = firstUnreadMessageInChat;
                        getMessages();

                        if (firstUnreadMessage == null) {
                            resetAndGetFromEnd();
                            return;
                        }

                        int position1 = mAdapter.findPositionByMessageId(firstUnreadMessage.getMessageId());
                        if (position1 > 0) {
                            LinearLayoutManager linearLayout = (LinearLayoutManager) recyclerView.getLayoutManager();
                            linearLayout.scrollToPositionWithOffset(position1 - 1, 0);
                        }
                    }
                    firstUnreadMessageInChat = null;
                    countNewMessage = 0;
                    txtNewUnreadMessage.setVisibility(View.GONE);
                    txtNewUnreadMessage.getTextView().setText(countNewMessage + "");
                } else {
                    setDownBtnGone();
                    /**
                     * if addToView is true this means that all new message is in adapter
                     * and just need go to end position in list otherwise we should clear all
                     * items and reload again from bottom
                     */
                    if (!addToView) {
                        resetMessagingValue();
                        getMessages();
                    } else {
                        scrollToEnd();
                    }
                }
            }
        });


        recyclerView.setOnFlingListener(new RecyclerView.OnFlingListener() {

            @Override
            public boolean onFling(int velocityX, int velocityY) {

            /*    if (Math.abs(velocityY) > MAX_VELOCITY_Y) {
                    velocityY = MAX_VELOCITY_Y * (int) Math.signum((double)velocityY);
                    mRecyclerView.fling(velocityX, velocityY);
                    return true;
                }*/

                return false;
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = recyclerView.getLayoutManager().getChildCount();
                int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                int pastVisibleItems = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                cardFloatingTime.setVisibility(View.VISIBLE);
                long item = mAdapter.getItemByPosition(layoutManager.findFirstVisibleItemPosition());
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(item);
                if (item != 0L) {
                    txtFloatingTime.setText(TimeUtils.getChatSettingsTimeAgo(G.fragmentActivity, calendar.getTime()));
                }
                gongingHandler.removeCallbacks(gongingRunnable);
                gongingHandler.postDelayed(gongingRunnable, 1000);

                if (pastVisibleItems + visibleItemCount >= totalItemCount && !isAnimateStart) {
                    isScrollEnd = false;
                    isAnimateStart = true;
                    isAnimateStart = false;
                    llScrollNavigate.setVisibility(View.GONE);
//                    llScrollNavigate.animate()
//                            .alpha(0.0f)
//                            .translationY(llScrollNavigate.getHeight() / 2)
//                            .setDuration(200)
//                            .setListener(new AnimatorListenerAdapter() {
//                                @Override
//                                public void onAnimationEnd(Animator animation) {
//                                    super.onAnimationEnd(animation);
//                                }
//                            });

                } else if (!isScrollEnd && !isAnimateStart) {
                    isAnimateStart = true;
                    setDownBtnVisible();
                    isAnimateStart = false;

//                    llScrollNavigate.animate()
//                            .alpha(1.0f)
//                            .translationY(0)
//                            .setDuration(200)
//                            .setListener(new AnimatorListenerAdapter() {
//                                @Override
//                                public void onAnimationEnd(Animator animation) {
//                                    super.onAnimationEnd(animation);
//                                }
//                            });

                    txtNewUnreadMessage.getTextView().setText(countNewMessage + "");
                    if (countNewMessage == 0) {
                        txtNewUnreadMessage.setVisibility(View.GONE);

                    } else {

                        txtNewUnreadMessage.setVisibility(View.VISIBLE);
                    }

                }
            }
        });

 /*       if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            card.setCardBackgroundColor(Color.parseColor("#ffffff"));
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            //    event.addBatch(0,0,0,0,0,0);
            //            card.setCardBackgroundColor(Color.parseColor("#ffffff"));
        } else if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {

            card.setCardBackgroundColor(Color.parseColor("#20000000"));

        } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
            *//* Reset Color *//*
            card.setCardBackgroundColor(Color.parseColor("#ffffff"));
            //  card.setOnClickListener(clickListener);

        }
        return false;
*/
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                return false;
            }
        });

        imvUserPicture = mHelperToolbar.getUserAvatarChat();
       /* imvUserPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToProfile();
            }
        });

        rootView.findViewById(R.id.ac_txt_cloud).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToProfile();
            }
        });*/

        imvSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!addToView) {
                    resetAndGetFromEnd();
                }

                if (isShowLayoutUnreadMessage) {
                    removeLayoutUnreadMessage();
                }
                //final Realm realmMessage = Realm.getDefaultInstance();

                HelperSetAction.setCancel(mRoomId);

                clearDraftRequest();

                if (hasForward) {
                    manageForwardedMessage();

                    if (edtChat.getText().length() == 0) {
                        return;
                    }
                }
                if (ll_attach_text.getVisibility() == View.VISIBLE) {
                    if (isCardToCardMessage) {
                        sendNewMessageForRequestCardToCard();
                        ll_attach_text.setVisibility(View.GONE);
                        edtChat.setFilters(new InputFilter[]{});
                        edtChat.setText("");

                        clearReplyView();
                        isCardToCardMessage = false;
                        return;
                    } else {
                        if (listPathString.size() == 0) {
                            return;
                        }
                        sendMessage(latestRequestCode, listPathString.get(0));
                        listPathString.clear();
                        ll_attach_text.setVisibility(View.GONE);
                        edtChat.setFilters(new InputFilter[]{});
                        edtChat.setText("");

                        clearReplyView();
                        return;
                    }
                }

                /**
                 * if use click on edit message, the message's text will be put to the EditText
                 * i set the message object for that view's tag to obtain it here
                 * request message edit only if there is any changes to the message text
                 */

                if (edtChat.getTag() != null && edtChat.getTag() instanceof StructMessageInfo) {
                    final StructMessageInfo messageInfo = (StructMessageInfo) edtChat.getTag();
                    final String message = getWrittenMessage();
                    if (!message.equals(messageInfo.messageText) && edtChat.getText().length() > 0) {
                        messageInfo.hasEmojiInText = RealmRoomMessage.isEmojiInText(message);

                        RealmRoomMessage.editMessageClient(mRoomId, parseLong(messageInfo.messageID), message);
                        RealmClientCondition.addOfflineEdit(mRoomId, Long.parseLong(messageInfo.messageID), message);

                        /**
                         * update message text in adapter
                         */
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.updateMessageText(parseLong(messageInfo.messageID), message);
                            }
                        });

                        /**
                         * should be null after requesting
                         */
                        removeEditedMessage();

                        /**
                         * send edit message request
                         */
                        if (chatType == CHAT) {
                            new RequestChatEditMessage().chatEditMessage(mRoomId, parseLong(messageInfo.messageID), message);
                        } else if (chatType == GROUP) {
                            new RequestGroupEditMessage().groupEditMessage(mRoomId, parseLong(messageInfo.messageID), message);
                        } else if (chatType == CHANNEL) {
                            new RequestChannelEditMessage().channelEditMessage(mRoomId, parseLong(messageInfo.messageID), message);
                        }
                    } else {
                        removeEditedMessage();
                    }
                } else { // new message has written
                    sendNewMessage();
                    scrollToEnd();
                }

                //realmMessage.close();
            }
        });

        G.openBottomSheetItem = new OpenBottomSheetItem() {
            @Override
            public void openBottomSheet(boolean isNew) {
                isNewBottomSheet = isNew;
                imvAttachFileButton.performClick();
                fastItemAdapter.notifyAdapterDataSetChanged();
            }

        };

        imvAttachFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!initAttach) {
                    initAttach = true;
                    initAttach();
                }

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                itemAdapterBottomSheet();
            }
        });

        sendMoney.setOnClickListener(view -> {
            if (G.isWalletActive && G.isWalletRegister && (chatType == CHAT) && !isCloudRoom && !isBot) {
                showSelectItem();
            } else {
                showCardToCard();
            }
        });


        imvMicButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (ContextCompat.checkSelfPermission(G.fragmentActivity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    try {
                        HelperPermission.getMicroPhonePermission(G.fragmentActivity, null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    voiceRecord.setItemTag("ivVoice");
                    // viewAttachFile.setVisibility(View.GONE);
                    viewMicRecorder.setVisibility(View.VISIBLE);

                    AppUtils.setVibrator(50);
                    G.handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            voiceRecord.startVoiceRecord();
                        }
                    }, 60);
                }

                return true;
            }
        });

        if (data.size() == 0) {
            fillStickerList();
        }
        // to toggle between keyboard and emoji popup
        imvSmileButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                emojiPopup.toggle();
                if (data != null && data.size() > 0) {
                    emojiPopup.updateStickerAdapter((ArrayList<StructGroupSticker>) data);
                }
            }
        });

        onUpdateSticker = new OnUpdateSticker() {
            @Override
            public void update() {

                data.clear();
                data = RealmStickers.getAllStickers(true);
                if (data != null && emojiPopup != null) {
                    emojiPopup.updateStickerAdapter((ArrayList<StructGroupSticker>) data);
                }
            }

            @Override
            public void updateRecentlySticker(ArrayList<String> structAllStickers) {
                if (structAllStickers != null) emojiPopup.onUpdateRecentSticker(structAllStickers);
            }


        };

        edtChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence text, int i, int i1, int i2) {

                if (text.length() > 0) {
                    HelperSetAction.setActionTyping(mRoomId, chatType);
                }

                // if in the seeting page send by enter is on message send by enter key
                if (text.toString().endsWith(System.getProperty("line.separator"))) {
                    if (sendByEnter) imvSendButton.performClick();
                }
               /* if (text.toString().equals(messageEdit) && isEditMessage) {
                    imvSendButton.setText(G.fragmentActivity.getResources().getString(R.string.md_close_button));
                } else {
                    imvSendButton.setText(G.fragmentActivity.getResources().getString(R.string.md_send_button));
                }*/

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (ll_attach_text.getVisibility() == View.GONE && hasForward == false) {

                    if (edtChat.getText().length() > 0) {
                        sendButtonVisibility(true);
                    } else {
                        if (!isEditMessage) {
                            sendButtonVisibility(false);
                        } else {
                            //imvSendButton.setText(G.fragmentActivity.getResources().getString(R.string.md_close_button));
                        }
                    }
                }


            }
        });

        //realm.close();
    }

    private void removeEditedMessage() {
        imvSendButton.setText(G.fragmentActivity.getResources().getString(R.string.md_send_button));
        edtChat.setTag(null);
        clearReplyView();
        isEditMessage = false;
        edtChat.setText("");
    }

    private void cancelAllRequestFetchHistory() {
        RequestQueue.cancelRequest(lastRandomRequestIdDown);
        RequestQueue.cancelRequest(lastRandomRequestIdUp);
        isWaitingForHistoryUp = false;
        isWaitingForHistoryDown = false;
    }

    private void showCardToCard() {
        cardToCardClick(null);
    }

    private void showSelectItem() {
        ChatMoneyTransferFragment transferAction;

        RealmRoom realmRoom = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
        if (realmRoom != null) {
            chatType = realmRoom.getType();
            if (chatType == CHAT) {
                chatPeerId = realmRoom.getChatRoom().getPeerId();
                if (imvUserPicture != null && txtName != null) {
                    ChatMoneyTransferFragment chatMoneyTransferFragment= ChatMoneyTransferFragment.getInstance(chatPeerId, imvUserPicture.getDrawable(), txtName.getText().toString());
                    transferAction = chatMoneyTransferFragment;

                    chatMoneyTransferFragment.setCardToCardCallBack((cardNum, amountNum, descriptionTv) -> {

                        sendNewMessageCardToCard(amountNum, cardNum, descriptionTv);

                        ll_attach_text.setVisibility(View.GONE);
                        edtChat.setFilters(new InputFilter[]{});
                        edtChat.setText("");

                        clearReplyView();

                    });

                    if (getFragmentManager() != null)
                        transferAction.show(getFragmentManager(), "PaymentFragment");
//                        transferAction.show(getFragmentManager(), "PaymentFragment");
                    transferAction.setMoneyTransferAction(this::showCardToCard);
                }
            }
        }


    }


    private void sendNewMessageForRequestCardToCard() {
        String[] messages = HelperString.splitStringEvery(getWrittenMessage(), Config.MAX_TEXT_LENGTH);
        if (messages.length == 0) {
            edtChat.setText("");
            Toast.makeText(context, R.string.please_write_your_message, Toast.LENGTH_LONG).show();
        } else {
            for (int i = 0; i < messages.length; i++) {
                final String message = messages[i];

                final RealmRoomMessage roomMessage = RealmRoomMessage.makeTextMessage(mRoomId, message, replyMessageId());

                if (roomMessage != null) {
                    JsonArray jsonArray = new JsonArray();
                    JsonArray jsonArray2 = new JsonArray();
                    JsonObject jsonObject=new JsonObject();
                    jsonArray.add(jsonArray2);
                    JsonObject json = new JsonObject();
                    json.addProperty("label", "Card to Card");
                    json.addProperty("imageUrl", "");
                    json.addProperty("actionType", "27");
                    json.addProperty("value", G.userId);
                    jsonArray2.add(json);


                    getRealmChat().executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            roomMessage.setRealmAdditional(RealmAdditional.put(jsonArray.toString(), AdditionalType.UNDER_MESSAGE_BUTTON));
                        }
                    });

                    edtChat.setText("");
                    lastMessageId = roomMessage.getMessageId();
                    mAdapter.add(new TextItem(mAdapter, chatType, FragmentChat.this).setMessage(StructMessageInfo.convert(getRealmChat(), roomMessage)).withIdentifier(SUID.id().get()));
                    clearReplyView();
                    scrollToEnd();

                    /**
                     * send splitted message in every one second
                     */
                    if (messages.length > 1) {
                        G.handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (roomMessage.isValid() && !roomMessage.isDeleted()) {
                                    new ChatSendMessageUtil().build(chatType, mRoomId, roomMessage);
                                }
                            }
                        }, 1000 * i);
                    } else {
                        new ChatSendMessageUtil().build(chatType, mRoomId, roomMessage);
                    }
                } else {
                    Toast.makeText(context, R.string.please_write_your_message, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void sendNewMessageCardToCard(String amount, String cardNumber, String description) {
        String mplCardNumber = cardNumber.replace("-", "");
        int mplAmount = Integer.parseInt(amount.replace(",", ""));

        final RealmRoomMessage roomMessage = RealmRoomMessage.makeTextMessage(mRoomId, description, replyMessageId());
        if (roomMessage != null) {

            JsonArray rootJsonArray = new JsonArray();
            JsonArray dataJsonArray = new JsonArray();

            JsonObject valueObject = new JsonObject();
            valueObject.addProperty("cardNumber", mplCardNumber);
            valueObject.addProperty("amount", mplAmount);
            valueObject.addProperty("userId", G.userId);

            JsonObject rootObject = new JsonObject();
            rootObject.addProperty("label", "Card to Card");
            rootObject.addProperty("imageUrl", "");
            rootObject.addProperty("actionType", "27");
            rootObject.add("value", valueObject);

            dataJsonArray.add(rootObject);
            rootJsonArray.add(dataJsonArray);

            getRealmChat().executeTransaction(realm -> roomMessage.setRealmAdditional(RealmAdditional.put(rootJsonArray.toString(), AdditionalType.CARD_TO_CARD_MESSAGE)));

            edtChat.setText("");
            lastMessageId = roomMessage.getMessageId();
            mAdapter.add(new CardToCardItem(mAdapter, chatType, FragmentChat.this).setMessage(StructMessageInfo.convert(getRealmChat(), roomMessage)).withIdentifier(SUID.id().get()));
            clearReplyView();
            scrollToEnd();

            /**
             * send splitted message in every one second
             */

            if (!description.isEmpty()) {
                G.handler.postDelayed(() -> {
                    if (roomMessage.isValid() && !roomMessage.isDeleted()) {
                        new ChatSendMessageUtil().build(chatType, mRoomId, roomMessage);
                    }
                }, 1000);
            } else {
                new ChatSendMessageUtil().build(chatType, mRoomId, roomMessage);
            }
        } else {
            Toast.makeText(context, R.string.please_write_your_message, Toast.LENGTH_LONG).show();
        }
    }

    private void sendNewMessage() {
        String[] messages = HelperString.splitStringEvery(getWrittenMessage(), Config.MAX_TEXT_LENGTH);
        if (messages.length == 0) {
            edtChat.setText("");
            Toast.makeText(context, R.string.please_write_your_message, Toast.LENGTH_LONG).show();
        } else {
            for (int i = 0; i < messages.length; i++) {
                final String message = messages[i];

                final RealmRoomMessage roomMessage = RealmRoomMessage.makeTextMessage(mRoomId, message, replyMessageId());
                if (roomMessage != null) {
                    edtChat.setText("");
                    lastMessageId = roomMessage.getMessageId();
                    mAdapter.add(new TextItem(mAdapter, chatType, FragmentChat.this).setMessage(StructMessageInfo.convert(getRealmChat(), roomMessage)).withIdentifier(SUID.id().get()));
                    clearReplyView();
                    scrollToEnd();

                    /**
                     * send splitted message in every one second
                     */
                    if (messages.length > 1) {
                        G.handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (roomMessage.isValid() && !roomMessage.isDeleted()) {
                                    new ChatSendMessageUtil().build(chatType, mRoomId, roomMessage);
                                }
                            }
                        }, 1000 * i);
                    } else {
                        new ChatSendMessageUtil().build(chatType, mRoomId, roomMessage);
                    }
                } else {
                    Toast.makeText(context, R.string.please_write_your_message, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void openWebViewForSpecialUrlChat(String mUrl) {


        try {
            setDownBtnGone();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (botInit != null) botInit.close();
     /*   StructWebView urlWebView = getUrlWebView(mUrl);
        if (urlWebView == null) {
            return;
        } else {

            urlWebViewForSpecialUrlChat = urlWebView.getUrl();
        }
        */

        urlWebViewForSpecialUrlChat = mUrl;
        if (webViewChatPage == null) webViewChatPage = rootView.findViewById(R.id.webViewChatPage);
        if (rootWebView == null) rootWebView = rootView.findViewById(R.id.rootWebView);
        if (progressWebView == null) progressWebView = rootView.findViewById(R.id.progressWebView);
        recyclerView.setVisibility(View.GONE);
        viewAttachFile.setVisibility(View.GONE);
        rootWebView.setVisibility(View.VISIBLE);
        webViewChatPage.getSettings().setLoadsImagesAutomatically(true);
        webViewChatPage.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webViewChatPage.clearCache(true);
        webViewChatPage.clearHistory();
        webViewChatPage.clearView();
        webViewChatPage.clearFormData();
        webViewChatPage.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webViewChatPage.getSettings().setJavaScriptEnabled(true);
        webViewChatPage.getSettings().setDomStorageEnabled(true);
        progressWebView.setVisibility(View.VISIBLE);

        webViewChatPage.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100) {
                    progressWebView.setVisibility(View.GONE);

                } else {
                    progressWebView.setVisibility(View.VISIBLE);

                }
            }
        });
        webViewChatPage.setWebViewClient(new MyWebViewClient() {

            @Override
            protected void onReceivedError(WebView webView, String url, int errorCode, String description) {
            }

            @Override
            protected boolean handleUri(WebView webView, Uri uri) {
                final String host = uri.getHost();
                final String scheme = uri.getScheme();
                // Returning false means that you are going to load this url in the webView itself
                // Returning true means that you need to handle what to do with the url e.g. open web page in a Browser

                // final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                // startActivity(intent);
                return false;

            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (url.toLowerCase().equals("igap://close")) {
                    makeWebViewGone();
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

            }
        });

        webViewChatPage.loadUrl(urlWebViewForSpecialUrlChat);
    }

    private void setTouchListener(Canvas c,
                                  RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder,
                                  float dX, float dY,
                                  int actionState, boolean isCurrentlyActive) {


        if (dX < -ViewMaker.dpToPixel(140)) {
            if (!isRepley) {
                Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.PARCELABLE_WRITE_RETURN_VALUE));
                } else {
                    //deprecated in API 26
                    v.vibrate(50);
                }
            }
            isRepley = true;

            // replay(message);
           /* if (!goToPositionWithAnimation(replyMessage.getMessageId(), 1000)) {
                goToPositionWithAnimation(replyMessage.getMessageId() * (-1), 1000);
            }*/
        } else {
            isRepley = false;
        }

       /* icon.setBounds(viewHolder.itemView.getRight() - 0, 0, viewHolder.itemView.getRight() - 0, 0 + icon.getIntrinsicHeight());
        icon.draw(c);*/


        View itemView = viewHolder.itemView;


   /*     DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        Drawable drawable = ContextCompat.getDrawable(G.fragmentActivity, R.mipmap.ic_launcher_round);
        Bitmap icon = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        //  Canvas canvas = new Canvas(icon);
        drawable.setBounds(displayMetrics.widthPixels - 109, itemView.getTop() + 9, itemView.getRight() - 22, itemView.getBottom() - 9);
        drawable.draw(c);*/


        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                swipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
                return false;
            }
        });

    }

    private void visibilityTextEmptyMessages() {
        if (mAdapter.getItemCount() > 0 || (prgWaiting != null && prgWaiting.getVisibility() == View.VISIBLE)) {
            txtEmptyMessages.setVisibility(View.GONE);
        } else {
            txtEmptyMessages.setVisibility(View.VISIBLE);
        }
    }

    private void dialogReport(final boolean isMessage, final long messageId) {
        if (!AndroidUtils.canOpenDialog()) {return;}
        List<String> items = new ArrayList<>();
        items.add(getString(R.string.st_Abuse));
        items.add(getString(R.string.st_Spam));
        items.add(getString(R.string.st_Violence));
        items.add(getString(R.string.st_Pornography));
        items.add(getString(R.string.st_Other));

        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment().setData(items, -1, position -> {
            if (items.get(position).equals(getString(R.string.st_Abuse))) {
                if (isMessage) {
                    new RequestClientRoomReport().roomReport(mRoomId, messageId, ProtoClientRoomReport.ClientRoomReport.Reason.ABUSE, "");
                } else {
                    new RequestClientRoomReport().roomReport(mRoomId, 0, ProtoClientRoomReport.ClientRoomReport.Reason.ABUSE, "");
                }
            } else if (items.get(position).equals(getString(R.string.st_Spam))) {
                if (isMessage) {
                    new RequestClientRoomReport().roomReport(mRoomId, messageId, ProtoClientRoomReport.ClientRoomReport.Reason.SPAM, "");
                } else {
                    new RequestClientRoomReport().roomReport(mRoomId, 0, ProtoClientRoomReport.ClientRoomReport.Reason.SPAM, "");
                }
            } else if (items.get(position).equals(getString(R.string.st_Violence))) {
                if (isMessage) {
                    new RequestClientRoomReport().roomReport(mRoomId, messageId, ProtoClientRoomReport.ClientRoomReport.Reason.VIOLENCE, "");
                } else {
                    new RequestClientRoomReport().roomReport(mRoomId, 0, ProtoClientRoomReport.ClientRoomReport.Reason.VIOLENCE, "");
                }
            } else if (items.get(position).equals(getString(R.string.st_Pornography))) {
                if (isMessage) {
                    new RequestClientRoomReport().roomReport(mRoomId, messageId, ProtoClientRoomReport.ClientRoomReport.Reason.PORNOGRAPHY, "");
                } else {
                    new RequestClientRoomReport().roomReport(mRoomId, 0, ProtoClientRoomReport.ClientRoomReport.Reason.PORNOGRAPHY, "");
                }
            } else if (items.get(position).equals(getString(R.string.st_Other))) {
                final MaterialDialog dialogReport = new MaterialDialog.Builder(G.fragmentActivity).title(R.string.report).inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE).alwaysCallInputCallback().input(G.context.getString(R.string.description), "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        // Do something
                        if (input.length() > 0) {

                            report = input.toString();
                            View positive = dialog.getActionButton(DialogAction.POSITIVE);
                            positive.setEnabled(true);

                        } else {
                            View positive = dialog.getActionButton(DialogAction.POSITIVE);
                            positive.setEnabled(false);
                        }
                    }
                }).positiveText(R.string.ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        if (isMessage) {
                            new RequestClientRoomReport().roomReport(mRoomId, messageId, ProtoClientRoomReport.ClientRoomReport.Reason.OTHER, report);
                        } else {
                            new RequestClientRoomReport().roomReport(mRoomId, 0, ProtoClientRoomReport.ClientRoomReport.Reason.OTHER, report);
                        }
                    }
                }).negativeText(R.string.cancel).build();

                View positive = dialogReport.getActionButton(DialogAction.POSITIVE);
                positive.setEnabled(false);

                DialogAnimation.animationDown(dialogReport);
                dialogReport.show();
            }
        });
        bottomSheetFragment.show(getFragmentManager(), "bottomSheet");

        G.onReport = () -> error(G.fragmentActivity.getResources().getString(R.string.st_send_report));


    }

    private void putExtra(Intent intent, StructMessageInfo messageInfo) {
        try {
            String message = messageInfo.forwardedFrom != null ? messageInfo.forwardedFrom.getMessage() : messageInfo.messageText;
            if (message != null) {
                intent.putExtra(Intent.EXTRA_TEXT, message);
            }
            String filePath = messageInfo.forwardedFrom != null ? messageInfo.forwardedFrom.getAttachment().getLocalFilePath() : messageInfo.attachment.getLocalFilePath();
            if (filePath != null) {
                intent.putExtra(Intent.EXTRA_STREAM, AppUtils.createtUri(new File(filePath)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * *************************** callbacks ***************************
     */

    @Override
    public void onSenderAvatarClick(View view, StructMessageInfo messageInfo, int position) {
        /**
         * set null for avoid from clear group room message adapter if user try for clearChatHistory
         */
        G.onClearChatHistory = null;
        if (getActivity() != null) {
            new HelperFragment(getActivity().getSupportFragmentManager(), FragmentContactsProfile.newInstance(mRoomId, parseLong(messageInfo.senderID), GROUP.toString())).setReplace(false).load();
        }
    }

    @Override
    public void onUploadOrCompressCancel(View view, final StructMessageInfo message, int pos, SendingStep sendingStep) {

        if (sendingStep == SendingStep.UPLOADING) {
            HelperSetAction.sendCancel(parseLong(message.messageID));

            if (HelperUploadFile.cancelUploading(message.messageID)) {
                deleteItem(parseLong(message.messageID), pos);
            }
        } else if (sendingStep == SendingStep.COMPRESSING) {

            /**
             * clear path for avoid from continue uploading after compressed file
             */
            for (StructUploadVideo structUploadVideo : structUploadVideos) {
                if (structUploadVideo.filePath.equals(message.attachment.getLocalFilePath())) {
                    structUploadVideo.filePath = "";
                }
            }
            deleteItem(parseLong(message.messageID), pos);
        } else if (sendingStep == SendingStep.CORRUPTED_FILE) {
            deleteItem(parseLong(message.messageID), pos);
        }
    }

    @Override
    public void onChatClearMessage(final long roomId, final long clearId) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (mAdapter != null) {
                    boolean cleared = false;
                    if (mAdapter.getAdapterItemCount() > 1) {
                        try {
                            if (Long.parseLong(mAdapter.getAdapterItem(mAdapter.getAdapterItemCount() - 1).mMessage.messageID) == clearId) {
                                cleared = true;
                                mAdapter.clear();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (!cleared) {
                        int selectedPosition = -1;
                        for (int i = (mAdapter.getAdapterItemCount() - 1); i >= 0; i--) {
                            try {
                                StructMessageInfo structMessageInfo = mAdapter.getAdapterItem(i).mMessage;
                                if (structMessageInfo != null && Long.parseLong(structMessageInfo.messageID) == clearId) {
                                    selectedPosition = i;
                                    break;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (selectedPosition != -1) {
                            for (int i = selectedPosition; i >= 0; i--) {
                                mAdapter.remove(i);
                            }
                        }
                    }
                }

                /**
                 * remove tag from edtChat if the message has deleted
                 */
                if (edtChat != null && edtChat.getTag() != null && edtChat.getTag() instanceof StructMessageInfo) {
                    edtChat.setTag(null);
                }
            }
        });
    }

    @Override
    public void onChatUpdateStatus(long roomId, final long messageId, final ProtoGlobal.RoomMessageStatus status, long statusVersion) {

        // I'm in the room
        if (mRoomId == roomId) {
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    if (mAdapter != null) {
                        mAdapter.updateMessageStatus(messageId, status);
                    }
                }
            });
        }
    }

    @Override
    public void onChatMessageSelectionChanged(int selectedCount, Set<AbstractMessage> selectedItems) {
        //   Toast.makeText(ActivityChat.this, "selected: " + Integer.toString(selectedCount), Toast.LENGTH_SHORT).show();
        if (selectedCount > 0) {
            FragmentChat.isInSelectionMode = true;
            //toolbar.setVisibility(View.GONE);
            mBtnReplySelected.setVisibility(View.VISIBLE);

            mTxtSelectedCounter.setText(selectedCount + " " + context.getResources().getString(R.string.item_selected));

            if (HelperCalander.isPersianUnicode) {
                mTxtSelectedCounter.setText(convertToUnicodeFarsiNumber(mTxtSelectedCounter.getText().toString()));
            }

            if (selectedCount > 1) {
                mBtnReplySelected.setVisibility(View.INVISIBLE);
            } else {

                if (chatType == CHANNEL) {
                    if (channelRole == ChannelChatRole.MEMBER) {
                        mBtnReplySelected.setVisibility(View.INVISIBLE);
                    }
                }
            }

            //+Realm realm = Realm.getDefaultInstance();

            isAllSenderId = true;

            for (AbstractMessage message : selectedItems) {

                RealmRoom realmRoom = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
                if (realmRoom != null) {

                    long messageSender = 0;
                    if (message != null && message.mMessage != null && message.mMessage.senderID != null) {
                        messageSender = parseLong(message.mMessage.senderID);
                    } else {
                        continue;
                    }

                    // if user clicked on any message which he wasn't its sender, remove edit mList option
                    if (chatType == CHANNEL) {
                        if (channelRole == ChannelChatRole.MEMBER) {
                            mBtnReplySelected.setVisibility(View.INVISIBLE);
                            mBtnDeleteSelected.setVisibility(View.GONE);
                            isAllSenderId = false;
                        }
                        final long senderId = G.userId;
                        ChannelChatRole roleSenderMessage = RealmChannelRoom.detectMemberRole(mRoomId, messageSender);
                        if (senderId != messageSender) {  // if message dose'nt belong to owner
                            if (channelRole == ChannelChatRole.MEMBER) {
                                mBtnDeleteSelected.setVisibility(View.GONE);
                                isAllSenderId = false;
                            } else if (channelRole == ChannelChatRole.MODERATOR) {
                                if (roleSenderMessage == ChannelChatRole.MODERATOR || roleSenderMessage == ChannelChatRole.ADMIN || roleSenderMessage == ChannelChatRole.OWNER) {
                                    mBtnDeleteSelected.setVisibility(View.GONE);
                                    isAllSenderId = false;
                                }
                            } else if (channelRole == ChannelChatRole.ADMIN) {
                                if (roleSenderMessage == ChannelChatRole.OWNER || roleSenderMessage == ChannelChatRole.ADMIN) {
                                    mBtnDeleteSelected.setVisibility(View.GONE);
                                    isAllSenderId = false;
                                }
                            }
                        } else {
                            mBtnDeleteSelected.setVisibility(View.VISIBLE);
                        }
                    } else if (chatType == GROUP) {

                        final long senderId = G.userId;
                        GroupChatRole roleSenderMessage = RealmGroupRoom.detectMemberRole(mRoomId, messageSender);

                        if (senderId != messageSender) {  // if message dose'nt belong to owner
                            if (groupRole == GroupChatRole.MEMBER) {
                                mBtnDeleteSelected.setVisibility(View.GONE);
                                isAllSenderId = false;
                            } else if (groupRole == GroupChatRole.MODERATOR) {
                                if (roleSenderMessage == GroupChatRole.MODERATOR || roleSenderMessage == GroupChatRole.ADMIN || roleSenderMessage == GroupChatRole.OWNER) {
                                    mBtnDeleteSelected.setVisibility(View.GONE);
                                    isAllSenderId = false;
                                }
                            } else if (groupRole == GroupChatRole.ADMIN) {
                                if (roleSenderMessage == GroupChatRole.OWNER || roleSenderMessage == GroupChatRole.ADMIN) {
                                    mBtnDeleteSelected.setVisibility(View.GONE);
                                    isAllSenderId = false;
                                }
                            }
                        } else {
                            mBtnDeleteSelected.setVisibility(View.VISIBLE);
                        }
                    } else if (realmRoom.getReadOnly()) {
                        mBtnReplySelected.setVisibility(View.INVISIBLE);
                    }
                }
            }

            if (!isAllSenderId) {
                mBtnDeleteSelected.setVisibility(View.GONE);
            }

            //realm.close();

            if (isPinAvailable) pinedMessageLayout.setVisibility(View.GONE);
            ll_AppBarSelected.setVisibility(View.VISIBLE);
        } else {
            FragmentChat.isInSelectionMode = false;
            if (isPinAvailable) pinedMessageLayout.setVisibility(View.VISIBLE);
            ll_AppBarSelected.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPreChatMessageRemove(final StructMessageInfo messageInfo, int position) {
        if (mAdapter.getAdapterItemCount() > 1 && position == mAdapter.getAdapterItemCount() - 1) {
            //RealmRoom.setLastMessageAfterLocalDelete(mRoomId, parseLong(messageInfo.messageID));
            RealmRoom.setLastMessage(mRoomId);
        }
    }

    @Override
    public void onMessageUpdate(long roomId, final long messageId, final ProtoGlobal.RoomMessageStatus status, final String identity, final ProtoGlobal.RoomMessage roomMessage) {
        // I'm in the room
        if (roomId == mRoomId && mAdapter != null) {
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    mAdapter.updateMessageIdAndStatus(messageId, identity, status, roomMessage);
                }
            });
        }

        if (soundPool != null && sendMessageSound != 0)
            playSendSound(roomId, roomMessage,chatType);

    }

    @Override
    public synchronized void onMessageReceive(final long roomId, String message, ProtoGlobal.RoomMessageType messageType, final ProtoGlobal.RoomMessage roomMessage, final ProtoGlobal.Room.Type roomType) {

        if (roomMessage.getMessageId() <= biggestMessageId) {
            return;
        }

        if (soundPool != null && sendMessageSound != 0)
            playReceiveSound(roomId, roomMessage,roomType);

        if (isBot) {

            try {

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (roomMessage.getAdditionalType() == Additional.WEB_VIEW.getAdditional()) {
//                            StructWebView item = getUrlWebView(roomMessage.getAdditionalData());
                            openWebViewForSpecialUrlChat(roomMessage.getAdditionalData());
                            return;
                        }

                        RealmRoomMessage rm = null;
                        boolean backToMenu = true;

                        RealmResults<RealmRoomMessage> result = getRealmChat().where(RealmRoomMessage.class).
                                equalTo(RealmRoomMessageFields.ROOM_ID, mRoomId).notEqualTo(RealmRoomMessageFields.AUTHOR_HASH, G.authorHash).findAll();
                        if (result.size() > 0) {
                            rm = result.last();
                            if (rm.getMessage() != null) {
                                if (rm.getMessage().toLowerCase().equals("/start") || rm.getMessage().equals("/back")) {
                                    backToMenu = false;
                                }
                            }
                        }
                        if (getActivity() != null) {
                            try {
                                if (roomMessage.getAuthor().getUser().getUserId() == chatPeerId) {

                                    if (rm.getRealmAdditional() != null && roomMessage.getAdditionalType() == AdditionalType.UNDER_KEYBOARD_BUTTON)
                                        botInit.updateCommandList(false, message, getActivity(), backToMenu, roomMessage, roomId, true);
                                    else
                                        botInit.updateCommandList(false, "clear", getActivity(), backToMenu, null, 0, true);
                                }
                            } catch (NullPointerException e) {
                            } catch (Exception e) {
                            }
                        }
                    }
                });

            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            try {
                if (isShowStartButton) {
                    if (rootView != null) {
                        rootView.post(new Runnable() {
                            @Override
                            public void run() {
                                rootView.findViewById(R.id.chl_ll_channel_footer).setVisibility(View.GONE);
                                if (webViewChatPage == null)
                                    rootView.findViewById(R.id.layout_attach_file).setVisibility(View.VISIBLE);
                            }
                        });
                    }
                    isShowStartButton = false;
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
            }

        }

        final Realm realm = Realm.getDefaultInstance();
        final RealmRoomMessage realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, roomMessage.getMessageId()).findFirst();

        if (realmRoomMessage != null && realmRoomMessage.isValid() && !realmRoomMessage.isDeleted()) {
            if (roomMessage.getAuthor().getUser() != null) {

                RealmRoomMessage messageCopy = realm.copyFromRealm(realmRoomMessage);

                if (roomMessage.getAuthor().getUser().getUserId() != G.userId) {
                    // I'm in the room
                    if (roomId == mRoomId) {
                        // I'm in the room, so unread messages count is 0. it means, I read all messages

                        RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
                        if (room != null) {
                            /**
                             * client checked  (room.getUnreadCount() <= 1)  because in HelperMessageResponse unreadCount++
                             */
                            if (room.getUnreadCount() <= 1 && countNewMessage < 1) {
                                firstUnreadMessage = realm.copyFromRealm(realmRoomMessage);
                            }
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {

                                    RealmRoom.setCountWithCallBack(realm, mRoomId,0);
                                }
                            });
                        }


                        /**
                         * when user receive message, I send update status as SENT to the message sender
                         * but imagine user is not in the room (or he is in another room) and received
                         * some messages when came back to the room with new messages, I make new update
                         * status request as SEEN to the message sender
                         */

                        //Start ClientCondition OfflineSeen
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, roomId).findFirst();
                                if (realmClientCondition != null) {
                                    realmClientCondition.getOfflineSeen().add(RealmOfflineSeen.put(realm, messageId));
                                }
                            }
                        });
                        /**
                         * I'm in the room, so unread messages count is 0. it means, I read all messages
                         */


                        if (!isNotJoin) {
                            // make update status to message sender that i've read his message

                            StructBackGroundSeen _BackGroundSeen = null;

                            ProtoGlobal.RoomMessageStatus roomMessageStatus;
                            if (G.isAppInFg && isEnd() && !isPaused) {


                                if (messageCopy.isValid() && !messageCopy.getStatus().equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.SEEN.toString())) {
                                    messageCopy.setStatus(ProtoGlobal.RoomMessageStatus.SEEN.toString());
                                }
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {

                                        RealmRoom.setCount(realm, mRoomId, 0);
                                        realm.copyToRealmOrUpdate(messageCopy);
                                    }
                                });

                                roomMessageStatus = ProtoGlobal.RoomMessageStatus.SEEN;
                            } else {

                                roomMessageStatus = ProtoGlobal.RoomMessageStatus.DELIVERED;

                                _BackGroundSeen = new StructBackGroundSeen();
                                _BackGroundSeen.messageID = roomMessage.getMessageId();
                                _BackGroundSeen.roomType = roomType;
                            }

                            if (chatType == CHAT) {
                                G.chatUpdateStatusUtil.sendUpdateStatus(roomType, roomId, roomMessage.getMessageId(), roomMessageStatus);

                                if (_BackGroundSeen != null) {
                                    backGroundSeenList.add(_BackGroundSeen);
                                }
                            } else if (chatType == GROUP && (roomMessage.getStatus() != ProtoGlobal.RoomMessageStatus.SEEN)) {
                                G.chatUpdateStatusUtil.sendUpdateStatus(roomType, roomId, roomMessage.getMessageId(), roomMessageStatus);

                                if (_BackGroundSeen != null) {
                                    backGroundSeenList.add(_BackGroundSeen);
                                }
                            }
                        }

                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (addToView) {
                                    switchAddItem(new ArrayList<>(Collections.singletonList(StructMessageInfo.convert(getRealmChat(), messageCopy))), false);
                                    if (isShowLayoutUnreadMessage) {
                                        removeLayoutUnreadMessage();
                                    }
                                }

                                setBtnDownVisible(messageCopy);

                            }
                        });

                        /**
                         * when client load item from unread and don't come down let's not add the message
                         * to the list and after insuring that not any more message in DOWN can add message
                         */

                    } else {
                        if (!isNotJoin) {
                            // user has received the message, so I make a new delivered update status request
                            if (roomType == CHAT) {
                                G.chatUpdateStatusUtil.sendUpdateStatus(roomType, roomId, roomMessage.getMessageId(), ProtoGlobal.RoomMessageStatus.DELIVERED);
                            } else if (roomType == GROUP && roomMessage.getStatus() == ProtoGlobal.RoomMessageStatus.SENT) {
                                G.chatUpdateStatusUtil.sendUpdateStatus(roomType, roomId, roomMessage.getMessageId(), ProtoGlobal.RoomMessageStatus.DELIVERED);
                            }
                        }
                    }
                } else {

                    if (roomId == mRoomId) {
                        // I'm sender . but another account sent this message and i received it.
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (addToView) {
                                    switchAddItem(new ArrayList<>(Collections.singletonList(StructMessageInfo.convert(getRealmChat(), messageCopy))), false);
                                    if (isShowLayoutUnreadMessage) {
                                        removeLayoutUnreadMessage();
                                    }
                                }

                                setBtnDownVisible(messageCopy);
                            }
                        });

                    }
                }
            }
        }

        realm.close();
    }

    private void playReceiveSound(long roomId, ProtoGlobal.RoomMessage roomMessage, ProtoGlobal.Room.Type roomType) {
        if (roomType == CHAT)
            if (roomId == this.mRoomId && sendMessageSound != 0 && !isPaused) {
                try {
                    soundPool.play(sendMessageSound, 1.0f, 1.0f, 1, 0, 1.0f);
                } catch (Exception e) {
                    Log.i(TAG, "playReceiveSound: " + e.getMessage());
                }
            }
    }

    private void playSendSound(long roomId, ProtoGlobal.RoomMessage roomMessage, ProtoGlobal.Room.Type roomType) {
        if (roomType == CHAT)
            if (roomId == this.mRoomId && receiveMessageSound != 0 && !isPaused) {
            try {
                soundPool.play(receiveMessageSound, 1.0f, 1.0f, 1, 0, 1.0f);
            } catch (Exception e) {
                Log.i(TAG, "playReceiveSound: " + e.getMessage());
            }
        }
    }

    private StructWebView getUrlWebView(String additionalData) {

        Gson gson = new Gson();
        StructWebView item = new StructWebView();
        try {
            item = gson.fromJson(additionalData, StructWebView.class);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e1) {
            e1.printStackTrace();
        }

        return item;
    }

    @Override
    public void onMessageFailed(long roomId, long messageId) {

        if (roomId == mRoomId && mAdapter != null) {
            mAdapter.updateMessageStatus(messageId, ProtoGlobal.RoomMessageStatus.FAILED);
        }
    }

    @Override
    public void onVoiceRecordDone(final String savedPath) {
        if (isShowLayoutUnreadMessage) {
            removeLayoutUnreadMessage();
        }
        sendCancelAction();

        //+Realm realm = Realm.getDefaultInstance();
        final long messageId = AppUtils.makeRandomId();
        final long updateTime = TimeUtils.currentLocalTime();
        final long senderID = G.userId;
        final long duration = AndroidUtils.getAudioDuration(G.fragmentActivity, savedPath) / 1000;

        getRealmChat().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                voiceLastMessage = RealmRoomMessage.makeVoiceMessage(realm, mRoomId, messageId, duration, updateTime, savedPath, getWrittenMessage());
            }
        });

        StructMessageInfo messageInfo;
        if (isReply()) {
            messageInfo = new StructMessageInfo(getRealmChat(), mRoomId, Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), ProtoGlobal.
                    RoomMessageType.VOICE, MyType.SendType.send, null, savedPath, updateTime, parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID));
        } else {
            if (isMessageWrote()) {
                messageInfo = new StructMessageInfo(getRealmChat(), mRoomId, Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), ProtoGlobal.
                        RoomMessageType.VOICE, MyType.SendType.send, null, savedPath, updateTime);
            } else {
                messageInfo = new StructMessageInfo(getRealmChat(), mRoomId, Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), ProtoGlobal.
                        RoomMessageType.VOICE, MyType.SendType.send, null, savedPath, updateTime);
            }
        }

        HelperUploadFile.startUploadTaskChat(mRoomId, chatType, savedPath, messageId, ProtoGlobal.RoomMessageType.VOICE, getWrittenMessage(), StructMessageInfo.getReplyMessageId(messageInfo), new HelperUploadFile.UpdateListener() {
            @Override
            public void OnProgress(int progress, FileUploadStructure struct) {
                if (canUpdateAfterDownload) {
                    insertItemAndUpdateAfterStartUpload(progress, struct);
                }
            }

            @Override
            public void OnError() {

            }
        });

        messageInfo.attachment.duration = duration;

        StructChannelExtra structChannelExtra = new StructChannelExtra();
        structChannelExtra.messageId = messageId;
        structChannelExtra.thumbsUp = "0";
        structChannelExtra.thumbsDown = "0";
        structChannelExtra.viewsLabel = "1";

        RealmRoom.setLastMessageWithRoomMessage(mRoomId, voiceLastMessage);

        if (RealmRoom.showSignature(mRoomId)) {
            structChannelExtra.signature = G.displayName;
        } else {
            structChannelExtra.signature = "";
        }
        messageInfo.channelExtra = structChannelExtra;
        mAdapter.add(new VoiceItem(mAdapter, chatType, this).setMessage(messageInfo));
        //realm.close();
        scrollToEnd();
        clearReplyView();
    }

    @Override
    public void onVoiceRecordCancel() {
        //empty

        sendCancelAction();
    }

    @Override
    public void onUserInfo(final ProtoGlobal.RegisteredUser user, String identity) {

        if (isCloudRoom) {
            mHelperToolbar.getCloudChatIcon().setVisibility(View.VISIBLE);
            imvUserPicture.setVisibility(View.GONE);
        } else {
            mHelperToolbar.getCloudChatIcon().setVisibility(View.GONE);
            imvUserPicture.setVisibility(View.VISIBLE);
            setAvatar();
        }
    }

    @Override
    public void onUserInfoTimeOut() {
        //empty
    }

    @Override
    public void onUserInfoError(int majorCode, int minorCode) {
        //empty
    }

    @Override
    public void onOpenClick(View view, StructMessageInfo message, int pos) {

        if (message.messageType == ProtoGlobal.RoomMessageType.STICKER) {
            checkSticker(message);
            return;
        }

        ProtoGlobal.RoomMessageType messageType = message.forwardedFrom != null ? message.forwardedFrom.getMessageType() : message.messageType;
        //+Realm realm = Realm.getDefaultInstance();
        if (messageType == ProtoGlobal.RoomMessageType.IMAGE || messageType == IMAGE_TEXT) {
            showImage(message, view);
        } else if (messageType == VIDEO || messageType == VIDEO_TEXT) {
            if (sharedPreferences.getInt(SHP_SETTING.KEY_DEFAULT_PLAYER, 1) == 0) {
                openMessage(message);
            } else {
                showImage(message, view);
            }
        } else if (messageType == ProtoGlobal.RoomMessageType.FILE || messageType == ProtoGlobal.RoomMessageType.FILE_TEXT) {
            openMessage(message);
        }
    }

    private void checkSticker(StructMessageInfo message) {

        try {
            JSONObject jObject = new JSONObject(message.additionalData.additionalData);
            String groupId = jObject.getString("groupId");
            String token = jObject.getString("token");
            Realm realm = Realm.getDefaultInstance();
            RealmStickers realmStickers = RealmStickers.checkStickerExist(groupId, realm);
            if (realmStickers == null || !realmStickers.isFavorite()) {
                openFragmentAddStickerToFavorite(groupId, token);
            }
            realm.close();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void openFragmentAddStickerToFavorite(String groupId, String token) {

        FragmentManager fm = getActivity().getSupportFragmentManager();
        DialogAddSticker dialogFragment = new DialogAddSticker().newInstance(groupId, token);
        if (fm != null) {
            dialogFragment.show(fm, "dialogFragment");
        }
    }

    private void openMessage(StructMessageInfo message) {
        String _filePath = null;
        String _token = message.forwardedFrom != null ? message.forwardedFrom.getAttachment().getToken() : message.attachment.token;
        RealmAttachment _Attachment = getRealmChat().where(RealmAttachment.class).equalTo(RealmAttachmentFields.TOKEN, _token).findFirst();

        if (_Attachment != null) {
            _filePath = _Attachment.getLocalFilePath();
        } else if (message.attachment != null) {
            _filePath = message.attachment.getLocalFilePath();
        }

        if (_filePath == null || _filePath.length() == 0) {
            return;
        }

        Intent intent = HelperMimeType.appropriateProgram(_filePath);
        if (intent != null) {
            try {
                startActivity(intent);
            } catch (Exception e) {
                // to prevent from 'No Activity found to handle Intent'
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, R.string.can_not_open_file, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDownloadAllEqualCashId(String cashId, String messageID) {

        int start = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

        if (start < 0) {
            start = 0;
        }

        for (int i = start; i < mAdapter.getItemCount() && i < start + 15; i++) {
            try {
                AbstractMessage item = mAdapter.getAdapterItem(i);
                if (item.mMessage.hasAttachment()) {
                    if (item.mMessage.getAttachment().cashID != null && item.mMessage.getAttachment().cashID.equals(cashId) && (!item.mMessage.messageID.equals(messageID))) {
                        mAdapter.notifyItemChanged(i);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onItemShowingMessageId(final StructMessageInfo messageInfo) {
        /**
         * if in current room client have new message that not seen yet
         * after first new message come in the view change view for unread count
         */
        if (firstUnreadMessageInChat != null && firstUnreadMessageInChat.isValid() && !firstUnreadMessageInChat.isDeleted() && firstUnreadMessageInChat.getMessageId() == parseLong(messageInfo.messageID)) {
            countNewMessage = 0;
            txtNewUnreadMessage.setVisibility(View.GONE);
            txtNewUnreadMessage.getTextView().setText(countNewMessage + "");

            firstUnreadMessageInChat = null;
        }

        if (chatType != CHANNEL && (!messageInfo.isSenderMe() && messageInfo.status != null && !messageInfo.status.equals(ProtoGlobal.RoomMessageStatus.SEEN.toString()) & !messageInfo.status.equals(ProtoGlobal.RoomMessageStatus.LISTENED.toString()))) {
            /**
             * set message status SEEN for avoid from run this block in each bindView
             */
            messageInfo.status = ProtoGlobal.RoomMessageStatus.SEEN.toString();

            RealmClientCondition.addOfflineSeenAsync(mRoomId, Long.parseLong(messageInfo.messageID));
            RealmRoomMessage.setStatusSeenInChatAsync(parseLong(messageInfo.messageID));
            if (!isPaused)
                G.chatUpdateStatusUtil.sendUpdateStatus(chatType, mRoomId, parseLong(messageInfo.messageID), ProtoGlobal.RoomMessageStatus.SEEN);
        }
    }

    @Override
    public void onPlayMusic(String messageId) {

        if (messageId != null && messageId.length() > 0) {

            try {
                if (MusicPlayer.downloadNextMusic(messageId)) {
                    mAdapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                HelperLog.setErrorLog(e);
            }
        }
    }

    @Override
    public boolean getShowVoteChannel() {
        return showVoteChannel;
    }

    @Override
    public void sendFromBot(Object message) {
        if (message instanceof RealmRoomMessage) {
            mAdapter.add(new TextItem(mAdapter, chatType, FragmentChat.this).setMessage(StructMessageInfo.convert(getRealmChat(), (RealmRoomMessage) message)).withIdentifier(SUID.id().get()));
            scrollToEnd();
        } else if (message instanceof String) {
            openWebViewForSpecialUrlChat(message.toString());
        }
    }

    @Override
    public void onContainerClick(View view, final StructMessageInfo message, int pos) {
        if (message == null) {
            return;
        }
        if (mAdapter.getSelectedItems().size() > 0) {
            view.performLongClick();
            return;
        }

        ProtoGlobal.RoomMessageType roomMessageType;
        if (message.forwardedFrom != null) {
            roomMessageType = message.forwardedFrom.getMessageType();
        } else {
            roomMessageType = message.messageType;
        }

        if (!AndroidUtils.canOpenDialog()) {return;}
        if (!isAdded() || G.fragmentActivity.isFinishing()) {
            return;
        }

        boolean shareLinkIsOn = false;
        RealmRoom room = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.ID, message.roomId).findFirst();
        if (room != null && room.getChannelRoom() != null && !room.getChannelRoom().isPrivate()) {
            shareLinkIsOn = true;
        }

        //TODO: optimize code
        List<String> items = new ArrayList<>();
        items.add(getString(R.string.replay_item_dialog));
        items.add(getString(R.string.share_item_dialog));
        if (shareLinkIsOn)
            items.add(getString(R.string.share_link_item_dialog));
        items.add(getString(R.string.forward_item_dialog));
        items.add(getString(R.string.delete_item_dialog));


        if (roomMessageType.toString().contains("IMAGE") || roomMessageType.toString().contains("VIDEO") || roomMessageType.toString().contains("GIF")) {
            items.add(getString(R.string.save_to_gallery));
        } else if (roomMessageType.toString().contains("AUDIO") || roomMessageType.toString().contains("VOICE")) {
            items.add(getString(R.string.save_to_Music));
        } else if (roomMessageType.toString().contains("FILE")) {
            items.add(getString(R.string.saveToDownload_item_dialog));
        }

        @ArrayRes int itemsRes = 0;
        switch (roomMessageType) {
            case TEXT:
                items.add(1, getString(R.string.copy_item_dialog));
                items.add(getString(R.string.edit_item_dialog));
                /*items.add(getString(R.string.report));*/
                break;
            case FILE_TEXT:
            case IMAGE_TEXT:
            case VIDEO_TEXT:
            case AUDIO_TEXT:
            case GIF_TEXT:
                items.add(1, getString(R.string.copy_item_dialog));
                items.add(getString(R.string.edit_item_dialog));
                break;
            case FILE:
            case IMAGE:
            case VIDEO:
            case AUDIO:
            case GIF:
                items.add(getString(R.string.edit_item_dialog));
                break;
            case VOICE:
            case LOCATION:
            case CONTACT:
            case STICKER:
            case LOG:
                break;
        }

        if (message.forwardedFrom != null || (rootView.findViewById(R.id.replayLayoutAboveEditText) != null && rootView.findViewById(R.id.replayLayoutAboveEditText).getVisibility() == View.VISIBLE)) {
            items.remove(getString(R.string.edit_item_dialog));
        }

        final boolean isPinedMessage = false;
        RealmRoom realmRoom = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.ID, message.roomId).findFirst();
        if (realmRoom != null) {
            /**
             * if user clicked on any message which he wasn't its sender, remove edit mList option
             */
            boolean showLayoutPin = RealmRoom.isPinedMessage(mRoomId, Long.parseLong(message.messageID));
            if (chatType == CHANNEL) {
                if (channelRole == ChannelChatRole.MEMBER) {
                    items.remove(getString(R.string.edit_item_dialog));
                    items.remove(getString(R.string.replay_item_dialog));
                    items.remove(getString(R.string.delete_item_dialog));
                } else {
                    showLayoutPin = true;
                }
                ChannelChatRole roleSenderMessage = RealmChannelRoom.detectMemberRole(mRoomId, parseLong(message.senderID));
                if (!G.authorHash.equals(message.authorHash)) {
                    if (channelRole == ChannelChatRole.MEMBER) {
                        items.remove(getString(R.string.delete_item_dialog));
                    } else if (channelRole == ChannelChatRole.MODERATOR) {
                        if (roleSenderMessage == ChannelChatRole.MODERATOR || roleSenderMessage == ChannelChatRole.ADMIN || roleSenderMessage == ChannelChatRole.OWNER) {
                            items.remove(getString(R.string.delete_item_dialog));
                        }
                    } else if (channelRole == ChannelChatRole.ADMIN) {
                        if (roleSenderMessage == ChannelChatRole.OWNER || roleSenderMessage == ChannelChatRole.ADMIN) {
                            items.remove(getString(R.string.delete_item_dialog));
                        }
                    }
                    if (channelRole != ChannelChatRole.OWNER) {
                        items.remove(getString(R.string.edit_item_dialog));
                    }
                }
            } else if (chatType == GROUP) {

                if (groupRole != GroupChatRole.MEMBER) {
                    showLayoutPin = true;
                }
                GroupChatRole roleSenderMessage = RealmGroupRoom.detectMemberRole(mRoomId, parseLong(message.senderID));
                if (!G.authorHash.equals(message.authorHash)) {
                    if (groupRole == GroupChatRole.MEMBER) {
                        items.remove(getString(R.string.delete_item_dialog));
                    } else if (groupRole == GroupChatRole.MODERATOR) {
                        if (roleSenderMessage == GroupChatRole.MODERATOR || roleSenderMessage == GroupChatRole.ADMIN || roleSenderMessage == GroupChatRole.OWNER) {
                            items.remove(getString(R.string.delete_item_dialog));
                        }
                    } else if (groupRole == GroupChatRole.ADMIN) {
                        if (roleSenderMessage == GroupChatRole.OWNER || roleSenderMessage == GroupChatRole.ADMIN) {
                            items.remove(getString(R.string.delete_item_dialog));
                        }
                    }
                    items.remove(getString(R.string.edit_item_dialog));
                }
            } else if (realmRoom.getReadOnly()) {
                items.remove(getString(R.string.replay_item_dialog));
            } else {
                if (!message.senderID.equalsIgnoreCase(Long.toString(G.userId))) {
                    items.remove(getString(R.string.edit_item_dialog));
                }
            }
            if (showLayoutPin && !isNotJoin) {
                items.add(getString(R.string.PIN));
                /*if (isPinedMessage) {
                    txtPin.setText(G.fragmentActivity.getResources().getString(R.string.unpin));
                    iconPin.setText(G.fragmentActivity.getResources().getString(R.string.md_unpin));
                }*/
            }

        }

        if (isChatReadOnly) {
            items.remove(getString(R.string.edit_item_dialog));
        }

        if (RealmRoom.isNotificationServices(mRoomId)) {
            items.remove(getString(R.string.report));
        }

        if (channelRole != ChannelChatRole.OWNER || groupRole != GroupChatRole.OWNER || isNotJoin) {
            items.add(getString(R.string.report));
        } else {
            items.remove(getString(R.string.report));
        }

        if (message.additionalData != null && message.additionalData.AdditionalType == AdditionalType.CARD_TO_CARD_MESSAGE) {
            items.clear();
            items.add(getString(R.string.replay_item_dialog));
            items.add(getString(R.string.delete_item_dialog));
        }

        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment().setData(items, -1, position -> {
            if (items.get(position).equals(getString(R.string.PIN))) {
                long _messageId = 0;
                if (!isPinedMessage) {
                    _messageId = Long.parseLong(message.messageID);
                    RealmRoom.updatePinedMessageDeleted(mRoomId, true);
                }
                sendRequestPinMessage(_messageId);
            } else if (items.get(position).equals(getString(R.string.replay_item_dialog))) {
                G.handler.postDelayed(() -> replay(message , false), 200);
            } else if (items.get(position).equals(getString(R.string.copy_item_dialog))) {
                ClipboardManager clipboard = (ClipboardManager) G.fragmentActivity.getSystemService(CLIPBOARD_SERVICE);
                String _text = message.forwardedFrom != null ? message.forwardedFrom.getMessage() : message.messageText;
                if (_text != null && _text.length() > 0) {
                    ClipData clip = ClipData.newPlainText("Copied Text", _text);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(context, R.string.text_copied, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, R.string.text_is_empty, Toast.LENGTH_SHORT).show();
                }
            } else if (items.get(position).equals(getString(R.string.share_item_dialog))) {
                shearedDataToOtherProgram(message);
            } else if (items.get(position).equals(getString(R.string.share_link_item_dialog))) {
                shearedLinkDataToOtherProgram(message);
            } else if (items.get(position).equals(getString(R.string.forward_item_dialog))) {
                mForwardMessages = new ArrayList<>(Arrays.asList(Parcels.wrap(message)));
                if (getActivity() instanceof  ActivityMain){
                    ((ActivityMain) getActivity()).setForwardMessage(true);
                }
                finishChat();
            } else if (items.get(position).equals(getString(R.string.delete_item_dialog))) {
                boolean bothDelete = RealmRoomMessage.isBothDelete(message.time);
                bothDeleteMessageId = new ArrayList<Long>();
                if (bothDelete) {
                    bothDeleteMessageId.add(Long.parseLong(message.messageID));
                }
                //final Realm realmCondition = Realm.getDefaultInstance();
                final ArrayList<Long> messageIds = new ArrayList<>();
                messageIds.add(Long.parseLong(message.messageID));
                if (chatType == ProtoGlobal.Room.Type.CHAT && !isCloudRoom && bothDeleteMessageId.size() > 0 && message.senderID.equalsIgnoreCase(Long.toString(G.userId))) {
                    // show both Delete check box
                    String delete;
                    String textCheckBox = G.context.getResources().getString(R.string.st_checkbox_delete) + " " + title;
                    if (HelperCalander.isPersianUnicode) {
                        delete = HelperCalander.convertToUnicodeFarsiNumber(G.context.getResources().getString(R.string.st_desc_delete, "1"));
                    } else {
                        delete = HelperCalander.convertToUnicodeFarsiNumber(G.context.getResources().getString(R.string.st_desc_delete, "the"));
                    }

                    if (!AndroidUtils.canOpenDialog()) {return;}
                    new MaterialDialog.Builder(G.fragmentActivity).limitIconToDefaultSize().content(delete).title(R.string.message).positiveText(R.string.ok).negativeText(R.string.cancel).onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            if (!dialog.isPromptCheckBoxChecked()) {
                                bothDeleteMessageId = null;
                            }

                            deleteMassage(getRealmChat(), message, messageIds, bothDeleteMessageId, chatType);
                        }
                    }).checkBoxPrompt(textCheckBox, false, null).show();

                } else {

                    new MaterialDialog.Builder(G.fragmentActivity).title(R.string.message).content(G.context.getResources().getString(R.string.st_desc_delete, "1")).positiveText(R.string.ok).negativeText(R.string.cancel).onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            bothDeleteMessageId = null;
                            deleteMassage(getRealmChat(), message, messageIds, bothDeleteMessageId, chatType);
                        }
                    }).show();
                }
            } else if (items.get(position).equals(getString(R.string.edit_item_dialog))) {
                // edit message
                // put message text to EditText
                if (message.messageText != null && !message.messageText.isEmpty()) {
                    edtChat.setText(message.messageText);
                    edtChat.setSelection(edtChat.getText().toString().length());
                    // put message object to edtChat's tag to obtain it later and
                    // found is user trying to edit a message
                }
                edtChat.setTag(message);
                isEditMessage = true;
                sendButtonVisibility(true);
                replay(message , true);
                G.handler.post(() -> editTextRequestFocus(edtChat));
            } else if (items.get(position).equals(getString(R.string.save_to_gallery))) {
                String filename;
                String filepath;
                ProtoGlobal.RoomMessageType fileType;
                if (message.forwardedFrom != null) {
                    fileType = message.forwardedFrom.getMessageType();
                    filename = message.forwardedFrom.getAttachment().getName();
                    filepath = message.forwardedFrom.getAttachment().getLocalFilePath() != null ? message.forwardedFrom.getAttachment().getLocalFilePath() : AndroidUtils.getFilePathWithCashId(message.forwardedFrom.getAttachment().getCacheId(), filename, fileType);
                } else {
                    fileType = message.messageType;
                    filename = message.getAttachment().name;
                    filepath = message.getAttachment().localFilePath != null ? message.getAttachment().localFilePath : AndroidUtils.getFilePathWithCashId(message.getAttachment().cashID, message.getAttachment().name, message.messageType);
                }
                if (new File(filepath).exists()) {
                    if (fileType.toString().contains(VIDEO.toString())) {
                        HelperSaveFile.saveFileToDownLoadFolder(filepath, filename, HelperSaveFile.FolderType.video, R.string.file_save_to_video_folder);
                    } else if (fileType.toString().contains(GIF.toString())) {
                        HelperSaveFile.saveFileToDownLoadFolder(filepath, filename, HelperSaveFile.FolderType.gif, R.string.file_save_to_picture_folder);
                    } else if (fileType.toString().contains(IMAGE.toString())) {
                        HelperSaveFile.saveFileToDownLoadFolder(filepath, filename, HelperSaveFile.FolderType.image, R.string.picture_save_to_galary);
                    }
                } else {
                    final ProtoGlobal.RoomMessageType _messageType = message.forwardedFrom != null ? message.forwardedFrom.getMessageType() : message.messageType;
                    String cacheId = message.forwardedFrom != null ? message.forwardedFrom.getAttachment().getCacheId() : message.getAttachment().cashID;
                    final String name = message.forwardedFrom != null ? message.forwardedFrom.getAttachment().getName() : message.getAttachment().name;
                    String fileToken = message.forwardedFrom != null ? message.forwardedFrom.getAttachment().getToken() : message.getAttachment().token;
                    String fileUrl = message.forwardedFrom != null ? message.forwardedFrom.getAttachment().getUrl() : message.getAttachment().url;
                    Long size = message.forwardedFrom != null ? message.forwardedFrom.getAttachment().getSize() : message.getAttachment().size;

                    if (cacheId == null) {
                        return;
                    }
                    ProtoFileDownload.FileDownload.Selector selector = ProtoFileDownload.FileDownload.Selector.FILE;

                    final String _path = AndroidUtils.getFilePathWithCashId(cacheId, name, _messageType);
                    if (fileToken != null && fileToken.length() > 0 && size > 0) {
                        HelperDownloadFile.getInstance().startDownload(message.messageType, message.messageID, fileToken, fileUrl, cacheId, name, size, selector, _path, 0, new HelperDownloadFile.UpdateListener() {
                            @Override
                            public void OnProgress(String path, int progress) {

                                if (progress == 100) {
                                    if (canUpdateAfterDownload) {
                                        G.handler.post(() -> {
                                            if (_messageType.toString().contains(VIDEO.toString())) {
                                                HelperSaveFile.saveFileToDownLoadFolder(_path, name, HelperSaveFile.FolderType.video, R.string.file_save_to_video_folder);
                                            } else if (_messageType.toString().contains(GIF.toString())) {
                                                HelperSaveFile.saveFileToDownLoadFolder(_path, name, HelperSaveFile.FolderType.gif, R.string.file_save_to_picture_folder);
                                            } else if (_messageType.toString().contains(IMAGE.toString())) {
                                                HelperSaveFile.saveFileToDownLoadFolder(_path, name, HelperSaveFile.FolderType.image, R.string.picture_save_to_galary);
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void OnError(String token) {

                            }
                        });
                    }
                    onDownloadAllEqualCashId(cacheId, message.messageID);
                }
            } else if (items.get(position).equals(getString(R.string.save_to_Music))) {
                String filename;
                String filepath;
                ProtoGlobal.RoomMessageType fileType;

                if (message.forwardedFrom != null) {
                    fileType = message.forwardedFrom.getMessageType();
                    filename = message.forwardedFrom.getAttachment().getName();
                    filepath = message.forwardedFrom.getAttachment().getLocalFilePath() != null ? message.forwardedFrom.getAttachment().getLocalFilePath() : AndroidUtils.getFilePathWithCashId(message.forwardedFrom.getAttachment().getCacheId(), filename, fileType);
                } else {
                    fileType = message.messageType;
                    filename = message.getAttachment().name;
                    filepath = message.getAttachment().localFilePath != null ? message.getAttachment().localFilePath : AndroidUtils.getFilePathWithCashId(message.getAttachment().cashID, message.getAttachment().name, message.messageType);
                }
                if (new File(filepath).exists()) {
                    HelperSaveFile.saveFileToDownLoadFolder(filepath, filename, HelperSaveFile.FolderType.music, R.string.save_to_music_folder);
                } else {
                    final ProtoGlobal.RoomMessageType _messageType = message.forwardedFrom != null ? message.forwardedFrom.getMessageType() : message.messageType;
                    String cacheId = message.forwardedFrom != null ? message.forwardedFrom.getAttachment().getCacheId() : message.getAttachment().cashID;
                    final String name = message.forwardedFrom != null ? message.forwardedFrom.getAttachment().getName() : message.getAttachment().name;
                    String fileToken = message.forwardedFrom != null ? message.forwardedFrom.getAttachment().getToken() : message.getAttachment().token;
                    String fileUrl = message.forwardedFrom != null ? message.forwardedFrom.getAttachment().getUrl() : message.getAttachment().url;
                    Long size = message.forwardedFrom != null ? message.forwardedFrom.getAttachment().getSize() : message.getAttachment().size;
                    if (cacheId == null) {
                        return;
                    }
                    ProtoFileDownload.FileDownload.Selector selector = ProtoFileDownload.FileDownload.Selector.FILE;

                    final String _path = AndroidUtils.getFilePathWithCashId(cacheId, name, _messageType);
                    if (fileToken != null && fileToken.length() > 0 && size > 0) {
                        HelperDownloadFile.getInstance().startDownload(message.messageType, message.messageID, fileToken, fileUrl, cacheId, name, size, selector, _path, 0, new HelperDownloadFile.UpdateListener() {
                            @Override
                            public void OnProgress(String path, int progress) {

                                if (progress == 100) {
                                    if (canUpdateAfterDownload) {
                                        G.handler.post(() -> HelperSaveFile.saveFileToDownLoadFolder(_path, name, HelperSaveFile.FolderType.music, R.string.save_to_music_folder));
                                    }
                                }
                            }

                            @Override
                            public void OnError(String token) {

                            }
                        });
                    }
                    onDownloadAllEqualCashId(cacheId, message.messageID);
                }
            } else if (items.get(position).equals(getString(R.string.saveToDownload_item_dialog))) {
                String filename;
                String filepath;
                ProtoGlobal.RoomMessageType fileType;

                if (message.forwardedFrom != null) {
                    fileType = message.forwardedFrom.getMessageType();
                    filename = message.forwardedFrom.getAttachment().getName();
                    filepath = message.forwardedFrom.getAttachment().getLocalFilePath() != null ? message.forwardedFrom.getAttachment().getLocalFilePath() : AndroidUtils.getFilePathWithCashId(message.forwardedFrom.getAttachment().getCacheId(), filename, fileType);
                } else {
                    fileType = message.messageType;
                    filename = message.getAttachment().name;
                    filepath = message.getAttachment().localFilePath != null ? message.getAttachment().localFilePath : AndroidUtils.getFilePathWithCashId(message.getAttachment().cashID, message.getAttachment().name, message.messageType);
                }

                if (new File(filepath).exists()) {
                    HelperSaveFile.saveFileToDownLoadFolder(filepath, filename, HelperSaveFile.FolderType.download, R.string.file_save_to_download_folder);
                } else {
                    final ProtoGlobal.RoomMessageType _messageType = message.forwardedFrom != null ? message.forwardedFrom.getMessageType() : message.messageType;
                    String cacheId = message.forwardedFrom != null ? message.forwardedFrom.getAttachment().getCacheId() : message.getAttachment().cashID;
                    final String name = message.forwardedFrom != null ? message.forwardedFrom.getAttachment().getName() : message.getAttachment().name;
                    String fileToken = message.forwardedFrom != null ? message.forwardedFrom.getAttachment().getToken() : message.getAttachment().token;
                    String fileUrl = message.forwardedFrom != null ? message.forwardedFrom.getAttachment().getUrl() : message.getAttachment().url;
                    Long size = message.forwardedFrom != null ? message.forwardedFrom.getAttachment().getSize() : message.getAttachment().size;

                    if (cacheId == null) {
                        return;
                    }
                    ProtoFileDownload.FileDownload.Selector selector = ProtoFileDownload.FileDownload.Selector.FILE;

                    final String _path = AndroidUtils.getFilePathWithCashId(cacheId, name, _messageType);
                    if (fileToken != null && fileToken.length() > 0 && size > 0) {
                        HelperDownloadFile.getInstance().startDownload(message.messageType, message.messageID, fileToken, fileUrl, cacheId, name, size, selector, _path, 0, new HelperDownloadFile.UpdateListener() {
                            @Override
                            public void OnProgress(String path, int progress) {

                                if (progress == 100) {
                                    if (canUpdateAfterDownload) {
                                        G.handler.post(() -> HelperSaveFile.saveFileToDownLoadFolder(_path, name, HelperSaveFile.FolderType.download, R.string.file_save_to_download_folder));
                                    }
                                }
                            }

                            @Override
                            public void OnError(String token) {

                            }
                        });
                    }

                    onDownloadAllEqualCashId(cacheId, message.messageID);
                }
            } else if (items.get(position).equals(getString(R.string.report))) {
                long messageId;
                if (message.forwardedFrom != null) {
                    messageId = message.forwardedFrom.getMessageId();
                } else {
                    messageId = Long.parseLong(message.messageID);
                }
                dialogReport(true, messageId);
            }
        });
        bottomSheetFragment.show(getFragmentManager(), "bottomSheet");
    }

    private void editTextRequestFocus(EditText editText) {
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    private void deleteMassage(Realm realm, final StructMessageInfo message, final ArrayList<Long> list, final ArrayList<Long> bothDeleteMessageId, final ProtoGlobal.Room.Type chatType) {

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                ArrayList list = new ArrayList();
                list.add(parseLong(message.messageID));
                deleteSelectedMessageFromAdapter(list);
            }
        });
        RealmRoomMessage.deleteSelectedMessages(realm, message.roomId, list, bothDeleteMessageId, chatType);
    }

    @Override
    public void onFailedMessageClick(View view, final StructMessageInfo message, final int pos) {
        final List<StructMessageInfo> failedMessages = mAdapter.getFailedMessages();
        new ResendMessage(G.fragmentActivity, new IResendMessage() {
            @Override
            public void deleteMessage() {
                if (pos >= 0 && mAdapter.getAdapterItemCount() > pos) {
                    mAdapter.remove(pos);
                    removeLayoutTimeIfNeed();
                }
            }

            @Override
            public void resendMessage() {

                for (int i = 0; i < failedMessages.size(); i++) {
                    if (failedMessages.get(i).messageID.equals(message.messageID)) {
                        if (failedMessages.get(i).attachment != null) {
                            if (HelperUploadFile.isUploading(message.messageID)) {
                                HelperUploadFile.reUpload(message.messageID);
                            }
                        }
                        break;
                    }
                }

                mAdapter.updateMessageStatus(parseLong(message.messageID), ProtoGlobal.RoomMessageStatus.SENDING);

                G.handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyItemChanged(mAdapter.findPositionByMessageId(Long.parseLong(message.messageID)));
                    }
                }, 300);


            }

            @Override
            public void resendAllMessages() {
                for (int i = 0; i < failedMessages.size(); i++) {
                    final int j = i;
                    G.handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.updateMessageStatus(parseLong(failedMessages.get(j).messageID), ProtoGlobal.RoomMessageStatus.SENDING);
                        }
                    }, 1000 * i);

                }
            }
        }, parseLong(message.messageID), failedMessages);
    }

    @Override
    public void onReplyClick(RealmRoomMessage replyMessage) {

        if (!goToPositionWithAnimation(replyMessage.getMessageId(), 1000)) {
            goToPositionWithAnimation(replyMessage.getMessageId() * (-1), 1000);
        }

    }

    @Override
    public void onForwardClick(StructMessageInfo message) {
        //finishChat();
        if (message == null) {
            mForwardMessages = getMessageStructFromSelectedItems();
            if (ll_AppBarSelected != null && ll_AppBarSelected.getVisibility() == View.VISIBLE) {
                mAdapter.deselect();
                if (isPinAvailable) pinedMessageLayout.setVisibility(View.VISIBLE);
                ll_AppBarSelected.setVisibility(View.GONE);
                clearReplyView();
            }
        } else {
            mForwardMessages = new ArrayList<>(Arrays.asList(Parcels.wrap(message)));
        }

        initAttachForward();
        itemAdapterBottomSheetForward();

        //new HelperFragment().removeAll(true);
    }

    @Override
    public void onSetAction(final long roomId, final long userIdR, final ProtoGlobal.ClientAction clientAction) {
        if (mRoomId == roomId && (userId != userIdR || (isCloudRoom))) {
            final String action = HelperGetAction.getAction(roomId, chatType, clientAction);

            RealmRoom.setAction(roomId, userIdR, action);

            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    if (action != null && !isBot) {
                        txtLastSeen.setText(action);
                    } else if (chatType == CHAT) {
                        if (isCloudRoom) {
                            txtLastSeen.setText(G.fragmentActivity.getResources().getString(R.string.chat_with_yourself));
                            goneCallButtons();
                        } else if (isBot) {
                            txtLastSeen.setText(G.fragmentActivity.getResources().getString(R.string.bot));
                        } else {
                            if (userStatus != null) {
                                if (userStatus.equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                                    txtLastSeen.setText(LastSeenTimeUtil.computeTime(chatPeerId, userTime, true, false));
                                } else {
                                    txtLastSeen.setText(userStatus);
                                }
                            }
                        }
                    } else if (chatType == GROUP) {
                        if (groupParticipantsCountLabel != null && HelperString.isNumeric(groupParticipantsCountLabel) && Integer.parseInt(groupParticipantsCountLabel) == 1) {
                            txtLastSeen.setText(groupParticipantsCountLabel + " " + G.fragmentActivity.getResources().getString(R.string.one_member_chat));
                        } else {
                            txtLastSeen.setText(groupParticipantsCountLabel + " " + G.fragmentActivity.getResources().getString(R.string.member_chat));
                        }
                    }
                    // change english number to persian number
                    if (HelperCalander.isPersianUnicode)
                        txtLastSeen.setText(convertToUnicodeFarsiNumber(txtLastSeen.getText().toString()));
                }
            });
        }
    }

    @Override
    public void onUserUpdateStatus(long userId, final long time, final String status) {
        if (chatType == CHAT && chatPeerId == userId && !isCloudRoom) {
            userStatus = AppUtils.getStatsForUser(status);
            setUserStatus(userStatus, time);
        }
    }

    @Override
    public void onLastSeenUpdate(final long userIdR, final String showLastSeen) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (chatType == CHAT && userIdR == chatPeerId && userId != userIdR) { // userId != userIdR means that , this isn't update status for own user
                    txtLastSeen.setText(showLastSeen);
                    //  avi.setVisibility(View.GONE);
                    //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    //    //txtLastSeen.setTextDirection(View.TEXT_DIRECTION_LTR);
                    //}
                    // change english number to persian number
                    if (HelperCalander.isPersianUnicode)
                        txtLastSeen.setText(convertToUnicodeFarsiNumber(txtLastSeen.getText().toString()));
                }
            }
        });
    }

    /**
     * GroupAvatar and ChannelAvatar
     */
    @Override
    public void onAvatarAdd(final long roomId, ProtoGlobal.Avatar avatar) {
        if (!isCloudRoom) {
            avatarHandler.getAvatar(new ParamWithAvatarType(imvUserPicture, roomId).avatarType(AvatarHandler.AvatarType.ROOM).showMain());
        }
    }

    @Override
    public void onAvatarAddError() {
        //empty
    }

    /**
     * Channel Message Reaction
     */

    @Override
    public void onChannelAddMessageReaction(final long roomId, final long messageId, final String reactionCounterLabel, final ProtoGlobal.RoomMessageReaction reaction, final long forwardedMessageId) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                mAdapter.updateVote(roomId, messageId, reactionCounterLabel, reaction, forwardedMessageId);
            }
        });
    }

    @Override
    public void onError(int majorCode, int minorCode) {
        //empty
    }

    @Override
    public void onChannelGetMessagesStats(final List<ProtoChannelGetMessagesStats.ChannelGetMessagesStatsResponse.Stats> statsList) {

        if (mAdapter != null) {
            for (final ProtoChannelGetMessagesStats.ChannelGetMessagesStatsResponse.Stats stats : statsList) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.updateMessageState(stats.getMessageId(), stats.getThumbsUpLabel(), stats.getThumbsDownLabel(), stats.getViewsLabel());
                    }
                });
            }
        }
    }

    @Override
    public void onChatDelete(long roomId) {
        if (roomId == mRoomId) {
            //  finish();
            finishChat();
        }
    }

    @Override
    public void onChatDeleteError(int majorCode, int minorCode) {

    }

    @Override
    public void onChangeState(final ConnectionState connectionState) {
        setConnectionText(connectionState);
    }

    @Override
    public void onBackgroundChanged(final String backgroundPath) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (imgBackGround != null) {
                    File f = new File(backgroundPath);
                    if (f.exists()) {
                        Drawable d = Drawable.createFromPath(f.getAbsolutePath());
                        //imgBackGround.setImageDrawable(d);
                        try {
                            imgBackGround.setBackgroundColor(Color.parseColor(backgroundPath));
                        } catch (Exception e) {
                        }

                    }
                }
            }
        });
    }

    private void updateShowItemInScreen() {
        /**
         * after comeback from other activity or background  the view should update
         */
        try {
            // this only notify item that show on the screen and no more
            recyclerView.getAdapter().notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * *************************** init layout ***************************
     */

    /**
     * detect that editText have character or just have space
     */
    private boolean isMessageWrote() {
        return !getWrittenMessage().isEmpty();
    }

    /**
     * get message and remove space from start and end
     */
    private String getWrittenMessage() {
        return edtChat.getText().toString().trim();
    }

    /**
     * clear history for this room
     */
    public void clearHistory(long roomId) {
        setDownBtnGone();
        saveMessageIdPositionState(0);
        RealmRoomMessage.clearHistoryMessage(roomId);
        addToView = true;

        if (botInit != null)
            botInit.updateCommandList(false, "clear", getActivity(), false, null, 0, false);
    }

    /**
     * message will be replied or no
     */
    private boolean isReply() {
        return mReplayLayout != null && mReplayLayout.getTag() instanceof StructMessageInfo;
    }

    private long replyMessageId() {
        if (isReply()) {
            return parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID);
        }
        return 0;
    }

    /**
     * if isReply() is true use from this method
     */
    private long getReplyMessageId() {
        return parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID);
    }

    /**
     * if isReply() is true use from this method
     * if replay layout is visible, gone it
     */
    private void clearReplyView() {
        if (mReplayLayout != null) {
            mReplayLayout.setTag(null);
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    mReplayLayout.setVisibility(View.GONE);
                }
            });
        }
    }

    private void hideProgress() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (prgWaiting != null) {
                    prgWaiting.setVisibility(View.GONE);
                    visibilityTextEmptyMessages();
                }
            }
        });
    }

    /**
     * clear all items that exist in view
     */
    private void clearAdapterItems() {
        mAdapter.clear();
        recyclerView.removeAllViews();
    }

    /**
     * client should send request for get user info because need to update user online timing
     */
    private void getUserInfo() {
        if (chatType == CHAT) {
            new RequestUserInfo().userInfo(chatPeerId);
        }
    }

    /**
     * call this method for set avatar for this room and this method
     * will be automatically detect id and chat type for show avatar
     */
    private void setAvatar() {
        long idForGetAvatar;
        AvatarHandler.AvatarType type;
        if (chatType == CHAT) {
            idForGetAvatar = chatPeerId;
            type = AvatarHandler.AvatarType.USER;
        } else {
            idForGetAvatar = mRoomId;
            type = AvatarHandler.AvatarType.ROOM;
        }

        final RealmRoom realmRoom = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
        if (realmRoom == null || !realmRoom.isValid()) {
            avatarHandler.getAvatar(new ParamWithAvatarType(imvUserPicture, chatPeerId).avatarSize(R.dimen.dp60).avatarType(AvatarHandler.AvatarType.USER).showMain());
        } else {
            Bitmap init = HelperImageBackColor.drawAlphabetOnPicture((int) context.getResources().getDimension(R.dimen.dp60), realmRoom.getInitials(), realmRoom.getColor());
            avatarHandler.getAvatar(new ParamWithInitBitmap(imvUserPicture, idForGetAvatar).initBitmap(init).showMain());
        }
    }

    private void resetAndGetFromEnd() {
        setDownBtnGone();
        firstUnreadMessageInChat = null;
        resetMessagingValue();
        countNewMessage = 0;
        txtNewUnreadMessage.setVisibility(View.GONE);
        txtNewUnreadMessage.getTextView().setText(countNewMessage + "");
        getMessages();
    }

    private ArrayList<Parcelable> getMessageStructFromSelectedItems() {
        ArrayList<Parcelable> messageInfos = new ArrayList<>(mAdapter.getSelectedItems().size());
        for (int item : mAdapter.getSelections()) {
            messageInfos.add(Parcels.wrap(mAdapter.getAdapterItem(item).mMessage));
        }
        return messageInfos;
    }

    /**
     * show current changeState for user if this room is chat
     *
     * @param status current changeState
     * @param time   if changeState is not online set latest online time
     */
    private void setUserStatus(final String status, final long time) {
        if (G.connectionState == ConnectionState.CONNECTING || G.connectionState == ConnectionState.WAITING_FOR_NETWORK) {
            setConnectionText(G.connectionState);
        } else {
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    userStatus = status;
                    userTime = time;
                    if (isCloudRoom) {
                        txtLastSeen.setText(G.fragmentActivity.getResources().getString(R.string.chat_with_yourself));
                        goneCallButtons();
                        //  avi.setVisibility(View.GONE);
                        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        //    //txtLastSeen.setTextDirection(View.TEXT_DIRECTION_LTR);
                        //}
                    } else if (isBot) {
                        txtLastSeen.setText(G.fragmentActivity.getResources().getString(R.string.bot));
                    } else {
                        if (status != null && txtLastSeen != null) {
                            if (status.equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                                txtLastSeen.setText(LastSeenTimeUtil.computeTime(chatPeerId, time, true, false));
                            } else {
                                txtLastSeen.setText(status);
                            }
                            // avi.setVisibility(View.GONE);
                            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            //    //txtLastSeen.setTextDirection(View.TEXT_DIRECTION_LTR);
                            //}
                            // change english number to persian number
                            if (HelperCalander.isPersianUnicode)
                                txtLastSeen.setText(convertToUnicodeFarsiNumber(txtLastSeen.getText().toString()));

                            checkAction();
                        }
                    }
                }
            });
        }
    }

    private void replay(StructMessageInfo item , boolean isEdit) {
        if (mAdapter != null) {
            Set<AbstractMessage> messages = mAdapter.getSelectedItems();
            // replay works if only one message selected
            inflateReplayLayoutIntoStub(item == null ? messages.iterator().next().mMessage : item , isEdit);

            ll_AppBarSelected.setVisibility(View.GONE);
            if (isPinAvailable) pinedMessageLayout.setVisibility(View.VISIBLE);


            mAdapter.deselect();

            edtChat.requestFocus();
            InputMethodManager imm = (InputMethodManager) G.context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(edtChat, InputMethodManager.SHOW_IMPLICIT);
            }

        }
    }

    private void checkAction() {
        //+Realm realm = Realm.getDefaultInstance();
        final RealmRoom realmRoom = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
        if (realmRoom != null && realmRoom.getActionState() != null) {
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    if (realmRoom.getActionState() != null && (chatType == GROUP || chatType == CHANNEL) || ((isCloudRoom || (!isCloudRoom && realmRoom.getActionStateUserId() != userId)))) {
                        txtLastSeen.setText(realmRoom.getActionState());
                        //  avi.setVisibility(View.VISIBLE);
                        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        //    //txtLastSeen.setTextDirection(View.TEXT_DIRECTION_LTR);
                        //}
                    } else if (chatType == CHAT) {
                        if (isCloudRoom) {
                            txtLastSeen.setText(G.fragmentActivity.getResources().getString(R.string.chat_with_yourself));
                            goneCallButtons();
                        } else {
                            if (userStatus != null) {
                                if (userStatus.equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                                    txtLastSeen.setText(LastSeenTimeUtil.computeTime(chatPeerId, userTime, true, false));
                                } else {
                                    txtLastSeen.setText(userStatus);
                                }
                            }
                        }
                        //  avi.setVisibility(View.GONE);
                        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        //    //txtLastSeen.setTextDirection(View.TEXT_DIRECTION_LTR);
                        //}
                    } else if (chatType == GROUP) {
                        //  avi.setVisibility(View.GONE);
                        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        //}
                        if (groupParticipantsCountLabel != null && HelperString.isNumeric(groupParticipantsCountLabel) && Integer.parseInt(groupParticipantsCountLabel) == 1) {
                            txtLastSeen.setText(groupParticipantsCountLabel + " " + G.fragmentActivity.getResources().getString(R.string.one_member_chat));
                        } else {
                            txtLastSeen.setText(groupParticipantsCountLabel + " " + G.fragmentActivity.getResources().getString(R.string.member_chat));
                        }
                    }
                    // change english number to persian number
                    if (HelperCalander.isPersianUnicode)
                        txtLastSeen.setText(convertToUnicodeFarsiNumber(txtLastSeen.getText().toString()));
                }
            });
        }
        //realm.close();
    }

    /**
     * change message status from sending to failed
     *
     * @param fakeMessageId messageId that create when created this message
     */
    private void makeFailed(final long fakeMessageId) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoomMessage.setStatusFailedInChat(realm, fakeMessageId);
            }
        });
        realm.close();
    }

    private void showErrorDialog(final int time) {

        if (dialogWait != null && dialogWait.isShowing()) {
            return;
        }

        boolean wrapInScrollView = true;
        dialogWait = new MaterialDialog.Builder(G.currentActivity).title(G.fragmentActivity.getResources().getString(R.string.title_limit_chat_to_unknown_contact)).customView(R.layout.dialog_remind_time, wrapInScrollView).positiveText(R.string.B_ok).autoDismiss(false).canceledOnTouchOutside(true).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        }).show();

        View v = dialogWait.getCustomView();
        if (v == null) {
            return;
        }
        //dialogWait.getActionButton(DialogAction.POSITIVE).setEnabled(true);
        final TextView remindTime = v.findViewById(R.id.remindTime);
        final TextView txtText = v.findViewById(R.id.textRemindTime);
        txtText.setText(G.fragmentActivity.getResources().getString(R.string.text_limit_chat_to_unknown_contact));
        CountDownTimer countWaitTimer = new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) ((millisUntilFinished) / 1000);
                long s = seconds % 60;
                long m = (seconds / 60) % 60;
                long h = (seconds / (60 * 60)) % 24;
                remindTime.setText(String.format("%d:%02d:%02d", h, m, s));
            }

            @Override
            public void onFinish() {
                remindTime.setText("00:00");
            }
        };
        countWaitTimer.start();
    }

    /**
     * update item progress
     */
    private void insertItemAndUpdateAfterStartUpload(int progress, final FileUploadStructure struct) {
        if (progress == 0) {
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    addItemAfterStartUpload(struct);
                }
            });
        } else if (progress == 100) {
            String messageId = struct.messageId + "";
            for (int i = mAdapter.getAdapterItemCount() - 1; i >= 0; i--) {
                AbstractMessage item = mAdapter.getAdapterItem(i);

                if (item.mMessage != null && item.mMessage.messageID.equals(messageId)) {
                    if (item.mMessage.hasAttachment()) {
                        item.mMessage.attachment.token = struct.token;
                    }
                    break;
                }
            }
        }
    }

    /**
     * add new item to view after start upload
     */
    private void addItemAfterStartUpload(final FileUploadStructure struct) {
        try {
            //Realm realm = Realm.getDefaultInstance();
            RealmRoomMessage roomMessage = getRealmChat().where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, struct.messageId).findFirst();
            if (roomMessage != null) {
                AbstractMessage message = null;

                if (mAdapter != null) {
                    message = mAdapter.getItemByFileIdentity(struct.messageId);

                    // message doesn't exists
                    if (message == null) {
                        switchAddItem(new ArrayList<>(Collections.singletonList(StructMessageInfo.convert(getRealmChat(), roomMessage))), false);
                        if (!G.userLogin) {
                            G.handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    makeFailed(struct.messageId);
                                }
                            }, 200);
                        }
                    }
                }
            }
            //realm.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * open profile for this room or user profile if room is chat
     */
    private void goToProfile() {
        if (getActivity() != null) {
            if (chatType == CHAT) {
                new HelperFragment(getActivity().getSupportFragmentManager(), FragmentContactsProfile.newInstance(mRoomId, chatPeerId, CHAT.toString())).setReplace(false).load();
            } else if (chatType == GROUP) {
                if (!isChatReadOnly) {
                    new HelperFragment(getActivity().getSupportFragmentManager(), FragmentGroupProfile.newInstance(mRoomId, isNotJoin)).setReplace(false).load();
                }
            } else if (chatType == CHANNEL) {
                if(!isNotJoin){
                    new HelperFragment(getActivity().getSupportFragmentManager(), FragmentChannelProfile.newInstance(mRoomId, isNotJoin)).setReplace(false).load();
                }
            }
        }
    }

    /**
     * copy text
     */
    public void copySelectedItemTextToClipboard() {
        String copyText = "";
        for (AbstractMessage _message : mAdapter.getSelectedItems()) {
            String text = _message.mMessage.forwardedFrom != null ? _message.mMessage.forwardedFrom.getMessage() : _message.mMessage.messageText;
            if (text == null || text.length() == 0) {
                continue;
            }

            if (copyText.length() > 0) {
                copyText = copyText + "\n" + text;
            } else {
                copyText = text;
            }
        }

        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText("Copied Text", copyText));

        mAdapter.deselect();
        ll_AppBarSelected.setVisibility(View.GONE);
        if (isPinAvailable) pinedMessageLayout.setVisibility(View.VISIBLE);
        clearReplyView();
    }

    /**
     * clear tag from edtChat and remove from view and delete from RealmRoomMessage
     */
    private void deleteItem(final long messageId, int position) {
        if (edtChat.getTag() != null && edtChat.getTag() instanceof StructMessageInfo) {
            if (Long.toString(messageId).equals(((StructMessageInfo) edtChat.getTag()).messageID)) {
                edtChat.setTag(null);
            }
        }

        if (position >= 0) {
            mAdapter.removeMessage(position);
        }
        RealmRoomMessage.deleteMessage(messageId);
    }

    private void onSelectRoomMenu(String message, long item) {
        switch (message) {
            case "txtMuteNotification":
                muteNotification(item);
                break;
            case "txtClearHistory":
                clearHistory(item);
                break;
            case "txtDeleteChat":
                deleteChat(item);
                break;
        }
    }

    private void deleteChat(final long chatId) {
        new RequestChatDelete().chatDelete(chatId);
    }

    private void muteNotification(final long roomId) {
        //+Realm realm = Realm.getDefaultInstance();

        isMuteNotification = !isMuteNotification;
        new RequestClientMuteRoom().muteRoom(roomId, isMuteNotification);

        if (isMuteNotification) {
            txtChannelMute.setText(R.string.unmute);
            iconMute.setVisibility(View.VISIBLE);
        } else {
            txtChannelMute.setText(R.string.mute);
            iconMute.setVisibility(View.GONE);
        }
        //realm.close();
    }

    private void removeLayoutUnreadMessage() {
        /**
         * remove unread layout message if already exist in chat list
         */
        if (isShowLayoutUnreadMessage) {
            for (int i = (mAdapter.getItemCount() - 1); i >= 0; i--) {
                if (mAdapter.getItem(i) instanceof UnreadMessage) {
                    mAdapter.remove(i);
                    break;
                }
            }
        }
        isShowLayoutUnreadMessage = false;
    }

    private void setDownBtnVisible() {
        if (llScrollNavigate != null)
            llScrollNavigate.setVisibility(View.VISIBLE);
        isScrollEnd = true;
    }

    private void setDownBtnGone() {
        if (llScrollNavigate != null)
            llScrollNavigate.setVisibility(View.GONE);
        isScrollEnd = false;
    }

    private void setBtnDownVisible(RealmRoomMessage realmRoomMessage) {
        if (isEnd()) {
            scrollToEnd();
        } else {
            if (countNewMessage == 0) {
                removeLayoutUnreadMessage();
                firstUnreadMessageInChat = realmRoomMessage;
            }
            countNewMessage++;
            setDownBtnVisible();
            if (txtNewUnreadMessage != null) {
                txtNewUnreadMessage.getTextView().setText(countNewMessage + "");
                txtNewUnreadMessage.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * check difference position to end of adapter
     *
     * @return true if lower than END_CHAT_LIMIT otherwise return false
     */
    private boolean isEnd() {
        if (addToView) {
            return ((recyclerView.getLayoutManager()) == null) || ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition() + END_CHAT_LIMIT > recyclerView.getAdapter().getItemCount();
        }
        return false;
        //return addToView && ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition() + END_CHAT_LIMIT > recyclerView.getAdapter().getItemCount();
    }

    /**
     * open fragment show image and show all image for this room
     */
    private void showImage(final StructMessageInfo messageInfo, View view) {
        if (getActivity() != null) {
            if (!isAdded() || getActivity().isFinishing()) {
                return;
            }

            // for gone app bar
            InputMethodManager imm = (InputMethodManager) G.fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            long selectedFileToken = parseLong(messageInfo.messageID);

            FragmentShowImage fragment = FragmentShowImage.newInstance();
            Bundle bundle = new Bundle();
            bundle.putLong("RoomId", mRoomId);
            bundle.putString("TYPE", messageInfo.messageType.toString());
            bundle.putLong("SelectedImage", selectedFileToken);
            fragment.setArguments(bundle);

            new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
        }
    }

    /**
     * scroll to bottom if unread not exits otherwise go to unread line
     * hint : just do in loaded message
     */
    private void scrollToEnd() {
        if (recyclerView == null || recyclerView.getAdapter() == null) return;
        if (recyclerView.getAdapter().getItemCount() < 2) {
            return;
        }

        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {

                try {
                    LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();

                    int lastPosition = llm.findLastVisibleItemPosition();
                    if (lastPosition + 30 > mAdapter.getItemCount()) {
                        recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                    } else {
                        recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        }, 300);
    }

    private void storingLastPosition() {
        try {
            if (recyclerView != null && mAdapter != null) {

                int firstVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                int lastVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                if (mAdapter.getItem(firstVisiblePosition) instanceof TimeItem || mAdapter.getItem(firstVisiblePosition) instanceof UnreadMessage) {
                    firstVisiblePosition++;
                }

                if (mAdapter.getItem(firstVisiblePosition) instanceof TimeItem || mAdapter.getItem(firstVisiblePosition) instanceof UnreadMessage) {
                    firstVisiblePosition++;
                }

                long lastScrolledMessageID = 0;

                if (mAdapter.getAdapterItemCount() - lastVisiblePosition > Config.STORE_MESSAGE_POSITION_LIMIT) {
                    lastScrolledMessageID = parseLong(mAdapter.getItem(firstVisiblePosition).mMessage.messageID);
                }

                saveMessageIdPositionState(lastScrolledMessageID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE && G.twoPaneMode) {
            G.maxChatBox = width - (width / 3) - ViewMaker.i_Dp(R.dimen.dp80);
        } else {
            G.maxChatBox = width - ViewMaker.i_Dp(R.dimen.dp80);
        }

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateShowItemInScreen();
            }
        }, 300);

        super.onConfigurationChanged(newConfig);
    }

    /**
     * save latest messageId position that user saw in chat before close it
     */
    private void saveMessageIdPositionState(final long messageId) {
        RealmRoom.setLastScrollPosition(mRoomId, messageId, firstVisiblePositionOffset);
    }

    /**
     * emoji initialization
     */
    private void setUpEmojiPopup() {
        switch (G.themeColor) {
            case Theme.BLUE_GREY_COMPLETE:
            case Theme.INDIGO_COMPLETE:
            case Theme.BROWN_COMPLETE:
            case Theme.GREY_COMPLETE:
            case Theme.TEAL_COMPLETE:
            case Theme.DARK:

                setEmojiColor(G.getTheme2BackgroundColor(), G.textTitleTheme, G.textTitleTheme);
                break;
            default:
                setEmojiColor(Color.parseColor("#eceff1"), "#61000000", "#61000000");


        }

    }

    private void setEmojiColor(int BackgroundColor, String iconColor, String dividerColor) {

        emojiPopup = EmojiPopup.Builder.fromRootView(rootView.findViewById(R.id.ac_ll_parent))
                .setOnEmojiBackspaceClickListener(new OnEmojiBackspaceClickListener() {

                    @Override
                    public void onEmojiBackspaceClick(View v) {

                    }
                }).setOnEmojiPopupShownListener(new OnEmojiPopupShownListener() {
                    @Override
                    public void onEmojiPopupShown() {
                        changeEmojiButtonImageResource(R.string.md_black_keyboard_with_white_keys);
                        isEmojiSHow = true;
                        if (botInit != null) botInit.close();
                    }
                }).setOnSoftKeyboardOpenListener(new OnSoftKeyboardOpenListener() {
                    @Override
                    public void onKeyboardOpen(final int keyBoardHeight) {
                        if (botInit != null) botInit.close();
                    }
                }).setOnEmojiPopupDismissListener(new OnEmojiPopupDismissListener() {
                    @Override
                    public void onEmojiPopupDismiss() {
                        changeEmojiButtonImageResource(R.string.md_emoticon_with_happy_face);
                        isEmojiSHow = false;
                    }
                }).setOnSoftKeyboardCloseListener(new OnSoftKeyboardCloseListener() {
                    @Override
                    public void onKeyboardClose() {
                        emojiPopup.dismiss();
                    }
                }).setOnStickerListener(new OnStickerListener() {
                    @Override
                    public void onItemSticker(StructItemSticker st) {

                        String additional = new Gson().toJson(new StructSendSticker(st.getId(), st.getName(), st.getGroupId(), st.getToken()));

                        final RealmRoomMessage[] rm = new RealmRoomMessage[1];
                        Long identity = AppUtils.makeRandomId();

                        int[] imageSize = AndroidUtils.getImageDimens(st.getUri());
                        getRealmChat().executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                rm[0] = RealmRoomMessage.makeAdditionalData(mRoomId, identity, st.getName(), additional, AdditionalType.STICKER, realm, ProtoGlobal.RoomMessageType.STICKER);
                                rm[0].setAttachment(identity, st.getUri(), imageSize[0], imageSize[1], new File(st.getUri()).length(), new File(st.getUri()).getName(), 0, LocalFileType.FILE);
                                rm[0].getAttachment().setToken(st.getToken());
                                rm[0].setAuthorHash(G.authorHash);
                                rm[0].setShowMessage(true);
                                rm[0].setCreateTime(TimeUtils.currentLocalTime());

                                if (isReply()) {
                                    rm[0].setReplyTo(realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID)).findFirst());
                                }
                            }
                        });

                        StructMessageInfo sm = StructMessageInfo.convert(getRealmChat(), rm[0]);
                        mAdapter.add(new StickerItem(mAdapter, chatType, FragmentChat.this).setMessage(sm));
                        scrollToEnd();

                        new ChatSendMessageUtil().build(chatType, mRoomId, rm[0]).sendMessage(identity + "");

                        if (isReply()) {
                            mReplayLayout.setTag(null);
                            mReplayLayout.setVisibility(View.GONE);
                        }

                    }
                })
                .setOnDownloadStickerListener(new OnDownloadStickerListener() {
                    @Override
                    public void downloadStickerItem(String token, String extention, long avatarSize, OnStickerItemDownloaded onStickerItemDownloaded) {
                        HelperDownloadSticker.stickerDownload(token, extention, avatarSize, ProtoFileDownload.FileDownload.Selector.FILE, RequestFileDownload.TypeDownload.STICKER, new HelperDownloadSticker.UpdateStickerListener() {

                            @Override
                            public void OnProgress(String path, String token, int progress) {
                                G.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (getActivity() == null || getActivity().isFinishing() || !isAdded())
                                            return;

                                        if (progress == 100) {
                                            onStickerItemDownloaded.onStickerItemDownload(token);
                                        }
                                    }
                                });
                            }

                            @Override
                            public void OnError(String token) {

                            }
                        });


                    }

                    @Override
                    public void downloadStickerAvatar(String token, String extention, long avatarSize, OnStickerAvatarDownloaded onStickerAvatarDownloaded) {
                        HelperDownloadSticker.stickerDownload(token, extention, avatarSize, ProtoFileDownload.FileDownload.Selector.FILE, RequestFileDownload.TypeDownload.STICKER, new HelperDownloadSticker.UpdateStickerListener() {
                            @Override
                            public void OnProgress(String path, String token, int progress) {
                                G.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (getActivity() == null || getActivity().isFinishing() || !isAdded())
                                            return;

                                        if (progress == 100) {
                                            onStickerAvatarDownloaded.onStickerAvatarDownload(token);
                                        }
                                    }
                                });

                            }

                            @Override
                            public void OnError(String token) {

                            }
                        });

                    }
                })
                .setOpenPageSticker(new OnOpenPageStickerListener() {
                    @Override
                    public void addSticker(String page) {
                        if (getActivity() != null) {
                            new HelperFragment(getActivity().getSupportFragmentManager(), FragmentSettingAddStickers.newInstance()).setReplace(false).load();
                        }
                    }

                    @Override
                    public void openSetting(ArrayList<StructGroupSticker> stickerList, ArrayList<StructItemSticker> recentStickerList) {
                        if (getActivity() != null) {
                            new HelperFragment(getActivity().getSupportFragmentManager(), FragmentSettingRemoveStickers.newInstance(data, recentStickerList)).setReplace(false).load();
                        }
                    }
                })
                .setBackgroundColor(BackgroundColor)
                .setIconColor(Color.parseColor(iconColor))
                .setDividerColor(Color.parseColor(dividerColor))
                .build(edtChat);

    }

    private void changeEmojiButtonImageResource(@StringRes int drawableResourceId) {
        imvSmileButton.setText(drawableResourceId);
    }

    /**
     * *************************** draft ***************************
     */
    private void setDraftMessage(final int requestCode) {

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (listPathString == null) return;
                if (listPathString.size() < 1) return;
                if (listPathString.get(0) == null) return;
                String filename = listPathString.get(0).substring(listPathString.get(0).lastIndexOf("/") + 1);
                switch (requestCode) {
                    case AttachFile.request_code_TAKE_PICTURE:
                        txtFileNameForSend.setText(G.fragmentActivity.getResources().getString(R.string.image_selected_for_send) + "\n" + filename);
                        break;
                    case AttachFile.requestOpenGalleryForImageMultipleSelect:
                        if (listPathString.size() == 1) {
                            if (!listPathString.get(0).toLowerCase().endsWith(".gif")) {
                                txtFileNameForSend.setText(G.fragmentActivity.getResources().getString(R.string.image_selected_for_send) + "\n" + filename);
                            } else {
                                txtFileNameForSend.setText(G.fragmentActivity.getResources().getString(R.string.gif_selected_for_send) + "\n" + filename);
                            }
                        } else {
                            txtFileNameForSend.setText(listPathString.size() + G.fragmentActivity.getResources().getString(R.string.image_selected_for_send) + "\n" + filename);
                        }

                        break;

                    case AttachFile.requestOpenGalleryForVideoMultipleSelect:
                        txtFileNameForSend.setText(G.fragmentActivity.getResources().getString(R.string.multi_video_selected_for_send) + "\n" + filename);
                        break;
                    case request_code_VIDEO_CAPTURED:

                        if (listPathString.size() == 1) {
                            txtFileNameForSend.setText(G.fragmentActivity.getResources().getString(R.string.video_selected_for_send));
                        } else {
                            txtFileNameForSend.setText(listPathString.size() + G.fragmentActivity.getResources().getString(R.string.video_selected_for_send) + "\n" + filename);
                        }
                        break;

                    case AttachFile.request_code_pic_audi:
                        if (listPathString.size() == 1) {
                            txtFileNameForSend.setText(G.fragmentActivity.getResources().getString(R.string.audio_selected_for_send) + "\n" + filename);
                        } else {
                            txtFileNameForSend.setText(listPathString.size() + G.fragmentActivity.getResources().getString(R.string.audio_selected_for_send) + "\n" + filename);
                        }
                        break;
                    case AttachFile.request_code_pic_file:
                        txtFileNameForSend.setText(G.fragmentActivity.getResources().getString(R.string.file_selected_for_send) + "\n" + filename);
                        break;
                    case AttachFile.request_code_open_document:
                        if (listPathString.size() == 1) {
                            txtFileNameForSend.setText(G.fragmentActivity.getResources().getString(R.string.file_selected_for_send) + "\n" + filename);
                        }
                        break;
                    case AttachFile.request_code_paint:
                        if (listPathString.size() == 1) {
                            txtFileNameForSend.setText(G.fragmentActivity.getResources().getString(R.string.pain_selected_for_send) + "\n" + filename);
                        }
                        break;
                    case AttachFile.request_code_contact_phone:
                        txtFileNameForSend.setText(G.fragmentActivity.getResources().getString(R.string.phone_selected_for_send) + "\n" + filename);
                        break;
                    case IntentRequests.REQ_CROP:
                        if (!listPathString.get(0).toLowerCase().endsWith(".gif")) {
                            txtFileNameForSend.setText(G.fragmentActivity.getResources().getString(R.string.crop_selected_for_send) + "\n" + filename);
                        } else {
                            txtFileNameForSend.setText(G.fragmentActivity.getResources().getString(R.string.gif_selected_for_send) + "\n" + filename);
                        }
                        break;
                }
            }
        }, 100);
    }

    private void showDraftLayout() {
        /**
         * onActivityResult happens before onResume, so Presenter does not have View attached. because use handler
         */
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isCardToCardMessage) {
                    if (listPathString == null) return;
                    if (listPathString.size() < 1) return;
                    if (listPathString.get(0) == null) return;
                }

                if (ll_attach_text == null) { // have null error , so reInitialize for avoid that

                    ll_attach_text = rootView.findViewById(R.id.ac_ll_attach_text);
                    layoutAttachBottom = rootView.findViewById(R.id.ll_chatRoom_send);
                    imvSendButton = rootView.findViewById(R.id.btn_chatRoom_send);
                }

                txtFileNameForSend = rootView.findViewById(R.id.ac_txt_file_neme_for_sending);
                if (isCardToCardMessage) {
                    txtFileNameForSend.setText(R.string.cardToCardRequest);
                }

                Utils.darkModeHandler(txtFileNameForSend);

                ll_attach_text.setVisibility(View.VISIBLE);
                // set maxLength  when layout attachment is visible
                edtChat.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Config.MAX_TEXT_ATTACHMENT_LENGTH)});

                sendButtonVisibility(true);
            }
        }, 100);
    }

    private void sendButtonVisibility(boolean visibility) {
        layoutAttachBottom.setVisibility(visibility ? View.GONE : View.VISIBLE);
        imvSendButton.clearAnimation();
        imvSendButton.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    /**
     * *************************** inner classes ***************************
     */

    private void setDraft() {
        if (!isNotJoin) {
            if (edtChat == null) {
                return;
            }

            if (mReplayLayout != null && mReplayLayout.getVisibility() == View.VISIBLE) {
                StructMessageInfo info = ((StructMessageInfo) mReplayLayout.getTag());
                if (info != null) {
                    replyToMessageId = parseLong(info.messageID);
                }
            } else {
                replyToMessageId = 0;
            }

            String message = edtChat.getText().toString();
            if (!message.trim().isEmpty() || ((mReplayLayout != null && mReplayLayout.getVisibility() == View.VISIBLE))) {
                hasDraft = true;
                RealmRoom.setDraft(mRoomId, message, replyToMessageId, chatType);
            } else {
                clearDraftRequest();
            }
        }
    }

    private void getDraft() {
        //+Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
        if (realmRoom != null) {
            RealmRoomDraft draft = realmRoom.getDraft();
            if (draft != null && draft.getMessage().length() > 0) {
                hasDraft = true;
                edtChat.setText(draft.getMessage());
            }
        }
        //realm.close();
//        clearLocalDraft();
    }

    private void clearLocalDraft() {
        RealmRoom.clearDraft(mRoomId);
    }

    private void clearDraftRequest() {
        if (hasDraft) {
            hasDraft = false;
            if (chatType == CHAT) {
                new RequestChatUpdateDraft().chatUpdateDraft(mRoomId, "", 0);
            } else if (chatType == GROUP) {
                new RequestGroupUpdateDraft().groupUpdateDraft(mRoomId, "", 0);
            } else if (chatType == CHANNEL) {
                new RequestChannelUpdateDraft().channelUpdateDraft(mRoomId, "", 0);
            }

            clearLocalDraft();
        }
    }

    /**
     * *************************** sheared data ***************************
     */


    private void insertShearedData() {
        /**
         * run this method with delay , because client get local message with delay
         * for show messages with async changeState and before run getLocalMessage this shared
         * item added to realm and view, and after that getLocalMessage called and new item
         * got from realm and add to view again but in this time from getLocalMessage method
         */
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (HelperGetDataFromOtherApp.hasSharedData) {
                    HelperGetDataFromOtherApp.hasSharedData = false;

                    for (HelperGetDataFromOtherApp.SharedData sharedData : HelperGetDataFromOtherApp.sharedList) {

                        edtChat.setText(sharedData.message);

                        switch (sharedData.fileType) {
                            case message:
                                imvSendButton.performClick();
                                break;
                            case video:
                                if (HelperGetDataFromOtherApp.sharedList.size() == 1 && (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && (sharedPreferences.getInt(SHP_SETTING.KEY_COMPRESS, 1) == 1))) {
                                    final String savePathVideoCompress = G.DIR_TEMP + "/VIDEO_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".mp4";
                                    mainVideoPath = sharedData.address;
                                    if (mainVideoPath == null) {
                                        return;
                                    }
                                    G.handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            new VideoCompressor().execute(mainVideoPath, savePathVideoCompress);
                                        }
                                    }, 200);
                                    sendMessage(request_code_VIDEO_CAPTURED, savePathVideoCompress);
                                } else {
                                    compressedPath.put(sharedData.address, true);
                                    sendMessage(request_code_VIDEO_CAPTURED, sharedData.address);
                                }
                                break;
                            case file:
                                sendMessage(AttachFile.request_code_open_document, sharedData.address);
                                break;
                            case audio:
                                sendMessage(AttachFile.request_code_pic_audi, sharedData.address);
                                break;
                            case image:
                                sendMessage(AttachFile.request_code_TAKE_PICTURE, sharedData.address);
                                break;
                        }

                        edtChat.setText("");
                    }

                    HelperGetDataFromOtherApp.sharedList.clear();

                }
            }
        }, 300);
    }

    private void shearedLinkDataToOtherProgram(StructMessageInfo messageInfo) {
        // when chat is channel this method will be called

        if (messageInfo == null) return;
        RealmRoom room = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.ID, messageInfo.roomId).findFirst();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "https://igap.net/" + room.getChannelRoom().getUsername() + "/" + messageInfo.messageID);
        startActivity(Intent.createChooser(intent, G.context.getString(R.string.share_link_item_dialog)));
    }

    private void shearedDataToOtherProgram(StructMessageInfo messageInfo) {

        if (messageInfo == null) return;

        try {
            isShareOk = true;
            Intent intent = new Intent(Intent.ACTION_SEND);
            String chooserDialogText = "";

            ProtoGlobal.RoomMessageType type = messageInfo.forwardedFrom != null ? messageInfo.forwardedFrom.getMessageType() : messageInfo.messageType;

            switch (type.toString()) {

                case "TEXT":
                    intent.setType("text/plain");
                    String message = messageInfo.forwardedFrom != null ? messageInfo.forwardedFrom.getMessage() : messageInfo.messageText;
                    intent.putExtra(Intent.EXTRA_TEXT, message);
                    break;
                case "CONTACT":
                    intent.setType("text/plain");
                    String messageContact;
                    if (messageInfo.forwardedFrom != null) {
                        messageContact = messageInfo.forwardedFrom.getRoomMessageContact().getFirstName() + " " + messageInfo.forwardedFrom.getRoomMessageContact().getLastName() + "\n" + messageInfo.forwardedFrom.getRoomMessageContact().getLastPhoneNumber();
                    } else {
                        messageContact = messageInfo.userInfo.firstName + "\n" + messageInfo.userInfo.phone;
                    }
                    intent.putExtra(Intent.EXTRA_TEXT, messageContact);
                    break;
                case "LOCATION":
                    String imagePathPosition = messageInfo.forwardedFrom != null ?
                            AppUtils.getLocationPath(messageInfo.forwardedFrom.getLocation().getLocationLat(), messageInfo.forwardedFrom.getLocation().getLocationLong()) :
                            AppUtils.getLocationPath(messageInfo.location.getLocationLat(), messageInfo.location.getLocationLong());
                    intent.setType("image/*");
                    if (imagePathPosition != null) {
                        intent.putExtra(Intent.EXTRA_STREAM, AppUtils.createtUri(new File(imagePathPosition)));
                    }
                    break;
                case "VOICE":
                case "AUDIO":
                case "AUDIO_TEXT":
                    intent.setType("audio/*");
                    AppUtils.shareItem(intent, messageInfo);
                    chooserDialogText = G.fragmentActivity.getResources().getString(R.string.share_audio_file);
                    break;
                case "IMAGE":
                case "IMAGE_TEXT":
                    intent.setType("image/*");
                    AppUtils.shareItem(intent, messageInfo);
                    chooserDialogText = G.fragmentActivity.getResources().getString(R.string.share_image);
                    break;
                case "VIDEO":
                case "VIDEO_TEXT":
                    intent.setType("video/*");
                    AppUtils.shareItem(intent, messageInfo);
                    chooserDialogText = G.fragmentActivity.getResources().getString(R.string.share_video_file);
                    break;
                case "FILE":
                case "FILE_TEXT":
                    String mfilepath = messageInfo.forwardedFrom != null ? messageInfo.forwardedFrom.getAttachment().getLocalFilePath() : messageInfo.attachment.getLocalFilePath();
                    if (mfilepath != null) {
                        Uri uri = AppUtils.createtUri(new File(mfilepath));

                        ContentResolver cR = context.getContentResolver();
                        MimeTypeMap mime = MimeTypeMap.getSingleton();
                        String mimeType = mime.getExtensionFromMimeType(cR.getType(uri));

                        if (mimeType == null || mimeType.length() < 1) {
                            mimeType = "*/*";
                        } else {
                            mimeType = "application/*" + mimeType;
                        }
                        intent.setType(mimeType);
                        intent.putExtra(Intent.EXTRA_STREAM, uri);
                        chooserDialogText = G.fragmentActivity.getResources().getString(R.string.share_file);
                    } else {

                        isShareOk = false;
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, R.string.file_not_download_yet, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    break;
            }

            if (!isShareOk) return;

            startActivity(Intent.createChooser(intent, chooserDialogText));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * init layout for hashtag up and down
     */
    private void initLayoutHashNavigationCallback() {

        hashListener = new OnComplete() {
            @Override
            public void complete(boolean result, String text, String messageId) {

                if (!initHash) {
                    initHash = true;
                    initHashView();
                }

                searchHash.setHashString(text);
                searchHash.setPosition(messageId);
                ll_navigateHash.setVisibility(View.VISIBLE);
                viewAttachFile.setVisibility(View.GONE);

                if (chatType == CHANNEL && channelRole == ChannelChatRole.MEMBER) {
                    if (layoutMute != null) layoutMute.setVisibility(View.GONE);
                }
            }
        };
    }

    /**
     * init layout hashtak for up and down
     */
    private void initHashView() {


        ll_navigateHash = rootView.findViewById(R.id.ac_ll_hash_navigation);
        btnUpHash = rootView.findViewById(R.id.ac_btn_hash_up);
        btnDownHash = rootView.findViewById(R.id.ac_btn_hash_down);
        txtHashCounter = rootView.findViewById(R.id.ac_txt_hash_counter);

        if (G.isDarkTheme){
            txtHashCounter.setTextColor(getContext().getResources().getColor(R.color.white));
        }

        searchHash = new SearchHash();

        btnHashLayoutClose = rootView.findViewById(R.id.ac_btn_hash_close);
        if (!G.isDarkTheme) btnHashLayoutClose.setTextColor(Color.parseColor(G.appBarColor));
        btnHashLayoutClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ll_navigateHash.setVisibility(View.GONE);

                mAdapter.toggleSelection(searchHash.lastMessageId, false, null);

                if (chatType == CHANNEL && channelRole == ChannelChatRole.MEMBER && !isNotJoin) {
                    layoutMute.setVisibility(View.VISIBLE);
                } else {
                    if (!isNotJoin) viewAttachFile.setVisibility(View.VISIBLE);
                }
            }
        });

        btnUpHash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                searchHash.upHash();
            }
        });

        btnDownHash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchHash.downHash();
            }
        });
    }

    /**
     * manage need showSpamBar for user or no
     */
    private void showSpamBar() {
        /**
         * use handler for run async
         */
        G.handler.post(new Runnable() {
            @Override
            public void run() {

                //+Realm realm = Realm.getDefaultInstance();
                RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(getRealmChat(), chatPeerId);
                RealmContacts realmContacts = getRealmChat().where(RealmContacts.class).equalTo(RealmContactsFields.ID, chatPeerId).findFirst();
                if (realmRegisteredInfo != null && realmRegisteredInfo.getId() != G.userId) {
                    if (phoneNumber == null) {
                        if (realmContacts == null && chatType == CHAT && !isChatReadOnly) {
                            initSpamBarLayout(realmRegisteredInfo);
                            vgSpamUser.setVisibility(View.VISIBLE);
                        }
                    }

                    if (realmRegisteredInfo.getId() != G.userId) {
                        if (!realmRegisteredInfo.getDoNotshowSpamBar()) {

                            if (realmRegisteredInfo.isBlockUser()) {
                                initSpamBarLayout(realmRegisteredInfo);
                                blockUser = true;
                                txtSpamUser.setText(G.fragmentActivity.getResources().getString(R.string.un_block_user));
                                vgSpamUser.setVisibility(View.VISIBLE);
                            } else {
                                if (vgSpamUser != null) {
                                    vgSpamUser.setVisibility(View.GONE);
                                }
                            }
                        }
                    }

                    if (realmContacts != null && realmRegisteredInfo.getId() != G.userId) {
                        if (realmContacts.isBlockUser()) {
                            if (!realmRegisteredInfo.getDoNotshowSpamBar()) {
                                initSpamBarLayout(realmRegisteredInfo);
                                blockUser = true;
                                txtSpamUser.setText(G.fragmentActivity.getResources().getString(R.string.un_block_user));
                                vgSpamUser.setVisibility(View.VISIBLE);
                            } else {
                                initSpamBarLayout(realmRegisteredInfo);
                                blockUser = true;
                                txtSpamUser.setText(G.fragmentActivity.getResources().getString(R.string.un_block_user));
                                vgSpamUser.setVisibility(View.VISIBLE);
                            }
                        } else {
                            if (vgSpamUser != null) {
                                vgSpamUser.setVisibility(View.GONE);
                            }
                        }
                    }
                }
                //realm.close();
            }
        });
    }

    /**
     * init spamBar layout
     */
    private void initSpamBarLayout(final RealmRegisteredInfo registeredInfo) {
        vgSpamUser = rootView.findViewById(R.id.layout_add_contact);
        txtSpamUser = rootView.findViewById(R.id.chat_txt_addContact);
        txtSpamClose = rootView.findViewById(R.id.chat_txt_close);
        txtSpamClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vgSpamUser.setVisibility(View.GONE);
                if (registeredInfo != null) {

                    //+Realm realm = Realm.getDefaultInstance();

                    getRealmChat().executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            registeredInfo.setDoNotshowSpamBar(true);
                        }
                    });

                    //realm.close();
                }
            }
        });

        txtSpamUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (blockUser) {
                    G.onUserContactsUnBlock = new OnUserContactsUnBlock() {
                        @Override
                        public void onUserContactsUnBlock(final long userId) {
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    blockUser = false;
                                    if (userId == chatPeerId) {
                                        txtSpamUser.setText(G.fragmentActivity.getResources().getString(R.string.block_user));
                                    }
                                }
                            });
                        }
                    };

                    new MaterialDialog.Builder(G.fragmentActivity).title(R.string.unblock_the_user).content(R.string.unblock_the_user_text).positiveText(R.string.ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            new RequestUserContactsUnblock().userContactsUnblock(chatPeerId);
                        }
                    }).negativeText(R.string.cancel).show();
                } else {

                    G.onUserContactsBlock = new OnUserContactsBlock() {
                        @Override
                        public void onUserContactsBlock(final long userId) {
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    blockUser = true;
                                    if (userId == chatPeerId) {
                                        txtSpamUser.setText(G.fragmentActivity.getResources().getString(R.string.un_block_user));
                                    }
                                }
                            });
                        }
                    };

                    new MaterialDialog.Builder(G.fragmentActivity).title(R.string.block_the_user).content(R.string.block_the_user_text).positiveText(R.string.ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            new RequestUserContactsBlock().userContactsBlock(chatPeerId);
                        }
                    }).negativeText(R.string.cancel).show();

                }
            }
        });
    }

    /**
     * initialize bottomSheet for use in attachment for forward
     */


    private void initAttachForward() {
        canClearForwardList = true;
        multiForwardList = new ArrayList<>();
        viewBottomSheetForward = getActivity().getLayoutInflater().inflate(R.layout.bottom_sheet_forward, null);

        fastItemAdapterForward = new FastItemAdapter();

        EditText edtSearch = viewBottomSheetForward.findViewById(R.id.edtSearch);
        edtSearch.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        final AppCompatTextView textSend = viewBottomSheetForward.findViewById(R.id.txtSend);
        textSend.setVisibility(View.GONE);
        final RecyclerView rcvItem = viewBottomSheetForward.findViewById(R.id.rcvBottomSheetForward);
        rcvItem.setLayoutManager(new GridLayoutManager(rcvItem.getContext(), 4, GridLayoutManager.VERTICAL, false));
        rcvItem.setItemViewCacheSize(100);
        rcvItem.setAdapter(fastItemAdapterForward);

        if (G.isDarkTheme){
            textSend.setBackgroundColor(getContext().getResources().getColor(R.color.gray));
            edtSearch.setTextColor(getContext().getResources().getColor(R.color.white));
            edtSearch.setBackground(getContext().getResources().getDrawable(R.drawable.fast_sorward_dark));
        }else {
            textSend.setBackgroundColor(getContext().getResources().getColor(R.color.green));
            edtSearch.setBackground(getContext().getResources().getDrawable(R.drawable.fast_sorward_light));
            edtSearch.setTextColor(getContext().getResources().getColor(R.color.black));

        }

        bottomSheetDialogForward = new BottomSheetDialog(getActivity(),G.isDarkTheme ? R.style.BaseBottomSheetDialog : R.style.BaseBottomSheetDialogLight);
        bottomSheetDialogForward.setContentView(viewBottomSheetForward);
        final BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) viewBottomSheetForward.getParent());

        fastItemAdapterForward.getItemFilter().withFilterPredicate(new IItemAdapter.Predicate<ItemBottomSheetForward>() {
            @Override
            public boolean filter(ItemBottomSheetForward item, CharSequence constraint) {
                return item.structBottomSheetForward.getDisplayName().toLowerCase().contains(String.valueOf(constraint));
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fastItemAdapterForward.filter(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        viewBottomSheetForward.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mBehavior.setPeekHeight(viewBottomSheetForward.getHeight());
                    viewBottomSheetForward.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
        //height is ready


        textSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canClearForwardList = false;
                forwardToChatRoom(mListForwardNotExict);
                prgWaiting.setVisibility(View.VISIBLE);
                viewBottomSheetForward.setEnabled(false);
            }
        });

        onForwardBottomSheet = structBottomSheetForward -> {

            if (structBottomSheetForward.isNotExistRoom()) {
                if (structBottomSheetForward.isChecked()) {
                    mListForwardNotExict.add(structBottomSheetForward);
                } else {
                    mListForwardNotExict.remove(structBottomSheetForward);
                }
            } else {
                if (structBottomSheetForward.isChecked()) {
                    multiForwardList.add(structBottomSheetForward.getId());
                } else {
                    multiForwardList.remove(structBottomSheetForward.getId());
                }
            }

            if (mListForwardNotExict.size() + multiForwardList.size() > 0) {
                textSend.setVisibility(View.VISIBLE);
            } else {
                textSend.setVisibility(View.GONE);
            }
        };

        bottomSheetDialogForward.show();

        bottomSheetDialogForward.setOnDismissListener(dialog -> {
            if (canClearForwardList) {
                removeForwardModeFromRoomList();
                mForwardMessages = null;
            }
        });
    }

    /**
     * initialize bottomSheet for use in attachment
     */
    private void initAttach() {

        fastItemAdapter = new FastItemAdapter();
        viewBottomSheet = G.fragmentActivity.getLayoutInflater().inflate(R.layout.bottom_sheet, null);

        send = viewBottomSheet.findViewById(R.id.txtSend);
        txtCountItem = viewBottomSheet.findViewById(R.id.txtNumberItem);
        View camera = viewBottomSheet.findViewById(R.id.camera);
        View picture = viewBottomSheet.findViewById(R.id.picture);
        View video = viewBottomSheet.findViewById(R.id.video);
        View music = viewBottomSheet.findViewById(R.id.music);
        boolean isCardToCardEnabled = false;
        if (!isBot && chatType == CHAT) {
            isCardToCardEnabled = true;
        }
        View close = viewBottomSheet.findViewById(R.id.close);
        View file = viewBottomSheet.findViewById(R.id.file);
        /*View paint = viewBottomSheet.findViewById(R.id.paint);*/
        View location = viewBottomSheet.findViewById(R.id.location);
        View contact = viewBottomSheet.findViewById(R.id.contact);


        TextView txtCamera = viewBottomSheet.findViewById(R.id.txtCamera);
        TextView textPicture = viewBottomSheet.findViewById(R.id.textPicture);
        TextView txtVideo = viewBottomSheet.findViewById(R.id.txtVideo);
        TextView txtMusic = viewBottomSheet.findViewById(R.id.txtMusic);
        TextView txtFile = viewBottomSheet.findViewById(R.id.txtFile);
        /*TextView txtPaint = viewBottomSheet.findViewById(R.id.txtPaint);*/
        TextView txtLocation = viewBottomSheet.findViewById(R.id.txtLocation);
        TextView txtContact = viewBottomSheet.findViewById(R.id.txtContact);
        TextView txtCamera2 = viewBottomSheet.findViewById(R.id.txtCamera2);
        TextView textPicture2 = viewBottomSheet.findViewById(R.id.textPicture2);
        TextView txtVideo2 = viewBottomSheet.findViewById(R.id.txtVideo2);
        TextView txtMusic2 = viewBottomSheet.findViewById(R.id.txtMusic2);
        TextView txtFile2 = viewBottomSheet.findViewById(R.id.txtFile2);
        /*TextView txtPaint2 = viewBottomSheet.findViewById(R.id.txtPaint2);*/
        TextView txtLocation2 = viewBottomSheet.findViewById(R.id.txtLocation2);
        TextView txtContact2 = viewBottomSheet.findViewById(R.id.txtContact2);

        txtCamera.setTextColor(Color.parseColor(G.attachmentColor));
        textPicture.setTextColor(Color.parseColor(G.attachmentColor));
        txtVideo.setTextColor(Color.parseColor(G.attachmentColor));
        txtMusic.setTextColor(Color.parseColor(G.attachmentColor));

        txtFile.setTextColor(Color.parseColor(G.attachmentColor));
        /*txtPaint.setTextColor(Color.parseColor(G.attachmentColor));*/
        txtLocation.setTextColor(Color.parseColor(G.attachmentColor));
        txtContact.setTextColor(Color.parseColor(G.attachmentColor));
        send.setTextColor(Color.parseColor(G.attachmentColor));
        txtCountItem.setTextColor(Color.parseColor(G.attachmentColor));

        txtCamera2.setTextColor(Color.parseColor(G.attachmentColor));
        textPicture2.setTextColor(Color.parseColor(G.attachmentColor));
        txtVideo2.setTextColor(Color.parseColor(G.attachmentColor));
        txtMusic2.setTextColor(Color.parseColor(G.attachmentColor));
        txtFile2.setTextColor(Color.parseColor(G.attachmentColor));
        /*txtPaint2.setTextColor(Color.parseColor(G.attachmentColor));*/
        txtLocation2.setTextColor(Color.parseColor(G.attachmentColor));
        txtContact2.setTextColor(Color.parseColor(G.attachmentColor));


        onPathAdapterBottomSheet = new OnPathAdapterBottomSheet() {
            @Override
            public void path(String path, boolean isCheck, boolean isEdit, StructBottomSheet mList, int id) {

                if (isEdit) {
                    bottomSheetDialog.dismiss();
                    new HelperFragment(getActivity().getSupportFragmentManager(), FragmentEditImage.newInstance(null, true, false, id)).setReplace(false).load();
                } else {
                    if (isCheck) {
                        StructBottomSheet item = new StructBottomSheet();
                        item.setPath(path);
                        item.setText("");
                        item.setId(id);
                        FragmentEditImage.textImageList.put(path, item);
                    } else {
                        FragmentEditImage.textImageList.remove(path);
                    }
                    if (FragmentEditImage.textImageList.size() > 0) {
                        send.setText(getString(R.string.md_send_button));
                        txtCountItem.setText("" + FragmentEditImage.textImageList.size() + " " + getString(item));
                    } else {
                        send.setText(getString(R.string.close_icon));
                        txtCountItem.setText(getString(R.string.navigation_drawer_close));
                    }
                }
            }
        };


        FragmentEditImage.completeEditImage = new FragmentEditImage.CompleteEditImage() {
            @Override
            public void result(String path, String message, HashMap<String, StructBottomSheet> textImageList) {
                listPathString = null;
                listPathString = new ArrayList<>();

                if (textImageList.size() == 0) {
                    return;
                }

                /**
                 * sort list
                 */
                ArrayList<StructBottomSheet> itemList = new ArrayList<StructBottomSheet>();
                for (Map.Entry<String, StructBottomSheet> items : textImageList.entrySet()) {
                    itemList.add(items.getValue());
                }

                Collections.sort(itemList);

                for (StructBottomSheet item : itemList) {
                    edtChat.setText(item.getText());
                    listPathString.add(item.getPath());
                    latestRequestCode = AttachFile.requestOpenGalleryForImageMultipleSelect;
                    ll_attach_text.setVisibility(View.VISIBLE);
                    imvSendButton.performClick();
                }
            }
        };
        rcvBottomSheet = viewBottomSheet.findViewById(R.id.rcvContent);
        rcvBottomSheet.setLayoutManager(new GridLayoutManager(G.fragmentActivity, 1, GridLayoutManager.HORIZONTAL, false));
        rcvBottomSheet.setItemViewCacheSize(100);
        rcvBottomSheet.setAdapter(fastItemAdapter);
        bottomSheetDialog = new BottomSheetDialog(getActivity(), G.isDarkTheme ? R.style.BaseBottomSheetDialog : R.style.BaseBottomSheetDialogLight);
        bottomSheetDialog.setContentView(viewBottomSheet);
        final BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) viewBottomSheet.getParent());

        viewBottomSheet.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mBehavior.setPeekHeight(viewBottomSheet.getHeight());
                    viewBottomSheet.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
        //height is ready

        onClickCamera = new OnClickCamera() {
            @Override
            public void onclickCamera() {
                try {
                    bottomSheetDialog.dismiss();
                    new AttachFile(G.fragmentActivity).requestTakePicture(FragmentChat.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        bottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
                FrameLayout bottomSheet = bottomSheetDialog.findViewById(android.support.design.R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        rcvBottomSheet.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(final View view) {
                if (isPermissionCamera) {

                    if (rcvBottomSheet.getChildAdapterPosition(view) == 0) {
                        isCameraAttached = true;
                    }
                    if (isCameraAttached) {
                        if (fotoapparatSwitcher != null) {
                            if (!isCameraStart) {
                                isCameraStart = true;
                                try {
                                    G.handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            fotoapparatSwitcher.start();
                                        }
                                    }, 50);
                                } catch (Exception e) {
                                    e.getMessage();
                                }
                            }
                        } else {
                            if (!isCameraStart) {
                                isCameraStart = true;
                                try {
                                    G.handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            fotoapparatSwitcher = Fotoapparat.with(G.fragmentActivity).into(view.findViewById(R.id.cameraView))           // view which will draw the camera preview
                                                    .photoSize(biggestSize())   // we want to have the biggest photo possible
                                                    .lensPosition(back())       // we want back camera
                                                    .build();

                                            fotoapparatSwitcher.start();
                                        }
                                    }, 100);
                                } catch (IllegalStateException e) {
                                    e.getMessage();
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(final View view) {

                if (isPermissionCamera) {
                    if (rcvBottomSheet.getChildAdapterPosition(view) == 0) {
                        isCameraAttached = false;
                    }
                    if (!isCameraAttached) {
                        if (fotoapparatSwitcher != null) {
                            //                    if (isCameraStart && ( rcvBottomSheet.getChildAdapterPosition(view)> 4  || rcvBottomSheet.computeHorizontalScrollOffset() >200)){
                            if (isCameraStart) {

                                try {
                                    fotoapparatSwitcher.stop();
                                    isCameraStart = false;
                                } catch (Exception e) {
                                    e.getMessage();
                                }
                            }
                        } else {
                            if (!isCameraStart) {
                                isCameraStart = false;
                                try {
                                    G.handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            fotoapparatSwitcher = Fotoapparat.with(G.fragmentActivity).into(view.findViewById(R.id.cameraView))           // view which will draw the camera preview
                                                    .photoSize(biggestSize())   // we want to have the biggest photo possible
                                                    .lensPosition(back())       // we want back camera
                                                    .build();

                                            fotoapparatSwitcher.stop();
                                        }
                                    }, 100);
                                } catch (IllegalStateException e) {
                                    e.getMessage();
                                }
                            }
                        }
                    }
                }
            }
        });

        rcvBottomSheet.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(final View v) {
                if (isPermissionCamera) {

                    if (fotoapparatSwitcher != null) {
                        G.handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!isCameraStart) {
                                    fotoapparatSwitcher.start();
                                    isCameraStart = true;
                                }
                            }
                        }, 50);
                    }
                }
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                if (isPermissionCamera) {
                    if (fotoapparatSwitcher != null) {
                        if (isCameraStart) {
                            fotoapparatSwitcher.stop();
                            isCameraStart = false;
                        }
                    }
                }
            }
        });

        if (HelperPermission.grantedUseStorage()) {
            rcvBottomSheet.setVisibility(View.VISIBLE);
        } else {
            rcvBottomSheet.setVisibility(View.GONE);
        }

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isNewBottomSheet = true;
                dialog.dismiss();
            }
        });

        listPathString = new ArrayList<>();

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();

                if (sharedPreferences.getInt(SHP_SETTING.KEY_CROP, 1) == 1) {
                    attachFile.showDialogOpenCamera(v, null, FragmentChat.this);
                } else {
                    attachFile.showDialogOpenCamera(v, null, FragmentChat.this);
                }
            }
        });
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                try {
                    attachFile.requestOpenGalleryForImageMultipleSelect(FragmentChat.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                try {
                    attachFile.requestOpenGalleryForVideoMultipleSelect(FragmentChat.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                try {
                    attachFile.requestPickAudio(FragmentChat.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (FragmentEditImage.textImageList.size() > 0) {
                    bottomSheetDialog.dismiss();
                    fastItemAdapter.clear();
                    //send.setImageResource(R.mipmap.ic_close);
                    send.setText(getString(R.string.close_icon));
                    txtCountItem.setText(getString(R.string.navigation_drawer_close));


                    final ArrayList<StructBottomSheet> itemList = new ArrayList<StructBottomSheet>();
                    for (Map.Entry<String, StructBottomSheet> items : FragmentEditImage.textImageList.entrySet()) {
                        itemList.add(items.getValue());
                    }
                    Collections.sort(itemList);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    if (itemList.size() == 1) {
                                        showDraftLayout();
                                        listPathString.add(itemList.get(0).getPath());
                                        listPathString.set(0, attachFile.saveGalleryPicToLocal(itemList.get(0).getPath()));
                                        setDraftMessage(AttachFile.requestOpenGalleryForImageMultipleSelect);
                                        latestRequestCode = AttachFile.requestOpenGalleryForImageMultipleSelect;
                                        //sendMessage(AttachFile.requestOpenGalleryForImageMultipleSelect, pathStrings.get(0));
                                    } else {
                                        for (StructBottomSheet items : itemList) {

                                            //if (!path.toLowerCase().endsWith(".gif")) {
                                            String localPathNew = attachFile.saveGalleryPicToLocal(items.path);
                                            edtChat.setText(items.getText());
                                            sendMessage(AttachFile.requestOpenGalleryForImageMultipleSelect, localPathNew);
                                            //}
                                        }

                                    }

                                }
                            });
                        }
                    }).start();
                } else {
                    bottomSheetDialog.dismiss();
                }
            }
        });
        file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                try {
                    attachFile.requestPickFile(new IPickFile() {
                        @Override
                        public void onPick(ArrayList<String> selectedPathList) {
                            for (String path : selectedPathList) {
                                Intent data = new Intent();
                                data.setData(Uri.parse(path));
                                onActivityResult(request_code_pic_file, Activity.RESULT_OK, data);
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        /*paint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                try {
                    attachFile.requestPaint(FragmentChat.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });*/
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                try {
                    attachFile.requestGetPosition(complete, FragmentChat.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                try {
                    attachFile.requestPickContact(FragmentChat.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void cardToCardClick(View v) {
        if (v == null) {
            isCardToCardMessage = true;
            showDraftLayout();
        } else {
            if ((Boolean) v.getTag()) {
                bottomSheetDialog.dismiss();
                isCardToCardMessage = true;
                showDraftLayout();
            } else {
                bottomSheetDialog.dismiss();
                HelperError.showSnackMessage(G.currentActivity.getString(R.string.disable), false);
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private void inflateReplayLayoutIntoStub(StructMessageInfo chatItem , boolean isEdit) {
        if (rootView.findViewById(R.id.replayLayoutAboveEditText) == null) {
            ViewStubCompat stubView = rootView.findViewById(R.id.replayLayoutStub);
            stubView.setInflatedId(R.id.replayLayoutAboveEditText);
            if (G.isDarkTheme)
                stubView.setLayoutResource(R.layout.layout_chat_reply_dark);
            else
                stubView.setLayoutResource(R.layout.layout_chat_reply);
            stubView.inflate();

            inflateReplayLayoutIntoStub(chatItem , isEdit);
        } else {
            mReplayLayout = rootView.findViewById(R.id.replayLayoutAboveEditText);
            mReplayLayout.setVisibility(View.VISIBLE);
            TextView replayTo = mReplayLayout.findViewById(R.id.replayTo);
            Utils.darkModeHandler(replayTo);
            replayTo.setTypeface(G.typeface_IRANSansMobile);
            TextView replayFrom = mReplayLayout.findViewById(R.id.replyFrom);
            replayFrom.setTypeface(G.typeface_IRANSansMobile);

            FontIconTextView replayIcon = rootView.findViewById(R.id.lcr_imv_replay);
            Utils.darkModeHandler(replayIcon);
            if (isEdit)
                replayIcon.setText(getString(R.string.edit_icon));
            else
                replayIcon.setText(getString(R.string.reply_icon));

            ImageView thumbnail = mReplayLayout.findViewById(R.id.thumbnail);
            TextView closeReplay = mReplayLayout.findViewById(R.id.cancelIcon);
            closeReplay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //clearReplyView();

                    if (isEdit)
                        removeEditedMessage();
                    else
                        clearReplyView();

                }
            });
            //+Realm realm = Realm.getDefaultInstance();
            thumbnail.setVisibility(View.VISIBLE);
            if (chatItem.forwardedFrom != null) {
                AppUtils.rightFileThumbnailIcon(thumbnail, chatItem.forwardedFrom.getMessageType(), chatItem.forwardedFrom);
                String _text = AppUtils.conversionMessageType(chatItem.forwardedFrom.getMessageType());
                if (_text != null && _text.length() > 0) {
                    ReplySetText(replayTo, _text);
                } else {
                    ReplySetText(replayTo, chatItem.forwardedFrom.getMessage());
                }
            } else {
                RealmRoomMessage message = getRealmChat().where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, parseLong(chatItem.messageID)).findFirst();
                AppUtils.rightFileThumbnailIcon(thumbnail, chatItem.messageType, message);
                String _text = AppUtils.conversionMessageType(chatItem.messageType);
                if (_text != null && _text.length() > 0) {
                    ReplySetText(replayTo, _text);
                } else {
                    ReplySetText(replayTo, chatItem.messageText);
                }
            }

            if (!isEdit){
                if (chatType == CHANNEL) {
                    RealmRoom realmRoom = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.ID, chatItem.roomId).findFirst();
                    if (realmRoom != null) {
                        replayFrom.setText(realmRoom.getTitle());
                    }
                } else {
                    RealmRegisteredInfo userInfo = RealmRegisteredInfo.getRegistrationInfo(getRealmChat(), parseLong(chatItem.senderID));
                    if (userInfo != null) {
                        replayFrom.setText(userInfo.getDisplayName());
                    }
                }
            }else {
                replayFrom.setText(getString(R.string.edit));
            }

            //realm.close();
            // I set tag to retrieve it later when sending message
            mReplayLayout.setTag(chatItem);
        }
    }

    private void ReplySetText(TextView replayTo, String text) {
        ArrayList<Tuple<Integer, Integer>> a = AbstractMessage.getBoldPlaces(text);
        text = AbstractMessage.removeBoldMark(text, a);
        replayTo.setText(text);
    }

    private void initLayoutChannelFooter() {
        ViewGroup layoutAttach = rootView.findViewById(R.id.layout_attach_file);


        layoutAttach.setVisibility(View.GONE);
        if (!isNotJoin) layoutMute.setVisibility(View.VISIBLE);


        layoutMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onSelectRoomMenu("txtMuteNotification", mRoomId);
            }
        });


        if (isMuteNotification) {
            txtChannelMute.setText(R.string.unmute);
        } else {
            txtChannelMute.setText(R.string.mute);
        }
    }

    private void initAppbarSelected() {
        ll_AppBarSelected = rootView.findViewById(R.id.ac_layout_selected_item);

        mTxtSelectedCounter = rootView.findViewById(R.id.ac_layout_selected_txt_counter);
        mBtnCopySelected = rootView.findViewById(R.id.ac_layout_selected_btn_copy);
        mBtnForwardSelected = rootView.findViewById(R.id.ac_layout_selected_btn_forward);
        mBtnReplySelected = rootView.findViewById(R.id.ac_layout_selected_btn_reply);
        mBtnDeleteSelected = rootView.findViewById(R.id.ac_layout_selected_btn_delete);

        //  btnReplaySelected = (MaterialDesignTextView)  rootView.findViewById(R.id.chl_btn_replay_selected);
        //mBtnReplySelected = rootView.findViewById(R.id.chl_ripple_replay_selected);

        if (chatType == CHANNEL) {
            if (channelRole == ChannelChatRole.MEMBER) {
                mBtnReplySelected.setVisibility(View.INVISIBLE);
            }
        } else {
            mBtnReplySelected.setVisibility(View.VISIBLE);
        }
        mBtnReplySelected.setOnClickListener(v -> {
            if (mAdapter != null && !mAdapter.getSelectedItems().isEmpty() && mAdapter.getSelectedItems().size() == 1) {
                replay(mAdapter.getSelectedItems().iterator().next().mMessage , false);
            }
        });
        mBtnCopySelected.setOnClickListener(v -> {

            copySelectedItemTextToClipboard();

        });

        mBtnForwardSelected.setOnClickListener(v -> {

            // forward selected messages to room list for selecting room
            if (mAdapter != null && mAdapter.getSelectedItems().size() > 0) {
                onForwardClick(null);
            }

        });

        mBtnDeleteSelected.setOnClickListener(v -> {

            final ArrayList<Long> list = new ArrayList<Long>();
            bothDeleteMessageId = new ArrayList<Long>();

            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    for (final AbstractMessage item : mAdapter.getSelectedItems()) {
                        try {
                            if (item != null && item.mMessage != null && item.mMessage.messageID != null) {
                                Long messageId = parseLong(item.mMessage.messageID);
                                list.add(messageId);
                                if (RealmRoomMessage.isBothDelete(item.mMessage.time)) {
                                    bothDeleteMessageId.add(messageId);
                                }
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }

                    final String count = list.size() + "";


                    if (chatType == ProtoGlobal.Room.Type.CHAT && !isCloudRoom && bothDeleteMessageId.size() > 0 && mAdapter.getSelectedItems().iterator().next().mMessage.senderID.equalsIgnoreCase(Long.toString(G.userId))) {
                        // show both Delete check box

                        String delete;
                        String textCheckBox = G.context.getResources().getString(R.string.st_checkbox_delete) + " " + title;
                        if (HelperCalander.isPersianUnicode) {
                            delete = HelperCalander.convertToUnicodeFarsiNumber(G.context.getResources().getString(R.string.st_desc_delete, count));

                        } else {
                            delete = HelperCalander.convertToUnicodeFarsiNumber(G.context.getResources().getString(R.string.st_desc_delete, "the"));

                        }
                        new MaterialDialog.Builder(G.fragmentActivity).limitIconToDefaultSize().content(delete).title(R.string.message).positiveText(R.string.ok).negativeText(R.string.cancel).onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                if (!dialog.isPromptCheckBoxChecked()) {
                                    bothDeleteMessageId = null;
                                }

                                RealmRoomMessage.deleteSelectedMessages(getRealmChat(), mRoomId, list, bothDeleteMessageId, chatType);
                                deleteSelectedMessageFromAdapter(list);
                            }
                        }).checkBoxPrompt(textCheckBox, false, null).show();

                    } else {
                        if (!G.fragmentActivity.isFinishing()) {
                            new MaterialDialog.Builder(G.fragmentActivity).title(R.string.message).content(G.context.getResources().getString(R.string.st_desc_delete, count)).positiveText(R.string.ok).negativeText(R.string.cancel).onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    bothDeleteMessageId = null;
                                    RealmRoomMessage.deleteSelectedMessages(getRealmChat(), mRoomId, list, bothDeleteMessageId, chatType);
                                    deleteSelectedMessageFromAdapter(list);
                                }
                            }).show();
                        }
                    }
                }
            });
        });

        if (chatType == CHANNEL && channelRole == ChannelChatRole.MEMBER && !isNotJoin) {
            initLayoutChannelFooter();
        }
    }

    private void deleteSelectedMessageFromAdapter(ArrayList<Long> list) {
        for (Long mId : list) {
            try {
                mAdapter.removeMessage(mId);
                // remove tag from edtChat if the message has deleted
                if (edtChat.getTag() != null && edtChat.getTag() instanceof StructMessageInfo) {
                    if (mId == Long.parseLong(((StructMessageInfo) edtChat.getTag()).messageID)) {
                        edtChat.setTag(null);
                    }
                }

                removeLayoutTimeIfNeed();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    private void removeLayoutTimeIfNeed() {
        if (mAdapter != null) {
            int size = mAdapter.getItemCount();
            for (int i = 0; i < size; i++) {

                if (mAdapter.getItem(i) instanceof TimeItem) {
                    if (i < size - 1) {
                        if (mAdapter.getItem(i + 1) instanceof TimeItem) {
                            mAdapter.remove(i);
                        }
                    } else {
                        mAdapter.remove(i);
                    }
                }
            }
        }
    }

    private void initLayoutSearchNavigation() {
        //  ll_navigate_Message = (LinearLayout)  rootView.findViewById(R.id.ac_ll_message_navigation);
        //  btnUpMessage = (TextView)  rootView.findViewById(R.id.ac_btn_message_up);
        txtClearMessageSearch = rootView.findViewById(R.id.ac_btn_clear_message_search);
        //  btnDownMessage = (TextView) findViewById(R.id.ac_btn_message_down);
        //  txtMessageCounter = (TextView) findViewById(R.id.ac_txt_message_counter);

        //btnUpMessage.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //
        //        if (selectedPosition > 0) {
        //            deSelectMessage(selectedPosition);
        //            selectedPosition--;
        //            selectMessage(selectedPosition);
        //            recyclerView.scrollToPosition(selectedPosition);
        //            txtMessageCounter.setText(selectedPosition + 1 + " " + getString(of) + " " + messageCounter);
        //        }
        //    }
        //});

        //btnDownMessage.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        if (selectedPosition < messageCounter - 1) {
        //            deSelectMessage(selectedPosition);
        //            selectedPosition++;
        //            selectMessage(selectedPosition);
        //            recyclerView.scrollToPosition(selectedPosition);
        //            txtMessageCounter.setText(selectedPosition + 1 + " " + getString(of) + messageCounter);
        //        }
        //    }
        //});

        final RippleView rippleClose = rootView.findViewById(R.id.chl_btn_close_ripple_search_message);
        rippleClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtSearchMessage.getText().toString().length() == 0) {

                    goneSearchBox(view);

                } else {
                    // deSelectMessage(selectedPosition);
                    edtSearchMessage.setText("");
                    btnHashLayoutClose.performClick();
                }
            }
        });

        ll_Search = rootView.findViewById(R.id.ac_ll_search_message);

        if (G.isDarkTheme) ll_Search.setBackgroundResource(R.drawable.shape_toolbar_background_dark);
        //btnCloseLayoutSearch = (Button)  rootView.findViewById(R.id.ac_btn_close_layout_search_message);
        edtSearchMessage = rootView.findViewById(R.id.chl_edt_search_message);
        edtSearchMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() > 0) {
                    if (FragmentChat.hashListener != null) {
                        FragmentChat.hashListener.complete(true, charSequence.toString(), "");
                    }
                } else {
                    btnHashLayoutClose.performClick();
                }

                //mAdapter.filter(charSequence);
                //
                //new Handler().postDelayed(new Runnable() {
                //    @Override
                //    public void run() {
                //        messageCounter = mAdapter.getAdapterItemCount();
                //
                //        if (messageCounter > 0) {
                //            selectedPosition = messageCounter - 1;
                //            recyclerView.scrollToPosition(selectedPosition);
                //
                //            if (charSequence.length() > 0) {
                //                selectMessage(selectedPosition);
                //                txtMessageCounter.setText(messageCounter + " " + getString(of) + " " + messageCounter);
                //            } else {
                //                txtMessageCounter.setText("0 " + getString(of) + " 0");
                //            }
                //        } else {
                //            txtMessageCounter.setText("0 " + getString(of) + " " + messageCounter);
                //            selectedPosition = 0;
                //        }
                //    }
                //}, 600);
                //
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void goneSearchBox(View view) {

        edtSearchMessage.setText("");
        ll_Search.setVisibility(View.GONE);
        layoutToolbar.setVisibility(View.VISIBLE);
        btnHashLayoutClose.performClick();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    private void itemAdapterBottomSheetForward() {

        String[] fieldNames = {RealmRoomFields.IS_PINNED, RealmRoomFields.PIN_ID, RealmRoomFields.UPDATED_TIME};
        Sort[] sort = {Sort.DESCENDING, Sort.DESCENDING, Sort.DESCENDING};
        results = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.KEEP_ROOM, false).equalTo(RealmRoomFields.IS_DELETED, false).
                equalTo(RealmRoomFields.READ_ONLY, false).notEqualTo(RealmRoomFields.ID, mRoomId).findAll().sort(fieldNames, sort);

        resultsContact = getRealmChat().where(RealmContacts.class).findAll().sort(RealmContactsFields.DISPLAY_NAME);

        List<Long> te = new ArrayList<>();
        te.add(chatPeerId);
        long identifier = 100L;
        for (RealmRoom r : results) {
            StructBottomSheetForward item = new StructBottomSheetForward();
            item.setId(r.getId());
            if (r.getType() == ProtoGlobal.Room.Type.CHAT) {
                te.add(r.getChatRoom().getPeerId());
            }
            item.setDisplayName(r.getTitle());
            if (r.getChatRoom() != null) item.setPeer_id(r.getChatRoom().getPeerId());
            item.setType(r.getType());
            item.setContactList(false);
            item.setNotExistRoom(false);
            identifier = identifier + 1;
            if (r.getChatRoom() != null && r.getChatRoom().getPeerId() > 0 && r.getChatRoom().getPeerId() == userId) {
                fastItemAdapterForward.add(0, new ItemBottomSheetForward(item, avatarHandler).withIdentifier(identifier));
            } else {
                fastItemAdapterForward.add(new ItemBottomSheetForward(item, avatarHandler).withIdentifier(identifier));
            }
        }

        for (RealmContacts r : resultsContact) {
            if (!te.contains(r.getId())) {
                StructBottomSheetForward item = new StructBottomSheetForward();
                item.setId(r.getId());
                item.setDisplayName(r.getDisplay_name());
                item.setContactList(true);
                item.setNotExistRoom(true);
                identifier = identifier + 1;
                fastItemAdapterForward.add(new ItemBottomSheetForward(item, avatarHandler).withIdentifier(identifier));
            }
        }

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isAdded()) {
                    bottomSheetDialogForward.show();
                }
            }
        }, 100);
    }

    public void itemAdapterBottomSheet() {

        if (fastItemAdapter != null) fastItemAdapter.clear();

        if (isNewBottomSheet || FragmentEditImage.itemGalleryList.size() <= 1) {

            if (listPathString != null) {
                listPathString.clear();
            } else {
                listPathString = new ArrayList<>();
            }

            FragmentEditImage.itemGalleryList.clear();
            if (isNewBottomSheet) {
                FragmentEditImage.textImageList.clear();
            }

            try {
                HelperPermission.getStoragePermision(G.fragmentActivity, new OnGetPermission() {
                    @Override
                    public void Allow() {
                        FragmentEditImage.itemGalleryList = getAllShownImagesPath(G.fragmentActivity);
                        if (rcvBottomSheet != null) rcvBottomSheet.setVisibility(View.VISIBLE);
                        checkCameraAndLoadImage();
                    }

                    @Override
                    public void deny() {
                        loadImageGallery();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            checkCameraAndLoadImage();
        }


    }

    private void checkCameraAndLoadImage() {
        boolean isCameraButtonSheet = sharedPreferences.getBoolean(SHP_SETTING.KEY_CAMERA_BUTTON_SHEET, true);
        if (isCameraButtonSheet) {
            try {
                HelperPermission.getCameraPermission(G.fragmentActivity, new OnGetPermission() {
                    @Override
                    public void Allow() {

                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                fastItemAdapter.add(new AdapterCamera("", onClickCamera).withIdentifier(99));
                                for (int i = 0; i < FragmentEditImage.itemGalleryList.size(); i++) {
                                    fastItemAdapter.add(new BottomSheetItem(FragmentEditImage.itemGalleryList.get(i), onPathAdapterBottomSheet).withIdentifier(100 + i));
                                }
                                isPermissionCamera = true;
                            }
                        });
                        G.handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (isAdded()) {
                                    showBottomSheet();
                                }
                            }
                        }, 100);
                    }

                    @Override
                    public void deny() {

                        loadImageGallery();

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            loadImageGallery();
        }
    }

    private void showBottomSheet() {
        bottomSheetDialog.show();
        if (FragmentEditImage.textImageList != null && FragmentEditImage.textImageList.size() > 0) {
            //send.setText(R.mipmap.send2);
            if (send != null)
                send.setText(G.fragmentActivity.getResources().getString(R.string.md_send_button));
            if (txtCountItem != null)
                txtCountItem.setText("" + FragmentEditImage.textImageList.size() + " " + G.fragmentActivity.getResources().getString(item));
        } else {
            //send.setImageResource(R.mipmap.ic_close);
            if (send != null)
                send.setText(G.fragmentActivity.getResources().getString(R.string.close_icon));
            if (txtCountItem != null)
                txtCountItem.setText(G.fragmentActivity.getResources().getString(R.string.navigation_drawer_close));
        }
    }

    private void loadImageGallery() {

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < FragmentEditImage.itemGalleryList.size(); i++) {
                    fastItemAdapter.add(new BottomSheetItem(FragmentEditImage.itemGalleryList.get(i), onPathAdapterBottomSheet).withIdentifier(100 + i));
                }
            }
        });

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isAdded()) {
                    showBottomSheet();
                }
            }
        }, 100);

    }

    @Override
    public void OnChannelUpdateReactionStatusResponse(long roomId, final boolean status) {
        if (roomId == mRoomId) {
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    showVoteChannel = status;
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    @Override
    public void OnChannelUpdateReactionStatusError() {

    }

    /**
     * *************************** Messaging ***************************
     */

    private void sendMessage(int requestCode, String filePath) {

        if (filePath == null || (filePath.length() == 0 && requestCode != AttachFile.request_code_contact_phone)) {
            clearReplyView();
            return;
        }

        if (isShowLayoutUnreadMessage) {
            removeLayoutUnreadMessage();
        }
        long messageId = AppUtils.makeRandomId();
        final long updateTime = TimeUtils.currentLocalTime();
        ProtoGlobal.RoomMessageType messageType = null;
        String fileName = null;
        long duration = 0;
        long fileSize = 0;
        int[] imageDimens = {0, 0};
        final long senderID = G.userId;

        /**
         * check if path is uri detect real path from uri
         */
        String path = getFilePathFromUri(Uri.parse(filePath));
        if (path != null) {
            filePath = path;
        }

        StructMessageInfo messageInfo = null;

        if (requestCode == AttachFile.requestOpenGalleryForVideoMultipleSelect && filePath.toLowerCase().endsWith(".gif")) {
            requestCode = AttachFile.requestOpenGalleryForImageMultipleSelect;
        }

        switch (requestCode) {
            case IntentRequests.REQ_CROP:

                if (!filePath.toLowerCase().endsWith(".gif")) {
                    if (isMessageWrote()) {
                        messageType = IMAGE_TEXT;
                    } else {
                        messageType = ProtoGlobal.RoomMessageType.IMAGE;
                    }
                } else {
                    if (isMessageWrote()) {
                        messageType = GIF_TEXT;
                    } else {
                        messageType = ProtoGlobal.RoomMessageType.GIF;
                    }
                }

                fileName = new File(filePath).getName();
                fileSize = new File(filePath).length();
                imageDimens = AndroidUtils.getImageDimens(filePath);
                if (isReply()) {
                    messageInfo = new StructMessageInfo(getRealmChat(), mRoomId, Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, null, filePath, updateTime, parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID));
                } else {
                    messageInfo = new StructMessageInfo(getRealmChat(), mRoomId, Long.toString(messageId), getWrittenMessage(), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, null, filePath, updateTime);
                }
                break;
            case AttachFile.request_code_TAKE_PICTURE:

                fileName = new File(filePath).getName();
                fileSize = new File(filePath).length();
                if (AndroidUtils.getImageDimens(filePath)[0] == 0 && AndroidUtils.getImageDimens(filePath)[1] == 0) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Picture Not Loaded", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                imageDimens = AndroidUtils.getImageDimens(filePath);
                if (isMessageWrote()) {
                    messageType = IMAGE_TEXT;
                } else {
                    messageType = ProtoGlobal.RoomMessageType.IMAGE;
                }
                if (isReply()) {
                    messageInfo = new StructMessageInfo(getRealmChat(), mRoomId, Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, null, filePath, updateTime, parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID));
                } else {
                    messageInfo = new StructMessageInfo(getRealmChat(), mRoomId, Long.toString(messageId), getWrittenMessage(), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, null, filePath, updateTime);
                }

                break;

            case AttachFile.requestOpenGalleryForImageMultipleSelect:
                if (!filePath.toLowerCase().endsWith(".gif")) {
                    if (isMessageWrote()) {
                        messageType = IMAGE_TEXT;
                    } else {
                        messageType = ProtoGlobal.RoomMessageType.IMAGE;
                    }
                } else {
                    if (isMessageWrote()) {
                        messageType = GIF_TEXT;
                    } else {
                        messageType = ProtoGlobal.RoomMessageType.GIF;
                    }
                }

                fileName = new File(filePath).getName();
                fileSize = new File(filePath).length();
                imageDimens = AndroidUtils.getImageDimens(filePath);

                if (isReply()) {
                    messageInfo = new StructMessageInfo(getRealmChat(), mRoomId, Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, null, filePath, updateTime, parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID));
                } else {
                    messageInfo = new StructMessageInfo(getRealmChat(), mRoomId, Long.toString(messageId), getWrittenMessage(), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, null, filePath, updateTime);
                }
                break;

            case AttachFile.requestOpenGalleryForVideoMultipleSelect:
            case request_code_VIDEO_CAPTURED:
                fileName = new File(filePath).getName();
                /**
                 * if video not compressed use from mainPath
                 */
                boolean compress = false;
                if (compressedPath.get(filePath) != null) {
                    compress = compressedPath.get(filePath);
                }
                if (compress) {
                    fileSize = new File(filePath).length();
                    duration = AndroidUtils.getAudioDuration(G.fragmentActivity, filePath) / 1000;
                } else {
                    fileSize = new File(mainVideoPath).length();
                    duration = AndroidUtils.getAudioDuration(G.fragmentActivity, mainVideoPath) / 1000;
                }

                if (isMessageWrote()) {
                    messageType = VIDEO_TEXT;
                } else {
                    messageType = VIDEO;
                }
                File videoFile = new File(filePath);
                String videoFileMime = FileUtils.getMimeType(videoFile);
                if (isReply()) {
                    messageInfo = new StructMessageInfo(getRealmChat(), mRoomId, Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, videoFileMime, filePath, null, filePath, null, updateTime, parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID));
                } else {
                    messageInfo = new StructMessageInfo(getRealmChat(), mRoomId, Long.toString(messageId), Long.toString(senderID), getWrittenMessage(), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, videoFileMime, filePath, null, filePath, null, updateTime);
                }
                break;
            case AttachFile.request_code_pic_audi:
                fileName = new File(filePath).getName();
                fileSize = new File(filePath).length();
                duration = AndroidUtils.getAudioDuration(G.fragmentActivity, filePath) / 1000;
                if (isMessageWrote()) {
                    messageType = ProtoGlobal.RoomMessageType.AUDIO_TEXT;
                } else {
                    messageType = ProtoGlobal.RoomMessageType.AUDIO;
                }
                String songArtist = AndroidUtils.getAudioArtistName(filePath);
                long songDuration = AndroidUtils.getAudioDuration(G.fragmentActivity, filePath);

                messageInfo = StructMessageInfo.buildForAudio(getRealmChat(), mRoomId, messageId, senderID, ProtoGlobal.RoomMessageStatus.SENDING, messageType, MyType.SendType.send, updateTime, getWrittenMessage(), null, filePath, songArtist, songDuration, isReply() ? parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID) : -1);
                break;
            case AttachFile.request_code_pic_file:
            case AttachFile.request_code_open_document:

                fileName = new File(filePath).getName();
                fileSize = new File(filePath).length();
                if (isMessageWrote()) {
                    messageType = ProtoGlobal.RoomMessageType.FILE_TEXT;
                } else {
                    messageType = ProtoGlobal.RoomMessageType.FILE;
                }
                File fileFile = new File(filePath);
                String fileFileMime = FileUtils.getMimeType(fileFile);
                if (isReply()) {
                    messageInfo = new StructMessageInfo(getRealmChat(), mRoomId, Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, fileFileMime, filePath, null, filePath, null, updateTime, parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID));
                } else {
                    messageInfo = new StructMessageInfo(getRealmChat(), mRoomId, Long.toString(messageId), Long.toString(senderID), getWrittenMessage(), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, fileFileMime, filePath, null, filePath, null, updateTime);
                }
                break;
            case AttachFile.request_code_contact_phone:
                if (latestUri == null) {
                    break;
                }
                ContactUtils contactUtils = new ContactUtils(G.fragmentActivity, latestUri);
                String name = contactUtils.retrieveName();
                String number = contactUtils.retrieveNumber();
                messageType = CONTACT;
                messageInfo = StructMessageInfo.buildForContact(getRealmChat(), mRoomId, messageId, senderID, MyType.SendType.send, updateTime, ProtoGlobal.RoomMessageStatus.SENDING, name, "", number, isReply() ? parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID) : -1);
                break;
            case AttachFile.request_code_paint:
                fileName = new File(filePath).getName();

                imageDimens = AndroidUtils.getImageDimens(filePath);
                if (isMessageWrote()) {
                    messageType = IMAGE_TEXT;
                } else {
                    messageType = ProtoGlobal.RoomMessageType.IMAGE;
                }
                if (isReply()) {
                    messageInfo = new StructMessageInfo(getRealmChat(), mRoomId, Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, null, filePath, updateTime, parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID));
                } else {
                    messageInfo = new StructMessageInfo(getRealmChat(), mRoomId, Long.toString(messageId), getWrittenMessage(), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, null, filePath, updateTime);
                }
                break;
        }

        final ProtoGlobal.RoomMessageType finalMessageType = messageType;
        final String finalFilePath = filePath;
        final String finalFileName = fileName;
        final long finalDuration = duration;
        final long finalFileSize = fileSize;
        final int[] finalImageDimens = imageDimens;

        final StructMessageInfo finalMessageInfo = messageInfo;
        final long finalMessageId = messageId;

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
                if (room != null) {
                    room.setDeleted(false);
                }

                RealmRoomMessage roomMessage = realm.createObject(RealmRoomMessage.class, finalMessageId);

                roomMessage.setMessageType(finalMessageType);
                roomMessage.setMessage(getWrittenMessage());

                RealmRoomMessage.addTimeIfNeed(roomMessage, realm);
                RealmRoomMessage.isEmojiInText(roomMessage, getWrittenMessage());

                roomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SENDING.toString());
                roomMessage.setRoomId(mRoomId);
                roomMessage.setAttachment(finalMessageId, finalFilePath, finalImageDimens[0], finalImageDimens[1], finalFileSize, finalFileName, finalDuration, LocalFileType.FILE);
                roomMessage.setUserId(senderID);
                roomMessage.setAuthorHash(G.authorHash);
                roomMessage.setShowMessage(true);
                roomMessage.setCreateTime(updateTime);
                if (isReply()) {
                    if (finalMessageInfo != null && finalMessageInfo.replayTo != null) {
                        roomMessage.setReplyTo(finalMessageInfo.replayTo);
                    }
                }

                /**
                 * make channel extra if room is channel
                 */
                if (chatType == CHANNEL) {
                    StructChannelExtra structChannelExtra = StructChannelExtra.makeDefaultStructure(finalMessageId, mRoomId);
                    finalMessageInfo.channelExtra = structChannelExtra;
                    RealmChannelExtra.convert(realm, structChannelExtra);
                    //roomMessage.setChannelExtra(RealmChannelExtra.convert(realm, structChannelExtra));
                }

                if (finalMessageType == CONTACT) {
                    roomMessage.setRoomMessageContact(RealmRoomMessageContact.put(realm, finalMessageInfo));
                }

                if (finalMessageType != CONTACT) {
                    finalMessageInfo.attachment = StructMessageAttachment.convert(roomMessage.getAttachment());
                }

                String makeThumbnailFilePath = "";
                if (finalMessageType == VIDEO || finalMessageType == VIDEO_TEXT) {
                    //if (compressedPath.get(finalFilePath)) {//(sharedPreferences.getInt(SHP_SETTING.KEY_TRIM, 1) == 0) ||
                    boolean compress = false;
                    if (compressedPath.get(finalFilePath) != null) {
                        compress = compressedPath.get(finalFilePath);
                    }
                    if (compress) {
                        makeThumbnailFilePath = finalFilePath;
                    } else {
                        makeThumbnailFilePath = mainVideoPath;
                    }
                }

                if (finalMessageType == VIDEO || finalMessageType == VIDEO_TEXT) {
                    Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(makeThumbnailFilePath, MediaStore.Video.Thumbnails.MINI_KIND);
                    if (bitmap != null) {
                        String path = AndroidUtils.saveBitmap(bitmap);
                        roomMessage.getAttachment().setLocalThumbnailPath(path);
                        roomMessage.getAttachment().setWidth(bitmap.getWidth());
                        roomMessage.getAttachment().setHeight(bitmap.getHeight());

                        finalMessageInfo.attachment.setLocalFilePath(roomMessage.getMessageId(), finalFilePath);
                        finalMessageInfo.attachment.width = bitmap.getWidth();
                        finalMessageInfo.attachment.height = bitmap.getHeight();
                    }

                    //if (compressedPath.get(finalFilePath)) {//(sharedPreferences.getInt(SHP_SETTING.KEY_TRIM, 1) == 0) ||
                    boolean compress = false;
                    if (compressedPath.get(finalFilePath) != null) {
                        compress = compressedPath.get(finalFilePath);
                    }
                    if (compress) {
                        HelperUploadFile.startUploadTaskChat(mRoomId, chatType, finalFilePath, finalMessageId, finalMessageType, getWrittenMessage(), StructMessageInfo.getReplyMessageId(finalMessageInfo), new HelperUploadFile.UpdateListener() {
                            @Override
                            public void OnProgress(int progress, FileUploadStructure struct) {
                                {
                                    insertItemAndUpdateAfterStartUpload(progress, struct);
                                }
                            }

                            @Override
                            public void OnError() {

                            }
                        });
                    } else {
                        compressingFiles.put(finalMessageId, null);
                        StructUploadVideo uploadVideo = new StructUploadVideo();
                        uploadVideo.filePath = finalFilePath;
                        uploadVideo.roomId = mRoomId;
                        uploadVideo.messageId = finalMessageId;
                        uploadVideo.messageType = finalMessageType;
                        uploadVideo.message = getWrittenMessage();
                        if (isReply()) {
                            uploadVideo.replyMessageId = parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID);
                        } else {
                            uploadVideo.replyMessageId = 0;
                        }
                        structUploadVideos.add(uploadVideo);

                        finalMessageInfo.attachment.compressing = G.fragmentActivity.getResources().getString(R.string.compressing);
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                switchAddItem(new ArrayList<>(Collections.singletonList(finalMessageInfo)), false);
                            }
                        });
                    }
                }

                RealmRoom.setLastMessageWithRoomMessage(realm, roomMessage.getRoomId(), roomMessage);
            }
        });
        realm.close();

        if (finalMessageType != VIDEO && finalMessageType != VIDEO_TEXT) {
            if (finalMessageType != CONTACT) {

                HelperUploadFile.startUploadTaskChat(mRoomId, chatType, finalFilePath, finalMessageId, finalMessageType, getWrittenMessage(), StructMessageInfo.getReplyMessageId(finalMessageInfo), new HelperUploadFile.UpdateListener() {
                    @Override
                    public void OnProgress(int progress, FileUploadStructure struct) {
                        if (canUpdateAfterDownload) {
                            insertItemAndUpdateAfterStartUpload(progress, struct);
                        }
                    }

                    @Override
                    public void OnError() {

                    }
                });
            } else {
                ChatSendMessageUtil messageUtil = new ChatSendMessageUtil().newBuilder(chatType, finalMessageType, mRoomId).message(getWrittenMessage());
                messageUtil.contact(finalMessageInfo.userInfo.firstName, finalMessageInfo.userInfo.lastName, finalMessageInfo.userInfo.phone);
                if (isReply()) {
                    messageUtil.replyMessage(parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID));
                }
                messageUtil.sendMessage(Long.toString(finalMessageId));
            }

            if (finalMessageType == CONTACT) {
                messageInfo.channelExtra = new StructChannelExtra();
                mAdapter.add(new ContactItem(getActivity(), mAdapter, chatType, this).setMessage(messageInfo));
            }
        }

        if (isReply()) {
            mReplayLayout.setTag(null);
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    mReplayLayout.setVisibility(View.GONE);
                }
            });
        }

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollToEnd();
            }
        }, 100);

    }

    public void sendCancelAction() {

        HelperSetAction.sendCancel(messageId);
    }

    public void sendPosition(final Double latitude, final Double longitude, final String imagePath) {
        sendCancelAction();

        if (isShowLayoutUnreadMessage) {
            removeLayoutUnreadMessage();
        }
        final long messageId = AppUtils.makeRandomId();
        RealmRoomMessage.makePositionMessage(mRoomId, messageId, replyMessageId(), latitude, longitude, imagePath);

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                RealmRoomMessage roomMessage = getRealmChat().where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId).findFirst();
                switchAddItem(new ArrayList<>(Collections.singletonList(StructMessageInfo.convert(getRealmChat(), roomMessage))), false);
                chatSendMessageUtil.build(chatType, mRoomId, roomMessage);
                scrollToEnd();
            }
        }, 300);

        clearReplyView();
    }

    /**
     * do forward actions if any message forward to this room
     */
    private void manageForwardedMessage() {
        if ((mForwardMessages != null && !isChatReadOnly) || multiForwardList.size() > 0) {
            final LinearLayout ll_Forward = rootView.findViewById(R.id.ac_ll_forward);
            Utils.darkModeHandlerGray(ll_Forward);
            int multiForwardSize = multiForwardList.size();
            if (hasForward || multiForwardSize > 0) {

                for (int i = 0; i < mForwardMessages.size(); i++) {
                    if (hasForward) {
                        sendForwardedMessage(Parcels.unwrap(mForwardMessages.get(i)), mRoomId, true, i);
                    } else {
                        for (int k = 0; k < multiForwardSize; k++) {
                            sendForwardedMessage(Parcels.unwrap(mForwardMessages.get(i)), multiForwardList.get(k), false, (i + k));
                        }
                    }
                }

                if (hasForward) {
                    imvCancelForward.performClick();
                } else {
                    multiForwardList.clear();
                    removeForwardModeFromRoomList();
                    mForwardMessages = null;
                }

            } else {
                imvCancelForward = rootView.findViewById(R.id.cslhf_imv_cansel);
                imvCancelForward.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ll_Forward.setVisibility(View.GONE);
                        hasForward = false;
                        removeForwardModeFromRoomList();
                        mForwardMessages = null;
                        if (edtChat.getText().length() == 0) {

                            sendButtonVisibility(false);
                        }
                    }
                });

                sendButtonVisibility(true);

                int _count = mForwardMessages.size();
                String str = _count > 1 ? G.fragmentActivity.getResources().getString(R.string.messages_selected) : G.fragmentActivity.getResources().getString(R.string.message_selected);

                EmojiTextViewE emMessage = rootView.findViewById(R.id.cslhf_txt_message);
                Utils.darkModeHandler(emMessage);

                FontIconTextView forwardIcon = rootView.findViewById(R.id.cslhs_imv_forward);
                Utils.darkModeHandler(forwardIcon);

                if (HelperCalander.isPersianUnicode) {

                    emMessage.setText(convertToUnicodeFarsiNumber(_count + " " + str));
                } else {

                    emMessage.setText(_count + " " + str);
                }

                hasForward = true;
                ll_Forward.setVisibility(View.VISIBLE);
            }
        }
    }

    private void removeForwardModeFromRoomList() {
        if (getActivity() instanceof ActivityMain){
            ((ActivityMain) getActivity()).setForwardMessage(false);
        }
    }

    private void sendForwardedMessage(final StructMessageInfo messageInfo, final long mRoomId, final boolean isSingleForward, int k) {


        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                final long messageId = AppUtils.makeRandomId();

                RealmRoom realmRoom = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
                if (realmRoom == null || realmRoom.getReadOnly()) {
                    return;
                }

                final ProtoGlobal.Room.Type type = realmRoom.getType();

                //final Realm realm = Realm.getDefaultInstance();

                getRealmChat().executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmRoomMessage.makeForwardMessage(realm, mRoomId, messageId, parseLong(messageInfo.messageID));
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {

                        RealmRoomMessage forwardedMessage = getRealmChat().where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId).findFirst();

                        if (forwardedMessage != null && forwardedMessage.isValid() && !forwardedMessage.isDeleted()) {
                            if (isSingleForward) {
                                switchAddItem(new ArrayList<>(Collections.singletonList(StructMessageInfo.convert(getRealmChat(), forwardedMessage))), false);
                                scrollToEnd();
                            }
                            RealmRoomMessage roomMessage = getRealmChat().where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, parseLong(messageInfo.messageID)).findFirst();
                            if (roomMessage != null) {
                                chatSendMessageUtil.buildForward(type, forwardedMessage.getRoomId(), forwardedMessage, roomMessage.getRoomId(), roomMessage.getMessageId());
                            }
                        }

                        //realm.close();
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        //realm.close();
                    }
                });
            }
        }, (50 * k));
    }

    private StructMessageInfo makeLayoutTime(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        String timeString = TimeUtils.getChatSettingsTimeAgo(G.fragmentActivity, calendar.getTime());

        RealmRoomMessage timeMessage = RealmRoomMessage.makeTimeMessage(time, timeString);

        StructMessageInfo theTime = StructMessageInfo.convert(getRealmChat(), timeMessage);

        return theTime;
    }

    private void switchAddItem(ArrayList<StructMessageInfo> messageInfos, boolean addTop) {
        if (prgWaiting != null && messageInfos.size() > 0) {
            prgWaiting.setVisibility(View.GONE);
        }
        long identifier = SUID.id().get();
        for (StructMessageInfo messageInfo : messageInfos) {

            ProtoGlobal.RoomMessageType messageType;
            if (messageInfo.forwardedFrom != null) {
                if (messageInfo.forwardedFrom.isValid()) {
                    messageType = messageInfo.forwardedFrom.getMessageType();
                } else {
                    return;
                }
            } else {
                messageType = messageInfo.messageType;
            }

            if (!messageInfo.isTimeOrLogMessage() || (messageType == LOG)) {
                int index = 0;
                if (addTop) {
                    if (messageInfo.showTime) {
                        for (int i = 0; i < mAdapter.getAdapterItemCount(); i++) {
                            if (mAdapter.getAdapterItem(i) instanceof TimeItem) {
                                if (!RealmRoomMessage.isTimeDayDifferent(messageInfo.time, mAdapter.getAdapterItem(i).mMessage.time)) {
                                    mAdapter.remove(i);
                                }
                                break;
                            }
                        }
                        mAdapter.add(0, new TimeItem(mAdapter, this).setMessage(makeLayoutTime(messageInfo.time)).withIdentifier(identifier++));

                        index = 1;
                    }
                } else {


                    /**
                     * don't allow for add lower messageId to bottom of list
                     */
                    if (parseLong(messageInfo.messageID) > biggestMessageId) {
                        if (!messageInfo.status.equals(ProtoGlobal.RoomMessageStatus.SENDING.toString())) {
                            biggestMessageId = parseLong(messageInfo.messageID);
                        }
                    } else {
                        continue;
                    }


                    if (lastMessageId == parseLong(messageInfo.messageID)) {
                        continue;
                    } else {
                        lastMessageId = parseLong(messageInfo.messageID);
                    }

                    if (messageInfo.showTime) {
                        if (mAdapter.getItemCount() > 0) {
                            if (mAdapter.getAdapterItem(mAdapter.getItemCount() - 1).mMessage != null && RealmRoomMessage.isTimeDayDifferent(messageInfo.time, mAdapter.getAdapterItem(mAdapter.getItemCount() - 1).mMessage.time)) {
                                mAdapter.add(new TimeItem(mAdapter, this).setMessage(makeLayoutTime(messageInfo.time)).withIdentifier(identifier++));

                            }
                        } else {
                            mAdapter.add(new TimeItem(mAdapter, this).setMessage(makeLayoutTime(messageInfo.time)).withIdentifier(identifier++));
                        }
                    }
                }

                switch (messageType) {
                    case TEXT:
                        if (messageInfo.additionalData != null && messageInfo.additionalData.AdditionalType == AdditionalType.CARD_TO_CARD_MESSAGE)
                            if (!addTop) {
                                mAdapter.add(new CardToCardItem(mAdapter, chatType, FragmentChat.this).setMessage(messageInfo).withIdentifier(identifier));
                            } else {
                                mAdapter.add(index, new CardToCardItem(mAdapter, chatType, FragmentChat.this).setMessage(messageInfo).withIdentifier(identifier));
                            }
                        else {
                            if (!addTop) {
                                mAdapter.add(new TextItem(mAdapter, chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                            } else {
                                mAdapter.add(index, new TextItem(mAdapter, chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                            }
                        }
                        break;
                    case WALLET:
                        if (messageInfo.structWallet.getRealmRoomMessageWalletCardToCard() != null) {
                            if (!addTop) {
                                mAdapter.add(new LogWalletCardToCard(mAdapter, chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                            } else {
                                mAdapter.add(index, new LogWalletCardToCard(mAdapter, chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                            }
                        } else {
                            if (!addTop) {
                                mAdapter.add(new LogWallet(mAdapter, chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                            } else {
                                mAdapter.add(index, new LogWallet(mAdapter, chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                            }
                        }

                        break;
                    case IMAGE:
                    case IMAGE_TEXT:
                        if (!addTop) {
                            mAdapter.add(new ImageWithTextItem(mAdapter, chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new ImageWithTextItem(mAdapter, chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case VIDEO:
                    case VIDEO_TEXT:
                        if (!addTop) {
                            mAdapter.add(new VideoWithTextItem(mAdapter, chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new VideoWithTextItem(mAdapter, chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case LOCATION:
                        if (!addTop) {
                            mAdapter.add(new LocationItem(mAdapter, chatType, this, getActivity()).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new LocationItem(mAdapter, chatType, this, getActivity()).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case FILE:
                    case FILE_TEXT:
                        if (!addTop) {
                            mAdapter.add(new FileItem(mAdapter, chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new FileItem(mAdapter, chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case STICKER:
                        if (!addTop) {
                            mAdapter.add(new StickerItem(mAdapter, chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new StickerItem(mAdapter, chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case VOICE:
                        if (!addTop) {
                            mAdapter.add(new VoiceItem(mAdapter, chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new VoiceItem(mAdapter, chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case AUDIO:
                    case AUDIO_TEXT:
                        if (!addTop) {
                            mAdapter.add(new AudioItem(mAdapter, chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new AudioItem(mAdapter, chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case CONTACT:
                        if (!addTop) {
                            mAdapter.add(new ContactItem(getActivity(), mAdapter, chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new ContactItem(getActivity(), mAdapter, chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case GIF:
                    case GIF_TEXT:
                        if (!addTop) {
                            mAdapter.add(new GifWithTextItem(mAdapter, chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new GifWithTextItem(mAdapter, chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case LOG:
                        if (messageInfo.showMessage) {
                            if (!addTop) {
                                mAdapter.add(new LogItem(mAdapter, this).setMessage(messageInfo).withIdentifier(identifier));
                            } else {
                                mAdapter.add(index, new LogItem(mAdapter, this).setMessage(messageInfo).withIdentifier(identifier));
                            }
                        }
                        break;
                }
            }
            identifier++;
        }
    }

    /**
     * manage save changeState , unread message , load from local or need get message from server and finally load message
     */
    private void getMessages() {
        //+Realm realm = Realm.getDefaultInstance();

        ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction;
        ArrayList<StructMessageInfo> messageInfos = new ArrayList<>();
        /**
         * get message in first enter to chat if has unread get message with down direction
         */
        RealmResults<RealmRoomMessage> results;
        RealmResults<RealmRoomMessage> resultsDown = null;
        RealmResults<RealmRoomMessage> resultsUp;
        long fetchMessageId = 0; // with this value realm will be queried for get message
        if (hasUnread() || hasSavedState()) {

            if (firstUnreadMessage == null || !firstUnreadMessage.isValid() || firstUnreadMessage.isDeleted()) {
                firstUnreadMessage = getFirstUnreadMessage(getRealmChat());
            }

            /**
             * show unread layout and also set firstUnreadMessageId in startFutureMessageIdUp
             * for try load top message and also topMore default value is true for this target
             */
            if (hasSavedState()) {
                fetchMessageId = getSavedState();

                if (hasUnread()) {
                    if (firstUnreadMessage == null) {
                        resetMessagingValue();
                        getMessages();
                        return;
                    }
                }
            } else {
                if (firstUnreadMessage == null) {
                    resetMessagingValue();
                    getMessages();
                    return;
                }
                unreadLayoutMessage();
                fetchMessageId = firstUnreadMessage.getMessageId();
            }

            if (hasUnread()) {
                countNewMessage = unreadCount;
                txtNewUnreadMessage.setVisibility(View.VISIBLE);
                txtNewUnreadMessage.getTextView().setText(countNewMessage + "");
                setDownBtnVisible();
                firstUnreadMessageInChat = firstUnreadMessage;
            }

            startFutureMessageIdUp = fetchMessageId;

            // we have firstUnreadMessage but for gapDetection method we need RealmResult so get this message with query; if we change gap detection method will be can use from firstUnreadMessage
            resultsDown = getRealmChat().where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, mRoomId).notEqualTo(RealmRoomMessageFields.CREATE_TIME, 0).equalTo(RealmRoomMessageFields.DELETED, false).equalTo(RealmRoomMessageFields.SHOW_MESSAGE, true).equalTo(RealmRoomMessageFields.MESSAGE_ID, fetchMessageId).findAll();

            addToView = false;
            direction = DOWN;
        } else {
            addToView = true;
            direction = UP;
        }

        /**
         * Hint: don't use "findFirst()" for find biggest message, because "findFirst()" just will be
         * returned latest message that inserted to "RealmRoomMessage" and it is not biggest always
         **/
        Number latestMessageId = getRealmChat().where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, mRoomId).notEqualTo(RealmRoomMessageFields.CREATE_TIME, 0).equalTo(RealmRoomMessageFields.DELETED, false).equalTo(RealmRoomMessageFields.SHOW_MESSAGE, true).max(RealmRoomMessageFields.MESSAGE_ID);
        if (latestMessageId != null) {
            resultsUp = getRealmChat().where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, mRoomId).equalTo(RealmRoomMessageFields.MESSAGE_ID, latestMessageId.longValue()).findAll();
        } else {
            /** use fake query for make empty RealmResult */
            resultsUp = getRealmChat().where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, -100).findAll();
        }

        long gapMessageId;
        if (direction == DOWN) {
            RealmQuery realmQuery = getRealmChat().where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, mRoomId).lessThanOrEqualTo(RealmRoomMessageFields.MESSAGE_ID, fetchMessageId).notEqualTo(RealmRoomMessageFields.CREATE_TIME, 0).equalTo(RealmRoomMessageFields.DELETED, false).equalTo(RealmRoomMessageFields.SHOW_MESSAGE, true);
            /**
             * if for UP changeState client have message detect gap otherwise try for get online message
             * because maybe client have message but not exist in Realm yet
             */
            if (realmQuery.count() > 1) {
                resultsUp = getRealmChat().where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, mRoomId).equalTo(RealmRoomMessageFields.MESSAGE_ID, fetchMessageId).notEqualTo(RealmRoomMessageFields.CREATE_TIME, 0).equalTo(RealmRoomMessageFields.DELETED, false).equalTo(RealmRoomMessageFields.SHOW_MESSAGE, true).findAll();
                gapDetection(resultsUp, UP);
            } else {
                getOnlineMessage(fetchMessageId, UP);
            }

            results = resultsDown;
            gapMessageId = gapDetection(results, direction);
        } else {
            results = resultsUp;
            gapMessageId = gapDetection(resultsUp, UP);
        }

        if (results.size() > 0) {

            Object[] object = getLocalMessage(getRealmChat(), mRoomId, results.first().getMessageId(), gapMessageId, true, direction);
            messageInfos = (ArrayList<StructMessageInfo>) object[0];
            if (messageInfos.size() > 0) {
                if (direction == UP) {
                    topMore = (boolean) object[1];
                    startFutureMessageIdUp = parseLong(messageInfos.get(messageInfos.size() - 1).messageID);
                } else {
                    bottomMore = (boolean) object[1];
                    startFutureMessageIdDown = parseLong(messageInfos.get(messageInfos.size() - 1).messageID);
                }
            } else {
                if (direction == UP) {
                    startFutureMessageIdUp = 0;
                } else {
                    startFutureMessageIdDown = 0;
                }
            }

            /**
             * if gap is exist ,check that reached to gap or not and if
             * reached send request to server for clientGetRoomHistory
             */
            if (gapMessageId > 0) {
                boolean hasSpaceToGap = (boolean) object[2];
                if (!hasSpaceToGap) {

                    long oldMessageId = 0;
                    if (messageInfos.size() > 0) {
                        /**
                         * this code is correct for UP or DOWN load message result
                         */
                        oldMessageId = parseLong(messageInfos.get(messageInfos.size() - 1).messageID);
                    }
                    /**
                     * send request to server for clientGetRoomHistory
                     */
                    getOnlineMessage(oldMessageId, direction);
                }
            } else {
                /**
                 * if gap not exist and also not exist more message in local
                 * send request for get message from server
                 */
                if ((direction == UP && !topMore) || (direction == DOWN && !bottomMore)) {
                    if (messageInfos.size() > 0) {
                        getOnlineMessage(parseLong(messageInfos.get(messageInfos.size() - 1).messageID), direction);
                    } else {
                        getOnlineMessage(0, direction);
                    }
                }
            }
        } else {
            /** send request to server for get message.
             * if direction is DOWN check again realmRoomMessage for detection
             * that exist any message without checking deleted changeState and if
             * exist use from that messageId instead of zero for getOnlineMessage
             */
            long oldMessageId = 0;
            if (direction == DOWN) {
                RealmRoomMessage realmRoomMessage = getRealmChat().where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, mRoomId).notEqualTo(RealmRoomMessageFields.CREATE_TIME, 0).equalTo(RealmRoomMessageFields.SHOW_MESSAGE, true).equalTo(RealmRoomMessageFields.MESSAGE_ID, fetchMessageId).findFirst();
                if (realmRoomMessage != null) {
                    oldMessageId = realmRoomMessage.getMessageId();
                }
            }

            getOnlineMessage(oldMessageId, direction);
        }

        if (direction == UP) {
            switchAddItem(messageInfos, true);
        } else {
            switchAddItem(messageInfos, false);
            if (hasSavedState()) {

                if (messageId != 0) {
                    if (goToPositionWithAnimation(savedScrollMessageId, 1000)) {
                        savedScrollMessageId = 0;
                    }
                } else {
                    int position = mAdapter.findPositionByMessageId(savedScrollMessageId);
                    LinearLayoutManager linearLayout = (LinearLayoutManager) recyclerView.getLayoutManager();
                    linearLayout.scrollToPositionWithOffset(position, firstVisiblePositionOffset);
                    savedScrollMessageId = 0;
                }

            }
        }

        /**
         * make scrollListener for detect change in scroll and load more in chat
         */
        scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                int firstVisiblePosition = linearLayoutManager.findFirstVisibleItemPosition();
                View view = linearLayoutManager.getChildAt(0);
                if (firstVisiblePosition > 0 && view != null) {
                    firstVisiblePositionOffset = view.getTop();
                }

                visibleItemCount = linearLayoutManager.getChildCount();
                totalItemCount = linearLayoutManager.getItemCount();

                if (firstVisiblePosition < scrollEnd) {  /** scroll to top */
                    loadMessage(UP);

                    /** if totalItemCount is lower than scrollEnd so (firstVisiblePosition < scrollEnd) is always true and we can't load DOWN,
                     * finally for solve this problem we to check following state and load DOWN even totalItemCount is lower than scrollEnd count
                     */
                    if (totalItemCount <= scrollEnd) {
                        loadMessage(DOWN);
                    }
                } else if (firstVisiblePosition + visibleItemCount >= (totalItemCount - scrollEnd)) { /** scroll to bottom */
                    loadMessage(DOWN);
                }
            }
        };

        recyclerView.addOnScrollListener(scrollListener);
        if (unreadCount > 0)
            recyclerView.scrollToPosition(0);


        //realm.close();
    }

    /**
     * first set gap for room message for correctly load message and after than call {@link #getMessages()}
     *
     * @param messageId set gap for this message id
     */
    private void setGapAndGetMessage(long messageId) {
        RealmRoomMessage.setGap(messageId);
        getMessages();
    }

    /**
     * manage load message from local or from server(online)
     */
    private void loadMessage(final ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction) {
        long gapMessageId;
        long startFutureMessageId;
        if (direction == UP) {
            gapMessageId = gapMessageIdUp;
            startFutureMessageId = startFutureMessageIdUp;
        } else {
            gapMessageId = gapMessageIdDown;
            startFutureMessageId = startFutureMessageIdDown;
        }
        if ((direction == UP && topMore) || (direction == DOWN && bottomMore)) {
            Object[] object = getLocalMessage(getRealmChat(), mRoomId, startFutureMessageId, gapMessageId, false, direction);
            if (direction == UP) {
                topMore = (boolean) object[1];
            } else {
                bottomMore = (boolean) object[1];
            }
            final ArrayList<StructMessageInfo> structMessageInfos = (ArrayList<StructMessageInfo>) object[0];
            if (structMessageInfos.size() > 0) {
                if (direction == UP) {
                    startFutureMessageIdUp = parseLong(structMessageInfos.get(structMessageInfos.size() - 1).messageID);
                } else {
                    startFutureMessageIdDown = parseLong(structMessageInfos.get(structMessageInfos.size() - 1).messageID);
                }
            } else {
                /**
                 * don't set zero. when user come to room for first time with -@roomId-
                 * for example : @public ,this block will be called and set zero this value and finally
                 * don't allow to user for get top history, also that sounds this block isn't helpful
                 */
                //if (direction == UP) {
                //    startFutureMessageIdUp = 0;
                //} else {
                //    startFutureMessageIdDown = 0;
                //}
            }

            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    if (direction == UP) {
                        switchAddItem(structMessageInfos, true);
                    } else {
                        switchAddItem(structMessageInfos, false);
                    }
                }
            });

            /**
             * if gap is exist ,check that reached to gap or not and if
             * reached send request to server for clientGetRoomHistory
             */
            if (gapMessageId > 0) {
                boolean hasSpaceToGap = (boolean) object[2];
                if (!hasSpaceToGap) {
                    /**
                     * send request to server for clientGetRoomHistory
                     */
                    long oldMessageId;
                    if (structMessageInfos.size() > 0) {
                        oldMessageId = parseLong(structMessageInfos.get(structMessageInfos.size() - 1).messageID);
                    } else {
                        oldMessageId = gapMessageId;
                    }

                    getOnlineMessage(oldMessageId, direction);
                }
            }
        } else if (gapMessageId > 0) {
            /**
             * detect old messageId that should get history from server with that
             * (( hint : in scroll changeState never should get online message with messageId = 0
             * in some cases maybe startFutureMessageIdUp Equal to zero , so i used from this if.))
             */
            if (startFutureMessageId != 0) {
                getOnlineMessage(startFutureMessageId, direction);
            }
        } else {

            if (((direction == UP && allowGetHistoryUp) || (direction == DOWN && allowGetHistoryDown)) && startFutureMessageId != 0) {
                getOnlineMessage(startFutureMessageId, direction);
            }
        }
    }

    /**
     * get message history from server
     *
     * @param oldMessageId if set oldMessageId=0 messages will be get from latest message that exist in server
     */
    private void getOnlineMessage(final long oldMessageId, final ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction) {
        if ((direction == UP && !isWaitingForHistoryUp && allowGetHistoryUp) || (direction == DOWN && !isWaitingForHistoryDown && allowGetHistoryDown)) {
            /**
             * show progress when start for get history from server
             */
            progressItem(SHOW, direction);

            if (!G.userLogin) {
                getOnlineMessageAfterTimeOut(oldMessageId, direction);
                return;
            }
            long reachMessageId;
            if (direction == UP) {
                reachMessageId = reachMessageIdUp;
                isWaitingForHistoryUp = true;
            } else {
                reachMessageId = reachMessageIdDown;
                isWaitingForHistoryDown = true;
            }


            int limit = Config.LIMIT_GET_HISTORY_NORMAL;
            if ((firstUp && direction == UP) || (firstDown && direction == DOWN)) {
                limit = Config.LIMIT_GET_HISTORY_LOW;
            }


            String requestId = MessageLoader.getOnlineMessage(getRealmChat(), mRoomId, oldMessageId, reachMessageId, limit, direction, new OnMessageReceive() {
                @Override
                public void onMessage(final long roomId, long startMessageId, long endMessageId, List<RealmRoomMessage> realmRoomMessages, boolean gapReached, boolean jumpOverLocal, ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction) {
                    if (roomId != mRoomId) {
                        return;
                    }
                    hideProgress();
                    /**
                     * hide progress received history
                     */
                    progressItem(HIDE, direction);
                    if (direction == UP) {
                        firstUp = false;
                        startFutureMessageIdUp = startMessageId;
                        isWaitingForHistoryUp = false;
                    } else {
                        firstDown = false;
                        startFutureMessageIdDown = endMessageId;
                        isWaitingForHistoryDown = false;
                    }

                    MessageLoader.sendMessageStatus(roomId, realmRoomMessages, chatType, ProtoGlobal.RoomMessageStatus.SEEN);

                    //                    if (realmRoomMessages.size() == 0) { // Hint : link browsable ; Commented Now!!!
                    //                        getOnlineMessage(oldMessageId, direction);
                    //                        return;
                    //                    }

                    /**
                     * I do this for set addToView true
                     */
                    if (direction == DOWN && realmRoomMessages.size() < (Config.LIMIT_GET_HISTORY_NORMAL - 1)) {
                        getOnlineMessage(startFutureMessageIdDown, direction);
                    }

                    /**
                     * when reached to gap and not jumped over local, set gapMessageIdUp = 0; do this action
                     * means that gap not exist (need this value for future get message) set topMore/bottomMore
                     * local after that gap reached true for allow that get message from
                     */
                    if (gapReached && !jumpOverLocal) {
                        if (direction == UP) {
                            gapMessageIdUp = 0;
                            reachMessageIdUp = 0;
                            topMore = true;
                        } else {
                            gapMessageIdDown = 0;
                            reachMessageIdDown = 0;
                            bottomMore = true;
                        }

                        gapDetection(realmRoomMessages, direction);
                    } else if ((direction == UP && isReachedToTopView()) || direction == DOWN && isReachedToBottomView()) {
                        /**
                         * check this changeState because if user is near to top view and not scroll get top message from server
                         */
                        //getOnlineMessage(startFutureMessageId, directionEnum);
                    }

                    final ArrayList<StructMessageInfo> structMessageInfos = new ArrayList<>();
                    for (RealmRoomMessage realmRoomMessage : realmRoomMessages) {
                        structMessageInfos.add(StructMessageInfo.convert(getRealmChat(), realmRoomMessage));
                    }

                    if (direction == UP) {
                        switchAddItem(structMessageInfos, true);
                    } else {
                        switchAddItem(structMessageInfos, false);
                    }

                    //realm.close();
                }

                @Override
                public void onError(int majorCode, int minorCode, long messageIdGetHistory, ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction) {
                    hideProgress();
                    /**
                     * hide progress if have any error
                     */
                    progressItem(HIDE, direction);

                    if (majorCode == 617) {

                        if (!isWaitingForHistoryUp && !isWaitingForHistoryDown && mAdapter.getItemCount() == 0) {
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                }
                            });
                        }

                        if (direction == UP) {
                            isWaitingForHistoryUp = false;
                            allowGetHistoryUp = false;
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    //TODO [Saeed Mozaffari] [2017-03-06 9:50 AM] - for avoid from 'Inconsistency detected. Invalid item position' error i set notifyDataSetChanged. Find Solution And Clear it!!!
                                    mAdapter.notifyDataSetChanged();
                                }
                            });
                        } else {
                            addToView = true;
                            isWaitingForHistoryDown = false;
                            allowGetHistoryDown = false;
                        }
                    }

                    /**
                     * if time out came up try again for get history with previous value
                     */
                    if (majorCode == 5) {
                        if (direction == UP) {
                            isWaitingForHistoryUp = false;
                        } else {
                            isWaitingForHistoryDown = false;
                        }
                    }

                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (majorCode == 5) {
                                getOnlineMessageAfterTimeOut(messageIdGetHistory, direction);
                            }
                        }
                    });


                }
            });

            if (direction == UP) {
                lastRandomRequestIdUp = requestId;
            } else {
                lastRandomRequestIdDown = requestId;
            }
        }
    }

    private void getOnlineMessageAfterTimeOut(final long messageIdGetHistory, final ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction) {
        if (G.userLogin) {
            getOnlineMessage(messageIdGetHistory, direction);
        } else {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getOnlineMessageAfterTimeOut(messageIdGetHistory, direction);
                }
            }, 1000);
        }
    }

    /**
     * detect gap exist in this room or not
     * (hint : if gapMessageId==0 means that gap not exist)
     * if gapMessageIdUp exist, not compute again
     */
    private long gapDetection(List<RealmRoomMessage> results, ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction) {
        if (((direction == UP && gapMessageIdUp == 0) || (direction == DOWN && gapMessageIdDown == 0)) && results.size() > 0) {
            Object[] objects = MessageLoader.gapExist(getRealmChat(), mRoomId, results.get(0).getMessageId(), direction);
            if (direction == UP) {
                reachMessageIdUp = (long) objects[1];
                return gapMessageIdUp = (long) objects[0];
            } else {
                reachMessageIdDown = (long) objects[1];
                return gapMessageIdDown = (long) objects[0];
            }
        }
        return 0;
    }

    private long gapDetection(RealmResults<RealmRoomMessage> results, ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction) {
        Realm realm = Realm.getDefaultInstance();
        long res = gapDetection(realm.copyFromRealm(results), direction);
        realm.close();
        return res;
    }

    /**
     * return true if now view is near to top
     */
    private boolean isReachedToTopView() {
        return firstVisiblePosition <= 5;
    }

    /**
     * return true if now view is near to bottom
     */
    private boolean isReachedToBottomView() {
        return (firstVisiblePosition + visibleItemCount >= (totalItemCount - 5));
    }

    /**
     * make unread layout message
     */
    private void unreadLayoutMessage() {
        int unreadMessageCount = unreadCount;
        if (unreadMessageCount > 0) {
            RealmRoomMessage unreadMessage = RealmRoomMessage.makeUnreadMessage(unreadMessageCount);
            mAdapter.add(0, new UnreadMessage(mAdapter, FragmentChat.this).setMessage(StructMessageInfo.convert(getRealmChat(), unreadMessage)).withIdentifier(SUID.id().get()));
            isShowLayoutUnreadMessage = true;

        }
    }

    /**
     * return first unread message for current room
     * (reason : use from this method for avoid from closed realm error)
     */
    private RealmRoomMessage getFirstUnreadMessage(Realm realm) {
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
        if (realmRoom != null) {
            return realmRoom.getFirstUnreadMessage();
        }
        return null;
    }

    /**
     * check that this room has unread or no
     */
    private boolean hasUnread() {
        return unreadCount > 0;
    }

    /**
     * check that this room has saved changeState or no
     */
    private boolean hasSavedState() {
        return savedScrollMessageId > 0;
    }

    /**
     * return saved scroll messageId
     */
    private long getSavedState() {
        return savedScrollMessageId;
    }

    /**
     * manage progress changeState in adapter
     *
     * @param progressState SHOW or HIDE changeState detect with enum
     * @param direction     define direction for show progress in UP or DOWN
     */
    private void progressItem(final ProgressState progressState, final ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                int progressIndex = 0;
                if (direction == DOWN) {
                    progressIndex = mAdapter.getAdapterItemCount() - 1;
                }
                if (progressState == SHOW) {
                    if ((mAdapter.getAdapterItemCount() > 0) && !(mAdapter.getAdapterItem(progressIndex) instanceof ProgressWaiting)) {
                        recyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                if (direction == DOWN && progressIdentifierDown == 0) {
                                    progressIdentifierDown = SUID.id().get();
                                    mAdapter.add(new ProgressWaiting(mAdapter, FragmentChat.this).withIdentifier(progressIdentifierDown));
                                } else if (direction == UP && progressIdentifierUp == 0) {
                                    progressIdentifierUp = SUID.id().get();
                                    mAdapter.add(0, new ProgressWaiting(mAdapter, FragmentChat.this).withIdentifier(progressIdentifierUp));
                                }
                            }
                        });
                    }
                } else {
                    /**
                     * i do this action with delay because sometimes instance wasn't successful
                     * for detect progress so client need delay for detect this instance
                     */
                    if ((mAdapter.getItemCount() > 0) && (mAdapter.getAdapterItem(progressIndex) instanceof ProgressWaiting)) {
                        mAdapter.remove(progressIndex);
                        if (direction == DOWN) {
                            progressIdentifierDown = 0;
                        } else {
                            progressIdentifierUp = 0;
                        }
                    } else {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                /**
                                 * if not detected progress item for remove use from item identifier and remove progress item
                                 */
                                if (direction == DOWN && progressIdentifierDown != 0) {
                                    for (int i = (mAdapter.getItemCount() - 1); i >= 0; i--) {
                                        if (mAdapter.getItem(i).getIdentifier() == progressIdentifierDown) {
                                            mAdapter.remove(i);
                                            progressIdentifierDown = 0;
                                            break;
                                        }
                                    }
                                } else if (direction == UP && progressIdentifierUp != 0) {
                                    for (int i = 0; i < (mAdapter.getItemCount() - 1); i++) {
                                        if (mAdapter.getItem(i).getIdentifier() == progressIdentifierUp) {
                                            mAdapter.remove(i);
                                            progressIdentifierUp = 0;
                                            break;
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * reset to default value for reload message again
     */
    private void resetMessagingValue() {
        prgWaiting.setVisibility(View.VISIBLE);
        clearAdapterItems();

        addToView = true;
        topMore = false;
        bottomMore = false;
        isWaitingForHistoryUp = false;
        isWaitingForHistoryDown = false;
        gapMessageIdUp = 0;
        gapMessageIdDown = 0;
        reachMessageIdUp = 0;
        reachMessageIdDown = 0;
        allowGetHistoryUp = true;
        allowGetHistoryDown = true;
        startFutureMessageIdUp = 0;
        startFutureMessageIdDown = 0;
        firstVisiblePosition = 0;
        visibleItemCount = 0;
        totalItemCount = 0;
        unreadCount = 0;
        biggestMessageId = 0;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        G.fragmentActivity = (FragmentActivity) activity;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState); //No call for super(). Bug on API Level > 11.
    }

    public void finishChat() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {

                if (getActivity() != null && isAdded()) {
                    Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(FragmentChat.class.getName());
                    removeFromBaseFragment(fragment);

                    if (G.iTowPanModDesinLayout != null) {
                        G.iTowPanModDesinLayout.onLayout(ActivityMain.chatLayoutMode.hide);
                    }
                }
            }
        });
    }

    private void error(String error) {
        if (isAdded()) {
            try {
                HelperError.showSnackMessage(error, false);
            } catch (IllegalStateException e) {
                e.getStackTrace();
            }
        }
    }

    @Override
    public void onPinMessage() {

        initPinedMessage();

    }

    @Override
    public void onBotCommandText(Object message, int botAction) {

        if (message instanceof String) {
            if (botAction == 0) {
                if (!isChatReadOnly) {
                    if (edtChat != null)
                        edtChat.setText(message.toString());
                    imvSendButton.performClick();
                }
            } else {
                openWebViewForSpecialUrlChat(message.toString());
            }
        } else if (message instanceof RealmRoomMessage) {
            mAdapter.add(new TextItem(mAdapter, chatType, FragmentChat.this).setMessage(StructMessageInfo.convert(getRealmChat(), (RealmRoomMessage) message)).withIdentifier(SUID.id().get()));

        }

    }

    @Override
    public boolean requestLocation() {
        try {
            attachFile.requestGetPosition(complete, FragmentChat.this);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    private void forwardToChatRoom(final ArrayList<StructBottomSheetForward> forwardList) {

        if (forwardList != null && forwardList.size() > 0) {

            final int[] count = {0};
            for (int i = 0; i < forwardList.size(); i++) {
                new RequestChatGetRoom().chatGetRoom(forwardList.get(i).getId(), new RequestChatGetRoom.OnChatRoomReady() {
                    @Override
                    public void onReady(ProtoGlobal.Room room) {
                        if (!multiForwardList.contains(room.getId())) {
                            multiForwardList.add(room.getId());
                            RealmRoom.putOrUpdate(room);
                        }

                        count[0]++;
                        if (count[0] >= forwardList.size()) {
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    G.refreshRealmUi();
                                    bottomSheetDialogForward.dismiss();
                                    hideProgress();
                                    forwardList.clear();
                                    manageForwardedMessage();
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(int major, int minor) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                bottomSheetDialogForward.dismiss();
                                hideProgress();
                                error(G.fragmentActivity.getResources().getString(R.string.faild));
                            }
                        });
                    }
                });
            }


        } else {
            manageForwardedMessage();
            bottomSheetDialogForward.dismiss();
            hideProgress();
        }

    }

    private void showPaymentDialog() {
        showSelectItem();
//        RealmRoom realmRoom = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
//        if (realmRoom != null) {
//            chatType = realmRoom.getType();
//            if (chatType == CHAT) {
//                chatPeerId = realmRoom.getChatRoom().getPeerId();
//                if (imvUserPicture != null && txtName != null) {
//                    paymentDialog = PaymentFragment.newInstance(chatPeerId, imvUserPicture.getDrawable(), txtName.getText().toString());
////                    paymentDialog.show(getFragmentManager(), "payment_dialog");
//                    new HelperFragment(getActivity().getSupportFragmentManager(), paymentDialog).setTag("PaymentFragment").setReplace(false).load();
//                }
//            }
//        }

    }

    @Override
    public void onLeftIconClickListener(View view) {
        if (webViewChatPage != null) {
            closeWebViewForSpecialUrlChat(false);
            return;
        }
        closeKeyboard(view);
        popBackStackFragment();
    }

    @Override
    public void onChatAvatarClickListener(View view) {
        goToProfile();
    }

    @Override
    public void onRightIconClickListener(View view) {

        List<String> items = new ArrayList<>();
        items.add(getString(R.string.Search));
        items.add(getString(R.string.clear_history));
        items.add(getString(R.string.delete_chat));
        if (isMuteNotification)
            items.add(getString(R.string.unmute_notification));
        else
            items.add(getString(R.string.mute_notification));
        items.add(getString(R.string.chat_to_group));
        items.add(getString(R.string.clean_up));
        items.add(getString(R.string.export_chat));

        if (chatType == CHAT) {
            if (!isChatReadOnly && !blockUser && !isBot) {
            } else {
                items.remove(getString(R.string.chat_to_group));
                items.remove(getString(R.string.export_chat));
            }
        } else {
            items.remove(getString(R.string.delete_chat));
            items.remove(getString(R.string.chat_to_group));

            if (chatType == GROUP && isPublicGroup) {
                items.remove(getString(R.string.clear_history));
            }

            if (chatType == CHANNEL) {
                items.remove(getString(R.string.clear_history));
                items.remove(getString(R.string.export_chat));
            }
            if (channelRole != ChannelChatRole.OWNER || groupRole != GroupChatRole.OWNER || isNotJoin) {
                items.add(getString(R.string.report));
            } else {
                items.remove(getString(R.string.report));
            }
        }

        //+Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom1 = getRealmChat().where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
        if (realmRoom1 != null) {

                /*if (realmRoom.getMute()) {
                    txtMuteNotification.setText(G.fragmentActivity.getResources().getString(R.string.unmute_notification));
                    iconMuteNotification.setText(G.fragmentActivity.getResources().getString(R.string.md_unMuted));
                } else {
                    txtMuteNotification.setText(G.fragmentActivity.getResources().getString(R.string.mute_notification));
                    iconMuteNotification.setText(G.fragmentActivity.getResources().getString(R.string.md_muted));
                }*/
        } else {
            items.remove(getString(R.string.clear_history));
            items.remove(getString(R.string.delete_chat));
            if (!isMuteNotification)
                items.remove(getString(R.string.mute_notification));
            else
                items.remove(getString(R.string.unmute_notification));
            items.remove(getString(R.string.chat_to_group));
            items.remove(getString(R.string.clean_up));
        }

        if (isNotJoin) {
            items.remove(getString(R.string.clear_history));
            if (!isMuteNotification)
                items.remove(getString(R.string.mute_notification));
            else
                items.remove(getString(R.string.unmute_notification));
            items.remove(getString(R.string.clean_up));
        }

        if (RealmRoom.isNotificationServices(mRoomId)) {
            items.remove(getString(R.string.report));
        }

        if (RealmRoom.isBot(chatPeerId)) {
            items.remove(getString(R.string.delete_chat));
        }


        if (G.isWalletActive && G.isWalletRegister && (chatType == CHAT) && !isCloudRoom && !isBot) {
            items.add(getString(R.string.SendMoney));
        } else {
            items.remove(getString(R.string.SendMoney));
        }

        if (isBot) {
            if (webViewChatPage != null) {
                items.remove(getString(R.string.Search));
                items.remove(getString(R.string.clear_history));
                items.remove(getString(R.string.delete_chat));
                if (!isMuteNotification)
                    items.remove(getString(R.string.mute_notification));
                else
                    items.remove(getString(R.string.unmute_notification));
                items.remove(getString(R.string.chat_to_group));
                items.remove(getString(R.string.clean_up));
                items.remove(getString(R.string.SendMoney));
                items.remove(getString(R.string.export_chat));
                items.add(getString(R.string.stop));
            }
        }

        TopSheetDialog topSheetDialog = new TopSheetDialog(getContext()).setListData(items, -1, position -> {
            if (items.get(position).equals(getString(R.string.Search))) {
                initLayoutSearchNavigation();
                layoutToolbar.setVisibility(View.GONE);
                ll_Search.setVisibility(View.VISIBLE);
                if (!initHash) {
                    initHash = true;
                    initHashView();
                }
                G.handler.post(() -> editTextRequestFocus(edtSearchMessage));
            } else if (items.get(position).equals(getString(R.string.clear_history))) {
                new MaterialDialog.Builder(G.fragmentActivity).title(R.string.clear_history).content(R.string.clear_history_content).positiveText(R.string.yes).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        onSelectRoomMenu("txtClearHistory", mRoomId);
                    }
                }).negativeText(R.string.no).show();
            } else if (items.get(position).equals(getString(R.string.delete_chat))) {
                new MaterialDialog.Builder(G.fragmentActivity).title(R.string.delete_chat).content(R.string.delete_chat_content).positiveText(R.string.yes).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        onSelectRoomMenu("txtDeleteChat", mRoomId);
                    }
                }).negativeText(R.string.no).show();
            } else if (items.get(position).equals(getString(R.string.mute_notification)) || items.get(position).equals(getString(R.string.unmute_notification)) ) {
                onSelectRoomMenu("txtMuteNotification", mRoomId);
            } else if (items.get(position).equals(getString(R.string.chat_to_group))) {
                new MaterialDialog.Builder(G.fragmentActivity).title(R.string.convert_chat_to_group_title).content(R.string.convert_chat_to_group_content).positiveText(R.string.yes).negativeText(R.string.no).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        //finish();
                        finishChat();
                        dialog.dismiss();
                        G.handler.post(() -> G.onConvertToGroup.openFragmentOnActivity("ConvertToGroup", mRoomId));
                    }
                }).show();
            } else if (items.get(position).equals(getString(R.string.clean_up))) {
                resetMessagingValue();
                setDownBtnGone();
                RealmRoomMessage.ClearAllMessageRoomAsync(getRealmChat(), mRoomId, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        recyclerView.addOnScrollListener(scrollListener);
                        saveMessageIdPositionState(0);
                        /**
                         * get history from server
                         */
                        topMore = true;
                        getOnlineMessage(0, UP);
                    }
                });

            } else if (items.get(position).equals(getString(R.string.report))) {
                dialogReport(false, 0);
            } else if (items.get(position).equals(getString(R.string.SendMoney))) {
                showPaymentDialog();
            } else if (items.get(position).equals(getString(R.string.export_chat))) {
                if (HelperPermission.grantedUseStorage()) {
                    exportChat();
                } else {
                    try {
                        HelperPermission.getStoragePermision(G.fragmentActivity, new OnGetPermission() {
                            @Override
                            public void Allow() {
                                exportChat();
                            }

                            @Override
                            public void deny() {
                                Toast.makeText(G.currentActivity, R.string.export_message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (items.get(position).equals(getString(R.string.stop))) {
                new MaterialDialog.Builder(G.fragmentActivity).title(R.string.stop).content(R.string.stop_message_bot).positiveText(R.string.yes).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        //   onSelectRoomMenu("txtClearHistory", mRoomId);
                        closeWebViewForSpecialUrlChat(true);
                        //  popBackStackFragment();

                    }
                }).negativeText(R.string.no).show();
            }
        });
        topSheetDialog.show();

            /*final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).customView(R.layout.chat_popup_dialog_custom, true).build();
            View v = dialog.getCustomView();

            DialogAnimation.animationUp(dialog);
            dialog.show();*/

    }

    @Override
    public void onSecondRightIconClickListener(View view) {
        CallSelectFragment.call(chatPeerId, false, ProtoSignalingOffer.SignalingOffer.Type.VOICE_CALLING);
    }

    @Override
    public void onThirdRightIconClickListener(View view) {
        CallSelectFragment.call(chatPeerId, false, ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING);
    }

    @Override
    public void onFourthRightIconClickListener(View view) {

    }

    /**
     * *** SearchHash ***
     */

    private class SearchHash {

        public String lastMessageId = "";
        private String hashString = "";
        private int currentHashPosition;

        private ArrayList<String> hashList = new ArrayList<>();

        void setHashString(String hashString) {
            this.hashString = hashString.toLowerCase();
        }

        public void setPosition(String messageId) {

            if (mAdapter == null) {
                return;
            }

            if (lastMessageId.length() > 0) {
                mAdapter.toggleSelection(lastMessageId, false, null);
            }

            currentHashPosition = 0;
            hashList.clear();

            for (int i = 0; i < mAdapter.getAdapterItemCount(); i++) {
                if (mAdapter.getItem(i).mMessage != null) {

                    if (messageId.length() > 0) {
                        if (mAdapter.getItem(i).mMessage.messageID.equals(messageId)) {
                            currentHashPosition = hashList.size();
                            lastMessageId = messageId;
                            mAdapter.getItem(i).mMessage.isSelected = true;
                            mAdapter.notifyItemChanged(i);
                        }
                    }

                    String mText = mAdapter.getItem(i).mMessage.forwardedFrom != null ? mAdapter.getItem(i).mMessage.forwardedFrom.getMessage() : mAdapter.getItem(i).mMessage.messageText;

                    if (mText.toLowerCase().contains(hashString)) {
                        hashList.add(mAdapter.getItem(i).mMessage.messageID);
                    }
                }
            }

            if (messageId.length() == 0) {
                txtHashCounter.setText(hashList.size() + " / " + hashList.size());

                if (hashList.size() > 0) {
                    currentHashPosition = hashList.size() - 1;
                    goToSelectedPosition(hashList.get(currentHashPosition));
                }
            } else {
                txtHashCounter.setText((currentHashPosition + 1) + " / " + hashList.size());
            }
        }

        void downHash() {
            if (currentHashPosition < hashList.size() - 1) {

                currentHashPosition++;

                goToSelectedPosition(hashList.get(currentHashPosition));

                txtHashCounter.setText((currentHashPosition + 1) + " / " + hashList.size());
            }
        }

        void upHash() {
            if (currentHashPosition > 0) {

                currentHashPosition--;

                goToSelectedPosition(hashList.get(currentHashPosition));
                txtHashCounter.setText((currentHashPosition + 1) + " / " + hashList.size());
            }
        }

        private void goToSelectedPosition(String messageid) {

            mAdapter.toggleSelection(lastMessageId, false, null);

            lastMessageId = messageid;

            mAdapter.toggleSelection(lastMessageId, true, recyclerView);
        }
    }

    /**
     * *** VideoCompressor ***
     */

    class VideoCompressor extends AsyncTask<String, Void, StructCompress> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected StructCompress doInBackground(String... params) {
            if (params[0] == null) { // if data is null
                StructCompress structCompress = new StructCompress();
                structCompress.compress = false;
                return structCompress;
            }
            File file = new File(params[0]);
            long originalSize = file.length();

            StructCompress structCompress = new StructCompress();
            structCompress.path = params[1];
            structCompress.originalPath = params[0];
            long endTime = AndroidUtils.getAudioDuration(G.fragmentActivity, params[0]);
            try {
                structCompress.compress = MediaController.getInstance().convertVideo(params[0], params[1], endTime);

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            structCompress.originalSize = originalSize;
            return structCompress;
        }

        @Override
        protected void onPostExecute(StructCompress structCompress) {
            super.onPostExecute(structCompress);
            if (structCompress.compress) {
                compressedPath.put(structCompress.path, true);
                for (StructUploadVideo structUploadVideo : structUploadVideos) {
                    if (structUploadVideo != null && structUploadVideo.filePath.equals(structCompress.path)) {
                        /**
                         * update new info after compress file with notify item
                         */

                        long fileSize = new File(structUploadVideo.filePath).length();
                        long duration = AndroidUtils.getAudioDuration(G.fragmentActivity, structUploadVideo.filePath) / 1000;

                        if (fileSize >= structCompress.originalSize) {
                            structUploadVideo.filePath = structCompress.originalPath;
                            mAdapter.updateVideoInfo(structUploadVideo.messageId, duration, structCompress.originalSize);
                        } else {
                            RealmAttachment.updateFileSize(structUploadVideo.messageId, fileSize);
                            mAdapter.updateVideoInfo(structUploadVideo.messageId, duration, fileSize);
                        }

                        HelperUploadFile.startUploadTaskChat(structUploadVideo.roomId, chatType, structUploadVideo.filePath, structUploadVideo.messageId, structUploadVideo.messageType, structUploadVideo.message, structUploadVideo.replyMessageId, new HelperUploadFile.UpdateListener() {
                            @Override
                            public void OnProgress(int progress, FileUploadStructure struct) {
                                if (canUpdateAfterDownload) {
                                    insertItemAndUpdateAfterStartUpload(progress, struct);
                                }
                            }

                            @Override
                            public void OnError() {

                            }
                        });
                    }
                }
            }
        }
    }
}
