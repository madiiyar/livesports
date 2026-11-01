package livesports.visitor;

import livesports.tree.*;

import java.util.List;

public final class RosterStatsVisitor implements Visitor<Void> {

    private int playerCount;
    private int staffCount;
    private int totalGoals;
    private int totalRating;

    @Override
    public Void visitLeague(League league) { visitAll(league.clubs()); return null; }

    @Override
    public Void visitClub(Club club) { visitAll(club.teams()); return null; }

    @Override
    public Void visitTeam(Team team) { visitAll(team.members()); return null; }

    @Override
    public Void visitPlayer(Player player) {
        playerCount++;
        totalGoals += player.goals();
        totalRating += player.rating();
        return null;
    }

    @Override
    public Void visitCoach(Coach coach) { staffCount++; return null; }

    @Override
    public Void visitPhysio(Physio physio) { staffCount++; return null; }

    @Override
    public Void visitAnalyst(Analyst analyst) { staffCount++; return null; }

    public int playerCount() { return playerCount; }
    public int staffCount() { return staffCount; }
    public int totalGoals() { return totalGoals; }

    public double averageRating() {
        return playerCount == 0 ? 0.0 : (double) totalRating / playerCount;
    }

    private void visitAll(List<? extends Element> children) {
        for (Element child : children) {
            child.accept(this);
        }
    }
}