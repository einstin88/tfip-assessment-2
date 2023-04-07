package assessment.batch3.authserver.model;

public record Captcha(
        Integer a,
        Integer b,
        String operator,
        Double answer) {

}
