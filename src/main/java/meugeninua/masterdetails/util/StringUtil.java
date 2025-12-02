package meugeninua.masterdetails.util;

public class StringUtil {

    public static String throwIfEmpty(String str, String message) {
        if (str == null || str.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return str;
    }

    public static String parseRegion(String arn) {
        throwIfEmpty(arn, "ARN must not be empty");
        var parts = arn.split(":");
        if (parts.length < 4) {
            throw new IllegalArgumentException(
                String.format("Cannot parse region from ARN [%s]", arn)
            );
        }
        return parts[3];
    }
}
