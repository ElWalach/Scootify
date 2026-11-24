package pk.wieik.scootify.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pk.wieik.scootify.repository.UserRepository;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/rejestracja")
public class RegisterServlet extends HttpServlet {
    private final UserRepository users = new UserRepository();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        showForm(request, response, "");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        String haslo = request.getParameter("haslo");
        if (haslo == null || !haslo.equals(request.getParameter("haslo2"))) {
            showForm(request, response, "Hasła nie są takie same.");
            return;
        }
        try {
            users.createUser(request.getParameter("login"), haslo, 0);
            response.sendRedirect(request.getContextPath() + "/logowanie?zarejestrowano=1");
        } catch (IllegalArgumentException ex) {
            showForm(request, response, ex.getMessage());
        } catch (SQLException ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("UNIQUE")) {
                showForm(request, response, "Ten login jest już zajęty.");
            } else {
                throw new ServletException("Błąd bazy podczas rejestracji", ex);
            }
        }
    }

    private void showForm(HttpServletRequest request, HttpServletResponse response, String error)
            throws IOException {
        String path = Html.escape(request.getContextPath());
        String errorHtml = error.isEmpty() ? "" : "<p class='error'>" + Html.escape(error) + "</p>";
        Html.send(response, "Scootify – rejestracja", """
            <h1>🛴 Scootify – rejestracja</h1>
            %s
            <form method="post" action="%s/rejestracja">
                <label>Login: <input name="login" required maxlength="64"></label><br>
                <label>Hasło (min. 12 znaków): <input type="password" name="haslo" required minlength="12"></label><br>
                <label>Powtórz hasło: <input type="password" name="haslo2" required minlength="12"></label><br>
                <button type="submit">Załóż konto</button>
            </form>
            <p><a href="%s/logowanie">Masz już konto? Zaloguj się</a></p>
            """.formatted(errorHtml, path, path));
    }
}