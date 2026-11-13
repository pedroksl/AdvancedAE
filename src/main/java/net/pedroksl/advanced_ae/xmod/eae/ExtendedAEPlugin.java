package net.pedroksl.advanced_ae.xmod.eae;

/*
 */

public class ExtendedAEPlugin {
    public static boolean isEntityProvider(Class<?> clazz) {
        /*
        try {
        	return clazz == TileExPatternProvider.class;
        } catch (Throwable ignored) {
        	return false;
        }
        */
        return false;
    }

    public static boolean isPartProvider(Class<?> clazz) {
        /*
        try {
        	return clazz == PartExPatternProvider.class;
        } catch (Throwable ignored) {
        	return false;
        }
        */
        return false;
    }
}
