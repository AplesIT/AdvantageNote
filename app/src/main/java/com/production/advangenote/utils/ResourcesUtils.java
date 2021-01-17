
package com.production.advangenote.utils;

import android.content.Context;


public class ResourcesUtils {

  public enum ResourceIdentifiers {XML, ID, ARRAY}


  public static int getXmlId(Context context, ResourceIdentifiers resourceIdentifier,
                             String resourceName) {
    return context.getResources()
        .getIdentifier(resourceName, resourceIdentifier.name().toLowerCase(),
            context.getPackageName());
  }

}
