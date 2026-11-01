package livesports.visitor;

import livesports.tree.*;

import java.util.List;
import java.util.StringJoiner;

public final class JsonExportVisitor implements Visitor<String> {

    @Override
    public String visitLeague(League league) {
        return "{\"type\":\"league\",\"id\":" + quote(league.id())
                + ",\"name\":" + quote(league.name())
                + ",\"clubs\":" + array(league.clubs()) + "}";
    }

    @Override
    public String visitClub(Club club) {
        return "{\"type\":\"club\",\"id\":" + quote(club.id())
                + ",\"name\":" + quote(club.name())
                + ",\"teams\":" + array(club.teams()) + "}";
    }

    @Override
    public String visitTeam(Team team) {
        return "{\"type\":\"team\",\"id\":" + quote(team.id())
                + ",\"name\":" + quote(team.name())
                + ",\"members\":" + array(team.members()) + "}";
    }

    @Override
    public String visitPlayer(Player player) {
        return "{\"type\":\"player\",\"id\":" + quote(player.id())
                + ",\"name\":" + quote(player.name())
                + ",\"salary\":" + player.salary()
                + ",\"goals\":" + player.goals()
                + ",\"rating\":" + player.rating() + "}";
    }

    @Override
    public String visitCoach(Coach coach) {
        return "{\"type\":\"coach\",\"id\":" + quote(coach.id())
                + ",\"name\":" + quote(coach.name())
                + ",\"salary\":" + coach.salary()
                + ",\"experienceYears\":" + coach.experienceYears() + "}";
    }

    @Override
    public String visitPhysio(Physio physio) {
        return "{\"type\":\"physio\",\"id\":" + quote(physio.id())
                + ",\"name\":" + quote(physio.name())
                + ",\"salary\":" + physio.salary() + "}";
    }

    @Override
    public String visitAnalyst(Analyst analyst) {
        return "{\"type\":\"analyst\",\"id\":" + quote(analyst.id())
                + ",\"name\":" + quote(analyst.name())
                + ",\"salary\":" + analyst.salary() + "}";
    }

    @Override
    public String visitScout(Scout scout) {
        return "{\"type\":\"scout\",\"id\":" + quote(scout.id())
                + ",\"name\":" + quote(scout.name())
                + ",\"salary\":" + scout.salary()
                + ",\"region\":" + quote(scout.region()) + "}";
    }

    private String array(List<? extends Element> children) {
        StringJoiner joiner = new StringJoiner(",", "[", "]");
        for (Element child : children) {
            joiner.add(child.accept(this));
        }
        return joiner.toString();
    }

    private static String quote(String text) {
        return "\"" + text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n") + "\"";
    }
}