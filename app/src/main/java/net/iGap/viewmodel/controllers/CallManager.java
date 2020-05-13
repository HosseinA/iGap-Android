package net.iGap.viewmodel.controllers;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.drawable.Icon;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.util.Log;
import android.widget.Toast;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.CallSelectFragment;
import net.iGap.helper.HelperPublicMethod;
import net.iGap.module.MusicPlayer;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.enums.CallState;
import net.iGap.module.webrtc.CallerInfo;
import net.iGap.module.webrtc.WebRTC;
import net.iGap.observers.eventbus.EventListener;
import net.iGap.proto.ProtoSignalingAccept;
import net.iGap.proto.ProtoSignalingCandidate;
import net.iGap.proto.ProtoSignalingLeave;
import net.iGap.proto.ProtoSignalingOffer;
import net.iGap.proto.ProtoSignalingSessionHold;
import net.iGap.realm.RealmCallConfig;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestSignalingAccept;
import net.iGap.request.RequestSignalingCandidate;
import net.iGap.request.RequestSignalingGetConfiguration;
import net.iGap.request.RequestSignalingLeave;
import net.iGap.request.RequestSignalingOffer;
import net.iGap.request.RequestSignalingRinging;
import net.iGap.request.RequestSignalingSessionHold;
import net.iGap.viewmodel.controllers.telecom.CallConnectionService;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import static org.webrtc.SessionDescription.Type.ANSWER;
import static org.webrtc.SessionDescription.Type.OFFER;

public class CallManager implements EventListener {

    private long callPeerId;
    private ProtoSignalingOffer.SignalingOffer.Type callType;

    private RealmRegisteredInfo info;
    private RealmCallConfig currentCallConfig;

    private boolean callAlive = false;
    private boolean isRinging = false;
    private boolean isIncoming = false;

    private boolean isMicEnable = true;

    private CallerInfo currentCallerInfo;

    private CallStateChange onCallStateChanged;

    protected static final boolean USE_CONNECTION_SERVICE = isDeviceCompatibleWithConnectionServiceAPI();

    private static volatile CallManager instance = null;

    private static String TAG = "amini_Manager";


    // make this class singlton
    public static CallManager getInstance() {
        CallManager localInstance = instance;
        if (localInstance == null) {
            synchronized (CallManager.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new CallManager();
                }
            }
        }
        return localInstance;
    }

    private CallManager() {
        Log.d(TAG, "CallManager: ");
        DbManager.getInstance().doRealmTask(realm -> {
            currentCallConfig = realm.where(RealmCallConfig.class).findFirst();
            if (currentCallConfig == null) {
                new RequestSignalingGetConfiguration().signalingGetConfiguration();
            }
        });

        currentCallerInfo = new CallerInfo();
        currentCallerInfo.name = "Abolfazl Abbasi";
        currentCallerInfo.userId = AccountManager.getInstance().getCurrentUser().getId();
    }

    /**
     * this function is called when we are receiving a call response from others
     *
     * @param response from server
     */
    public void onOffer(ProtoSignalingOffer.SignalingOfferResponse.Builder response) {
        Log.d(TAG, "onOffer: ");
        if (invalidCallType(response.getType()))
            return;
        // set data for future use.
        callPeerId = response.getCallerUserId();
        callType = response.getType();

        WebRTC.getInstance().setCallType(callType);
        // activate ringing state for caller.
        isRinging = true;
        isIncoming = true;
        new RequestSignalingRinging().signalingRinging();
        // generate SDP
        G.handler.post(() -> WebRTC.getInstance().setRemoteDesc(new SessionDescription(OFFER, response.getCallerSdp())));
    }

    /**
     * this function is called when we are making a call to others
     */
    public void makeOffer(long called_userId, String callerSdp) {
        Log.d(TAG, "makeOffer: ");
        new RequestSignalingOffer().signalingOffer(called_userId, callType, callerSdp);
    }

    /**
     * this function is step 1 when making a call
     */
    public void startCall(long callPeerId, ProtoSignalingOffer.SignalingOffer.Type callType) {
        Log.d(TAG, "startCall: **************************************************************");
        Log.i(TAG, "startCall: " + callPeerId + " " + callType);
        this.callPeerId = callPeerId;
        this.callType = callType;
        // TODO: 5/12/2020 music player is changed and must be checked
        if (MusicPlayer.mp != null) {
            if (MusicPlayer.mp.isPlaying()) {
                MusicPlayer.stopSound();
                MusicPlayer.pauseSoundFromIGapCall = true;
            }
        }
        WebRTC.getInstance().createOffer(callPeerId);
    }

    /**
     * this function is called after ringing response comes from server
     */
    public void onRing() {
        Log.d(TAG, "onRing: ");
        isRinging = true;
        G.handler.post(() -> changeState(CallState.RINGING));
    }

    /**
     * this function is called after accept response comes from server
     *
     * @param response from server
     */
    public void onAccept(ProtoSignalingAccept.SignalingAcceptResponse.Builder response) {
        Log.d(TAG, "onAccept: ");
        G.handler.post(() -> {
            WebRTC.getInstance().setOfferLocalDescription();
            WebRTC.getInstance().setRemoteDesc(new SessionDescription(ANSWER, response.getCalledSdp()));
        });
    }

    /**
     * this function is called when user decide to answer
     */
    public void makeAccept(String sdp) {
        Log.d(TAG, "makeAccept: ");
        new RequestSignalingAccept().signalingAccept(sdp);
    }

    /**
     * this function is called after candidate response comes from server
     *
     * @param builder from server
     */
    public void onCandidate(ProtoSignalingCandidate.SignalingCandidateResponse.Builder builder) {
        Log.d(TAG, "onCandidate: ");
        G.handler.post(() -> WebRTC.getInstance()
                .peerConnectionInstance()
                .addIceCandidate(new IceCandidate(builder.getPeerSdpMId(), builder.getPeerSdpMLineIndex(), builder.getPeerCandidate())));
    }

    /**
     * this function is called when user wants to send its candidate info to peer
     */
    public void exchangeCandidate(String sdpMId, int sdpMLineIndex, String candidate) {
        Log.d(TAG, "exchangeCandidate: ");
        new RequestSignalingCandidate().signalingCandidate(sdpMId, sdpMLineIndex, candidate);
    }

    /**
     * this function is called after leave response comes from server in behave of other side
     *
     * @param builder from server
     */
    public void onLeave(ProtoSignalingLeave.SignalingLeaveResponse.Builder builder) {
        G.handler.post(() -> {
            Log.d(TAG, "onLeave: " + builder.getType());
            // TODO: 5/6/2020 this part needs to change based on new design
            try {
                AudioManager am = (AudioManager) G.context.getSystemService(Context.AUDIO_SERVICE);
                G.appChangeRinggerMode = false;
                am.setRingerMode(G.mainRingerMode);
            } catch (Exception e) {
            }

            isRinging = false;
            switch (builder.getType()) {
                case REJECTED:
                    changeState(CallState.REJECT);
                    break;
                case NOT_ANSWERED:
                    changeState(CallState.NOT_ANSWERED);
                    break;
                case UNAVAILABLE:
                    changeState(CallState.UNAVAILABLE);
                    break;
                case TOO_LONG:
                    changeState(CallState.TOO_LONG);
                    break;
            }
            // TODO: 5/10/2020 why ??
            changeState(CallState.LEAVE_CALL);
        });
    }

    public void leaveCall() {
        Log.d(TAG, "leaveCall: ");
        new RequestSignalingLeave().signalingLeave();
    }

    public void onHold(ProtoSignalingSessionHold.SignalingSessionHoldResponse.Builder builder) {
        Log.d(TAG, "onHold: ");
        if (builder.getHold()) {
            WebRTC.getInstance().toggleSound(false);
            changeState(CallState.ON_HOLD);
        } else {
            WebRTC.getInstance().unMuteSound();
            changeState(CallState.CONNECTED);
        }
//        G.onHoldBackgroundChanegeListener this needs to be deleted.
    }

    public void holdCall(boolean state) {
        Log.d(TAG, "holdCall: ");
        new RequestSignalingSessionHold().signalingSessionHold(state);
    }

    public void onError(int major, int minor) {
        Log.d(TAG, "onError: ");
        int messageID = R.string.e_call_permision;
        switch (major) {
            case 900:
            case 916:
            case 907:
                //                RINGING_BAD_PAYLOAD
                //                LEAVE_BAD_PAYLOAD
                //                GET_CONFIGURATION_BAD_PAYLOAD
                messageID = R.string.call_error_badPayload;
                break;
            case 901:
            case 920:
            case 917:
            case 914:
            case 911:
            case 908:
            case 903:
                //                OFFER_INTERNAL_SERVER_ERROR
                //                RINGING_INTERNAL_SERVER_ERROR
                //                ACCEPT_INTERNAL_SERVER_ERROR
                //                CANDIDATE_INTERNAL_SERVER_ERROR
                //                LEAVE_INTERNAL_SERVER_ERROR
                //                SESSION_HOLD_INTERNAL_SERVER_ERROR
                //                GET_CONFIGURATION_INTERNAL_SERVER_ERROR
                messageID = R.string.call_error_internalServer;
                break;
            case 902:
                //                OFFER_BAD_PAYLOAD
                switch (minor) {
                    case 1:
                    case 2:
                    case 3:
                        //                        Caller_SDP is invalid
                        //                        Type is invalid
                        //                        CalledUser_ID is invalid
                        messageID = R.string.call_error_offer;
                        break;
                    default:
                        messageID = R.string.call_error_badPayload;
                        break;
                }
                break;
            case 904:
                switch (minor) {
                    case 6:
                        messageID = R.string.e_904_6;
                        changeState(CallState.UNAVAILABLE);
                        break;
                    case 7:
                        messageID = R.string.e_904_7;
                        changeState(CallState.UNAVAILABLE);
                        break;
                    case 8:
                        messageID = R.string.e_904_8;
                        changeState(CallState.UNAVAILABLE);
                        break;
                    case 9:
                        messageID = R.string.e_904_9;
                        changeState(CallState.BUSY);
                        break;
                    default:
                        changeState(CallState.UNAVAILABLE);
                        break;
                }
                break;
            case 921:
            case 918:
            case 915:
            case 912:
            case 909:
                //                RINGING_FORBIDDEN
                //                ACCEPT_FORBIDDEN
                //                CANDIDATE_FORBIDDEN
                //                LEAVE_FORBIDDEN
                //                SESSION_HOLD_FORBIDDEN
                //                OFFER_FORBIDDEN
                messageID = R.string.call_error_forbidden;
                break;
            case 905:
            case 906:
                //                OFFER_PRIVACY_PROTECTION
                //                OFFER_BLOCKED_BY_PEER
                messageID = R.string.e_906_1;
                break;
            case 910:
                //                ACCEPT_BAD_PAYLOAD
                if (minor == 1) {
                    //                    Called_SDP is invalid
                    messageID = R.string.call_error_accept;
                } else {
                    messageID = R.string.call_error_badPayload;
                }
                break;
            case 913:
                //                CANDIDATE_BAD_PAYLOAD
                switch (minor) {
                    case 1:
                    case 2:
                    case 3:
                        //                        SDP_M_Line_Index is invalid
                        //                        SDP_MID is invalid
                        //                        Candidate is invalid
                        messageID = R.string.call_error_candidate;
                        break;
                    default:
                        messageID = R.string.call_error_badPayload;
                        break;
                }
                break;
            case 919:
                //                SESSION_HOLD_BAD_PAYLOAD
                if (minor == 1) {
                    //                    Hold is invalid
                    messageID = R.string.call_error_hold;
                } else {
                    messageID = R.string.call_error_badPayload;
                }
                break;
        }
        if (onCallStateChanged != null)
            onCallStateChanged.onError(messageID, major, minor);
    }

    public void toggleSpeaker() {

    }

    public void toggleMic() {
        Log.d(TAG, "toggleMic: ");
        WebRTC.getInstance().toggleSound(!isMicEnable);
        isMicEnable = !isMicEnable;
    }

    public void toggleCamera() {
        WebRTC.getInstance().switchCamera();
    }

    public void endCall() {
        Log.d(TAG, "endCall: ");
        leaveCall();
    }

    public void directMessage() {
        HelperPublicMethod.goToChatRoom(callPeerId, null, null);
    }

    public void acceptCall() {
        Log.d(TAG, "acceptCall: ");
        WebRTC.getInstance().createAnswer();
    }

    /**
     * check for acceptable modes for call
     *
     * @param type is from server
     * @return true if it is NOT secret chat and screen sharing.
     */
    private boolean invalidCallType(ProtoSignalingOffer.SignalingOffer.Type type) {
        return type == ProtoSignalingOffer.SignalingOffer.Type.SECRET_CHAT || type == ProtoSignalingOffer.SignalingOffer.Type.SCREEN_SHARING || type == ProtoSignalingOffer.SignalingOffer.Type.UNRECOGNIZED;
    }

    public void onSdpSuccess() {
        Log.d(TAG, "onSdpSuccess: ");
        if (isIncoming)
            openCallInterface();
        else {
            isRinging = false;
            // TODO: 5/5/2020 this lines should be changed and be deleted.
            G.isVideoCallRinging = false;
            try {
                AudioManager am = (AudioManager) G.context.getSystemService(Context.AUDIO_SERVICE);
                G.mainRingerMode = am.getRingerMode();
                G.appChangeRinggerMode = true;
                am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            } catch (Exception e) {
            }
            if (G.videoCallListener != null) {

                G.videoCallListener.notifyBackgroundChange();
            }
            // until here
        }
    }

    private void openCallInterface() {
        Log.d(TAG, "openCallInterface: ");
        CallSelectFragment.call(callPeerId, isIncoming, callType);
    }

    public boolean isCallAlive() {
        return callAlive;
    }

    public CallerInfo getCurrentCallerInfo() {
        Log.d(TAG, "getCurrentCallerInfo: ");
        return currentCallerInfo;
    }

    @Override
    public void receivedMessage(int id, Object... message) {

    }

    public void cleanUp() {
        Log.d(TAG, "cleanUp: ");
        onCallStateChanged = null;
        WebRTC.getInstance().close();

        instance = null;
    }

    public long getCallPeerId() {
        return callPeerId;
    }

    public ProtoSignalingOffer.SignalingOffer.Type getCallType() {
        return callType;
    }

    public void setOnCallStateChanged(CallStateChange onCallStateChanged) {
        this.onCallStateChanged = onCallStateChanged;
    }

    public boolean isIncoming() {
        return isIncoming;
    }

    public interface CallStateChange {
        void onCallStateChanged(CallState callState);

        void onError(int messageID, int major, int minor);
    }

    public void changeState(CallState callState) {
        if (onCallStateChanged != null)
            onCallStateChanged.onCallStateChanged(callState);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void placeOutgoingCall(Context mContext) {
        TelecomManager tm = (TelecomManager) mContext.getSystemService(Context.TELECOM_SERVICE);
        PhoneAccountHandle phoneAccountHandle = addAccountToTelecomManager(mContext);
        if (!tm.isOutgoingCallPermitted(phoneAccountHandle)) {
            Toast.makeText(mContext, "R.string.outgoingCallNotPermitted", Toast.LENGTH_SHORT).show();
            return;
        }
        Bundle extras = new Bundle();
        extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle);
        tm.placeCall(Uri.fromParts("tel", "+98" + info.getPhoneNumber(), null), extras);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void placeIncomingCall(Context mContext) {
        TelecomManager tm = (TelecomManager) mContext.getSystemService(Context.TELECOM_SERVICE);
        PhoneAccountHandle phoneAccountHandle = addAccountToTelecomManager(mContext);
        if (!tm.isIncomingCallPermitted(phoneAccountHandle)) {
            Toast.makeText(mContext, "R.string.incomingCallNotPermitted", Toast.LENGTH_SHORT).show();
            return;
        }
        Bundle extras = new Bundle();
        extras.putParcelable(TelecomManager.EXTRA_INCOMING_CALL_ADDRESS, Uri.parse(info.getPhoneNumber()));
        tm.addNewIncomingCall(phoneAccountHandle, extras);
    }

    @TargetApi(Build.VERSION_CODES.O)
    protected PhoneAccountHandle addAccountToTelecomManager(Context mContext) {
        TelecomManager tm = (TelecomManager) mContext.getSystemService(Context.TELECOM_SERVICE);
        PhoneAccountHandle handle = new PhoneAccountHandle(new ComponentName(mContext, CallConnectionService.class), "1001");
        DbManager.getInstance().doRealmTask(realm -> {
            info = realm.where(RealmUserInfo.class).findFirst().getUserInfo();
        });
        PhoneAccount account = new PhoneAccount.Builder(handle, info.getDisplayName())
                .setCapabilities(PhoneAccount.CAPABILITY_SELF_MANAGED)
                .setIcon(Icon.createWithResource(mContext, R.drawable.logo_igap))
                .setHighlightColor(0xff2ca5e0)
                .addSupportedUriScheme("sip")
                .build();
        tm.registerPhoneAccount(account);
        return handle;
    }

    private static boolean isDeviceCompatibleWithConnectionServiceAPI() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return false;
        // some non-Google devices don't implement the ConnectionService API correctly so, sadly,
        // we'll have to whitelist only a handful of known-compatible devices for now
        return "angler".equals(Build.PRODUCT)            // Nexus 6P
                || "bullhead".equals(Build.PRODUCT)        // Nexus 5X
                || "sailfish".equals(Build.PRODUCT)        // Pixel
                || "marlin".equals(Build.PRODUCT)        // Pixel XL
                || "walleye".equals(Build.PRODUCT)        // Pixel 2
                || "taimen".equals(Build.PRODUCT)        // Pixel 2 XL
                || "blueline".equals(Build.PRODUCT)        // Pixel 3
                || "crosshatch".equals(Build.PRODUCT);    // Pixel 3 XL
    }

    public boolean isMicMute() {
        return isMicEnable;
    }
}
