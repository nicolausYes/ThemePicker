package com.github.nicolausyes.themepicker.util;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 23-Nov-15.
 */
public class ViewUtil {
    public static List<View> getAllChildren(View view) {
        List<View> visited = new ArrayList<View>();
        List<View> unvisited = new ArrayList<View>();
        unvisited.add(view);

        while (!unvisited.isEmpty()) {
            View child = unvisited.remove(0);
            visited.add(child);
            if (!(child instanceof ViewGroup)) continue;
            ViewGroup group = (ViewGroup) child;
            final int childCount = group.getChildCount();
            for (int i=0; i<childCount; i++) unvisited.add(group.getChildAt(i));
        }

        return visited;
    }

    public static List<View> getAllChildrenOfType(View view, Class classType) {
        List<View> listViews = getAllChildren(view);
        List<View> result = new ArrayList<>();
        for(View v : listViews) {
            if(classType.isInstance(v))
                result.add(v);
        }

        return result;
    }
}
