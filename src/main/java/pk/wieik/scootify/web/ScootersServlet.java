package pk.wieik.scootify.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import pk.wieik.scootify.repository.ScooterRepository;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/hulajnogi")
public class ScootersServlet extends HttpServlet {
    private final ScooterRepository scooters = new ScooterRepository();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/logowanie");
            return;
        }
        try {
            StringBuilder html = new StringBuilder();
            html.append("<h1>🛴 Flota hulajnóg</h1><p>Zalogowano jako: <strong>")
                    .append(Html.escape((String) session.getAttribute("login")))
                    .append("</strong> (rola: ")
                    .append(Html.roleName(session.getAttribute("uprawnienia")))
                    .append(")</p><table><thead><tr>")
                    .append("<th>Kod QR</th><th>Model</th><th>V max</th><th>Strefa</th>")
                    .append("<th>Bateria</th><th>Status</th><th>Lokalizacja</th>")
                    .append("</tr></thead><tbody>");

            var rows = scooters.findAll();
            for (var row : rows) {
                html.append("<tr><td>").append(Html.escape(row.kodQr()))
                        .append("</td><td>").append(Html.escape(row.model()))
                        .append("</td><td>").append(row.maxPredkosc()).append(" km/h")
                        .append("</td><td>").append(Html.escape(row.strefa()))
                        .append("</td><td>").append(row.bateria()).append("%")
                        .append("</td><td>").append(Html.escape(row.status()))
                        .append("</td><td>").append(row.szerokosc()).append(", ").append(row.dlugosc())
                        .append("</td></tr>");
            }
            html.append("</tbody></table>");
            if (rows.isEmpty()) {
                html.append("<p>Brak hulajnóg. Uruchom skrypt sql/dane-testowe.sql.</p>");
            }
            html.append("<form method='post' action='")
                    .append(Html.escape(request.getContextPath()))
                    .append("/wyloguj'><button>Wyloguj</button></form>");
            Html.send(response, "Scootify – flota", html.toString());
        } catch (SQLException ex) {
            throw new ServletException("Błąd odczytu hulajnóg", ex);
        }
    }
}