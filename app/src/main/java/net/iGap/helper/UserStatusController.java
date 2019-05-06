package net.iGap.helper;

import android.util.Log;

import net.iGap.proto.ProtoUserUpdateStatus;
import net.iGap.request.RequestUserUpdateStatus;

public class UserStatusController implements RequestUserUpdateStatus.onUserStatus {

    private static final UserStatusController ourInstance = new UserStatusController();

    public static UserStatusController getInstance() {
        return ourInstance;
    }

    private final Object mutex;
    private boolean isOnlineRequestPending;
    private boolean isOfflineRequestPending;

    private int offlineTry;

    private UserStatusController() {
        this.mutex = new Object();
        this.offlineTry = 0;
    }

    public void setOnline() {
        synchronized (mutex) {
            if (isOnlineRequestPending) {
                return;
            }

            if (isOfflineRequestPending) {
                LooperThreadHelper.getInstance().getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setOnline();
                    }
                }, 1000);
                return;
            }
            isOnlineRequestPending = new RequestUserUpdateStatus().userUpdateStatus(ProtoUserUpdateStatus.UserUpdateStatus.Status.ONLINE, this);
            if (!isOnlineRequestPending) {
                LooperThreadHelper.getInstance().getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setOnline();
                    }
                }, 1000);
            }
        }
    }

    public void setOffline() {
        synchronized (mutex) {
            if (isOfflineRequestPending) {
                return;
            }

            if (isOnlineRequestPending) {
                LooperThreadHelper.getInstance().getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setOffline();
                    }
                }, 1000);
                return;
            }

            offlineTry = offlineTry + 1;
            if (offlineTry > 15) {
                offlineTry = 0;
                return;
            }

            isOfflineRequestPending = new RequestUserUpdateStatus().userUpdateStatus(ProtoUserUpdateStatus.UserUpdateStatus.Status.OFFLINE, this);
            if (!isOfflineRequestPending) {
                LooperThreadHelper.getInstance().getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setOffline();
                    }
                }, 1000);
            }
        }
    }

    @Override
    public void onUpdateUserStatus() {
        handleResponse(false);
    }

    @Override
    public void onError(int major, int minor) {
        handleResponse(major == 5 && minor == 1);
    }

    private void handleResponse(boolean timeout) {
        boolean isOnlineResponse = false;
        synchronized (mutex) {
            if (isOfflineRequestPending && isOnlineRequestPending) {
                HelperLog.setErrorLog("Bagi Error Please Check!");
                Log.d("bagi", "error in UserStatus");
            }

            if (isOnlineRequestPending) {
                isOnlineResponse = true;
                isOnlineRequestPending = false;
            }

            if (isOfflineRequestPending) {
                isOnlineResponse = false;
                offlineTry = 0;
                isOfflineRequestPending = false;
            }
        }

        if (timeout) {
            if (isOnlineResponse) {
                setOnline();
            } else {
                setOffline();
            }
        }
    }
}
