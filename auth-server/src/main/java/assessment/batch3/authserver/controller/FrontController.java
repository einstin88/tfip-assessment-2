package assessment.batch3.authserver.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import assessment.batch3.authserver.Utils;
import assessment.batch3.authserver.Exception.AuthErrorException;
import assessment.batch3.authserver.model.Captcha;
import assessment.batch3.authserver.model.LoginForm;
import assessment.batch3.authserver.service.AuthenticationService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
public class FrontController {
    private static final Logger log = LoggerFactory.getLogger(FrontController.class);

    private static final String AUTH_STATUS = "auth_status"; // boolean val
    private static final Integer AUTH_TIMEOUT = 600; // in seconds
    private static final Integer LOGIN_LIMIT = 3;

    private static final String CAPTCHA = "captcha"; // double val

    @Autowired
    private AuthenticationService svc;

    @GetMapping({ "/", "index.html" })
    public String getLandingPage(Model model, HttpSession session) {
        log.info(">>> Request for landing page...");

        session.removeAttribute(CAPTCHA); // To simulate fresh login attempt
        model.addAttribute("loginForm", new LoginForm("", "", 0d));

        return "view0";
    }

    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String postUserLogin(
            @Valid LoginForm loginForm,
            BindingResult result,
            Model model,
            HttpSession session) {

        log.info(">>> Login attempt by: " + loginForm);

        // Check if user is disabled
        Boolean isDisabled = svc.getUserDisabled(loginForm.username()); 
        if (isDisabled != null && isDisabled)
            return "view2";

        /*
         * - Check captcha if it exists.
         * - If exist & correctly answered, remove answer from session
         * - If exist & wrongly answered, +1 login attempt
         */
        Double captchaAnswer = (Double) session.getAttribute(CAPTCHA);
        if (captchaAnswer != null && captchaAnswer != loginForm.captcha()) {
            log.info("--- Captcha validation failed");
            ObjectError err = new FieldError("captchaErr", CAPTCHA, "Incorrect captcha!");
            result.addError(err);

            return handleFailedLoginOrCaptcha(loginForm, model, session);
        }
        session.removeAttribute(CAPTCHA); // Captcha passed...

        // Check for form errors
        if (result.hasErrors()) {
            log.error("--- Form validation failed");
            return "view0";
        }

        /*
         * Attempts to authenticate the credentials
         * - failed authentication will throw an error and handled by this following
         * catch block
         * - successful authentication will be redirected to success page
         */
        try {
            log.info(">>> Form validation successful. Proceeding with authentication.");
            svc.authenticate(loginForm);

            log.info(">>> Authentication successful");
            session.setAttribute(AUTH_STATUS, true);
            session.setMaxInactiveInterval(AUTH_TIMEOUT);

            return "redirect:/protected";

        } catch (AuthErrorException e) {
            log.error("--- Authentication: " + e.getMessage());
            return handleFailedLoginOrCaptcha(loginForm, model, session);
        }
    }

    @GetMapping("/protected")
    public String getProtectedContent(HttpSession session) {
        Boolean authenticated = (Boolean) session.getAttribute(AUTH_STATUS);

        if (authenticated == null || !authenticated)
            return "redirect:/";

        return "protected/view1";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute(AUTH_STATUS);

        return "redirect:/";
    }

    private String handleFailedLoginOrCaptcha(
            LoginForm loginForm,
            Model model,
            HttpSession session) {

        Integer loginAttempts = svc.setLoginAttempt(loginForm.username());

        if (loginAttempts > LOGIN_LIMIT) {
            svc.disableUser(loginForm.username());
            return "view2";
        }
        addCaptcha(model, session);

        return "view0";
    }

    private void addCaptcha(Model model, HttpSession session) {
        Captcha newCaptcha = Utils.newCaptcha();
        model.addAttribute("captcha", newCaptcha);
        session.setAttribute(CAPTCHA, newCaptcha.answer());

        // log.info(">> Captcha: " + newCaptcha);
    }
}
