package com.sktlab.bizconfmobile.activity;

import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.util.Util;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class VersionStatementActivity extends BaseActivity {

	private TextView mTvVersionState;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_verision_statement);

		mTvVersionState = (TextView) findViewById(R.id.tv_version_statement);

		String versionStatement = Util.replaceString(this,
				R.string.toast_version_statement, Util.getVersionName(this));

		mTvVersionState.setText(Html.fromHtml(versionStatement));

		mTvVersionState.setMovementMethod(LinkMovementMethod.getInstance());
	}

}
