package com.limkee1.order;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.limkee1.R;
import com.limkee1.entity.Customer;
import com.limkee1.navigation.NavigationActivity;
import java.util.ArrayList;
import java.util.List;

public class MainOrderHistoryFragment extends Fragment {

    private MainOrderHistoryFragment.OnFragmentInteractionListener mListener;
    private View view;
    private Customer customer;
    private String isEnglish;
    private Bundle myBundle;

    public MainOrderHistoryFragment(){}

    public static MainOrderHistoryFragment newInstance() {
        MainOrderHistoryFragment fragment = new MainOrderHistoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        Bundle bundle = getArguments();
        customer = bundle.getParcelable("customer");
        isEnglish = bundle.getString("language");
        if (isEnglish.equals("Yes")){
            ((NavigationActivity)getActivity()).setActionBarTitle("Order History");
        } else {
            ((NavigationActivity)getActivity()).setActionBarTitle("订单历史");
        }
    }

    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {
        MainOrderHistoryFragment.Adapter adapter = new MainOrderHistoryFragment.Adapter(getChildFragmentManager());
     //   Adapter adapter = new Adapter(getChildFragmentManager());
        Fragment pastFragment = new OrderHistoryFragment();
        Fragment cancelledFragment = new CancelledOrderFragment();

        myBundle = new Bundle();
        myBundle.putString("language", isEnglish);
        myBundle.putParcelable("customer", customer);

        pastFragment.setArguments(myBundle);
        cancelledFragment.setArguments(myBundle);
        if (isEnglish.equals("Yes")){
            adapter.addFragment(pastFragment, "Completed");
            adapter.addFragment(cancelledFragment, "Cancelled");
        } else {
            adapter.addFragment(pastFragment, "已完成");
            adapter.addFragment(cancelledFragment, "取消");
        }

        viewPager.setAdapter(adapter);
    }

        static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    // Adapter for the viewpager using FragmentPagerAdapter
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main_order_history, container, false);
        // Set ViewPager for each Tabs
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);
        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) view.findViewById(R.id.tabs);
        //let tab be clickable
        tabs.bringToFront();
        tabs.setupWithViewPager(viewPager);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NavigationActivity) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
