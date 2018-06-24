package util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Password_Validator {

    private static Pattern pattern;
    private static Matcher matcher;

    private static final String PASSWORD_PATTERN =
            "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%*_+-.=?/&]).{8,15})";

    public Password_Validator() {
        pattern = Pattern.compile(PASSWORD_PATTERN);
    }

    /**
     * Validate password with regular expression
     * @param password password for validation
     * @return true valid password, false invalid password
     */
    public static boolean validate(final String password) {

        matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
