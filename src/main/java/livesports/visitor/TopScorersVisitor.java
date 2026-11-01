package livesports.visitor;

import livesports.tree.*;

import java.util.ArrayList;
import java.util.List;

public final class TopScorersVisitor implements Visitor<List<Player>> {

    private final int minGoals;

    public TopScorersVisitor(int minGoals) {
        this.minGoals = minGoals;
    }

    @Override
    public List<Player> visitLeague(League league) { return collect(league.clubs()); }

    @Override
    public List<Player> visitClub(Club club) { return collect(club.teams()); }

    @Override
    public List<Player> visitTeam(Team team) { return collect(team.members()); }

    @Override
    public List<Player> visitPlayer(Player player) {
        return player.goals() >= minGoals ? List.of(player) : List.of();
    }

    @Override
    public List<Player> visitCoach(Coach coach) { return List.of(); }

    @Override
    public List<Player> visitPhysio(Physio physio) { return List.of(); }

    @Override
    public List<Player> visitAnalyst(Analyst analyst) { return List.of(); }

    @Override
    public List<Player> visitScout(Scout scout) { return List.of(); }

    private List<Player> collect(List<? extends Element> children) {
        List<Player> result = new ArrayList<>();
        for (Element child : children) {
            result.addAll(child.accept(this));
        }
        return result;
    }
}