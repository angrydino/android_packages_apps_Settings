/*
 * Copyright (C) 2012 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.profiles;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.android.settings.R;

public class ProfileAirplaneModePreference extends Preference implements
        CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private boolean mProtectFromCheckedChange = false;

    private CheckBox mCheckBox;

    final static String TAG = "ProfileSilentModePreference";

    private ProfileConfig.AirplaneModeItem mAirplaneModeItem;

    final static int defaultChoice = -1;

    private int currentChoice;

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public ProfileAirplaneModePreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * @param context
     * @param attrs
     */
    public ProfileAirplaneModePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * @param context
     */
    public ProfileAirplaneModePreference(Context context) {
        super(context);
        init();
    }

    @Override
    public View getView(View convertView, ViewGroup parent) {
        View view = super.getView(convertView, parent);

        View widget = view.findViewById(R.id.profile_checkbox);
        if ((widget != null) && widget instanceof CheckBox) {
            mCheckBox = (CheckBox) widget;
            mCheckBox.setOnCheckedChangeListener(this);

            mProtectFromCheckedChange = true;
            mCheckBox.setChecked(isChecked());
            mProtectFromCheckedChange = false;
        }

        View textLayout = view.findViewById(R.id.text_layout);
        if ((textLayout != null) && textLayout instanceof LinearLayout) {
            textLayout.setOnClickListener(this);
        }

        return view;
    }

    private void init() {
        setLayoutResource(R.layout.preference_streamvolume);
    }

    public boolean isChecked() {
        return mAirplaneModeItem != null && mAirplaneModeItem.mSettings.isOverride();
    }

    public void setAirplaneModeItem(ProfileConfig.AirplaneModeItem airplaneModeItem) {
        mAirplaneModeItem = airplaneModeItem;

        if (mCheckBox != null) {
            mCheckBox.setChecked(mAirplaneModeItem.mSettings.isOverride());
        }
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (mProtectFromCheckedChange) {
            return;
        }

        mAirplaneModeItem.mSettings.setOverride(isChecked);

        callChangeListener(isChecked);
    }

    protected Dialog createAirplaneModeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final String[] AirplaneModeValues = getContext().getResources().getStringArray(
                R.array.profile_connection_values);

        currentChoice = mAirplaneModeItem.mSettings.getValue();

        builder.setTitle(R.string.profile_airplanemode_title);
        builder.setSingleChoiceItems(R.array.profile_connection_entries, currentChoice,
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                currentChoice = item;
            }
        });

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (currentChoice != defaultChoice) {
                    int value = Integer.parseInt(AirplaneModeValues[currentChoice]);
                    mAirplaneModeItem.mSettings.setValue(currentChoice);
                    setSummary(value == 1 ? getContext().getString(R.string.connection_state_enabled) : getContext()
                            .getString(R.string.connection_state_disabled));
                }
            }
        });

        builder.setNegativeButton(android.R.string.cancel, null);
        return builder.create();
    }

    public ProfileConfig.AirplaneModeItem getAirplaneModeItem() {
        return mAirplaneModeItem;
    }

    @Override
    public void onClick(android.view.View v) {
        if ((v != null) && (R.id.text_layout == v.getId())) {
            createAirplaneModeDialog().show();
        }
    }

    public void setSummary(Context context) {
        int value = mAirplaneModeItem.mSettings.getValue();
        mAirplaneModeItem.mSettings.setValue(currentChoice);
        setSummary(value == 1 ? getContext().getString(R.string.connection_state_enabled) : getContext().getString(
                R.string.connection_state_disabled));
    }
}
