/*
 * Copyright (C) 2015 Dominik Schürmann <dominik@dominikschuermann.de>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sufficientlysecure.keychain.ui.wizard;


import android.app.Activity;
import android.os.Bundle;

import org.sufficientlysecure.keychain.R;
import org.sufficientlysecure.keychain.ui.base.BaseViewModel;
import org.sufficientlysecure.keychain.ui.base.WizardFragmentListener;
import org.sufficientlysecure.keychain.util.Passphrase;

public class PinUnlockWizardFragmentViewModel implements BaseViewModel {
    public static final String STATE_SAVE_LAST_KEYWORD = "STATE_SAVE_LAST_KEYWORD";
    public static final String STATE_SAVE_CURRENT_KEYWORD = "STATE_SAVE_CURRENT_KEYWORD";
    public static final String STATE_SAVE_OPERATION_STATE = "STATE_SAVE_OPERATION_STATE";

    private OperationState mOperationState = OperationState.OPERATION_STATE_INPUT_FIRST_KEYWORD;
    private StringBuilder mLastInputKeyWord;
    private StringBuilder mCurrentInputKeyWord;
    private Activity mActivity;
    private OnViewModelEventBind mOnViewModelEventBind;
    private WizardFragmentListener mWizardFragmentListener;

    /**
     * View Model communication
     */
    public interface OnViewModelEventBind {
        void onOperationStateError(String error);

        void onOperationStateOK(String showText);

        void onOperationStateCompleted(String showText);

        void hideNavigationButtons(boolean hideBack, boolean hideNext);
    }

    /**
     * Operation state
     */
    public enum OperationState {
        OPERATION_STATE_INPUT_FIRST_KEYWORD,
        OPERATION_STATE_INPUT_SECOND_KEYWORD,
        OPERATION_STATE_FINISHED
    }

    public PinUnlockWizardFragmentViewModel(OnViewModelEventBind viewModelEventBind,
                                            WizardFragmentListener wizardActivity) {
        mOnViewModelEventBind = viewModelEventBind;
        mWizardFragmentListener = wizardActivity;

        if (mOnViewModelEventBind == null || mWizardFragmentListener == null) {
            throw new NullPointerException();
        }
    }

    @Override
    public void prepareViewModel(Bundle savedInstanceState, Bundle arguments, Activity activity) {
        mActivity = activity;
        if (savedInstanceState == null) {
            initializeUnlockOperation();
        } else {
            restoreViewModelState(savedInstanceState);
        }

        mOnViewModelEventBind.hideNavigationButtons(false, false);
    }

    @Override
    public void saveViewModelState(Bundle outState) {
        outState.putSerializable(STATE_SAVE_LAST_KEYWORD, mLastInputKeyWord);
        outState.putSerializable(STATE_SAVE_OPERATION_STATE, mOperationState);
        outState.putSerializable(STATE_SAVE_CURRENT_KEYWORD, mCurrentInputKeyWord);
    }

    @Override
    public void restoreViewModelState(Bundle savedInstanceState) {
        mLastInputKeyWord = (StringBuilder) savedInstanceState.getSerializable(STATE_SAVE_LAST_KEYWORD);
        mOperationState = (OperationState) savedInstanceState.getSerializable(STATE_SAVE_OPERATION_STATE);
        mCurrentInputKeyWord = (StringBuilder) savedInstanceState.getSerializable(STATE_SAVE_CURRENT_KEYWORD);
    }

    /**
     * Initializes the operation
     */
    public void initializeUnlockOperation() {
        if (mLastInputKeyWord == null) {
            mLastInputKeyWord = new StringBuilder();
        } else {
            clearInputKeyword();
        }

        if (mCurrentInputKeyWord == null) {
            mCurrentInputKeyWord = new StringBuilder();
        } else {
            clearInputKeyword();
        }

        mOperationState = OperationState.OPERATION_STATE_INPUT_FIRST_KEYWORD;
    }

    /**
     * Resets the keyword input to its initial step, allowing the user to re-input the pin again.
     */
    public void resetKeywordInputToBegin() {
        if (mLastInputKeyWord == null) {
            mLastInputKeyWord = new StringBuilder();
        } else {
            clearInputKeyword();
        }

        if (mCurrentInputKeyWord == null) {
            mCurrentInputKeyWord = new StringBuilder();
        } else {
            clearInputKeyword();
        }

        mOperationState = OperationState.OPERATION_STATE_INPUT_FIRST_KEYWORD;
    }


    /**
     * Handles the first pin input operation.
     *
     * @return
     */
    public boolean onOperationStateInputFirstKeyword() {
        mOnViewModelEventBind.onOperationStateOK("");
        if (mCurrentInputKeyWord.length() == 0) {
            mOnViewModelEventBind.onOperationStateError(mActivity.getString(R.string.error_no_pin));
            resetCurrentKeyword();
            return false;
        }
        mLastInputKeyWord.append(mCurrentInputKeyWord);
        mOperationState = OperationState.OPERATION_STATE_INPUT_SECOND_KEYWORD;
        resetCurrentKeyword();
        mOnViewModelEventBind.onOperationStateOK(mActivity.getString(R.string.reenter_pin));
        return true;
    }

    /**
     * handles the second pin input operation.
     *
     * @return
     */
    public boolean onOperationStateInputSecondKeyword() {
        if (!(mLastInputKeyWord.toString().equals(mCurrentInputKeyWord.toString()))) {
            mOnViewModelEventBind.onOperationStateError(mActivity.getString(R.string.error_pin_mismatch));
            resetKeywordInputToBegin();
            return false;
        } else if (mCurrentInputKeyWord.length() == 0) {
            mOnViewModelEventBind.onOperationStateError(mActivity.getString(R.string.error_no_pin));
            resetKeywordInputToBegin();
            return false;
        }
        mOperationState = OperationState.OPERATION_STATE_FINISHED;
        resetCurrentKeyword();
        mOnViewModelEventBind.onOperationStateCompleted("");
        return true;
    }

    /**
     * Updates the operation state.
     *
     * @return
     */
    public boolean updateOperationState() {
        if (mOperationState == OperationState.OPERATION_STATE_FINISHED) {
            return true;
        }

        switch (mOperationState) {
            case OPERATION_STATE_INPUT_FIRST_KEYWORD: {
                return onOperationStateInputFirstKeyword();
            }
            case OPERATION_STATE_INPUT_SECOND_KEYWORD: {
                return onOperationStateInputSecondKeyword();
            }
            default:
                return false;
        }
    }

    /**
     * Clears all input keywords if they were initialized.
     */
    private void clearInputKeyword() {
        if (mLastInputKeyWord != null) {
            mLastInputKeyWord.setLength(0);
        }
        if (mCurrentInputKeyWord != null) {
            mCurrentInputKeyWord.setLength(0);
        }
    }

    /**
     * Returns the current Pin length.
     * @return
     */
    public Integer getPinLength() {
        return mCurrentInputKeyWord.length();
    }

    /**
     * Resets the current input keyword.
     */
    public void resetCurrentKeyword() {
        mCurrentInputKeyWord.setLength(0);
    }

    /**
     * Appends the input text to the current keyword.
     *
     * @param text
     */
    public void appendToCurrentKeyword(CharSequence text) {
        mCurrentInputKeyWord.append(text);
    }

    /**
     * Performs operations when the user clicks on the wizard next button.
     * The view model itself will callback to the wizard activity for non ui methods.
     *
     * @return
     */
    public boolean onNextClicked() {
        if (mOperationState != OperationState.OPERATION_STATE_FINISHED) {
            updateOperationState();
            return false;
        } else {
            Passphrase passphrase = new Passphrase(mLastInputKeyWord.toString());
            passphrase.setSecretKeyType(mWizardFragmentListener.getSecretKeyType());
            mWizardFragmentListener.setPassphrase(passphrase);
            return true;
        }
    }
}
