package com.limkee1.dashboard;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

public class DashboardFragment extends Fragment {
    private DashboardFragment.OnFragmentInteractionListener mListener;
    private DashboardFragment mAdapter;
    static DashboardFragment fragment;
    private View view;
    private String isEnglish;
    private Customer customer;
    private Bundle myBundle;

    public DashboardFragment() {
    }

    public static DashboardFragment newInstance() {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        isEnglish = bundle.getString("language");
        customer = bundle.getParcelable("customer");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isEnglish.equals("Yes")){
            ((NavigationActivity) getActivity()).setActionBarTitle("Dashboard");
        } else {
            ((NavigationActivity) getActivity()).setActionBarTitle("仪表板");
        }
        Bundle bundle = getArguments();
        isEnglish = bundle.getString("language");
        customer = bundle.getParcelable("customer");

    }


    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {
        DashboardFragment.Adapter adapter = new DashboardFragment.Adapter(getChildFragmentManager());
        Fragment salesFragment = new TotalSalesFragment();
        Fragment topProductFragment = new TopPurchasedFragment();

        myBundle = new Bundle();
        myBundle.putString("language", isEnglish);
        myBundle.putParcelable("customer", customer);

        salesFragment.setArguments(myBundle);
        topProductFragment.setArguments(myBundle);
        if (isEnglish.equals("Yes")){
            adapter.addFragment(salesFragment, "Spendings");
            adapter.addFragment(topProductFragment, "Products");
        } else {
            adapter.addFragment(salesFragment, "总花费");
            adapter.addFragment(topProductFragment, "频繁采购");
        }
        viewPager.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main_order_history, container, false);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);
        setupViewPager(viewPager);
        TabLayout tabs = (TabLayout) view.findViewById(R.id.tabs);
        tabs.bringToFront();
        tabs.setupWithViewPager(viewPager);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DashboardFragment.OnFragmentInteractionListener) {
            mListener = (DashboardFragment.OnFragmentInteractionListener) context;
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

    //adaptor
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
}
