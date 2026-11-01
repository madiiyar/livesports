package livesports.visitor;

import livesports.tree.*;

import java.util.List;

public final class TotalSalaryVisitor implements Visitor<Integer> {

    @Override
    public Integer visitLeague(League league) { return sumOf(league.clubs()); }

    @Override
    public Integer visitClub(Club club) { return sumOf(club.teams()); }

    @Override
    public Integer visitTeam(Team team) { return sumOf(team.members()); }

    @Override
    public Integer visitPlayer(Player player) { return player.salary(); }

    @Override
    public Integer visitCoach(Coach coach) { return coach.salary(); }

    @Override
    public Integer visitPhysio(Physio physio) { return physio.salary(); }

    @Override
    public Integer visitAnalyst(Analyst analyst) { return analyst.salary(); }

    @Override
    public Integer visitScout(Scout scout) { return scout.salary(); }

    private int sumOf(List<? extends Element> children) {
        int total = 0;
        for (Element child : children) {
            total += child.accept(this);   // double dispatch happens here
        }
        return total;
    }
}