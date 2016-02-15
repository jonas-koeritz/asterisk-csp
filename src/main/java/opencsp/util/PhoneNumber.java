package opencsp.util;

import opencsp.csta.Provider;


public class PhoneNumber {
    public static String cleanup(Provider provider, String input) {
        input = input.replaceAll("\\D", "");
        String prefix = provider.getCountryCode() + provider.getAreaCode() + provider.getSystemPrefix();
        if(input.indexOf(prefix) == 0) {
            return input.substring(prefix.length());
        }
        return input;
    }
}
