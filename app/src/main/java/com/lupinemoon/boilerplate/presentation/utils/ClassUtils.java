package com.lupinemoon.boilerplate.presentation.utils;

import timber.log.Timber;

public class ClassUtils {

    /**
     * Gets a fully qualified class name. Intended to help get a reference to an activity to use when building a deep link intent.
     *
     * @param className     - Class name to deep link to from pushwoosh notification payload
     * @param knownPackages - At this time, a static list of known packages in constants.
     * @return - Null or Class
     */
    public static Class<?> getClassByKnownPackages(String className, String[] knownPackages) {
        Class<?> targetClass = null;
        for (int i = 0; i < knownPackages.length; i++) {
            try {
                String fqName = String.format("%s%s%s", knownPackages[i], ".", className);
                targetClass = Class.forName(fqName);
                // If we match, break since we have found the activity.
                // Provided only 1 activity within a project matches this package/activity FQN
                break;
            } catch (ClassNotFoundException exception) {
                Timber.e(exception, "Class not found within known packages.");
            }
        }
        return targetClass;
    }


}
