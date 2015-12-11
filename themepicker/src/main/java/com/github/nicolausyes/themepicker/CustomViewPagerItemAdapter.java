package com.github.nicolausyes.themepicker;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.ogaclejapan.smarttablayout.utils.ViewPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.ViewPagerItems;

class CustomViewPagerItemAdapter extends ViewPagerItemAdapter {

    interface OnInstantiateItemListener {
        void onItemInstantiated();
    }

    OnInstantiateItemListener onInstantiateItemListener;

    public CustomViewPagerItemAdapter(ViewPagerItems pages) {
        super(pages);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = (View)super.instantiateItem(container, position);
        view.setTag(position);
        return view;
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        super.finishUpdate(container);
        if(onInstantiateItemListener != null)
            onInstantiateItemListener.onItemInstantiated();
    }

    public void setOnInstantiateItemListener(OnInstantiateItemListener onInstantiateItemListener) {
        this.onInstantiateItemListener = onInstantiateItemListener;
    }
}