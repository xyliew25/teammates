package teammates.ui.servlets;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.datatransfer.UserInfoCookie;
import teammates.common.util.Logger;

/**
 * Servlet that handles the OAuth2 callback.
 */
public class OAuth2CallbackServlet extends AuthServlet {

    private static final Logger log = Logger.getLogger();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String email = req.getParameter("email");
        if (email == null) {
            return;
        }

        UserInfoCookie uic = new UserInfoCookie(email.replaceFirst("@gmail\\.com$", ""));
        Cookie cookie = getLoginCookie(uic);
        resp.addCookie(cookie);

        String nextUrl = req.getParameter("nextUrl");
        if (nextUrl == null) {
            nextUrl = "/";
        }
        // Prevent HTTP response splitting
        nextUrl = resp.encodeRedirectURL(nextUrl.replace("\r\n", ""));
        resp.sendRedirect(nextUrl);
    }

    private void logAndPrintError(HttpServletRequest req, HttpServletResponse resp, int status, String message)
            throws IOException {
        resp.setStatus(status);
        resp.getWriter().print(message);

        log.request(req, status, message);
    }

}
