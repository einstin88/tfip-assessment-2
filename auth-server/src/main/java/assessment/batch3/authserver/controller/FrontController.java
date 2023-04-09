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

    /*
     * Tracking of app states:
     * 1) Session-managed states = authenticated status (10mins), captcha answer
     * 2) Redis-managed states = login attempts (30mins), locked out status (30mins)
     */
    private static final String AUTH_STATUS = "auth_status"; // a session key w/ boolean val
    private static final Integer AUTH_TIMEOUT = 600; // in seconds
    private static final Integer LOGIN_LIMIT = 3;

    private static final String CAPTCHA = "captcha"; // a session key w/ double val

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
        Boolean isDisabled = svc.isUserDisabled(loginForm.username()); 
        if (isDisabled != null && isDisabled)
            return "view2";

        /*
         * - Check if captcha exists.
         * - If exist & wrongly answered, +1 login attempt
         * - If exist & correctly answered, remove answer from session
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
         * - successful authentication will be redirected to success page
         * - failed authentication will throw an error and handled by the following
         * catch block
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

    /*
     * Protected resource that can only be accessed if authenticated
     */
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

    /*
     * Helper function to reduce code repetition in the login logic
     * - keeps record of a username's login attempt for 30mins on redis
     */
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

    /*
     * Simple function to create a new random captcha
     */
    private void addCaptcha(Model model, HttpSession session) {
        Captcha newCaptcha = Utils.newRandomCaptcha();
        model.addAttribute("captcha", newCaptcha);
        session.setAttribute(CAPTCHA, newCaptcha.answer());

        // log.info(">> Captcha: " + newCaptcha);
    }
}
