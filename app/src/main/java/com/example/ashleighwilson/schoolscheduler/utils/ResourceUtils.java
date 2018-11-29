package com.example.ashleighwilson.schoolscheduler.utils;

import android.content.Context;

public class ResourceUtils {

    public enum ResourceIdentifiers {xml, id, array}

    public static int getXmlId(Context context, ResourceIdentifiers resourceIdentifier, String resourceName) {

        return context.getResources().getIdentifier(resourceName, resourceIdentifier.name(), context.getPackageName());
    }
}
