package assessment.batch3.authserver;

import java.security.SecureRandom;
import java.util.Random;

import assessment.batch3.authserver.model.Captcha;
import assessment.batch3.authserver.model.LoginForm;
import jakarta.json.Json;

public class Utils {
    private static final String[] OPERATORS = { "+", "-", "*", "/" };

    public static String toJsonLogin(LoginForm form) {
        return Json.createObjectBuilder()
                .add("username", form.username())
                .add("password", form.password())
                .build().toString();
    }

    // Generate new captcha object will all parameters
    public static Captcha newCaptcha() {
        Random rand = new SecureRandom();

        Integer a = generateRandomNumber(50);
        Integer b = generateRandomNumber(50);
        String operator = OPERATORS[rand.nextInt(OPERATORS.length - 1)];
        Double answer = 0d;

        switch (operator) {
            case "+" -> {
                answer = Double.valueOf(a + b);
            }
            case "-" -> {
                answer = Double.valueOf(a - b);
            }
            case "*" -> {
                answer = Double.valueOf(a * b);
            }
            case "/" -> {
                answer = Double.valueOf(a) / Double.valueOf(b);
            }
            default -> {
            }
        }

        return new Captcha(a, b, operator, answer);
    }

    private static Integer generateRandomNumber(Integer limit) {
        Random rand = new SecureRandom();

        Integer num = rand.nextInt(limit);

        if (num != 0)
            return num;

        return generateRandomNumber(limit);
    }
}
