package org.sufficientlysecure.keychain.ui.keyunlock.activities;


import android.content.Context;
import android.os.Bundle;

import org.sufficientlysecure.keychain.ui.keyunlock.Model.WizardModel;
import org.sufficientlysecure.keychain.ui.keyunlock.base.BaseViewModel;

/**
 * View Model for CreateKeyWizardActivity
 * // TODO: 17/06/2015 mUseSmartCardSettings is a placeholder
 */
public class CreateKeyWizardViewModel implements BaseViewModel {
    public static final String STATE_SAVE_WIZARD_STEP = "STATE_SAVE_WIZARD_STEP";
    public static final String STATE_SAVE_WIZARD_MODEL = "STATE_SAVE_WIZARD_MODEL";
    public static final String EXTRA_FIRST_TIME = "EXTRA_FIRST_TIME";
    private WizardStep mWizardStep = WizardStep.WIZARD_STEP_BEGIN;
    private WizardModel mWizardModel;
    private Context mContext;
    private boolean mUseSmartCardSettings = false;
    private boolean mFirstTime = true;

    /**
     * Wizard screen steps
     */
    public enum WizardStep {
        WIZARD_STEP_BEGIN,
        WIZARD_STEP_CHOOSE_UNLOCK_METHOD,
        WIZARD_STEP_KEYWORD_INPUT_VERIFICATION,
        WIZARD_STEP_CONTACT_NAME,
        WIZARD_STEP_CONTACT_EMAILS,
        WIZARD_STEP_FINALIZE
    }

    @Override
    public void prepareViewModel(Bundle savedInstanceState, Bundle arguments, Context context) {
        mContext = context;
        mWizardModel = new WizardModel();
        if (savedInstanceState != null) {
            restoreViewModelState(savedInstanceState);
        }
        mWizardStep = WizardStep.WIZARD_STEP_BEGIN;
    }

    @Override
    public void saveViewModelState(Bundle outState) {
        outState.putSerializable(STATE_SAVE_WIZARD_STEP, mWizardStep);
        outState.putParcelable(STATE_SAVE_WIZARD_MODEL, mWizardModel);
    }

    @Override
    public void restoreViewModelState(Bundle savedInstanceState) {
        mWizardStep = (CreateKeyWizardViewModel.WizardStep) savedInstanceState.
                getSerializable(STATE_SAVE_WIZARD_STEP);

        mWizardModel = savedInstanceState.getParcelable(STATE_SAVE_WIZARD_MODEL);

        mFirstTime = savedInstanceState.getBoolean(EXTRA_FIRST_TIME);
    }

    @Override
    public void onViewModelCreated() {

    }

    /**
     * Updates the state of the wizard when the user presses the back button.
     */
    public void updateWizardStateOnBack() {
        switch (mWizardStep) {
            case WIZARD_STEP_BEGIN: {
            }
            break;
            case WIZARD_STEP_CHOOSE_UNLOCK_METHOD: {
                mWizardStep = WizardStep.WIZARD_STEP_BEGIN;
            }
            break;
            case WIZARD_STEP_KEYWORD_INPUT_VERIFICATION: {
                mWizardStep = WizardStep.WIZARD_STEP_CHOOSE_UNLOCK_METHOD;
            }
            break;
            case WIZARD_STEP_CONTACT_NAME: {
                mWizardStep = WizardStep.WIZARD_STEP_KEYWORD_INPUT_VERIFICATION;

            }
            break;
            case WIZARD_STEP_CONTACT_EMAILS: {
                mWizardStep = WizardStep.WIZARD_STEP_CONTACT_NAME;

            }
            break;
            case WIZARD_STEP_FINALIZE: {
                mWizardStep = WizardStep.WIZARD_STEP_CONTACT_EMAILS;
            }
            break;
            default:
                break;
        }
    }

    /**
     * Updates the state of the wizard when the user presses the next button.
     */
    public void updateWizardStateOnNext() {
        switch (mWizardStep) {
            case WIZARD_STEP_BEGIN: {
                mWizardStep = WizardStep.WIZARD_STEP_CHOOSE_UNLOCK_METHOD;
            }
            break;
            case WIZARD_STEP_CHOOSE_UNLOCK_METHOD: {
                mWizardStep = WizardStep.WIZARD_STEP_KEYWORD_INPUT_VERIFICATION;
            }
            break;
            case WIZARD_STEP_KEYWORD_INPUT_VERIFICATION: {
                mWizardStep = WizardStep.WIZARD_STEP_CONTACT_NAME;
            }
            break;
            case WIZARD_STEP_CONTACT_NAME: {
                mWizardStep = WizardStep.WIZARD_STEP_CONTACT_EMAILS;

            }
            break;
            case WIZARD_STEP_CONTACT_EMAILS: {
                mWizardStep = WizardStep.WIZARD_STEP_FINALIZE;

            }
            break;
            case WIZARD_STEP_FINALIZE: {
                //key creation time ?
            }
            break;
            default:
                break;
        }
    }

    /**
     * Wizard Current Step methods.
     */

    public WizardStep getWizardStep() {
        return mWizardStep;
    }

    public void setWizardStep(WizardStep wizardStep) {
        this.mWizardStep = wizardStep;
    }

    /**
     * Wizard Model Methods.
     */

    public WizardModel getWizardModel() {
        return mWizardModel;
    }

    public void setWizardModel(WizardModel mWizardModel) {
        this.mWizardModel = mWizardModel;
    }

    public boolean isUseSmartCardSettings() {
        return mUseSmartCardSettings;
    }

    public void setUseSmartCardSettings(boolean useSmartCardSettings) {
        mUseSmartCardSettings = useSmartCardSettings;
    }

    public boolean isFirstTime() {
        return mFirstTime;
    }

    public void setFirstTime(boolean firstTime) {
        mFirstTime = firstTime;
    }
}