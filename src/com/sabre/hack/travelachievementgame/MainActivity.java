package com.sabre.hack.travelachievementgame;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AppEventsLogger;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphLocation;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphPlace;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.FacebookDialog.ShareDialogBuilder;
import com.facebook.widget.PickerFragment;
import com.facebook.widget.PlacePickerFragment;
import com.sabre.hack.travelachievementgame.MyLocation.LocationResult;

public class MainActivity extends FragmentActivity implements
ActionBar.TabListener {
	private final boolean bypassLocation = false;
	private Location mockLocation = new Location("") {
		{
			setLatitude(47.6097);
			setLongitude(-122.3331);
		}
	};

	private final String PENDING_ACTION_BUNDLE_KEY = "com.sabre.hack.travelachievementgame:PendingAction";


	private static final String PERMISSION = "publish_actions";
	private PendingAction pendingAction = PendingAction.NONE;
	private GraphUser user;
	private GraphPlace place;
	private List<GraphUser> tags;
	private boolean canPresentShareDialog;

	private enum PendingAction {
		NONE,
		POST_STATUS_UPDATE
	}
	private UiLifecycleHelper uiHelper;

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	private FacebookDialog.Callback dialogCallback = new FacebookDialog.Callback() {
		@Override
		public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
			Log.d("HelloFacebook", String.format("Error: %s", error.toString()));
		}

		@Override
		public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
			Log.d("HelloFacebook", "Success!");
		}
	};

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			String name = savedInstanceState.getString(PENDING_ACTION_BUNDLE_KEY);
			pendingAction = PendingAction.valueOf(name);
		}


		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
		.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))

					.setTabListener(this));
		}
		// start Facebook Login
		Session.openActiveSession(this, true, new Session.StatusCallback() {

			// callback when session changes state
			@Override
			public void call(Session session, SessionState state, Exception exception) {
				if (session.isOpened()) {

					// make request to the /me API
					Request.newMeRequest(session, new Request.GraphUserCallback() {

						// callback after Graph API response with user object
						@Override
						public void onCompleted(GraphUser user, Response response) {
							if (user != null) {
								Toast.makeText(getApplicationContext(), "Hello " + user.getFirstName(), Toast.LENGTH_SHORT).show();
							}
						}
					}).executeAsync();
				}
			}
		});

		final FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.fragment_container);
		if (fragment != null) {
			// If we're being re-created and have a fragment, we need to a) hide the main UI controls and
			// b) hook up its listeners again.
			//	        	findViewById(R.id.checkin).setVisibility(View.GONE);
			if (fragment instanceof PlacePickerFragment) {
				setPlacePickerListeners((PlacePickerFragment) fragment);
			}
		}

		// Listen for changes in the back stack so we know if a fragment got popped off because the user
		// clicked the back button.
		fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
			@Override
			public void onBackStackChanged() {
				if (fm.getBackStackEntryCount() == 0) {
					// We need to re-show our UI.
					//	                	findViewById(R.id.checkin).setVisibility(View.VISIBLE);
				}
			}
		});
		// Can we present the share dialog for regular links?
		canPresentShareDialog = FacebookDialog.canPresentShareDialog(this,
				FacebookDialog.ShareDialogFeature.SHARE_DIALOG);
	}

    @Override
    protected void onResume() {
        super.onResume();
        uiHelper.onResume();

        // Call the 'activateApp' method to log an app event for use in analytics and advertising reporting.  Do so in
        // the onResume methods of the primary Activities that an app may be launched into.
        AppEventsLogger.activateApp(this);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);

        outState.putString(PENDING_ACTION_BUNDLE_KEY, pendingAction.name());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data, dialogCallback);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main_dummy,
					container, false);
			int section_number = getArguments().getInt(
					ARG_SECTION_NUMBER);
			if(section_number != 1)
				rootView.findViewById(R.id.checkin).setVisibility(View.GONE);
			return rootView;
		}
	}

	private boolean hasPublishPermission() {
		Session session = Session.getActiveSession();
		return session != null && session.getPermissions().contains("publish_actions");
	}

	private void handlePendingAction() {
		PendingAction previouslyPendingAction = pendingAction;
		// These actions may re-set pendingAction if they are still pending, but we assume they
		// will succeed.
		pendingAction = PendingAction.NONE;

		switch (previouslyPendingAction) {
		case POST_STATUS_UPDATE:
			postStatusUpdate();
			break;
		default:
			break;
		}
	}


    private void showPublishResult(String message, GraphObject result, FacebookRequestError error) {
        String title = null;
        String alertMessage = null;
        if (error == null) {
            title = getString(R.string.success);
            String id = result.cast(GraphObjectWithId.class).getId();
            alertMessage = getString(R.string.successfully_posted_post, message, id);
        } else {
            title = getString(R.string.error);
            alertMessage = error.getErrorMessage();
        }

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(alertMessage)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    private FacebookDialog.ShareDialogBuilder createShareDialogBuilderForLink() {
    	ShareDialogBuilder result = new FacebookDialog.ShareDialogBuilder(this);
		if(place!= null)
		{
			result.setLink("http://www.facebook.com/"+place.getId());
			result.setName(place.getName());
		}
    	return result;
    }
	private interface GraphObjectWithId extends GraphObject {
		String getId();
	}
	private void postStatusUpdate() {
		Log.i("postStatusUpdate", "called");
		if (canPresentShareDialog) {
			Log.d("postStatusUpdate", "canPresentShareDialog true");
			FacebookDialog shareDialog = createShareDialogBuilderForLink().build();
			uiHelper.trackPendingDialogCall(shareDialog.present());
		} else if (user != null && hasPublishPermission()) {
			Log.d("postStatusUpdate", "canPresentShareDialog false, user not null, haspublishingpermission true");
			final String message = getString(R.string.status_update, user.getFirstName(), (new Date().toString()));
			Request request = Request
					.newStatusUpdateRequest(Session.getActiveSession(), message, place, tags, new Request.Callback() {
						@Override
						public void onCompleted(Response response) {
							showPublishResult(message, response.getGraphObject(), response.getError());
							Log.d("postStatusUpdate", "complete");
						}
					});
			request.executeAsync();
		} else {
			pendingAction = PendingAction.POST_STATUS_UPDATE;
		}
	}
	
	public void onClickPickPlace(View v) {
		Log.d("onClickPickPlace", "clicked");
		final PlacePickerFragment fragment = new PlacePickerFragment();
		LocationResult locationResult = new LocationResult(){
			@Override
			public void gotLocation(Location location){
				Log.d("onClickPickPlace", "getLocation");
				if(bypassLocation)
					fragment.setLocation(mockLocation);
				else
					fragment.setLocation(location);
				fragment.setTitleText("Pick a Location");

				setPlacePickerListeners(fragment);

				showPickerFragment(fragment);
			}
		};
		MyLocation myLocation = new MyLocation();
		myLocation.getLocation(this, locationResult);
	}
    
	private void performPublish(PendingAction action, boolean allowNoSession) {
		Session session = Session.getActiveSession();
		if (session != null) {
			pendingAction = action;
			if (hasPublishPermission()) {
				// We can do the action right away.
				handlePendingAction();
				return;
			} else if (session.isOpened()) {
				// We need to get new permissions, then complete the action when we get called back.
				session.requestNewPublishPermissions(new Session.NewPermissionsRequest(this, PERMISSION));
				return;
			}
		}

		if (allowNoSession) {
			pendingAction = action;
			handlePendingAction();
		}
	}

	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		if (pendingAction != PendingAction.NONE &&
				(exception instanceof FacebookOperationCanceledException ||
						exception instanceof FacebookAuthorizationException)) {
			new AlertDialog.Builder(MainActivity.this)
			.setTitle(R.string.cancelled)
			.setMessage(R.string.permission_not_granted)
			.setPositiveButton(R.string.ok, null)
			.show();
			pendingAction = PendingAction.NONE;
		} else if (state == SessionState.OPENED_TOKEN_UPDATED) {
			handlePendingAction();
		}
	}

	private void showPickerFragment(PickerFragment<?> fragment) {
		fragment.setOnErrorListener(new PickerFragment.OnErrorListener() {
			@Override
			public void onError(PickerFragment<?> pickerFragment, FacebookException error) {
				String text = getString(R.string.exception, error.getMessage());
				Log.e("PickerFragment Error", text);
			}
		});

		FragmentManager fm = getSupportFragmentManager();
		fm.beginTransaction()
		.replace(R.id.fragment_container, fragment)
		.addToBackStack(null)
		.commit();

//		       findViewById(R.id.checkin).setVisibility(View.GONE);

		// We want the fragment fully created so we can use it immediately.
		fm.executePendingTransactions();

		fragment.loadData(false);
	}

	private void onPlacePickerDone(PlacePickerFragment fragment) {
		FragmentManager fm = getSupportFragmentManager();
		fm.popBackStack();

		GraphPlace selection = fragment.getSelection();

		place = selection;
		String results;
		if (selection != null) {
            GraphLocation location = selection.getLocation();

            results = String.format("Name: %s\nCategory: %s\nLocation: (%f,%f)\nStreet: %s, %s, %s, %s, %s",
                    selection.getName(), selection.getCategory(),
                    location.getLatitude(), location.getLongitude(),
                    location.getStreet(), location.getCity(), location.getState(), location.getZip(),
                    location.getCountry());
        } else {
            results = "<No place selected>";
        }
		((TextView)findViewById(R.id.detail)).setText(results);
		performPublish(PendingAction.POST_STATUS_UPDATE, canPresentShareDialog);
	}

	private void setPlacePickerListeners(final PlacePickerFragment fragment) {
		fragment.setOnDoneButtonClickedListener(new PlacePickerFragment.OnDoneButtonClickedListener() {
			@Override
			public void onDoneButtonClicked(PickerFragment<?> pickerFragment) {
				onPlacePickerDone(fragment);
			}
		});
		fragment.setOnSelectionChangedListener(new PlacePickerFragment.OnSelectionChangedListener() {
			@Override
			public void onSelectionChanged(PickerFragment<?> pickerFragment) {
				if (fragment.getSelection() != null) {
					onPlacePickerDone(fragment);
				}
			}
		});
	}
}
