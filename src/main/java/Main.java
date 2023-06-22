import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    static ObjectMapper  obj1 = new ObjectMapper();

    public static void main(String[] args) {
        Javalin a1 = Javalin.create(config -> {
            config.staticFiles.add("/public", Location.CLASSPATH);
        }).start(8080);

        a1.get("/", ctx -> ctx.result("Hello Wordsada"));
        a1.get("/getSquad", ctx -> {
            List<Humans> osoby = new ArrayList<>();
            DatabaseUtil.executeQueryAndReturnResponse("SELECT * FROM public.\"Magazyn31\"", resultSet -> {
                try {
                    while (resultSet.next()) {
                        osoby.add(new Humans(resultSet.getString("name"), resultSet.getInt("age"), resultSet.getInt("id")));

                    }
                } catch (SQLException e) {
                    LOGGER.info(e.toString());
                }
                return null;
            });
            String line = "";
            for (Humans humans : osoby) {
                line = line + humans.getName();
            }
            ctx.html("<div>name:" + line + "</div>");
            //ctx.result(osoby.toString());

        });
        a1.post("/createSquad", ctx -> {
            LOGGER.info(ctx.body());
            Humans humans = obj1.readValue(ctx.body(), Humans.class);
            LOGGER.info("dupa");
            DatabaseUtil.executeQuery("INSERT INTO public.\"Magazyn31\" (name, age) VALUES('"+ humans.getName() + "'," + humans.getAge() + ")");
            ctx.redirect("/getSquad");
        });
        a1.get("/test", ctx -> {
           ctx.html("<h2>Check your reservation:</h2>\n" +
                   "<form method=\"get\" action=\"/check-reservation\">\n" +
                   "    Choose day\n" +
                   "    <select name=\"day\">\n" +
                   "        <option value=\"saturday\">Saturday</option>\n" +
                   "        <option value=\"sunday\">Sunday</option>\n" +
                   "    </select>\n" +
                   "    <br>\n" +
                   "    <button>Submit</button>\n" +
                   "</form>");
        });
    }

}
