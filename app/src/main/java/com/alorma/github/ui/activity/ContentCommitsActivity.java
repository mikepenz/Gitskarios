package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.commit.CommitsListFragment;

/**
 * Created by Bernat on 14/07/2015.
 */
public class ContentCommitsActivity extends BackActivity {

    private static final String REPO_INFO = "REPO_INFO";
    private static final String PATH = "PATH";
    private static final String NAME = "NAME";

    public static Intent createLauncherIntent(Context context, RepoInfo repoInfo, String name, String path) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(REPO_INFO, repoInfo);
        bundle.putString(PATH, path);
        bundle.putString(NAME, name);

        Intent intent = new Intent(context, ContentCommitsActivity.class);
        intent.putExtras(bundle);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_toolbar);

        if (getIntent() != null && getIntent().getExtras() != null) {
            RepoInfo repoInfo = getIntent().getExtras().getParcelable(REPO_INFO);
            String path = getIntent().getExtras().getString(PATH);
            String name = getIntent().getExtras().getString(NAME);

            setTitle(getString(R.string.content_history, name));

            if (getSupportActionBar() != null) {
                getSupportActionBar().setSubtitle(path);
            }

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content, CommitsListFragment.newInstance(repoInfo, path));
            ft.commit();
        }
    }
}
