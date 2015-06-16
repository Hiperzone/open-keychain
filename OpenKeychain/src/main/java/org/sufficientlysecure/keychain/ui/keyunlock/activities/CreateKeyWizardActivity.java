package org.sufficientlysecure.keychain.ui.keyunlock.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import org.sufficientlysecure.keychain.R;
import org.sufficientlysecure.keychain.pgp.CanonicalizedSecretKey;
import org.sufficientlysecure.keychain.ui.base.BaseActivity;
import org.sufficientlysecure.keychain.ui.keyunlock.base.WizardFragmentListener;
import org.sufficientlysecure.keychain.ui.keyunlock.base.WizardFragment;
import org.sufficientlysecure.keychain.ui.keyunlock.dialogs.PinUnlockDialog;
import org.sufficientlysecure.keychain.ui.keyunlock.wizard.EmailWizardFragment;
import org.sufficientlysecure.keychain.ui.keyunlock.wizard.NameWizardFragment;
import org.sufficientlysecure.keychain.ui.keyunlock.wizard.PinUnlockWizardFragment;
import org.sufficientlysecure.keychain.ui.keyunlock.wizard.UnlockChoiceWizardFragment;
import org.sufficientlysecure.keychain.ui.keyunlock.wizard.WelcomeWizardFragment;
import org.sufficientlysecure.keychain.ui.keyunlock.wizard.WizardConfirmationFragment;
import org.sufficientlysecure.keychain.util.Passphrase;

import java.util.ArrayList;

/**
 * Activity for creating keys with different security options.
 */
public class CreateKeyWizardActivity
        extends BaseActivity
        implements PinUnlockDialog.onKeyUnlockListener, WizardFragmentListener {

    public static final String TAG = "CreateKeyWizardActivity";
    public static final String FRAGMENT_TAG = "CurrentWizardFragment";

    private CreateKeyWizardViewModel mCreateKeyWizardViewModel;
    private Button mNextButton;
    private Button mBackButton;
    private LinearLayout mCreateKeyWizardActivityButtonContainer;
    private WizardFragment mCurrentVisibleFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCreateKeyWizardViewModel = new CreateKeyWizardViewModel();
        mCreateKeyWizardViewModel.prepareViewModel(savedInstanceState, getIntent().getExtras(),
                this);

        if (savedInstanceState == null) {
            updateWizardState();
        } else {
            mCurrentVisibleFragment = (WizardFragment) getSupportFragmentManager().
                    findFragmentByTag(FRAGMENT_TAG);
        }
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.create_key_wizard_activity);

        mNextButton = (Button) findViewById(R.id.nextButton);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextClicked(v);
            }
        });

        mBackButton = (Button) findViewById(R.id.backButton);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackClicked(v);
            }
        });

        mCreateKeyWizardActivityButtonContainer =
                (LinearLayout) findViewById(R.id.createKeyWizardActivityButtonContainer);

        setTitle(getString(R.string.create_key_wizard_title));
    }

    /**
     * Updates the interface and the viewModel state when the user clicks on the back button.
     *
     * @param view
     */
    public void onBackClicked(View view) {
        getSupportFragmentManager().popBackStack();
        mCreateKeyWizardViewModel.updateWizardStateOnBack();
    }

    /**
     * Updates the interface and the viewModel state when the user clicks on the next button.
     *
     * @param view
     */
    public void onNextClicked(View view) {
        if (mCurrentVisibleFragment != null && mCurrentVisibleFragment.onNextClicked()) {
            mCreateKeyWizardViewModel.updateWizardStateOnNext();
            updateWizardState();
        }
    }

    /**
     * Callback for when the user confirms his unlock keyword (passphrase, pin, etc)
     * This method is only called when there is a double keyword confirmation.
     */
    @Override
    public void onNewUnlockKeywordConfirmed() {
        mCreateKeyWizardViewModel.updateWizardStateOnNext();
        updateWizardState();
    }

    /**
     * User canceled the unlock method dialog, go to previous step.
     */
    @Override
    public void onNewUnlockMethodCancel() {
        mCreateKeyWizardViewModel.updateWizardStateOnBack();
    }

    /**
     * Callback for when the user confirms his unlock pin
     * This method is only called when there is an unlock request.
     */
    @Override
    public void onUnlockRequest() {
        Log.v(TAG, "onUnlockRequest");
    }

    @Override
    public void onHideNavigationButtons(boolean hide) {
        if (hide) {
            mCreateKeyWizardActivityButtonContainer.setVisibility(View.INVISIBLE);
            mCreateKeyWizardActivityButtonContainer.animate().setDuration(400);
            mCreateKeyWizardActivityButtonContainer.animate().alpha(0);

        } else {
            mCreateKeyWizardActivityButtonContainer.setVisibility(View.VISIBLE);
            mCreateKeyWizardActivityButtonContainer.animate().setDuration(400);
            mCreateKeyWizardActivityButtonContainer.animate().alpha(1);
        }
    }

    @Override
    public void onAdvanceToNextWizardStep() {
        mCreateKeyWizardViewModel.updateWizardStateOnNext();
        updateWizardState();
    }


    /**
     * Updates the model with the current selected unlock type.
     *
     * @param secretKeyType
     */
    @Override
    public void setUnlockMethod(CanonicalizedSecretKey.SecretKeyType secretKeyType) {
        mCreateKeyWizardViewModel.getWizardModel().setSecretKeyType(secretKeyType);
    }

    @Override
    public void setPassphrase(Passphrase passphrase) {
        mCreateKeyWizardViewModel.getWizardModel().setPassword(passphrase);
    }

    @Override
    public void setUserName(CharSequence userName) {
        mCreateKeyWizardViewModel.getWizardModel().setName(userName.toString());
    }

    @Override
    public void setAdditionalEmails(ArrayList<String> additionalEmails) {
        mCreateKeyWizardViewModel.getWizardModel().setAdditionalEmails(additionalEmails);
    }

    @Override
    public void setEmail(CharSequence email) {
        mCreateKeyWizardViewModel.getWizardModel().setEmail(email.toString());
    }

    @Override
    public CharSequence getName() {
        return mCreateKeyWizardViewModel.getWizardModel().getName();
    }

    @Override
    public CharSequence getEmail() {
        return mCreateKeyWizardViewModel.getWizardModel().getEmail();
    }

    @Override
    public ArrayList<String> getAdditionalEmails() {
        return mCreateKeyWizardViewModel.getWizardModel().getAdditionalEmails();
    }

    @Override
    public Passphrase getPassphrase() {
        return mCreateKeyWizardViewModel.getWizardModel().getPassword();
    }

    @Override
    public void onWizardFragmentVisible(WizardFragment fragment) {
        mCurrentVisibleFragment = fragment;
    }

    /**
     * Updates the wizard screen state.
     */
    private void updateWizardState() {
        switch (mCreateKeyWizardViewModel.getWizardStep()) {
            case WIZARD_STEP_BEGIN: {
                mCurrentVisibleFragment = WelcomeWizardFragment.newInstance();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.unlockWizardFragmentContainer, mCurrentVisibleFragment,
                        FRAGMENT_TAG);
                transaction.commit();
            }
            break;
            case WIZARD_STEP_CHOOSE_UNLOCK_METHOD: {
                mCreateKeyWizardActivityButtonContainer.setVisibility(View.VISIBLE);
                mCreateKeyWizardActivityButtonContainer.animate().setDuration(300);
                mCreateKeyWizardActivityButtonContainer.animate().alpha(1);


                mCurrentVisibleFragment = UnlockChoiceWizardFragment.newInstance();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.addToBackStack(null);
                transaction.setCustomAnimations(R.anim.frag_slide_in_from_right,
                        R.anim.frag_slide_out_to_left,
                        R.anim.frag_slide_in_from_left, R.anim.frag_slide_out_to_right);
                transaction.replace(R.id.unlockWizardFragmentContainer, mCurrentVisibleFragment,
                        FRAGMENT_TAG);
                transaction.commit();
            }
            break;
            case WIZARD_STEP_KEYWORD_INPUT_VERIFICATION: {
                instantiateUnlockMethodFragment();
            }
            break;
            case WIZARD_STEP_CONTACT_NAME: {
                mCurrentVisibleFragment = NameWizardFragment.newInstance();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.frag_slide_in_from_right,
                        R.anim.frag_slide_out_to_left,
                        R.anim.frag_slide_in_from_left, R.anim.frag_slide_out_to_right);
                transaction.addToBackStack(null);
                transaction.replace(R.id.unlockWizardFragmentContainer, mCurrentVisibleFragment,
                        FRAGMENT_TAG);
                transaction.commit();

            }
            break;
            case WIZARD_STEP_CONTACT_EMAILS: {
                mCurrentVisibleFragment = EmailWizardFragment.newInstance();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.frag_slide_in_from_right,
                        R.anim.frag_slide_out_to_left,
                        R.anim.frag_slide_in_from_left, R.anim.frag_slide_out_to_right);
                transaction.addToBackStack(null);
                transaction.replace(R.id.unlockWizardFragmentContainer, mCurrentVisibleFragment,
                        FRAGMENT_TAG);
                transaction.commit();
            }
            break;
            case WIZARD_STEP_FINALIZE: {
                //finalize the creation of the key
                mCurrentVisibleFragment = WizardConfirmationFragment.
                        newInstance();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.frag_slide_in_from_right,
                        R.anim.frag_slide_out_to_left,
                        R.anim.frag_slide_in_from_left, R.anim.frag_slide_out_to_right);
                transaction.addToBackStack(null);
                transaction.replace(R.id.unlockWizardFragmentContainer, mCurrentVisibleFragment,
                        FRAGMENT_TAG);
                transaction.commit();
            }

            default:
                break;
        }
    }

    /**
     * Instantiates the unlock fragment based on its type.
     */
    private void instantiateUnlockMethodFragment() {
        switch (mCreateKeyWizardViewModel.getWizardModel().getSecretKeyType()) {
            case PIN: {
                mCurrentVisibleFragment = new PinUnlockWizardFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.addToBackStack(null);
                transaction.setCustomAnimations(R.anim.frag_slide_in_from_right,
                        R.anim.frag_slide_out_to_left,
                        R.anim.frag_slide_in_from_left, R.anim.frag_slide_out_to_right);
                transaction.replace(R.id.unlockWizardFragmentContainer, mCurrentVisibleFragment,
                        FRAGMENT_TAG);
                transaction.commit();

            }
            break;

            case PATTERN: {

            }
            break;
            default: {

            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mCreateKeyWizardViewModel.saveViewModelState(outState);
    }
}
