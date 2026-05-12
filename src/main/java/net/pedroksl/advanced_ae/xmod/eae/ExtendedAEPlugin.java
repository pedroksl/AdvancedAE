package net.pedroksl.advanced_ae.xmod.eae;

public class ExtendedAEPlugin {
    public static boolean isEntityProvider(Class<?> clazz) {
        try {
            return false;
            //            return clazz == TileExPatternProvider.class;
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static boolean isPartProvider(Class<?> clazz) {
        try {
            return false;
            //            return clazz == PartExPatternProvider.class;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
