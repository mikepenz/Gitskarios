package com.alorma.github.ui.fragment.gists;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Gist;
import com.alorma.github.sdk.services.client.GithubListClient;
import com.alorma.github.sdk.services.gists.UserGistsClient;
import com.alorma.github.ui.activity.gists.CreateGistActivity;
import com.alorma.github.ui.activity.gists.GistDetailActivity;
import com.alorma.github.ui.activity.gists.GistsFileActivity;
import com.alorma.github.ui.adapter.GistsAdapter;
import com.alorma.github.ui.fragment.base.LoadingListFragment;
import com.alorma.github.ui.fragment.detail.repo.BackManager;
import com.alorma.gitskarios.core.Pair;
import com.mikepenz.octicons_typeface_library.Octicons;
import java.util.List;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class AuthUserGistsFragment extends LoadingListFragment<GistsAdapter>
    implements GistsAdapter.GistsAdapterListener, BackManager {

  public static AuthUserGistsFragment newInstance() {
    return new AuthUserGistsFragment();
  }

  @Override
  protected void loadArguments() {

  }

  @Override
  public void onResume() {
    super.onResume();

    getActivity().setTitle(R.string.navigation_gists);
  }

  @Override
  protected int getLightTheme() {
    return R.style.AppTheme_Gists;
  }

  @Override
  protected int getDarkTheme() {
    return R.style.AppTheme_Dark_Gists;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    GistsAdapter adapter = new GistsAdapter(LayoutInflater.from(getActivity()));
    adapter.setFilesCallback(item -> {
      Intent launcherIntent =
              GistsFileActivity.createLauncherIntent(getActivity(), item.filename, item.content);
      startActivity(launcherIntent);
    });
    adapter.setGistsAdapterListener(this);
    setAdapter(adapter);

    if (getActivity() != null) {
      AppCompatActivity activity = (AppCompatActivity) getActivity();
      ActionBar actionBar = activity.getSupportActionBar();
      if (actionBar != null) {
        int color = ContextCompat.getColor(activity, R.color.md_blue_grey_600);
        int colorDark = ContextCompat.getColor(activity, R.color.md_blue_grey_800);
        ColorDrawable colorDrawable = new ColorDrawable(color);

        actionBar.setBackgroundDrawable(colorDrawable);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          activity.getWindow().setStatusBarColor(colorDark);
        }
      }
    }
  }

  @Override
  protected Octicons.Icon getNoDataIcon() {
    return Octicons.Icon.oct_gist;
  }

  @Override
  protected int getNoDataText() {
    return R.string.no_gists;
  }

  @Override
  protected void executeRequest() {
    super.executeRequest();

    setAction(new UserGistsClient());
  }

  @Override
  protected void executePaginatedRequest(int page) {
    super.executePaginatedRequest(page);

    setAction(new UserGistsClient(page));
  }

  private void setAction(GithubListClient<List<Gist>> userGistsClient) {
    userGistsClient.observable()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .map(new Func1<Pair<List<Gist>, Integer>, List<Gist>>() {
          @Override
          public List<Gist> call(Pair<List<Gist>, Integer> listIntegerPair) {
            setPage(listIntegerPair.second);
            return listIntegerPair.first;
          }
        })
        .subscribe(new Subscriber<List<Gist>>() {
          @Override
          public void onCompleted() {
            stopRefresh();
          }

          @Override
          public void onError(Throwable e) {
            stopRefresh();
            if (getAdapter() == null || getAdapter().getItemCount() == 0) {
              setEmpty();
            }
          }

          @Override
          public void onNext(List<Gist> gists) {
            if (refreshing) {
              getAdapter().clear();
            }
            getAdapter().addAll(gists);
          }
        });
  }

  @Override
  protected Octicons.Icon getFABGithubIcon() {
    return Octicons.Icon.oct_plus;
  }

  @Override
  protected boolean useFAB() {
    return true;
  }

  @Override
  public void onGistSelected(Gist gist) {
    Intent launcherIntent = GistDetailActivity.createLauncherIntent(getActivity(), gist.id);
    startActivity(launcherIntent);
  }

  @Override
  protected void fabClick() {
    try {
      Intent intent = CreateGistActivity.createLauncherIntent(getActivity());
      startActivity(intent);
    } catch (ActivityNotFoundException e) {
      e.printStackTrace();
    }
  }

  @Override
  public boolean onBackPressed() {
    return false;
  }
}