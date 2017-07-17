package net.iGap.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import java.util.HashMap;
import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperAvatar;
import net.iGap.interfaces.OnAvatarGet;
import net.iGap.module.AndroidUtils;
import net.iGap.module.CircleImageView;
import net.iGap.realm.RealmGeoNearbyDistance;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRegisteredInfoFields;
import net.iGap.request.RequestGeoGetComment;
import net.iGap.request.RequestGeoGetNearbyDistance;

import static net.iGap.G.inflater;

public class FragmentMapUsers extends Fragment {

    private FragmentActivity mActivity;
    private RecyclerView mRecyclerView;
    private MapUserAdapter mAdapter;
    private HashMap<Long, CircleImageView> hashMapAvatar = new HashMap<>();

    public FragmentMapUsers() {
        // Required empty public constructor
    }

    public static FragmentMapUsers newInstance() {
        return new FragmentMapUsers();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map_users, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initComponent(view);
        if (FragmentiGapMap.location != null) {
            new RequestGeoGetNearbyDistance().getNearbyDistance(FragmentiGapMap.location.getLatitude(), FragmentiGapMap.location.getLongitude());
        }
    }

    private void initComponent(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rcy_map_user);
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(RealmGeoNearbyDistance.class).findAll().deleteAllFromRealm();
            }
        });
        mAdapter = new MapUserAdapter(realm.where(RealmGeoNearbyDistance.class).findAll(), true);
        mRecyclerView.setAdapter(mAdapter);
        realm.close();
    }

    private class MapUserAdapter extends RealmRecyclerViewAdapter<RealmGeoNearbyDistance, MapUserAdapter.ViewHolder> {
        public MapUserAdapter(RealmResults<RealmGeoNearbyDistance> data, boolean autoUpdate) {
            super(data, autoUpdate);
        }

        @Override
        public MapUserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MapUserAdapter.ViewHolder(inflater.inflate(R.layout.map_user_item, parent, false));
        }

        @Override
        public void onBindViewHolder(final MapUserAdapter.ViewHolder holder, int i) {
            RealmGeoNearbyDistance item = getItem(i);
            if (item == null) {
                return;
            }
            Realm realm = Realm.getDefaultInstance();
            RealmRegisteredInfo registeredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, item.getUserId()).findFirst();
            if (registeredInfo == null) {
                realm.close();
                return;
            }

            holder.username.setText(registeredInfo.getDisplayName());
            if (item.isHasComment()) {
                if (item.getComment().isEmpty()) {
                    holder.comment.setText("getting comment...");
                    new RequestGeoGetComment().getComment(item.getUserId());
                } else {
                    holder.comment.setText(item.getComment());
                }

            } else {
                holder.comment.setText("no comment");
            }

            holder.distance.setText(item.getDistance() + "");

            hashMapAvatar.put(item.getUserId(), holder.avatar);
            HelperAvatar.getAvatar(item.getUserId(), HelperAvatar.AvatarType.USER, new OnAvatarGet() {
                @Override
                public void onAvatarGet(final String avatarPath, final long ownerId) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), hashMapAvatar.get(ownerId));
                        }
                    });
                }

                @Override
                public void onShowInitials(final String initials, final String color) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            holder.avatar.setImageBitmap(net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.avatar.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                        }
                    });
                }
            });

            realm.close();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            public CircleImageView avatar;
            public TextView username;
            public TextView comment;
            public TextView distance;

            public ViewHolder(View itemView) {
                super(itemView);

                avatar = (CircleImageView) itemView.findViewById(R.id.img_user_avatar_map);
                username = (TextView) itemView.findViewById(R.id.txt_user_name_map);
                comment = (TextView) itemView.findViewById(R.id.txt_user_comment_map);
                distance = (TextView) itemView.findViewById(R.id.txt_user_distance_map);
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (FragmentActivity) activity;
    }
}
